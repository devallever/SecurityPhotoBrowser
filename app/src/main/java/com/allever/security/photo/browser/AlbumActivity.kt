package com.allever.security.photo.browser

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.allever.lib.common.app.App
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.ItemListener

import com.allever.lib.common.util.ToastUtils
import com.allever.lib.permission.PermissionManager

import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.app.GlobalData
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeEvent
import com.allever.security.photo.browser.bean.event.EncodeEvent
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.password.PasswordConfig
import com.allever.security.photo.browser.ui.GalleryActivity
import com.allever.security.photo.browser.ui.SettingActivity
import com.allever.security.photo.browser.ui.adapter.PrivateAlbumAdapter
import com.allever.security.photo.browser.ui.dialog.AlbumDialog
import com.allever.security.photo.browser.ui.mvp.presenter.AlbumPresenter
import com.allever.security.photo.browser.ui.mvp.view.AlbumView
import com.allever.security.photo.browser.util.DialogHelper
import com.allever.security.photo.browser.util.FileUtil
import com.allever.security.photo.browser.util.MD5

import com.android.absbase.ui.widget.RippleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class AlbumActivity : Base2Activity<AlbumView, AlbumPresenter>(), AlbumView, View.OnClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private lateinit var mIvSetting: RippleImageView
    private lateinit var mBtnAddAlbum: View
    private var mImageFolderList = mutableListOf<ImageFolder>()

    private var mClickAlbumPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        EventBus.getDefault().register(this)

        //清除私密相册密码验证记录
        PasswordConfig.secretCheckPass = false

        initView()

        initData()

        mPresenter.requestPermission(this, Runnable {
            mPresenter.getPrivateAlbumData()
        })
    }

    override fun createPresenter(): AlbumPresenter = AlbumPresenter()

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelTask()
        EventBus.getDefault().unregister(this)
        GlobalData.albumData.clear()
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.album_recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mIvSetting = findViewById(R.id.album_iv_setting)
        mIvSetting.setOnClickListener(this)
        mBtnAddAlbum = findViewById(R.id.album_btn_add_album)
        mBtnAddAlbum.setOnClickListener(this)

        mAlbumBottomDialog = AlbumDialog(mAlbumBottomDialogCallback)
    }

    private fun initData() {
        mPrivateAlbumAdapter = PrivateAlbumAdapter(this, R.layout.item_private_album, mImageFolderList)
        mRecyclerView.adapter = mPrivateAlbumAdapter
        mPrivateAlbumAdapter.mItemListener = object : ItemListener {
            override fun onItemClick(position: Int, holder: BaseViewHolder) {
                GalleryActivity.start(
                    this@AlbumActivity,
                    mImageFolderList[position].name!!,
                    mImageFolderList[position].dir!!,
                    ArrayList(mImageFolderList[position].data)
                )
                mClickAlbumPosition = position
            }
        }
        mPrivateAlbumAdapter.listener = object : PrivateAlbumAdapter.ItemClickListener {
            override fun onMoreClick(position: Int) {
                mMorePosition = position
                mAlbumBottomDialog?.show(supportFragmentManager, AlbumActivity::class.java.simpleName)
            }
        }
    }

    /***
     * 底部弹窗
     */
    private var mAlbumBottomDialog: AlbumDialog? = null
    private val mAlbumBottomDialogCallback = object : AlbumDialog.Callback {
        override fun onDeleteClick(dialog: AppCompatDialogFragment) {
            dialog.dismissAllowingStateLoss()
            mPresenter.deleteAlbum(this@AlbumActivity, mMorePosition)
        }

        override fun onRenameClick(dialog: AppCompatDialogFragment) {
            dialog.dismissAllowingStateLoss()
            mPresenter.renameAlbum(this@AlbumActivity, mMorePosition)
        }
    }

    private var mMorePosition = 0

    override fun onClick(v: View?) {
        when (v) {
            mIvSetting -> {
                SettingActivity.start(this)
            }
            mBtnAddAlbum -> {
                mPresenter.requestPermission(this, Runnable {
                    mPresenter.handleAddAlbum(this)
                })
            }
        }
    }

    override fun updateAlbumList(data: MutableList<ImageFolder>) {
        mImageFolderList.clear()
        mImageFolderList.addAll(data)
        mPrivateAlbumAdapter.notifyDataSetChanged()
    }

    override fun updateAddAlbum(data: ImageFolder) {
        mPrivateAlbumAdapter.addData(mImageFolderList.size, data)
        //滚动到底部
        mHandler.postDelayed({
            mRecyclerView.smoothScrollToPosition(mImageFolderList.size)
        }, 200)
    }

    override fun showBottomDialog() {
        mAlbumBottomDialog?.show(supportFragmentManager, AlbumActivity::class.java.simpleName)
    }

    override fun hideBottomDialog() {
        mAlbumBottomDialog?.dismiss()
    }

    override fun updateDeleteAlbum() {
        mImageFolderList.removeAt(mMorePosition)
        mPrivateAlbumAdapter.notifyDataSetChanged()
    }

    override fun updateRenameAlbum(albumName: String, dir: String) {
        val imageFolder = mImageFolderList[mMorePosition]
        imageFolder.dir = dir
        imageFolder.name = albumName
        mPrivateAlbumAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    ToastUtils.show("获取加密相册内容")
                    //获取加密相册内容
                    if (PermissionManager.hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                       mPresenter.getPrivateAlbumData()
                    }
                }
                1 -> {
                    //创建相册
                    ToastUtils.show("创建相册")
                    if (PermissionManager.hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        mPresenter.handleAddAlbum(this)
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveDecodeEvent(decodeEvent: DecodeEvent) {
        val imageFolder = mImageFolderList[mClickAlbumPosition]
        decodeEvent.indexList.map {
            val thumbnailBean = imageFolder.data?.get(it)
            val nameMd5 = MD5.getMD5Str(thumbnailBean?.path!!)
            val linkFile = File(imageFolder.dir, nameMd5)
            if (linkFile.exists()) {
                linkFile.delete()
            }
            imageFolder.data?.removeAt(it)
            imageFolder.count = imageFolder.data?.size?:0
        }
        //刷新界面
        mPrivateAlbumAdapter.notifyDataSetChanged()

        GlobalData.albumData.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveEncodeEvent(encodeEvent: EncodeEvent) {
        val imageFolder = mImageFolderList[mClickAlbumPosition]
        encodeEvent.thumbnailBeanList.map {
            imageFolder.data?.add(it)
        }

        imageFolder.count = imageFolder.data?.size?:0
        //排序
        val sortThumbnailBeans = ArrayList<ThumbnailBean>()
        sortThumbnailBeans.addAll(imageFolder.data!!)
        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
            java.lang.Long.compare(arg1.date, arg0.date)
        })

        imageFolder.data = sortThumbnailBeans

        //刷新界面
        mPrivateAlbumAdapter.notifyDataSetChanged()

        GlobalData.albumData.clear()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AlbumActivity::class.java)
            context.startActivity(intent)
        }
    }
}

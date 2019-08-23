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

    private val mAlbumImageFolderMap = LinkedHashMap<String, ImageFolder>()
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private var mAddAlbumDialog: AlertDialog? = null
    //修改相册名称弹窗
    private var mRenameAlbumDialog: AlertDialog? = null

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
        mRecyclerView.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?

        mIvSetting = findViewById(R.id.album_iv_setting)
        mIvSetting.setOnClickListener(this)
        mBtnAddAlbum = findViewById(R.id.album_btn_add_album)
        mBtnAddAlbum.setOnClickListener(this)
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
                if (mAlbumBottomDialog == null) {
                    mAlbumBottomDialog = AlbumDialog(mAlbumBottomDialogCallback)
                }
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

            //删除提示弹窗
            val builder = AlertDialog.Builder(this@AlbumActivity)
                .setMessage(R.string.tips_dialog_delete_resource)
                .setNegativeButton(R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.delete, DialogInterface.OnClickListener { dialog, which ->
                    //启动一个Task删除， 遍历删除

                    if (mMorePosition < 0 || mMorePosition >= mImageFolderList.size) {
                        return@OnClickListener
                    }
                    val imageFolder = mImageFolderList[mMorePosition]
                    getDeleteAlbumTask().execute(imageFolder)
                    dialog.dismiss()
                })
            builder.show()

        }

        override fun onRenameClick(dialog: AppCompatDialogFragment) {
            if (mAlbumBottomDialog != null) {
                mAlbumBottomDialog!!.dismissAllowingStateLoss()
            }

            val imageFolder = mImageFolderList[mMorePosition] ?: return

            val albumName = imageFolder.name

            val builder = DialogHelper.Builder()
                .setTitleContent(getString(R.string.rename_album))
                .isShowMessage(false)
                .isShowEditText(true)
                .setOkContent(getString(R.string.save))
                .setCancelContent(getString(R.string.cancel))
                .setEditTextContent(albumName?:"")

            mRenameAlbumDialog = DialogHelper.createEditTextDialog(
                this@AlbumActivity,
                builder,
                object : DialogHelper.EditDialogCallback{
                    override fun onOkClick(dialog: AlertDialog, etContent: String) {
                        if (TextUtils.isEmpty(etContent)) {
                            ToastUtils.show(getString(R.string.tips_please_input_album_name))
                            return
                        }

                        val albumPath = PrivateHelper.PATH_ALBUM + File.separator + etContent

                        //判断相册是是否重复
                        if (FileUtil.isExistsFile(albumPath)) {
                            //已存在，
                            ToastUtils.show(getString(R.string.already_exist_album))
                            return
                        }

                        if (mMorePosition >= mImageFolderList.size) {
                            return
                        }

                        val imageFolder = mImageFolderList.get(mMorePosition) ?: return

                        val albumDir = imageFolder.dir
                        val albumDirFile = File(albumDir)
                        val albumDesDirFile = File(albumPath)
                        var renameOk = false
                        try {
                            renameOk = albumDirFile.renameTo(albumDesDirFile)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (!renameOk) {
                            com.android.absbase.utils.ToastUtils.show(com.android.absbase.App.getContext().getString(R.string.album_rename_failed))
                            dialog.dismiss()
                            mAlbumBottomDialog?.dismiss()
                            return
                        }

                        dialog.dismiss()
                        mAlbumBottomDialog?.dismiss()

                        mAlbumImageFolderMap.remove(albumDir)
                        imageFolder.dir = (albumPath)
                        imageFolder.name = (albumDesDirFile.name)
                        mAlbumImageFolderMap[albumPath] = imageFolder
                        mPrivateAlbumAdapter.notifyDataSetChanged()

                    }

                    override fun onCancelClick(dialog: AlertDialog) {
                        mAlbumBottomDialog?.dismiss()
                    }
                })
            mRenameAlbumDialog?.show()
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

    override fun addAlbum(data: ImageFolder) {
        mPrivateAlbumAdapter.addData(mImageFolderList.size, data)
        //滚动到底部
        mHandler.postDelayed({
            mRecyclerView.smoothScrollToPosition(mImageFolderList.size)
        }, 200)
    }

    private fun handleAddAlbum() {
        mPresenter.handleAddAlbum(this)
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

    /***
     * 删除相册异步任务
     * @return
     */
    fun getDeleteAlbumTask(): AsyncTask<ImageFolder, Void, Boolean> {
        return object : AsyncTask<ImageFolder, Void, Boolean>() {
            override fun doInBackground(vararg imageFolders: ImageFolder): Boolean? {
                if (imageFolders.isEmpty()) {
                    return null
                }

                val imageFolder = imageFolders[0] ?: return null

                val thumbnailBeanList = imageFolder.data
                if (thumbnailBeanList != null) {
                    if (thumbnailBeanList.size > 0) {
                        //有内容，遍历删除加密文件，
                        for (bean in thumbnailBeanList!!) {
                            //
                            val fileNameMd5 = MD5.getMD5Str(bean.path)
                            FileUtil.deleteFile(PrivateHelper.PATH_ENCODE_ORIGINAL + File.separator + fileNameMd5)
                        }
                    }

                    //删除目录
                    FileUtil.deleteFolder(imageFolder.dir)
                    return true
                }

                return false
            }

            override fun onPostExecute(isSuccess: Boolean?) {
                if (isSuccess!!) {
                    //已删除，刷新数据
                    com.android.absbase.utils.ToastUtils.show(getString(R.string.delete_finish))

                    mImageFolderList.removeAt(mMorePosition)
                    mPrivateAlbumAdapter.notifyDataSetChanged()

                } else {
                    //失败，不做处理
                }
                //弹窗消失
                mAlbumBottomDialog?.dismiss()
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

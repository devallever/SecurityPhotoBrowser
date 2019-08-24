package com.allever.security.photo.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.ItemListener
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.ui.GalleryActivity
import com.allever.security.photo.browser.ui.SettingActivity
import com.allever.security.photo.browser.ui.adapter.PrivateAlbumAdapter
import com.allever.security.photo.browser.ui.dialog.AlbumDialog
import com.allever.security.photo.browser.ui.mvp.presenter.AlbumPresenter
import com.allever.security.photo.browser.ui.mvp.view.AlbumView

import com.android.absbase.ui.widget.RippleImageView
import kotlin.collections.ArrayList

class AlbumActivity : Base2Activity<AlbumView, AlbumPresenter>(), AlbumView, View.OnClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private lateinit var mIvSetting: RippleImageView
    private lateinit var mBtnAddAlbum: View
    private var mImageFolderList = mutableListOf<ImageFolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        //清除私密相册密码验证记录
        mPresenter.clearPasswordStatus()

        initView()

        initData()

        mPresenter.requestPermission(this, Runnable {
            mPresenter.getPrivateAlbumData()
        })
    }

    override fun createPresenter(): AlbumPresenter = AlbumPresenter()

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.destroy()
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
                mPresenter.setAlbumClickPosition(position)
            }
        }
        mPrivateAlbumAdapter.listener = object : PrivateAlbumAdapter.ItemClickListener {
            override fun onMoreClick(position: Int) {
                mPresenter.setMorePosition(position)
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
            mPresenter.deleteAlbum(this@AlbumActivity)
        }

        override fun onRenameClick(dialog: AppCompatDialogFragment) {
            dialog.dismissAllowingStateLoss()
            mPresenter.renameAlbum(this@AlbumActivity)
        }
    }

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

    override fun updateDeleteAlbum(position: Int) {
        mPrivateAlbumAdapter.remove(position)
    }

    override fun updateRenameAlbum(position: Int, albumName: String, dir: String) {
        val imageFolder = mImageFolderList[position]
        imageFolder.dir = dir
        imageFolder.name = albumName
        mPrivateAlbumAdapter.updateData(position)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPresenter.onActivityResult(this, requestCode, resultCode, data)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AlbumActivity::class.java)
            context.startActivity(intent)
        }
    }
}

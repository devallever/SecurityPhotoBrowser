package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDialogFragment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.ItemListener
import com.allever.lib.notchcompat.NotchCompat
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.activity_album.top_bar
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.app.BaseActivity
import org.xm.secret.photo.album.bean.ImageFolder
import org.xm.secret.photo.album.ui.adapter.PrivateAlbumAdapter
import org.xm.secret.photo.album.ui.dialog.AlbumDialog
import org.xm.secret.photo.album.ui.mvp.presenter.AlbumPresenter
import org.xm.secret.photo.album.ui.mvp.view.AlbumView

import kotlin.collections.ArrayList

class AlbumActivity : BaseActivity<AlbumView, AlbumPresenter>(), AlbumView, View.OnClickListener, AlbumDialog.Callback {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AlbumActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private var mAlbumBottomDialog: AlbumDialog? = null

    override fun getContentView(): Int =
        R.layout.activity_album
    override fun createPresenter(): AlbumPresenter = AlbumPresenter()
    override fun initView() {
        NotchCompat.adaptNotchWithFullScreen(window)
        checkNotch(Runnable {
            addStatusBar(rootLayout, top_bar)
        })
        mRecyclerView = findViewById(R.id.album_recycler_view)
        mRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)

        findViewById<View>(R.id.album_iv_setting).setOnClickListener(this)
        findViewById<View>(R.id.album_btn_add_album).setOnClickListener(this)

        mAlbumBottomDialog = AlbumDialog(this)
    }

    override fun initData() {
        //清除私密相册密码验证记录
        mPresenter.clearPasswordStatus()

        mPrivateAlbumAdapter = PrivateAlbumAdapter(this,
            R.layout.item_private_album, mImageFolderList)
        mRecyclerView.adapter = mPrivateAlbumAdapter
        mPrivateAlbumAdapter.mItemListener = object : ItemListener {
            override fun onItemClick(position: Int, holder: BaseViewHolder) {
                GalleryActivity.start(
                    this@AlbumActivity,
                    mImageFolderList[position].name!!,
                    mImageFolderList[position].dir!!,
                    mImageFolderList[position].data?.let { ArrayList(it) }
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

        mPresenter.requestPermission(this, Runnable {
            mPresenter.getPrivateAlbumData()
        })
    }

    override fun onDestroy() {
        mPresenter.destroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.album_iv_setting -> {
                SettingActivity.start(this)
            }
            R.id.album_btn_add_album -> {
                mPresenter.requestPermission(this, Runnable {
                    mPresenter.handleAddAlbum(this)
                })
            }
        }
    }

    override fun onBackPressed() {
        if (isPasswordViewShowing()) {
            super.onBackPressed()
            return
        }
        checkExit()
    }

    override fun onDeleteClick(dialog: AppCompatDialogFragment) {
        dialog.dismissAllowingStateLoss()
        mPresenter.deleteAlbum(this@AlbumActivity)
    }

    override fun onRenameClick(dialog: AppCompatDialogFragment) {
        dialog.dismissAllowingStateLoss()
        mPresenter.renameAlbum(this@AlbumActivity)
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
}

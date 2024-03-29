package org.xm.secret.photo.album.ui

import android.Manifest
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.ItemListener
import com.allever.lib.permission.PermissionManager
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.BaseFragment
import org.xm.secret.photo.album.bean.ImageFolder
import org.xm.secret.photo.album.ui.adapter.PrivateAlbumAdapter
import org.xm.secret.photo.album.ui.dialog.AlbumDialog
import org.xm.secret.photo.album.ui.mvp.presenter.AlbumPresenter
import org.xm.secret.photo.album.ui.mvp.view.AlbumView

class AlbumFragment : BaseFragment<AlbumView, AlbumPresenter>(), AlbumView, View.OnClickListener,
    AlbumDialog.Callback {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private var mAlbumBottomDialog: AlbumDialog? = null

    private var mBannerAd: IAd? = null

    override fun getContentView(): Int = R.layout.fragment_album

    override fun createPresenter(): AlbumPresenter = AlbumPresenter()
    override fun initView(root: View) {
        mRecyclerView = root.findViewById(R.id.album_recycler_view)
        mRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 2)

        root.findViewById<View>(R.id.album_btn_add_album).setOnClickListener(this)
        mBannerContainer = root.findViewById(R.id.bannerContainer)
        mAlbumBottomDialog = AlbumDialog(this)
    }

    override fun initData() {
        loadBanner()
        //清除私密相册密码验证记录
        mPresenter.clearPasswordStatus()

        mPrivateAlbumAdapter =
            PrivateAlbumAdapter(activity!!, R.layout.item_private_album, mImageFolderList)
        mRecyclerView.adapter = mPrivateAlbumAdapter
        mPrivateAlbumAdapter.mItemListener = object : ItemListener {
            override fun onItemClick(position: Int, holder: BaseViewHolder) {
                GalleryActivity.start(
                    activity!!,
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
                activity?.supportFragmentManager?.let {
                    mAlbumBottomDialog?.show(
                        it,
                        AlbumActivity::class.java.simpleName
                    )
                }

            }
        }

        if (PermissionManager.hasPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)) {
            mPresenter.requestPermission(activity, Runnable {
                mPresenter.getPrivateAlbumData()
            })
        } else {
            AlertDialog.Builder(activity!!)
                .setMessage(R.string.permission_tips)
                .setPositiveButton(R.string.permission_accept) { dialog, which ->
                    dialog.dismiss()
                    mPresenter.requestPermission(activity, Runnable {
                        mPresenter.getPrivateAlbumData()
                    })
                }
                .setNegativeButton(R.string.permission_reject) { dialog, which ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.album_btn_add_album -> {
                if (PermissionManager.hasPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)) {
                    mPresenter.requestPermission(activity, Runnable {
                        mPresenter.handleAddAlbum(activity!!)
                    })
                } else {
                    AlertDialog.Builder(activity!!)
                        .setMessage(R.string.permission_tips)
                        .setPositiveButton(R.string.permission_accept) { dialog, which ->
                            dialog.dismiss()
                            mPresenter.requestPermission(activity, Runnable {
                                mPresenter.handleAddAlbum(activity!!)
                            })
                        }
                        .setNegativeButton(R.string.permission_reject) { dialog, which ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            }
        }
    }

    override fun onDeleteClick(dialog: AppCompatDialogFragment) {
        dialog.dismissAllowingStateLoss()
        mPresenter.deleteAlbum(activity!!)
    }

    override fun onRenameClick(dialog: AppCompatDialogFragment) {
        dialog.dismissAllowingStateLoss()
        mPresenter.renameAlbum(activity!!)
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
        activity?.supportFragmentManager?.let {
            mAlbumBottomDialog?.show(
                it,
                AlbumActivity::class.java.simpleName
            )
        }

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
        mPresenter.onActivityResult(activity!!, requestCode, resultCode, data)
    }

    override fun onPause() {
        super.onPause()
        mBannerAd?.onAdPause()
    }

    override fun onResume() {
        super.onResume()
        mBannerAd?.onAdResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.destroy()
        mBannerAd?.destroy()
    }

    private lateinit var mBannerContainer: ViewGroup
    private fun loadBanner() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_ALBUM_BANNER, mBannerContainer, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mBannerAd = ad
            }
            override fun onFailed(msg: String) {}
            override fun onShowed() {}
            override fun onDismiss() {}

        })
    }
}
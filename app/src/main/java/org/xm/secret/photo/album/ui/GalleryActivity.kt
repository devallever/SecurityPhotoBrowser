package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.common.app.App
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.ItemListener
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.allever.lib.common.util.DLog
import com.allever.lib.common.util.toast
import com.allever.lib.notchcompat.NotchCompat
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.BaseActivity
import org.xm.secret.photo.album.bean.SeparatorBean
import org.xm.secret.photo.album.bean.ThumbnailBean
import org.xm.secret.photo.album.bean.event.DecodeEvent
import org.xm.secret.photo.album.bean.event.EncodeEvent
import org.xm.secret.photo.album.function.endecode.PrivateBean
import org.xm.secret.photo.album.function.endecode.PrivateHelper
import org.xm.secret.photo.album.function.endecode.UnLockAndRestoreListener
import org.xm.secret.photo.album.function.endecode.UnLockListListener
import org.xm.secret.photo.album.ui.adapter.GalleryAdapter
import org.xm.secret.photo.album.ui.mvp.presenter.GalleryPresenter
import org.xm.secret.photo.album.ui.mvp.view.GalleryView
import org.xm.secret.photo.album.util.DialogHelper
import org.xm.secret.photo.album.util.MD5
import org.xm.secret.photo.album.util.SharePreferenceUtil
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.activity_gallery.rootLayout
import kotlinx.android.synthetic.main.include_top_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class GalleryActivity : BaseActivity<GalleryView, GalleryPresenter>(), GalleryView, View.OnClickListener {

    private lateinit var mBtnExport: ImageView
    private lateinit var mGalleryAdapter: GalleryAdapter
    private val mGalleryData = mutableListOf<Any>()
    private val mThumbnailBeanList = mutableListOf<ThumbnailBean>()
    //多选导出
    private val mExportThumbnailBeanList = mutableListOf<ThumbnailBean>()
    private val mExportThumbnailBeanIndexList = mutableListOf<Int>()

    private var mAlbumName: String? = null
    private var mAlbumPath: String? = null

    private var mPrivateThumbMap = HashMap<PrivateBean, ThumbnailBean>()

    private lateinit var mLoadingDialog: AlertDialog

    private var mSelectMode = false

    private var mBannerAd: IAd? = null
    private var mInsertAd: IAd? = null

    override fun getContentView(): Int = R.layout.activity_gallery
    override fun createPresenter(): GalleryPresenter = GalleryPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        mAlbumName = intent.getStringExtra(EXTRA_ALBUM_NAME)
        mAlbumPath = intent.getStringExtra(EXTRA_ALBUM_PATH)
        super.onCreate(savedInstanceState)
    }
    override fun initView() {
        NotchCompat.adaptNotchWithFullScreen(window)
        checkNotch(Runnable {
            addStatusBar(rootLayout, top_bar)
        })

        findViewById<View>(R.id.iv_back).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_label).text = mAlbumName
        findViewById<View>(R.id.gallery_btn_pick).setOnClickListener(this)
        mLoadingDialog = DialogHelper.createLoadingDialog(this, getString(R.string.export_resource), false)
        mBtnExport = findViewById(R.id.iv_right)
        mBtnExport.setOnClickListener(this)
        mBtnExport.setImageResource(R.drawable.nav_button_export)
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.gallery_recycler_view)
        val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
        //解决最后单个跨列问题
        gridLayoutManager.spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemType = mGalleryAdapter.getItemViewType(position)
                return if (itemType == GALLERY_ITEM_TYPE_SEPARATOR) {
                    3
                } else {
                    1
                }
            }
        }
        recyclerView.layoutManager = gridLayoutManager
        mGalleryAdapter = GalleryAdapter(this, mGalleryData, object : MultiItemTypeSupport<Any> {
            override fun getLayoutId(itemType: Int): Int {
                return when (itemType) {
                    GALLERY_ITEM_TYPE_IMAGE -> {
                        R.layout.item_gallery
                    }
                    GALLERY_ITEM_TYPE_SEPARATOR -> {
                        R.layout.item_seperator
                    }
                    else -> {
                        0
                    }
                }
            }

            override fun getItemViewType(position: Int, t: Any): Int {
                return when (mGalleryData[position]) {
                    is ThumbnailBean -> {
                        GALLERY_ITEM_TYPE_IMAGE
                    }
                    is SeparatorBean -> {
                        GALLERY_ITEM_TYPE_SEPARATOR

                    }
                    else -> {
                        GALLERY_ITEM_TYPE_NONE
                    }
                }
            }
        })

        mGalleryAdapter.mItemListener = object : ItemListener{
            override fun onItemClick(position: Int, holder: BaseViewHolder) {
                if (mSelectMode) {
                    selectItem(position, holder)
                } else {
                    val thumbnailBeanPosition = mThumbnailBeanList.indexOf(mGalleryData[position])
                    PreviewActivity.start(this@GalleryActivity, ArrayList(mThumbnailBeanList), thumbnailBeanPosition, PreviewActivity.TYPE_ENCODE)
                }
            }

            override fun onItemLongClick(position: Int, holder: BaseViewHolder): Boolean {
                mSelectMode = true
                selectItem(position, holder)
                return true
            }

        }

        recyclerView.adapter = mGalleryAdapter
    }

    override fun initData() {
        mGalleryData.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mGalleryData.map {
            if (it is ThumbnailBean) {
                mThumbnailBeanList.add(it)
            }
        }

        EventBus.getDefault().register(this)

        loadBanner()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.gallery_btn_pick -> {
                PickActivity.start(this, mAlbumName!!)
            }

            R.id.iv_right -> {
                loadInsertAd()
                restoreResourceList(mExportThumbnailBeanList)
                mSelectMode = false
            }
        }
    }

    override fun onBackPressed() {
        if (mSelectMode) {
            mSelectMode = false
            //清空选中状态
            mExportThumbnailBeanList.map {
                it.isChecked = false
            }
            mExportThumbnailBeanList.clear()
            mExportThumbnailBeanIndexList.clear()
            mGalleryAdapter.notifyDataSetChanged()
            mBtnExport.visibility = View.GONE
            return
        }
        super.onBackPressed()
    }

    private fun selectItem(position: Int, holder: BaseViewHolder) {
        val item = mGalleryData[position]
        if (item is ThumbnailBean) {
            val state = !item.isChecked
            item.isChecked = state
            holder.setVisible(R.id.gallery_iv_select, state)

            if (item.isChecked) {
                //add
                mExportThumbnailBeanList.add(item)
                mExportThumbnailBeanIndexList.add(position)
            } else {
                //remove
                mExportThumbnailBeanList.remove(item)
                mExportThumbnailBeanIndexList.remove(position)
            }

            if (mExportThumbnailBeanList.size > 0) {
                //show
                mBtnExport.visibility = View.VISIBLE
            } else {
                //hide
                mBtnExport.visibility = View.GONE
            }
        }
    }

    override fun showLoading() {
        mLoadingDialog.show()
    }

    override fun hideLoading() {
        mLoadingDialog.dismiss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveDecodeEvent(decodeEvent: DecodeEvent) {
        if (!decodeEvent.needRefresh) {
            return
        }

        decodeEvent.indexList.map {
            val removeBean = mThumbnailBeanList[it]
            mThumbnailBeanList.remove(removeBean)
            mGalleryData.remove(removeBean)
        }

        mGalleryAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveEncodeEvent(encodeEvent: EncodeEvent) {
        encodeEvent.thumbnailBeanList.map {
            mGalleryData.add(it)
            mThumbnailBeanList.add(it)
        }

        //排序
        val sortThumbnailBeans = ArrayList<Any>()
        sortThumbnailBeans.addAll(mGalleryData)
        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
            if (arg0 is ThumbnailBean && arg1 is ThumbnailBean) {
                java.lang.Long.compare(arg1.date, arg0.date)
            } else {
                //不作处理
                java.lang.Long.compare(0, 0)
            }
        })

        mGalleryData.clear()
        mGalleryData.addAll(sortThumbnailBeans)

        mThumbnailBeanList.clear()
        mGalleryData.map {
            if (it is ThumbnailBean) {
                mThumbnailBeanList.add(it)
            }
        }

        mGalleryAdapter.notifyDataSetChanged()
    }

    /**
     * 移除加密资源
     */
    private fun restoreResourceList(beanList: MutableList<ThumbnailBean>) {
        val privateBeanList = mutableListOf<PrivateBean>()
        beanList.map {
            val privateBean = PrivateBean()
            val name = MD5.getMD5Str(it.path)
            val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
            if (privateBean.resolveHead(file.absolutePath)) {
                privateBeanList.add(privateBean)
                mPrivateThumbMap[privateBean] = it
            }
        }

        PrivateHelper.unLockList(privateBeanList, object : UnLockListListener{
            override fun onStart() {
                showLoading()
            }

            override fun onSuccess(successList: List<PrivateBean>) {
                mHandler.postDelayed({
                    toast(R.string.export_success)
                    hideLoading()
                    mInsertAd?.show()

                    val exportIndexList = mutableListOf<Int>()
                    successList.map {
                        val thumbnailBean = mPrivateThumbMap[it]
                        if (thumbnailBean != null) {
                            thumbnailBean.isChecked = false
                            SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(thumbnailBean.path), null)
                            exportIndexList.add(mGalleryData.indexOf(thumbnailBean))
                            mThumbnailBeanList.remove(thumbnailBean)
                            mGalleryData.remove(thumbnailBean)
                            mPrivateThumbMap.remove(it)
                        }

//                    val nameMd5 = MD5.getMD5Str(thumbnailBean?.path!!)
//                    val linkFile = File(mAlbumPath, nameMd5)
//                    if (linkFile.exists()) {
//                        linkFile.delete()
//                    }
                    }
                    mGalleryAdapter.notifyDataSetChanged()
                    mExportThumbnailBeanList.clear()
                    mBtnExport.visibility = View.GONE

                    val decodeEvent = DecodeEvent()
                    decodeEvent.needRefresh = false
                    decodeEvent.indexList = exportIndexList
                    EventBus.getDefault().post(decodeEvent)
                }, 5000)
            }

            override fun onFailed(errors: List<PrivateBean>) {
                mHandler.postDelayed({
                    toast(R.string.export_fail)
                    hideLoading()
                    mInsertAd?.show()

                    errors.map {
                        val thumbnailBean = mPrivateThumbMap[it]
                        if (thumbnailBean != null) {
                            thumbnailBean.isChecked = false
                        }
                    }
                    mGalleryAdapter.notifyDataSetChanged()
                    mExportThumbnailBeanList.clear()
                    mBtnExport.visibility = View.GONE
                }, 5000)
            }
        })
    }

    /**
     * 移除加密资源
     */
    private fun restoreResource(bean: ThumbnailBean) {
        val privateBean = PrivateBean()
        val name = MD5.getMD5Str(bean.path)
        val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
        if (privateBean.resolveHead(file.absolutePath)) {
            PrivateHelper.unLockAndRestore(privateBean, object : UnLockAndRestoreListener {
                override fun onStart() {
                    DLog.d("export onStart")
                }

                override fun onSuccess() {
                    SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(bean.path), null)
                    finish()
                    DLog.d("export onSuccess")
                }

                override fun onFailed(msg: String) {
                    DLog.d("export onFailed")
                }
            })
        }

    }

    private fun hideLoadingAndShowAd() {
        hideLoading()
        mInsertAd?.show()
    }

    companion object {
        fun start(context: Context, albumName: String, albumPath: String, data: ArrayList<ThumbnailBean>?) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(EXTRA_ALBUM_NAME, albumName)
            intent.putExtra(EXTRA_ALBUM_PATH, albumPath)
            intent.putParcelableArrayListExtra(EXTRA_DATA, data)
            context.startActivity(intent)
        }

        private const val GALLERY_ITEM_TYPE_NONE = -1
        private const val GALLERY_ITEM_TYPE_IMAGE = 0
        private const val GALLERY_ITEM_TYPE_SEPARATOR = 1

        private const val EXTRA_ALBUM_NAME = "EXTRA_ALBUM_NAME"
        private const val EXTRA_DATA = "EXTRA_DATA"
        private const val EXTRA_ALBUM_PATH = "EXTRA_ALBUM_PATH"
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
        EventBus.getDefault().unregister(this)
        mBannerAd?.destroy()
        mInsertAd?.destroy()
    }

    private fun loadBanner() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_GALLERY_BANNER, bannerContainer, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mBannerAd = ad
            }
            override fun onFailed(msg: String) {}
            override fun onShowed() {}
            override fun onDismiss() {}

        })
    }

    private fun loadInsertAd() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_EXPORT_INSERT, window.decorView as ViewGroup, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mInsertAd = ad
            }
            override fun onFailed(msg: String) {}
            override fun onShowed() {}
            override fun onDismiss() {
            }

        })
    }
}
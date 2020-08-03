package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.common.app.App
import com.allever.lib.common.util.DLog
import com.allever.lib.common.util.toast
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.BaseActivity
import org.xm.secret.photo.album.bean.ThumbnailBean
import org.xm.secret.photo.album.bean.event.DecodeEvent
import org.xm.secret.photo.album.function.endecode.PrivateBean
import org.xm.secret.photo.album.function.endecode.PrivateHelper
import org.xm.secret.photo.album.function.endecode.UnLockAndRestoreListener
import org.xm.secret.photo.album.ui.mvp.presenter.PreviewPresenter
import org.xm.secret.photo.album.ui.mvp.view.PreviewView
import org.xm.secret.photo.album.util.*
import com.android.absbase.utils.ResourcesUtils
import com.android.absbase.utils.ToastUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.Exception

class PreviewActivity : BaseActivity<PreviewView, PreviewPresenter>(), PreviewView, androidx.viewpager.widget.ViewPager.OnPageChangeListener,
    View.OnClickListener {
    private var mViewPager: androidx.viewpager.widget.ViewPager? = null
    private var mPagerAdapter: PreviewFragmentPagerAdapter? = null
    private var mThumbnailBeanList: MutableList<ThumbnailBean> = mutableListOf()
    private var mPosition = 0
    private lateinit var mLoadingDialog: AlertDialog
    private lateinit var mDeleteDialog: AlertDialog
    private var mSourceType = TYPE_ENCODE

    private var mInsertAd: IAd? = null

    override fun createPresenter(): PreviewPresenter = PreviewPresenter()
    override fun getContentView(): Int = R.layout.activity_priview
    override fun initData() {
        mThumbnailBeanList.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mPosition = intent.getIntExtra(EXTRA_POSITION, 0)

        mViewPager = findViewById(R.id.preview_view_pager)
        mPagerAdapter = PreviewFragmentPagerAdapter(supportFragmentManager, mThumbnailBeanList)
        mViewPager?.adapter = mPagerAdapter
        mViewPager?.addOnPageChangeListener(this)

        mViewPager?.currentItem = mPosition
    }

    override fun initView() {
        findViewById<View>(R.id.preview_iv_export).setOnClickListener(this)
        findViewById<View>(R.id.iv_back).setOnClickListener(this)
        findViewById<View>(R.id.preview_iv_delete).setOnClickListener(this)
        findViewById<View>(R.id.preview_iv_share).setOnClickListener(this)
        mLoadingDialog = DialogHelper.createLoadingDialog(this, getString(R.string.export_resource), false)
        mDeleteDialog = DialogHelper.createMessageDialog(this, getString(R.string.tips_dialog_delete_resource),
            DialogInterface.OnClickListener { dialog, which ->
                val fileNameMd5 = MD5.getMD5Str(mThumbnailBeanList[mPosition].path)
                FileUtil.deleteFile(PrivateHelper.PATH_ENCODE_ORIGINAL + File.separator + fileNameMd5)
                mHandler.postDelayed({
                    ToastUtils.show(getString(R.string.delete_finish))
                    val decodeList = mutableListOf<ThumbnailBean>()
                    decodeList.add(mThumbnailBeanList[mPosition])
                    val decodeEvent = DecodeEvent()
                    decodeEvent.thumbnailBeanList = decodeList
                    decodeEvent.indexList.add(mPosition)
                    //删除后的行为和导出的行为一致
                    EventBus.getDefault().post(decodeEvent)
                    hideDeleteDialog()
                    finish()
                }, 500)
            })
        val bottomBar = findViewById<View>(R.id.preview_bottom_bar)
        mSourceType = intent.getIntExtra(EXTRA_SOURCE_TYPE, TYPE_ENCODE)
        if (mSourceType == TYPE_ENCODE) {
            bottomBar.visibility = View.VISIBLE
        } else {
            bottomBar.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.preview_iv_export -> {
                DLog.d("select item tempPath = ${mThumbnailBeanList[mPosition].tempPath}")
                restoreResource(mThumbnailBeanList[mPosition])
                loadInsertAd()
            }
            R.id.preview_iv_delete -> {
                showDeleteDialog()
            }
            R.id.preview_iv_share -> {
                share()
            }
        }

    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (checkOutOfBoundary()) {
            return
        }
        val fragment = mPagerAdapter?.instantiateItem(mPosition) as? PreviewFragment
        fragment?.pause()
        mPosition = position
    }

    private fun checkOutOfBoundary(): Boolean {
        val result = mPosition !in 0 until mThumbnailBeanList.size
        if (result) {
            ToastUtils.show(ResourcesUtils.getString(R.string.preview_boundary_error_tips))
        }
        return result
    }

    private fun share(){
        val thumbnailBean = mThumbnailBeanList[mPosition]
        val shareFilePath = if (thumbnailBean.sourceType == ThumbnailBean.SYSTEM){
            thumbnailBean.path
        }else{
            thumbnailBean.tempPath
        }
        if (MediaTypeUtil.isImage(thumbnailBean.type)){
            ShareHelper.shareTo(this, shareFilePath, ShareHelper.TYPE_IMAGE)
        }else if (MediaTypeUtil.isVideo(thumbnailBean.type)){
            try {
                run {
                    val fileName = "${FileUtil.getFileName(shareFilePath)}.mp4"
                    val videoCacheFilePath = "${PrivateHelper.PATH_DECODE_TEMP}${File.separator}$fileName"
                    val targetFile = File(videoCacheFilePath)
                    if (!targetFile.exists()){
                        targetFile.createNewFile()
                        FileUtil.copyFile(File(shareFilePath), targetFile)
                    }
                    ShareHelper.shareTo(this, videoCacheFilePath, ShareHelper.TYPE_VIDEO)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
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
//                    showVideoSavingAnim()
                    DLog.d("export onStart")
                    showLoading()
                }

                override fun onSuccess() {
                    mHandler.postDelayed({
                        hideLoading()
                        mInsertAd?.show()
                        toast(R.string.export_success)

                        SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(bean.path), null)

                        val decodeList = mutableListOf<ThumbnailBean>()
                        decodeList.add(bean)
                        val decodeEvent = DecodeEvent()
                        decodeEvent.thumbnailBeanList = decodeList
                        decodeEvent.indexList.add(mPosition)
//                    decodeEvent.index = mPosition
                        EventBus.getDefault().post(decodeEvent)
                        finish()
                        DLog.d("export onSuccess")
                    }, 5000)

                }

                override fun onFailed(msg: String) {
                    mHandler.postDelayed({
                        hideLoading()
                        mInsertAd?.show()
                        toast(R.string.export_fail)
                    }, 5000)
                    DLog.d("export onFailed")
                }
            })
        }

    }

    override fun showLoading() {
        mLoadingDialog.show()
    }

    override fun hideLoading() {
        mLoadingDialog.dismiss()
    }

    override fun showDeleteDialog() {
        mDeleteDialog.show()
    }

    override fun hideDeleteDialog() {
        mDeleteDialog.dismiss()
    }


    companion object {
        fun start(context: Context, thumbnailBean: ArrayList<ThumbnailBean>, position: Int, sourceType: Int) {
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_DATA, thumbnailBean)
            intent.putExtra(EXTRA_POSITION, position)
            intent.putExtra(EXTRA_SOURCE_TYPE, sourceType)
            context.startActivity(intent)
        }

        private const val EXTRA_DATA = "EXTRA_DATA"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_SOURCE_TYPE = "EXTRA_SOURCE_TYPE"

        public const val TYPE_SYSTEM = 1
        public const val TYPE_ENCODE = 2
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mInsertAd?.destroy()
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
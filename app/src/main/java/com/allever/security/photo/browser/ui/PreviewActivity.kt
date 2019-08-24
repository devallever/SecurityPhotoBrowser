package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.View
import com.allever.lib.common.app.App
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeEvent
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.endecode.UnLockAndRestoreListener
import com.allever.security.photo.browser.ui.mvp.presenter.PreviewPresenter
import com.allever.security.photo.browser.ui.mvp.view.PreviewView
import com.allever.security.photo.browser.util.DialogHelper
import com.allever.security.photo.browser.util.FileUtil
import com.allever.security.photo.browser.util.MD5
import com.allever.security.photo.browser.util.SharePreferenceUtil
import com.android.absbase.utils.ResourcesUtils
import com.android.absbase.utils.ToastUtils
import org.greenrobot.eventbus.EventBus
import java.io.File

class PreviewActivity : Base2Activity<PreviewView, PreviewPresenter>(), PreviewView, ViewPager.OnPageChangeListener,
    View.OnClickListener {
    private var mViewPager: ViewPager? = null
    private var mPagerAdapter: PreviewFragmentPagerAdapter? = null
    private var mThumbnailBeanList: MutableList<ThumbnailBean> = mutableListOf()
    private var mPosition = 0
    private lateinit var mLoadingDialog: AlertDialog
    private lateinit var mDeleteDialog: AlertDialog
    private var mSourceType = TYPE_ENCODE

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
            }
            R.id.preview_iv_delete -> {
                showDeleteDialog()
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
                    hideLoading()
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
                }

                override fun onFailed(msg: String) {
                    hideLoading()
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
}
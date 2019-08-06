package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeEvent
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.endecode.UnLockAndRestoreListener
import com.allever.security.photo.browser.util.MD5
import com.allever.security.photo.browser.util.SharePreferenceUtil
import org.greenrobot.eventbus.EventBus
import java.io.File

class PreviewActivity : Base2Activity(), ViewPager.OnPageChangeListener, View.OnClickListener {
    private var mViewPager: ViewPager? = null
    private var mIvBack: ImageView? = null
    private var mIvSelect: ImageView? = null
    private lateinit var mIvExport: ImageView
    private var mPagerAdapter: PreviewFragmentPagerAdapter? = null
    private var mThumbnailBeanList: MutableList<ThumbnailBean> = mutableListOf()
    private var mPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priview)

        mThumbnailBeanList.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mPosition = intent.getIntExtra(EXTRA_POSITION, 0)

        mIvExport = findViewById(R.id.preview_iv_export)
        mIvExport.setOnClickListener(this)

        mViewPager = findViewById(R.id.preview_view_pager)
        mPagerAdapter = PreviewFragmentPagerAdapter(supportFragmentManager, mThumbnailBeanList)
        mViewPager?.adapter = mPagerAdapter
        mViewPager?.addOnPageChangeListener(this)

        mViewPager?.currentItem = mPosition

    }

    override fun onClick(v: View?) {
        when (v) {
            mIvExport -> {
                DLog.d("select item tempPath = ${mThumbnailBeanList[mPosition].tempPath}")
                restoreResource(mThumbnailBeanList[mPosition])
            }
        }

    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onPageSelected(position: Int) {
        mPosition = position
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
                }

                override fun onSuccess() {
                    SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(bean.path), null)

                    val decodeList = mutableListOf<ThumbnailBean>()
                    decodeList.add(bean)
                    val decodeEvent = DecodeEvent()
                    decodeEvent.thumbnailBeanList = decodeList
                    EventBus.getDefault().post(decodeEvent)
                    finish()
                    DLog.d("export onSuccess")
                }

                override fun onFailed(msg: String) {
                    DLog.d("export onFailed")
                }
            })
        }

    }


    companion object {
        fun start(context: Context, thumbnailBean: ArrayList<ThumbnailBean>, position: Int) {
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putParcelableArrayListExtra(EXTRA_DATA, thumbnailBean)
            intent.putExtra(EXTRA_POSITION, position)
            context.startActivity(intent)
        }

        private const val EXTRA_DATA = "EXTRA_DATA"
        private const val EXTRA_POSITION = "EXTRA_POSITION"
    }
}
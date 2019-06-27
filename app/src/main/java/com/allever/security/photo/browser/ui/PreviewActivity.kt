package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.ImageView
import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean

class PreviewActivity : BaseActivity() {

    private var mViewPager: ViewPager? = null
    private var mIvBack: ImageView? = null
    private var mIvSelect: ImageView? = null
    private var mPagerAdapter: PreviewFragmentPagerAdapter? = null
    private var mThumbnailBeanList: MutableList<ThumbnailBean> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priview)

        initTestData()

        mViewPager = findViewById(R.id.preview_view_pager)
        mPagerAdapter = PreviewFragmentPagerAdapter(supportFragmentManager, mThumbnailBeanList)
        mViewPager?.adapter = mPagerAdapter

    }

    private fun initTestData() {
        for (i in 0 .. 10) {
            val thumbnailBean = ThumbnailBean()
            mThumbnailBeanList.add(thumbnailBean)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PreviewActivity::class.java)
            context.startActivity(intent)
        }
    }
}
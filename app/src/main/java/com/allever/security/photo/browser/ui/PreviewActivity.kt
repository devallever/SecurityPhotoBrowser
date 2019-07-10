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
    private var mPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_priview)

        mThumbnailBeanList.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mPosition = intent.getIntExtra(EXTRA_POSITION, 0)

//        initTestData()

        mViewPager = findViewById(R.id.preview_view_pager)
        mPagerAdapter = PreviewFragmentPagerAdapter(supportFragmentManager, mThumbnailBeanList)
        mViewPager?.adapter = mPagerAdapter

        mViewPager?.currentItem = mPosition

    }

    private fun initTestData() {
        for (i in 0 .. 10) {
            val thumbnailBean = ThumbnailBean()
            mThumbnailBeanList.add(thumbnailBean)
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
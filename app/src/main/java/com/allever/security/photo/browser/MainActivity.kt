package com.allever.security.photo.browser

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.allever.lib.common.ui.widget.tab.TabLayout
import com.allever.lib.common.util.DisplayUtils
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.ui.AlbumFragment
import com.allever.security.photo.browser.ui.GuideFragment
import com.allever.security.photo.browser.ui.MainTabModel
import com.allever.security.photo.browser.ui.SettingFragment
import com.allever.security.photo.browser.ui.adapter.ViewPagerAdapter
import com.allever.security.photo.browser.ui.mvp.presenter.MainPresenter
import com.allever.security.photo.browser.ui.mvp.view.MainView

class MainActivity: Base2Activity<MainView, MainPresenter>(), MainView,
    TabLayout.OnTabSelectedListener{

    private lateinit var mVp: ViewPager
    private lateinit var mViewPagerAdapter: ViewPagerAdapter
    private lateinit var mTab: TabLayout
    private lateinit var mTvTitle: TextView
    private var mainTabHighlight = 0
    private var mainTabUnSelectColor = 0

    private var mFragmentList = mutableListOf<Fragment>()

    override fun getContentView(): Any = R.layout.activity_main

    override fun initView() {

        mTab = findViewById(R.id.tab_layout)
        mVp = findViewById(R.id.id_main_vp)
        mTvTitle = findViewById(R.id.id_main_tv_title)

        mainTabHighlight = resources.getColor(R.color.main_tab_highlight)
        mainTabUnSelectColor = resources.getColor(R.color.main_tab_unselect_color)

        initViewPagerData()
        initViewPager()
        initTab()

    }

    override fun initData() {
    }


    override fun createPresenter(): MainPresenter = MainPresenter()

    private fun initViewPagerData() {
        mFragmentList.add(AlbumFragment())
        mFragmentList.add(GuideFragment())
        mFragmentList.add(SettingFragment())
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this, mFragmentList)
    }

    private fun initViewPager() {
        mVp.offscreenPageLimit = 3
        mVp.adapter = mViewPagerAdapter

        mVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mTvTitle.text = getString(MainTabModel.getTabAt(position).labelResId)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initTab() {
        //tab
        mVp.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTab))
        mTab.setOnTabSelectedListener(this)

        val tabCount = MainTabModel.tabCount
        for (i in 0 until tabCount) {
            val MainTabModel = MainTabModel.getTab(i)
            val labelId = MainTabModel.labelResId
            val tab = mTab.newTab()
                .setTag(MainTabModel)
                .setCustomView(getTabView(i))
                .setContentDescription(labelId)
            val drawable = MainTabModel.drawable
            if (drawable != null) {
                tab.icon = drawable
            } else {
                tab.setIcon(MainTabModel.iconResId)
            }

            tab.setText(labelId)
            val imageView = tab.customView?.findViewById<ImageView>(R.id.icon)
            imageView?.colorFilter = null
            //解决首次tab文字颜色异常
//            val textView = tab.customView?.findViewById<TextView>(R.id.text1)
//            textView?.setTextColor(mTab.tabTextColors)
            mTab.addTab(tab)
        }

        mTab.setSelectedTabIndicatorWidth(DisplayUtils.dip2px(0))
        mTab.setSelectedTabIndicatorHeight(DisplayUtils.dip2px(0))
        mTab.setSelectedTabIndicatorColor(mainTabHighlight)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        mVp.currentItem = tab.position

        MainTabModel.selectedTab = (tab.tag as MainTabModel.Tab)
        for (i in 0 until mTab.tabCount) {
            val aTab = mTab.getTabAt(i)
            if (aTab != null) {
                val imageView = aTab.customView?.findViewById<ImageView>(R.id.icon)
                val textView = aTab.customView?.findViewById<TextView>(R.id.text1)
                if (aTab === tab) {
                    imageView?.setColorFilter(mainTabHighlight, PorterDuff.Mode.SRC_IN)
                    textView?.setTextColor(mainTabHighlight)
                } else {
                    imageView?.colorFilter = null
                    textView?.setTextColor(mainTabUnSelectColor)
                }
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {}

    override fun onTabReselected(tab: TabLayout.Tab) {}

    private fun getTabView(position: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_tab, null)
        val imageView = view.findViewById<ImageView>(R.id.icon)
        val textView = view.findViewById<TextView>(R.id.text1)
        val tab = MainTabModel.getTab(position)
        textView.setText(tab.labelResId)
        imageView.setImageResource(tab.iconResId)
        return view
    }
}
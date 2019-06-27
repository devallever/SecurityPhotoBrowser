package com.allever.security.photo.browser.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.ui.adapter.SelectAlbumAdapter
import com.allever.security.photo.browser.ui.widget.tab.TabLayout
import com.android.absbase.utils.ResourcesUtils

class PickActivity : BaseActivity(), TabLayout.OnTabSelectedListener, PickFragment.PickCallback, View.OnClickListener {

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: ViewPager
    private lateinit var mFragmentPageAdapter: FragmentPageAdapter
    private lateinit var mLlAlbumTitleContainer: ViewGroup
    private lateinit var mFlSelectAlbumContainer: ViewGroup
    private lateinit var mIvSelectAlbum: ImageView
    private lateinit var mAlbumRecyclerView: RecyclerView

    private lateinit var mAlbumAdapter: SelectAlbumAdapter

    //fragment数据的数据集
    private var mFragmentDataMap = mutableMapOf<TabModel.Tab, MutableList<ThumbnailBean>>()
    //底部选中的数据
    private var mSelectedData = mutableListOf<ThumbnailBean>()
    //All数据
    private var mAllData = mutableListOf<ThumbnailBean>()
    //Video数据
    private var mVideoData = mutableListOf<ThumbnailBean>()
    //Photo数据
    private var mPhotoData = mutableListOf<ThumbnailBean>()
    //相册列表数据
    private var mAlbumData = mutableListOf<ImageFolder>()

    private var mFragments = mutableListOf<Fragment>()

    private var mSelectAlbumContainerAnimShow: Animator? = null
    private var mSelectAlbumContainerAnimHide: Animator? = null
    private var mIvArrowRotateAnimUp: Animator? = null
    private var mIvArrowRotateAnimDown: Animator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick)

        initView()

        initData()

    }

    override fun onBackPressed() {
        if (mFlSelectAlbumContainer.visibility == View.VISIBLE) {
            showSelectAlbumContainer(false)
            return
        }
        super.onBackPressed()
    }

    private fun initView() {
        initTabs()
        initViewPager()
        initAnim()
        initAlbumRecyclerView()
    }

    private fun initTabs() {
        //Tab
        val tabCount = TabModel.tabCount
        mTabs = findViewById(R.id.tab_bar)
        for (i in 0 until tabCount) {
            val tabModel = TabModel.getTab(i)
            val tabView = getTabView(tabModel, i)
            val tab = mTabs.newTab()
                .setCustomView(tabView)
            tab.tag = tabModel
            mTabs.addTab(tab)
        }
        mTabs.setOnTabSelectedListener(this)
        mTabs.setSelectedTabIndicatorWidth(ResourcesUtils.resources.getDimension(R.dimen.album_selected_tab_indicator_width).toInt())
        mTabs.setSelectedTabIndicatorHeight(ResourcesUtils.resources.getDimension(R.dimen.album_selected_tab_indicator_height).toInt())
        val indicatorColor = ResourcesUtils.resources.getColor(R.color.album_tab_indicator_color)
        mTabs.setSelectedTabIndicatorColor(indicatorColor)
        mTabs.needDrawSelectedIndicator(true)
    }

    private fun initViewPager() {
        // 先清除现有的fragment
        // 原因: 其他界面崩溃后,该界面会出现唤醒的情况, 而onCreate内部会重建所有的fragment,导致界面刷新异常的问题
        mViewPager = findViewById(R.id.view_pager)
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabs))
        val fragments = supportFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            fragments.map {
                beginTransaction.remove(it)
            }
            beginTransaction.commitAllowingStateLoss()
        }

        val tabCount = TabModel.tabCount
        for (i in 0 until tabCount) {
            val fragment = PickFragment()
            fragment.callback = this
            fragment.type = TabModel.getTab(i)
            mFragments.add(fragment)
        }
        mFragmentDataMap[TabModel.Tab.ALL] = mAllData
        mFragmentDataMap[TabModel.Tab.VIDEO] = mVideoData
        mFragmentDataMap[TabModel.Tab.PICTURE] = mPhotoData

        mFragmentPageAdapter = FragmentPageAdapter(supportFragmentManager, mFragments)
        mViewPager.adapter = mFragmentPageAdapter
        mViewPager.currentItem = 0
    }

    private fun initAnim() {
        mLlAlbumTitleContainer = findViewById(R.id.ll_album_title_container)
        mFlSelectAlbumContainer = findViewById(R.id.fl_select_album_container)
        mIvSelectAlbum = findViewById(R.id.iv_select_album)
        mLlAlbumTitleContainer.setOnClickListener(this)
        mFlSelectAlbumContainer.setOnClickListener(this)
        mIvSelectAlbum.setOnClickListener(this)

        mSelectAlbumContainerAnimShow = ObjectAnimator.ofFloat(mFlSelectAlbumContainer, "alpha", 0f, 1f)
        mSelectAlbumContainerAnimShow?.duration = ANIMATION_DURATION
        mSelectAlbumContainerAnimShow?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                mFlSelectAlbumContainer.visibility = View.VISIBLE
            }
        })


        mSelectAlbumContainerAnimHide = ObjectAnimator.ofFloat(mFlSelectAlbumContainer, "alpha", 1f, 0f)
        mSelectAlbumContainerAnimHide?.duration = ANIMATION_DURATION
        mSelectAlbumContainerAnimHide?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                mFlSelectAlbumContainer.visibility = View.GONE
            }
        })

        mIvArrowRotateAnimUp = ObjectAnimator.ofFloat(mIvSelectAlbum, "rotation", 0f, 180f)
        mIvArrowRotateAnimUp?.duration = ANIMATION_DURATION

        mIvArrowRotateAnimDown = ObjectAnimator.ofFloat(mIvSelectAlbum, "rotation", 180f, 360f)
        mIvArrowRotateAnimDown?.duration = ANIMATION_DURATION
    }

    private fun initAlbumRecyclerView() {
        mAlbumRecyclerView = findViewById(R.id.select_album_recycler_view)
        mAlbumRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initData() {
        for (i in 0..30) {
            val imageFolder = ImageFolder()
            mAlbumData.add(imageFolder)
        }

        mAlbumAdapter = SelectAlbumAdapter(this, R.layout.item_slect_album, mAlbumData)
        mAlbumRecyclerView.adapter = mAlbumAdapter
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab ?: return
        mViewPager.currentItem = tab.position
        TabModel.selectedTab = tab.tag as TabModel.Tab

        for (i in 0 until mTabs.tabCount) {
            val tabAt = mTabs.getTabAt(i)
            if (tabAt != null) {
                val textView = tabAt.customView?.findViewById<TextView>(R.id.tv_tab)
                if (tabAt == tab) {
                    textView?.setTextColor(resources.getColor(R.color.album_tab_selected_color))
                } else {
                    textView?.setTextColor(resources.getColor(R.color.album_tab_un_selected_color))
                }
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onClick(v: View?) {
        when (v) {
            mLlAlbumTitleContainer, mIvSelectAlbum -> {
                val visibility = mFlSelectAlbumContainer.visibility
                when (visibility) {
                    View.VISIBLE -> {
                        showSelectAlbumContainer(false)
                    }
                    View.GONE -> {
                        showSelectAlbumContainer(true)
                    }
                }
            }

            mFlSelectAlbumContainer -> {
                showSelectAlbumContainer(false)
            }
        }
    }

    private fun showSelectAlbumContainer(show: Boolean) {
        if (show) {
            mSelectAlbumContainerAnimShow?.start()
            mIvArrowRotateAnimUp?.start()
        } else {
            mSelectAlbumContainerAnimHide?.start()
            mIvArrowRotateAnimDown?.start()
        }
    }

    private fun getTabView(tab: TabModel.Tab, position: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
        val tvLabel = view.findViewById<TextView>(R.id.tv_tab)
        tvLabel?.text = resources.getString(tab.labelResId)
        //修改第一个Tab为选中状态
        if (position == 0) {
            tvLabel?.setTextColor(resources.getColor(R.color.album_tab_selected_color))
        }
        return view
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PickActivity::class.java)
            context.startActivity(intent)
        }

        private const val ANIMATION_DURATION = 200L
    }
}
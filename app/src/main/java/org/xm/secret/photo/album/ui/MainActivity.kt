package org.xm.secret.photo.album.ui

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.comment.CommentHelper
import com.allever.lib.comment.CommentListener
import com.allever.lib.common.ui.widget.tab.TabLayout
import com.allever.lib.common.util.DisplayUtils
import com.allever.lib.notchcompat.NotchCompat
import com.allever.lib.recommend.RecommendActivity
import com.allever.lib.recommend.RecommendDialogHelper
import com.allever.lib.recommend.RecommendDialogListener
import com.allever.lib.recommend.RecommendGlobal
import com.allever.lib.ui.widget.ShakeHelper
import com.allever.lib.umeng.UMeng
import kotlinx.android.synthetic.main.activity_main.*
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.BaseActivity
import org.xm.secret.photo.album.ui.adapter.ViewPagerAdapter
import org.xm.secret.photo.album.ui.mvp.presenter.MainPresenter
import org.xm.secret.photo.album.ui.mvp.view.MainView
import kotlinx.android.synthetic.main.include_top_bar.iv_right
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainView, MainPresenter>(), MainView,
    TabLayout.OnTabSelectedListener, View.OnClickListener {

    private lateinit var mVp: ViewPager
    private lateinit var mViewPagerAdapter: ViewPagerAdapter
    private lateinit var mTab: TabLayout
    private lateinit var mTvTitle: TextView
    private var mainTabHighlight = 0
    private var mainTabUnSelectColor = 0

    private var mFragmentList = mutableListOf<Fragment>()

    private var mExitInsertAd: IAd? = null
    private var mIsAdLoaded = false
    private var mBackInsertAd: IAd? = null

    private lateinit var mShakeAnimator: ObjectAnimator

    override fun getContentView(): Any =
        R.layout.activity_main

    override fun initView() {
        NotchCompat.adaptNotchWithFullScreen(window)
        checkNotch(Runnable {
            addStatusBar(rootLayout, top_bar)
        })

        mShakeAnimator = ShakeHelper.createShakeAnimator(iv_right, true)
        mShakeAnimator.start()

        mTab = findViewById(R.id.tab_layout)
        mVp = findViewById(R.id.id_main_vp)
        mTvTitle = findViewById(R.id.id_main_tv_title)
        iv_right.setOnClickListener(this)
        iv_right.visibility = View.GONE

        mainTabHighlight = resources.getColor(R.color.main_tab_highlight)
        mainTabUnSelectColor = resources.getColor(R.color.main_tab_unselect_color)

        initViewPagerData()
        initViewPager()
        initTab()

    }

    override fun initData() {
        loadExitInsert()
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_right -> {
                RecommendActivity.start(this, UMeng.getChannel())
            }
        }
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

    private fun loadExitInsert() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_EXIT_INSERT, window.decorView as ViewGroup, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mIsAdLoaded = true
                mExitInsertAd = ad
            }

            override fun onShowed() {
                mIsAdLoaded = false
            }

            override fun onDismiss() {}
            override fun onFailed(msg: String) {}
        })
    }

    override fun onDestroy() {
        mBackInsertAd?.destroy()
        mExitInsertAd?.destroy()
//        mShakeAnimator.cancel()
        super.onDestroy()
    }

    override fun onBackPressed() {

        if (isPasswordViewShowing()) {
            super.onBackPressed()
            return
        }

        if (mIsAdLoaded) {
            mExitInsertAd?.show()
            mIsAdLoaded = false
        } else {
            if (UMeng.getChannel() == "google") {
                //谷歌渠道，首次评分，其余推荐
                if (mIsShowComment) {
                    if (RecommendGlobal.recommendData.isEmpty()) {
                        showComment()
                    } else {
                        showRecommendDialog()
                    }
                } else {
                    showComment()
                }
            } else {
                //其他渠道推荐
                if (RecommendGlobal.recommendData.isEmpty()) {
                    checkExit()
                } else {
                    showRecommendDialog()
                }
            }
        }
    }

    private fun showRecommendDialog() {
        val dialog = RecommendDialogHelper.createRecommendDialog(this, object :
            RecommendDialogListener {
            override fun onMore(dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onReject(dialog: Dialog?) {
                dialog?.dismiss()
                GlobalScope.launch {
                    delay(200)
                    finish()
                }
            }

            override fun onBackPress(dialog: Dialog?) {
                dialog?.dismiss()
                GlobalScope.launch {
                    delay(200)
                    finish()
                }
            }
        })

        RecommendDialogHelper.show(this, dialog)
    }

    private var mIsShowComment = false
    private fun showComment() {
        val dialog = CommentHelper.createCommentDialog(this, object : CommentListener {
            override fun onComment(dialog: Dialog?) {
                dialog?.dismiss()
            }

            override fun onReject(dialog: Dialog?) {
                dialog?.dismiss()
                GlobalScope.launch {
                    delay(200)
                    finish()
                }
            }

            override fun onBackPress(dialog: Dialog?) {
                dialog?.dismiss()
                GlobalScope.launch {
                    delay(200)
                    finish()
                }
            }
        })

        CommentHelper.show(this, dialog)
        mIsShowComment = true
    }
}
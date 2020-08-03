package org.xm.secret.photo.album.ui

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.common.app.App
import com.allever.lib.common.util.ShareHelper
import com.allever.lib.common.util.Tool
import com.allever.lib.common.util.toast
import com.allever.lib.permission.PermissionUtil
import com.allever.lib.recommend.RecommendGlobal
import com.allever.lib.umeng.UMeng
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.Base2Fragment
import org.xm.secret.photo.album.ui.mvp.presenter.SettingPresenter
import org.xm.secret.photo.album.ui.mvp.view.SettingView
import org.xm.secret.photo.album.util.FeedbackHelper

class SettingFragment: Base2Fragment<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {

    private var mBannerAd: IAd? = null
    private var mInsertAd: IAd? = null
    private var mVideoAd: IAd? = null

    override fun getContentView(): Int = R.layout.fragment_setting

    override fun initView(root: View) {
        root.findViewById<View>(R.id.setting_tv_share).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_permission).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_modify_password).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_feedback).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_about).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_support).setOnClickListener(this)

        mBannerContainer = root.findViewById(R.id.bannerContainer)

    }

    override fun initData() {
        loadBanner()
    }

    override fun createPresenter(): SettingPresenter = SettingPresenter()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setting_tv_permission -> {
                PermissionUtil.GoToSetting(activity)
            }
            R.id.setting_tv_modify_password -> {
                ChangePasswordActivity.start(activity!!, true)
            }
            R.id.setting_tv_feedback -> {
                FeedbackHelper.feedback(activity)
            }
            R.id.setting_tv_about -> {
                AboutActivity.start(activity)
            }
            R.id.setting_tv_share -> {
                var url = RecommendGlobal.getUrl(App.context.packageName)
                if (TextUtils.isEmpty(url)) {
                    url = "https://play.google.com/store/apps/details?id=${App.context.packageName}"
                }
                val msg = getString(R.string.share_content, getString(R.string.app_name), url)
                ShareHelper.shareText(this, msg)
            }
            R.id.setting_tv_support -> {
                if (UMeng.getChannel().equals("google", true)) {
                    Tool.openInGooglePlay(activity!!, App.context.packageName)
                } else {
                    supportUs()
                }
            }
        }
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
        mBannerAd?.destroy()
    }

    private lateinit var mBannerContainer: ViewGroup
    private fun loadBanner() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_SETTING_BANNER, mBannerContainer, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mBannerAd = ad
            }
            override fun onFailed(msg: String) {}
            override fun onShowed() {}
            override fun onDismiss() {}

        })
    }

    private fun supportUs() {
        AlertDialog.Builder(activity!!)
            .setTitle("温馨提示")
            .setMessage("该操作会消耗一定的数据流量，您要观看吗?")
            .setPositiveButton("立即观看") { dialog, which ->
                dialog.dismiss()
                //流程加载视频  -> 下载 -> 插屏
                loadEncourageVideoAd()
//                loadInsert()
            }
            .setNegativeButton("残忍拒绝") { dialog, which ->
                dialog.dismiss()
                toast("您可以点击下方小广告，也是对我们的一种支持。")
            }
            .create()
            .show()
    }

    private fun loadEncourageVideoAd() {
        mVideoAd?.destroy()
        mVideoAd = null
        AdChainHelper.loadAd(AdConstant.AD_NAME_SETTING_VIDEO, null, object : AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mVideoAd = ad
                mVideoAd?.show()
            }

            override fun onShowed() {
            }

            override fun onDismiss() {
            }

            override fun onFailed(msg: String) {
                loadInsert()
            }

        })
    }

    private fun loadInsert() {

        AdChainHelper.loadAd(AdConstant.AD_NAME_SETTING_INSERT, activity?.window?.decorView as ViewGroup, object : AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mInsertAd = ad
                mInsertAd?.show()
            }

            override fun onShowed() {
            }

            override fun onDismiss() {
            }

            override fun onFailed(msg: String) {
                toast("请求失败, 您可以点击下方小广告，也是对我们的一种支持。")
            }

        })
    }
}
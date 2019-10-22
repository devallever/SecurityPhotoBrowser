package com.allever.security.photo.browser.ui

import android.view.View
import android.widget.TextView
import com.allever.lib.common.app.App
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Fragment
import com.allever.security.photo.browser.ui.mvp.presenter.SettingPresenter
import com.allever.security.photo.browser.ui.mvp.view.SettingView
import com.allever.security.photo.browser.util.FeedbackHelper
import com.allever.security.photo.browser.util.SystemUtils

class SettingFragment: Base2Fragment<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {
    override fun getContentView(): Int = R.layout.fragment_setting

    override fun initView(root: View) {
        root.findViewById<View>(R.id.setting_tv_share).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_modify_password).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_feedback).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_about).setOnClickListener(this)
    }

    override fun initData() { }

    override fun createPresenter(): SettingPresenter = SettingPresenter()

    override fun onClick(v: View?) {
        when (v?.id) {
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
                val msg = "【隐私相册浏览器】这个应用很不错，推荐一下。获取地址 \nhttps://shouji.baidu.com/software/26327319.html"
                val intent = SystemUtils.getShareIntent(App.context, msg)
                startActivity(intent)
            }
        }
    }
}
package com.allever.security.photo.browser.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Fragment
import com.allever.security.photo.browser.ui.mvp.presenter.SettingPresenter
import com.allever.security.photo.browser.ui.mvp.view.SettingView
import com.allever.security.photo.browser.util.FeedbackHelper

class SettingFragment: Base2Fragment<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {
    override fun getContentView(): Int = R.layout.fragment_setting

    override fun initView(root: View) {
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
        }
    }
}
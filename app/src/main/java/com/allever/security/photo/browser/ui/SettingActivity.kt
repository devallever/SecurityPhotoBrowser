package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.ToastUtils
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.ui.mvp.presenter.SettingPresenter
import com.allever.security.photo.browser.ui.mvp.view.SettingView
import com.allever.security.photo.browser.util.FeedbackHelper

class SettingActivity : Base2Activity<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {
    override fun createPresenter(): SettingPresenter = SettingPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.setting_tv_modify_password).setOnClickListener(this)
        findViewById<TextView>(R.id.setting_tv_feedback).setOnClickListener(this)
        findViewById<TextView>(R.id.setting_tv_about).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_label).text = getString(R.string.setting)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setting_tv_modify_password -> {
                ChangePasswordActivity.start(this, true)
            }
            R.id.setting_tv_feedback -> {
                FeedbackHelper.feedback(this)
            }
            R.id.setting_tv_about -> {
                AboutActivity.start(this)
            }
            R.id.iv_back -> {
                finish()
            }

        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
            context.startActivity(intent)
        }
    }
}
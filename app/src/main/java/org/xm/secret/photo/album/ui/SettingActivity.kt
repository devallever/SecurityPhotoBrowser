package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.app.Base2Activity
import org.xm.secret.photo.album.ui.mvp.presenter.SettingPresenter
import org.xm.secret.photo.album.ui.mvp.view.SettingView
import org.xm.secret.photo.album.util.FeedbackHelper

class SettingActivity : Base2Activity<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {

    override fun getContentView(): Int = R.layout.activity_setting
    override fun createPresenter(): SettingPresenter = SettingPresenter()
    override fun initView() {
        findViewById<TextView>(R.id.setting_tv_modify_password).setOnClickListener(this)
        findViewById<TextView>(R.id.setting_tv_feedback).setOnClickListener(this)
        findViewById<TextView>(R.id.setting_tv_about).setOnClickListener(this)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_label).text = getString(R.string.setting)
    }
    override fun initData() {}

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
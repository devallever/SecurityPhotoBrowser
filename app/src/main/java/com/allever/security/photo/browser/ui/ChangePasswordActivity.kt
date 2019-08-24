package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.function.password.PasswordHelper
import com.allever.security.photo.browser.ui.mvp.presenter.ChangePasswordPresenter
import com.allever.security.photo.browser.ui.mvp.view.ChangePasswordView


class ChangePasswordActivity : Base2Activity<ChangePasswordView, ChangePasswordPresenter>(), ChangePasswordView, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private var mLlPwdContainer: LinearLayout? = null
    private var mPwdView: View? = null

    private var mPasswordHelper: PasswordHelper? = null

    private var mShowPasswordViewFirst: Boolean = false

    override fun getContentView(): Int = R.layout.activity_modify_password
    override fun createPresenter(): ChangePasswordPresenter = ChangePasswordPresenter()
    override fun onCreate(savedInstanceState: Bundle?) {
        mShowPasswordViewFirst = intent?.getBooleanExtra(EXTRA_SHOW_PASSWORD_VIEW_FIRST, false) ?: false
        mPasswordHelper = PasswordHelper(mHandler, object : PasswordHelper.PasswordCallback {
            override fun checkPwdRight() {
                super.checkPwdRight()
                if (mShowPasswordViewFirst) {
                    finish()
                } else {
                    //确认密码正确
                    mPwdView = null
                    mPasswordHelper?.release()
                    mLlPwdContainer?.removeAllViews()
                }
            }
        })
        super.onCreate(savedInstanceState)
    }
    override fun initView() {
        findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_label).text = getString(R.string.setting_modify_password)
        mLlPwdContainer = findViewById(R.id.id_setting_secret_vault_pwd_container)

        if (mShowPasswordViewFirst) {
            findViewById<View>(R.id.id_bg_mask)?.visibility = View.VISIBLE
            findViewById<View>(R.id.id_iv_bg)?.visibility = View.VISIBLE
            handleChangePassword()
        } else {
            findViewById<View>(R.id.id_bg_mask)?.visibility = View.GONE
            findViewById<View>(R.id.id_iv_bg)?.visibility = View.GONE
        }

    }
    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    private fun handleChangePassword() {
        //显示密码键盘
        mPasswordHelper?.init()
        if (mPwdView == null) {
            mPwdView = mPasswordHelper?.getPasswordView()
            //去掉logo
            mLlPwdContainer?.addView(mPwdView)
        }
        mPasswordHelper?.modifyPasscode()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

    }

    override fun onBackPressed() {
        if (mShowPasswordViewFirst) {
            super.onBackPressed()
        } else {
            if (mPwdView != null) {
                mPwdView = null
                mLlPwdContainer?.removeAllViews()
                mPasswordHelper?.release()
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        private const val EXTRA_SHOW_PASSWORD_VIEW_FIRST = "EXTRA_SHOW_PASSWORD_VIEW_FIRST"
        fun start(context: Context, showPasswordView: Boolean) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            intent.putExtra(EXTRA_SHOW_PASSWORD_VIEW_FIRST, showPasswordView)
            context.startActivity(intent)
        }
    }

}
package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.util.SystemUtils

class AboutActivity: BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initView()

    }

    private fun initView() {
        findViewById<View>(R.id.about_privacy).setOnClickListener(this)
        findViewById<View>(R.id.iv_back).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.about_privacy -> {
                val privacyUrl = "https://plus.google.com/116794250597377070773/posts/SYoEZWDm77x"
                SystemUtils.startWebView(App.context, privacyUrl)
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
        }
    }
}
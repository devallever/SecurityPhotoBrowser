package com.allever.security.photo.browser.ui

import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.ActivityCollector
import com.allever.security.photo.browser.AlbumActivity
import com.allever.security.photo.browser.MainActivity
import com.allever.security.photo.browser.R

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mHandler.postDelayed({
            ActivityCollector.startActivity(this, MainActivity::class.java)
            finish()
        }, 1000)
    }
}
package com.allever.security.photo.browser.ui

import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.AlbumActivity
import com.allever.security.photo.browser.R

class SplashActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mHandler.postDelayed({
            AlbumActivity.start(this)
            finish()
        }, 1000)
    }
}
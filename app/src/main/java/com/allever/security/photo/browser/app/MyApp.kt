package com.allever.security.photo.browser.app

import com.allever.lib.common.app.App
import com.allever.lib.umeng.UMeng
import com.allever.security.photo.browser.BuildConfig

class MyApp : App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
        if (!BuildConfig.DEBUG) {
            UMeng.init(this)
        }
    }
}
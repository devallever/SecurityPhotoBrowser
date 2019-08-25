package com.allever.security.photo.browser.app

import com.allever.lib.common.app.App
import com.allever.lib.umeng.UMeng

class MyApplication : App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
        UMeng.init(this, "5d61ebd3570df3add50003a6", "google")
    }
}
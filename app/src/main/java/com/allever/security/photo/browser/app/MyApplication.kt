package com.allever.security.photo.browser.app

import com.allever.lib.common.app.App

class MyApplication: App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
    }
}
package com.allever.security.photo.browser.app

import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import com.allever.lib.common.app.App
import com.allever.lib.common.util.DLog
import com.allever.lib.umeng.UMeng
import com.allever.security.photo.browser.BuildConfig

class MyApplication : App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
        UMeng.init(this)
    }
}
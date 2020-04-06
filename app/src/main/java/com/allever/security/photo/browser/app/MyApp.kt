package com.allever.security.photo.browser.app

import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.common.app.App
import com.allever.lib.recommend.RecommendGlobal
import com.allever.lib.umeng.UMeng
import com.allever.security.photo.browser.BuildConfig
import com.allever.security.photo.browser.ad.AdConstant
import com.allever.security.photo.browser.ad.AdFactory

class MyApp : App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
        if (!BuildConfig.DEBUG) {
            UMeng.init(this)
        }

        AdChainHelper.init(this, AdConstant.adData, AdFactory())

        RecommendGlobal.init(UMeng.getChannel())
    }
}
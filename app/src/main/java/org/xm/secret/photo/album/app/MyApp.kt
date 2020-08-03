package org.xm.secret.photo.album.app

import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.common.app.App
import com.allever.lib.recommend.RecommendGlobal
import com.allever.lib.umeng.UMeng
import org.xm.secret.photo.album.BuildConfig

import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.ad.AdFactory

class MyApp : App() {
    override fun onCreate() {
        super.onCreate()
        com.android.absbase.App.setContext(this)
        if (!BuildConfig.DEBUG) {
            UMeng.init(this)
        }

        AdChainHelper.init(this, AdConstant.adData, AdFactory())

//        RecommendGlobal.init(UMeng.getChannel())
    }
}
package org.xm.secret.photo.album.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allever.lib.ad.chain.AdChainHelper
import com.allever.lib.ad.chain.AdChainListener
import com.allever.lib.ad.chain.IAd
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseFragment
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant

class GuideFragment: BaseFragment() {

    private var mBannerAd: IAd? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(App.context).inflate(R.layout.fragment_guide, container, false)
        mBannerContainer = view.findViewById(R.id.bannerContainer)
        loadBanner()
        return view
    }

    override fun onPause() {
        super.onPause()
        mBannerAd?.onAdPause()
    }

    override fun onResume() {
        super.onResume()
        mBannerAd?.onAdResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBannerAd?.destroy()
    }

    private lateinit var mBannerContainer: ViewGroup
    private fun loadBanner() {
        AdChainHelper.loadAd(AdConstant.AD_NAME_GUIDE_BANNER, mBannerContainer, object :
            AdChainListener {
            override fun onLoaded(ad: IAd?) {
                mBannerAd = ad
            }
            override fun onFailed(msg: String) {}
            override fun onShowed() {}
            override fun onDismiss() {}

        })
    }

}
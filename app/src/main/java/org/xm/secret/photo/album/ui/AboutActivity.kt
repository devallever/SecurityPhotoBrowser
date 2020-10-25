package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.SystemUtils
import com.allever.lib.notchcompat.NotchCompat
import com.allever.lib.umeng.UMeng
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_about.rootLayout
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.android.synthetic.main.include_top_bar.top_bar
import org.xm.secret.photo.album.BuildConfig
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.ad.AdConstant
import org.xm.secret.photo.album.app.BaseActivity2

class AboutActivity: BaseActivity2(), View.OnClickListener {

    private val PRIVACY_URL = "https://secretphotoalbum.flycricket.io/privacy.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initView()

    }

    private fun initView() {
        NotchCompat.adaptNotchWithFullScreen(window)
        checkNotch(Runnable {
            addStatusBar(rootLayout, top_bar)
        })

        about_privacy.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tv_label.text = getString(R.string.about)
        val channel = UMeng.getChannel()
        val last = if (BuildConfig.DEBUG) {
            "(Debug)-$channel\n" +
                    "${App.context.packageName}\n" +
                    "AdMob-${AdConstant.ADMOB_APP_ID}"
        } else {
            if (channel == "ad") {
                "(Release)-$channel\n" +
                        "${App.context.packageName}\n" +
                        "AdMob-${AdConstant.ADMOB_APP_ID}"
            } else {
                ""
            }
        }
        findViewById<TextView>(R.id.about_app_version).text = "v${BuildConfig.VERSION_NAME}$last"
        findViewById<TextView>(R.id.about_right).text =
            String.format(getString(R.string.about_right), getString(R.string.app_name))

   }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.about_privacy -> {
                SystemUtils.startWebView(App.context, PRIVACY_URL)
            }
        }
    }

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, AboutActivity::class.java)
            context?.startActivity(intent)
        }
    }
}
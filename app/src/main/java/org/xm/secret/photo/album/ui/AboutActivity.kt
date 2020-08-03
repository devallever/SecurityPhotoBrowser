package org.xm.secret.photo.album.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.SystemUtils
import com.allever.lib.umeng.UMeng
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.include_top_bar.*
import org.xm.secret.photo.album.BuildConfig
import org.xm.secret.photo.album.R

class AboutActivity: BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initView()

    }

    private fun initView() {
        about_privacy.setOnClickListener(this)
        iv_back.setOnClickListener(this)
        tv_label.text = getString(R.string.about)
        val channel = UMeng.getChannel()
        val last = if (BuildConfig.DEBUG) {
            "(Debug)-$channel"
        } else {
            ""
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
                val privacyUrl = "http://x.xiniubaba.com/x.php/1ycP23/5049"
                SystemUtils.startWebView(App.context, privacyUrl)
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
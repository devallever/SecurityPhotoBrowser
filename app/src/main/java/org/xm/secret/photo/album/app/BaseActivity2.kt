package org.xm.secret.photo.album.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.SystemUtils
import com.allever.lib.notchcompat.NotchCompat
import org.xm.secret.photo.album.R

open class BaseActivity2: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun checkNotch(runnable: Runnable?) {
        NotchCompat.hasNotch(window, runnable)
    }

    protected fun addStatusBar(rootLayout: ViewGroup, toolBar: View) {
        val statusBarView = View(this)
        statusBarView.id = statusBarView.hashCode()
        statusBarView.setBackgroundResource(R.drawable.top_bar_bg)
        val statusBarHeight = SystemUtils.getStatusBarHeight(this)
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight)

        if (rootLayout is RelativeLayout) {
            rootLayout.addView(statusBarView, lp)
            val topBarLp = toolBar.layoutParams as? RelativeLayout.LayoutParams
            topBarLp?.addRule(RelativeLayout.BELOW, statusBarView.id)
        } else if (rootLayout is LinearLayout) {
            rootLayout.addView(statusBarView, 0, lp)
        }
    }
}
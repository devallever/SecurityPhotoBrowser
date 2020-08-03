package org.xm.secret.photo.album.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.allever.lib.common.mvp.BaseMvpActivity
import com.allever.lib.common.mvp.BasePresenter
import org.xm.secret.photo.album.function.password.PrivateViewManager
import org.xm.secret.photo.album.function.password.PrivateViewProxy
import com.allever.lib.common.util.ActivityCollector
import com.allever.lib.common.util.SystemUiUtils
import com.allever.lib.common.util.SystemUtils
import com.allever.lib.notchcompat.NotchCompat
import org.xm.secret.photo.album.R
import java.lang.RuntimeException

abstract class BaseActivity<V, P: BasePresenter<V>> : BaseMvpActivity<V, P>() {

    protected lateinit var mPrivateViewProxy: PrivateViewProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(val contentView = getContentView()) {
            is Int -> {
                setContentView(contentView)
            }
            is View -> {
                setContentView(contentView)
            }
            else -> {
                throw RuntimeException("Please check contentView type")
            }
        }

        initView()
        initData()
        mPrivateViewProxy = PrivateViewManager.getProxy(this)
        mPrivateViewProxy.init()
    }

    override fun onResume() {
        super.onResume()
        mPrivateViewProxy.showPasswordIfNeed()
    }

    override fun onPause() {
        super.onPause()
        mPrivateViewProxy.showPasswordIfNeed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPrivateViewProxy.release()
    }

    override fun onBackPressed() {
        if (isPasswordViewShowing()) {
            ActivityCollector.finishAll()
        } else {
            super.onBackPressed()
        }
    }

    protected fun isPasswordViewShowing(): Boolean {
        return mPrivateViewProxy.passwordViewShowing
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun getContentView(): Any

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
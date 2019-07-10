package com.allever.security.photo.browser.function.password

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.android.absbase.App
import android.view.WindowManager


object PrivateViewManager {


    fun getProxy(activity: Activity): PrivateViewProxy {
        return PrivateViewProxy(activity)
    }
}

class PrivateViewProxy(val activity: Activity) {
    private var passwordHelper: PasswordHelper
    private var followupRunnable: Runnable? = null

    var passwordViewShowing = false
        private set

    private val mainHandler = android.os.Handler(Looper.getMainLooper()) {
        true
    }

    init {
        passwordHelper = PasswordHelper(
            mainHandler,
            object : PasswordHelper.PasswordCallback {
                override fun onPwdActionDown() {

                }

                override fun onPwdActionUp() {
                }

                override fun checkPwdRight() {
                    followupRunnable?.run()
                    showPasswordView(false)
                    PasswordConfig.secretCheckPass = true
                }
            })
    }

    fun init() {
        registerBroadcast(activity)
        passwordHelper.init()
    }

    fun release() {
        unregisterReceiver(activity)
        passwordHelper.release()
    }

    private fun registerBroadcast(activity: Activity) {
        val mFilter = IntentFilter()
        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)//home键
        mFilter.addAction(Intent.ACTION_SCREEN_ON)  //开屏
        mFilter.addAction(Intent.ACTION_SCREEN_OFF)//锁屏
        mFilter.addAction(Intent.ACTION_USER_PRESENT)//解锁
        activity.registerReceiver(mHomeBroadcastReceiver, mFilter)
    }

    private fun unregisterReceiver(activity: Activity) {
        activity.unregisterReceiver(mHomeBroadcastReceiver)
    }

    private val mHomeBroadcastReceiver = object : BroadcastReceiver() {
        private val SYSTEM_DIALOG_REASON_KEY = "reason"
        private val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        private val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            var needCheck = false
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                        // 短按home键
                        needCheck = true
                        PasswordConfig.secretCheckPass = false
                    } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                        // 长按home键
                        needCheck = true
                        PasswordConfig.secretCheckPass = false
                    }
                }
            }
            when (action) {
                Intent.ACTION_SCREEN_ON -> {
                    needCheck = true
                    PasswordConfig.secretCheckPass = false
                }
                Intent.ACTION_SCREEN_OFF -> {
                    needCheck = true
                    PasswordConfig.secretCheckPass = false
                }
                else -> {
                    // 解锁
                }
            }
            if (needCheck && context is Activity) {
                showPasswordIfNeed()
            }
        }
    }

    /**
     * 显示密码键盘,如果满足条件
     *
     * runnable: 确认密码后的后续操作, 比如一些弹窗,尽量不要再密码键盘上谈起
     */
    @JvmOverloads
    fun showPasswordIfNeed(runnable: Runnable? = null): Boolean {
        followupRunnable = runnable
        return showPasswordView(!PasswordConfig.secretCheckPass)
    }

    private fun getNavigationBarHeight(decorView: View?): Int {
        val windowManager = try {
            App.getContext().getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        } catch (e: Throwable) {
            null
        }
        return if (windowManager != null && decorView != null) {
            val point = Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                windowManager.defaultDisplay.getRealSize(point)
            } else {
                windowManager.defaultDisplay.getSize(point)
            }
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            point.y - rect.height()
        } else {
            val resources = App.getContext().resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
    }

    private fun showPasswordView(visible: Boolean): Boolean {
        var centent = activity.findViewById(android.R.id.content) as? ViewGroup
        var martinBottom = 0
        if (centent == null) {
            val window = activity.window
            centent = window?.decorView as? FrameLayout
            martinBottom = getNavigationBarHeight(window?.decorView)
        }

        var passwordView: View? = null
        if (centent != null) {
            for (i in 0 until centent.childCount) {
                val view = centent.getChildAt(i)
                if (view?.tag == WINDOW_PASSWORD_VIEW) {
                    passwordView = view
                }
            }
        }
        if (visible) {
            if (passwordView == null) {
                passwordView = passwordHelper.getPasswordView()
                passwordView?.tag =
                        WINDOW_PASSWORD_VIEW
                val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                lp.bottomMargin = martinBottom
                centent?.addView(passwordView, lp)
                passwordView?.bringToFront()
                passwordView?.isClickable = true
            }
            passwordHelper.show(true)
            passwordViewShowing = true
        } else {
            centent?.removeView(passwordView)
            passwordHelper.show(false)
            passwordViewShowing = false
            followupRunnable = null
        }
        return passwordViewShowing
    }

    companion object {
        private const val WINDOW_PASSWORD_VIEW = "window-passwordView"
    }
}
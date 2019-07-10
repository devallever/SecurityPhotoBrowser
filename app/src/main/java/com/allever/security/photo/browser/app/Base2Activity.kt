package com.allever.security.photo.browser.app

import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.function.password.PrivateViewManager
import com.allever.security.photo.browser.function.password.PrivateViewProxy

open class Base2Activity: BaseActivity() {

    protected lateinit var mPrivateViewProxy: PrivateViewProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    protected fun isPasswordViewShowing(): Boolean {
        return mPrivateViewProxy.passwordViewShowing
    }
}
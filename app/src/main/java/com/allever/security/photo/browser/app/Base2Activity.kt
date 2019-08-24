package com.allever.security.photo.browser.app

import android.os.Bundle
import android.view.View
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.mvp.BaseMvpActivity
import com.allever.lib.common.mvp.BasePresenter
import com.allever.security.photo.browser.function.password.PrivateViewManager
import com.allever.security.photo.browser.function.password.PrivateViewProxy

abstract class Base2Activity<V, P: BasePresenter<V>> : BaseMvpActivity<V, P>() {

    protected lateinit var mPrivateViewProxy: PrivateViewProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
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

    protected fun isPasswordViewShowing(): Boolean {
        return mPrivateViewProxy.passwordViewShowing
    }

    abstract fun initView()
    abstract fun initData()
    abstract fun getContentView(): Int
}
package org.xm.secret.photo.album.app

import android.os.Bundle
import android.view.View
import com.allever.lib.common.mvp.BaseMvpActivity
import com.allever.lib.common.mvp.BasePresenter
import org.xm.secret.photo.album.function.password.PrivateViewManager
import org.xm.secret.photo.album.function.password.PrivateViewProxy
import com.allever.lib.common.util.ActivityCollector
import java.lang.RuntimeException

abstract class Base2Activity<V, P: BasePresenter<V>> : BaseMvpActivity<V, P>() {

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
}
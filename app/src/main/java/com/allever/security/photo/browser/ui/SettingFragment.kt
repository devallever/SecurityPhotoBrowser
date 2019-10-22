package com.allever.security.photo.browser.ui

import android.view.View
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Fragment
import com.allever.security.photo.browser.ui.mvp.presenter.SettingPresenter
import com.allever.security.photo.browser.ui.mvp.view.SettingView

class SettingFragment: Base2Fragment<SettingView, SettingPresenter>(), SettingView {
    override fun getContentView(): Int = R.layout.fragment_setting

    override fun initView(root: View) {
    }

    override fun initData() {
    }

    override fun createPresenter(): SettingPresenter = SettingPresenter()
}
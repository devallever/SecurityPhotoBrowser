package com.allever.security.photo.browser.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseFragment
import com.allever.security.photo.browser.R

class GuideFragment: BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(App.context).inflate(R.layout.fragment_guide, container, false)
        return view
    }

}
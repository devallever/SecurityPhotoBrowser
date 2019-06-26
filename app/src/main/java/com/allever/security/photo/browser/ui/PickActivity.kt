package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.R

class PickActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, PickActivity::class.java)
            context.startActivity(intent)
        }
    }
}
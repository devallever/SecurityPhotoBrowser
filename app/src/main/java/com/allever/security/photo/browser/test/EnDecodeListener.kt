package com.allever.security.photo.browser.test

interface EnDecodeListener {
    fun onStart()
    fun onSuccess(path: String)
    fun onFail()
}
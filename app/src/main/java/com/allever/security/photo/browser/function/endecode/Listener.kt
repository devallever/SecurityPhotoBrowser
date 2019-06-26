package com.allever.security.photo.browser.function.endecode


/**
 * 加密回调
 */
interface EncodeListener {
    fun onEncodeStart()
    fun onEncodeSuccess(head: PrivateBean)
    fun onEncodeFailed(msg: String, head: PrivateBean)
}

/**
 * 加密多个
 */
interface EncodeListListener {
    fun onStart()
    fun onSuccess(successList: List<PrivateBean>, errorList: List<PrivateBean>)
    fun onFailed(successList: List<PrivateBean>, errorList: List<PrivateBean>)
}

/**
 * 解密回调
 */
interface DecodeListener {
    fun onDecodeStart()
    fun onDecodeSuccess(privateBean: PrivateBean)
    fun onDecodeFailed(msg: String)
}

/**
 * 解密多个
 */
interface DecodeListListener {
    fun onStart()
    fun onSuccess()
    fun onFailed(errorPaths: List<String>)
}

/**
 * 解锁回调
 */
interface UnLockAndRestoreListener {
    fun onStart()
    fun onSuccess()
    fun onFailed(msg: String)
}

/**
 * 解锁多个
 */
interface UnLockListListener {
    fun onStart()
    fun onSuccess()
    fun onFailed(errors: List<PrivateBean>)
}
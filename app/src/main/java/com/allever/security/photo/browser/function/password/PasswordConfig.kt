package com.allever.security.photo.browser.function.password

import com.android.absbase.utils.SpUtils

object PasswordConfig {

    /**
     * secret
     */
    const val KEY_SECRET_VAULT_PASSWORD = "cm_ksvp_asda"
    var secretVaultPassword: String
        get() = SpUtils.obtain().get(KEY_SECRET_VAULT_PASSWORD, "") ?: ""
        set(value) = SpUtils.obtain().save(KEY_SECRET_VAULT_PASSWORD, value)


    /**
     * 私密相册密码验证通过
     */
    const val KEY_SECRET_VAULT_PASSWORD_PASS = "cm_wsxzer"
    var secretCheckPass: Boolean
        get() = SpUtils.obtain()[KEY_SECRET_VAULT_PASSWORD_PASS, false]
        set(value) = SpUtils.obtain().save(KEY_SECRET_VAULT_PASSWORD_PASS, value)

    /**
     * 私密相册是否需要刷新
     */
    const val KEY_SECRET_VAULT_NEED_REFRESH = "cm_zerdfsdg"
    var secretVaultNeedRefresh: Boolean
        get() = SpUtils.obtain()[KEY_SECRET_VAULT_NEED_REFRESH, false]
        set(value) = SpUtils.obtain().save(KEY_SECRET_VAULT_NEED_REFRESH, value)

    /**
     *  setting
     * 私密相册是否隐藏
     */
    const val KEY_SECRET_VAULT_NEED_HIDE = "cm_vbgd"
    var secretVaultNeedHide: Boolean
        get() = SpUtils.obtain()[KEY_SECRET_VAULT_NEED_HIDE, false]
        set(value) = SpUtils.obtain().save(KEY_SECRET_VAULT_NEED_HIDE, value)

    /**
     * 设置项隐藏显示私密相册的状态
     */
    const val KEY_SECRET_VAULT_NEED_HIDE_RESUME = "cm_qwesdas"
    var secretVaultNeedHideResume: Boolean
        get() = SpUtils.obtain()[KEY_SECRET_VAULT_NEED_HIDE_RESUME, false]
        set(value) = SpUtils.obtain().save(KEY_SECRET_VAULT_NEED_HIDE_RESUME, value)
}
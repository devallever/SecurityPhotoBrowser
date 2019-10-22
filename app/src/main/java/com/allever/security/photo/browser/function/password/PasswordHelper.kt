package com.allever.security.photo.browser.function.password

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Service
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.IntDef
import com.allever.lib.common.app.App
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.allever.security.photo.browser.R

class PasswordHelper(val handler: Handler, val callback: PasswordCallback?) {

    companion object {
        public const val STATUS_NEW_PWD = 0 //设置密码
        public const val STATUS_CONFORM_PWD = 1 //确认密码
        public const val STATUS_CHECK_PWD = 2 //验证已经存在的密码

        public const val TYPE_MODIFY = 0
        public const val TYPE_CHECK = 1
    }

    @IntDef(
        value = [
            STATUS_NEW_PWD, STATUS_CONFORM_PWD, STATUS_CHECK_PWD
        ]
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Status

//    private var mPwdStatus = STATUS_OLD_PWD

    // 是否验证通过
    private var mIsPasswordPass = false
    @Status
    private var mSettingPassword = -1

    private val mBubbleSize = 4

    private var mLlBubbleContainer: LinearLayout? = null
    private var mTvTips: TextView? = null
    private var mIvLogo: ImageView? = null
    private var mIvBg: View? = null
    private var mFlBgMask: View? = null

    private var mNumbers = mutableListOf<Int>()
    private var mBubbleViews = mutableListOf<ImageView>()
    private var mKeyboardView: KeyboardView? = null

    private var mPasswordView: View? = null

    private var mType = TYPE_CHECK

    private var colorFilter: Int = 0
    private var defaultColorFilter: Int = 0

    init {

    }

    fun init() {
        initView()
        initPasswordVibrator()
        initPasswordAnimal()
    }

    fun release() {
        mIsPasswordPass = false
        mObjectAnimator?.cancel()
        mVibrator?.cancel()
        mNumbers.clear()
        mBubbleViews.clear()
        mType = TYPE_CHECK
        mSettingPassword = -1
    }

    fun getPasswordView(): View? {
        return mPasswordView
    }

    fun isPass(): Boolean {
        return mIsPasswordPass
    }


    private fun showLogo(isShow: Boolean) {
        if (isShow) {
            mIvLogo?.visibility = View.VISIBLE
        } else {
            mIvLogo?.visibility = View.GONE
        }
    }

    private fun showBackground(isShow: Boolean) {
        if (isShow) {
            mIvBg?.visibility = View.VISIBLE
            mFlBgMask?.visibility = View.VISIBLE
        } else {
            mIvBg?.visibility = View.GONE
            mFlBgMask?.visibility = View.GONE
        }
    }

    private fun initView() {

        colorFilter = App.context.resources.getColor(R.color.default_gray)
        defaultColorFilter = App.context.resources.getColor(R.color.default_theme_color)

        mPasswordView = LayoutInflater.from(App.context).inflate(R.layout.secret_vault_password_layout, null)
        mLlBubbleContainer = mPasswordView?.findViewById(R.id.ll_bubbles)
        mTvTips = mPasswordView?.findViewById(R.id.tv_password_tip)
        mKeyboardView = mPasswordView?.findViewById(R.id.keyboard_view)
        mIvLogo = mPasswordView?.findViewById(R.id.id_iv_logo)
        mIvBg = mPasswordView?.findViewById(R.id.id_iv_bg)
        mFlBgMask = mPasswordView?.findViewById(R.id.id_bg_mask)

        mKeyboardView?.onKeyboardClick = object : KeyboardView.OnKeyboardClick {
            override fun onClick(num: Int) {
                if (mNumbers.size < mBubbleSize) {
                    mNumbers.add(num)
                    invalidate()
                    if (mNumbers.size == mBubbleSize) {
                        checkPassword()
                    }
                }
            }

            override fun onDelete() {
                if (mNumbers.size > 0) {
                    mNumbers.removeAt(mNumbers.size - 1)
                    invalidate()
                }
            }

        }

        for (i in 0 until mBubbleSize) {
            val view = View.inflate(App.context, R.layout.view_password_bubble, null)
            val bubbleView = view.findViewById(R.id.bubble) as ImageView
            bubbleView.setImageResource(R.drawable.album_icon_password)
            bubbleView.setColorFilter(colorFilter)
            mBubbleViews.add(bubbleView)
            mLlBubbleContainer?.addView(view)
        }
    }

    private var mVibrator: Vibrator? = null
    private fun initPasswordVibrator() {
        mVibrator = App.context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    }

    private val end = 25f
    private val start = 0f
    private var mObjectAnimator: ObjectAnimator? = null
    private val duration = 500L
    private val delayTime = 200L
    private fun initPasswordAnimal() {
        mObjectAnimator = ObjectAnimator.ofFloat(
            mLlBubbleContainer, "translationX",
            start, end, start, -end, start,
            start - 5, end, start - 5, -end, start - 5,
            start - 10, end, start - 10, -end, start - 10, start
        )
        mObjectAnimator?.duration = duration

        mObjectAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                if (mVibrator?.hasVibrator() == true) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mVibrator?.vibrate(VibrationEffect.createOneShot(duration, 254))
                    } else {
                        mVibrator?.vibrate(duration)
                    }
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                //动画结束后没回调???
                mNumbers.clear()
                invalidate()
                mVibrator?.cancel()
            }

            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
        })
    }


    private fun invalidate() {
        for (view in mBubbleViews) {
            view.setImageResource(R.drawable.album_icon_password)
            view.setColorFilter(colorFilter)
        }
        for (i in 0 until mNumbers.size) {
            mBubbleViews[i].setImageResource(R.drawable.album_icon_password_green)
            mBubbleViews[i].setColorFilter(defaultColorFilter)
        }
    }

    fun show(show: Boolean) {
        mType = TYPE_CHECK
        if (show) {
            if (PasswordConfig.secretVaultPassword.isEmpty()) {
                mTvTips?.text = App.context.getString(R.string.album_select_secret_vault_new_password_tip)
                mSettingPassword =
                        STATUS_NEW_PWD
            } else {
                mTvTips?.text = App.context.getString(R.string.album_select_secret_vault_password_tip)
                mSettingPassword =
                        STATUS_CHECK_PWD
            }
            PasswordConfig.secretCheckPass = false
            mIsPasswordPass = false
            mPasswordView?.visibility = View.VISIBLE
        } else {
            mPasswordView?.visibility = View.GONE
        }
    }

    fun modifyPasscode() {
        mTvTips?.text = App.context.getString(R.string.enter_old_pwd)
        mSettingPassword =
                STATUS_CHECK_PWD
        mType = TYPE_MODIFY
        showLogo(false)
        showBackground(false)
    }

    private fun checkPasswordRight() {
        //Gp
//        if(mSettingPassword == STATUS_CONFORM_PWD || mSettingPassword == STATUS_CHECK_PWD){
//            val timeMillis = System.currentTimeMillis()
//            if(PasswordConfig.purchaseSubSize <= 0 && (timeMillis - PasswordConfig.firstEnterAPPOfDay > TimeUtils.MILLIS_IN_DAY)){
//                PasswordConfig.firstEnterAPPOfDay = timeMillis
//                BillingActivity.startActivity(App.context)
//            }
//        }
        //密码正确，延迟0.2秒
        handler.postDelayed({
            mSettingPassword = -1
            mNumbers.clear()
            invalidate()
            mIsPasswordPass = true
            callback?.checkPwdRight()
        }, delayTime)
    }

    private fun checkPasswordError() {
        handler.postDelayed({
            for (view in mBubbleViews) {
                view.setImageResource(R.drawable.album_icon_password_red)
                view.setColorFilter(defaultColorFilter)
            }
            mNumbers.clear()
            mObjectAnimator?.start()
        }, delayTime)
    }

    private var password: String? = null
    private fun checkPassword() {
        val sb = StringBuilder()
        for (number in mNumbers) {
            sb.append(number)
        }
        if (mSettingPassword == STATUS_NEW_PWD) {
            password = sb.toString()
            mTvTips?.text = App.context.getString(R.string.album_select_secret_vault_comfirm_password_tip)
            mNumbers.clear()
            invalidate()
            mSettingPassword =
                    STATUS_CONFORM_PWD
        } else if (mSettingPassword == STATUS_CONFORM_PWD) {
            if (sb.toString() == password) {
                PasswordConfig.secretVaultPassword = password as String
                password = null
                checkPasswordRight()
            } else {
                checkPasswordError()
            }
        } else if (mSettingPassword == STATUS_CHECK_PWD) {
            if (sb.toString() == PasswordConfig.secretVaultPassword) {
                if (mType == TYPE_MODIFY) {
                    //旧密码正确
                    mNumbers.clear()
                    invalidate()
                    mTvTips?.text = App.context.getString(R.string.enter_new_pwd)
                    mSettingPassword =
                            STATUS_NEW_PWD
                } else {
                    checkPasswordRight()
                }

            } else {
                checkPasswordError()
            }
        }
    }

    public interface PasswordCallback {
        fun onPwdActionDown() {}
        fun onPwdActionUp() {}
        fun checkPwdRight() {}
    }
}
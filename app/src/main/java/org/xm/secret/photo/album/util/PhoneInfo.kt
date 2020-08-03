package org.xm.secret.photo.album.util

import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.android.absbase.utils.PlatformUtils


/**
 * 机型判断接口
 */
object PhoneInfo {

    /**
     * 是否是小米机型
     *
     * @return
     */
    val isXiaoMi: Boolean
        get() = Build.BRAND.equals("Xiaomi", ignoreCase = true)

    /**
     * 是否是索尼机型
     *
     * @return
     */
    val isSony: Boolean
        get() = Build.BRAND.equals("SEMC", ignoreCase = true) || Build.BRAND.equals("Sony", ignoreCase = true)

    /**
     * 是否是三星机型
     *
     * @return
     */
    val isSamsung: Boolean
        get() = "samsung".equals(Build.BRAND, ignoreCase = true)

    /**
     * 是否OES支持异常机型
     *
     * @return
     */
    //						|| "GT-I9502".equals(Build.MODEL)
    val isNotSupportOES: Boolean
        get() = ("Galaxy Nexus" == Build.MODEL
                || isSony && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                || "GT-I8552" == Build.MODEL
                || "SM-T110" == Build.MODEL
                || "SM-T211" == Build.MODEL
                || "XT910" == Build.MODEL
                || "SM-G3818" == Build.MODEL)

    /**
     * 是否停止录像需要重启摄像头机型
     *
     * @return
     */
    val isVideoIssueDevice: Boolean
        get() = "SM-N9108V" == Build.MODEL

    /**
     * 是否是录像不支持预览渲染机型
     *
     * @return
     */
    val isNotSupportVideoRender: Boolean
        get() = if ("GT-I9300" == Build.MODEL
            || "HUAWEI C8950D" == Build.MODEL
            || Build.MODEL.contains("HUAWEI P6")
            || Build.MODEL.contains("HTC Sensation XE")
            || "C2305" == Build.MODEL
            || "GT-S7270" == Build.MODEL
            || "GT-S7275" == Build.MODEL
            || "GT-S7272" == Build.MODEL
            || "SM-T210" == Build.MODEL
            || "ST26a" == Build.MODEL
            || Build.MODEL.toLowerCase().contains("htc desire 310")
            || "Coolpad" == Build.BRAND && "9900" == Build.MODEL
            || "XT910" == Build.MODEL
            || Build.MODEL.toLowerCase().contains("vs840")
            || "SCH-I435" == Build.MODEL
            || "SM-N910F" == Build.MODEL && PlatformUtils.version() != PlatformUtils.VERSION_CODES.LOLLIPOP
        ) {
            true
        } else false

    val isSupportVideoFilter: Boolean
        get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
            || isNotSupportVideoRender || isMeizu
        ) {
            false
        } else true

    /**
     * 华硕
     *
     * @return
     */
    val isASUS: Boolean
        get() = "asus".equals(Build.BRAND, ignoreCase = true)

    /**
     * 是否是魅族机型
     *
     * @return
     */
    val isMeizu: Boolean
        get() = "Meizu" == Build.BRAND

    /**
     * 是否是Moto机型
     *
     * @return
     */
    val isMoto: Boolean
        get() = "motorola" == Build.BRAND

    /**
     * 是否是魅族MX4机型
     *
     * @return
     */
    val isMeizuMX4: Boolean
        get() = isMeizu && "MX4" == Build.MODEL

    /**
     * 是否是MIUI
     *
     * @return
     */
    val isMiui: Boolean
        get() = !TextUtils.isEmpty(SystemProperties.get("ro.miui.ui.version.name", null))

    /**
     * 是否是华为机型
     *
     * @return
     */
    val isHuawei: Boolean
        get() = Build.BRAND.toLowerCase().contains("huawei")

    /**
     * 是否是奇酷手机
     *
     * @return
     */
    val isQiku: Boolean
        get() = Build.BRAND.equals("Qiku", ignoreCase = true)

    /**
     * 是否不支持锁屏机型
     *
     * @return
     */
    val isNotSupportLocker: Boolean
        get() = true || isXiaoMi || isMiui || isHuawei || isQiku

    /**
     * 如果是5.0或者以上 则支持写外置SDCARD
     *
     * @return
     */
    val isSupportWriteExtSdCard: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    /**
     * 华为 MT7不支持从mount读取外置SDCARD的路径
     *
     * @return
     */
    val isNotSupportReadExtSdcardPathFromMount: Boolean
        get() = isHuawei && Build.MODEL.toLowerCase().contains("mt7") || isSamsung && Build.MODEL.contains("SM-G930P")

    /**
     * 设置分辨率是否需要重开摄像头
     *
     * @return
     */
    fun needToRestartCamera(): Boolean {
        return Build.MODEL == "LG-P880"
    }


    /**
     * 获取手机IMSI
     *
     * @param context
     * @return
     */
    fun getPhoneIMSI(context: Context): String? {
        var imsi: String? = null

        try {
            if (imsi == null) {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                imsi = tm.subscriberId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var simsi: String? = null
        try {
            if (imsi != null && imsi.length > 0) {
                simsi = MD5.getMD5Str(imsi) //单向加密
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return simsi
    }

    /**
     * 是否是需要剔除的分辨率
     *
     * @param size
     * @return
     */
    fun isBadPictureSize(size: Camera.Size?): Boolean {
        if ("SCH-I435" != Build.MODEL || size == null) {
            return false
        }
        return if (size.width == 1920 && size.height == 1080) {
            true
        } else false
    }

    fun isBadVideoSize(size: Camera.Size, frontFacing: Boolean): Boolean {
        // 不支持2K分辨率
        if (/*(isMeizu() || isMoto()) && */size.width * size.height >= 2560 * 1440) {
            return true
        }
        if ("SM-N910F" == Build.MODEL && frontFacing) {
            if (Math.abs(size.width / size.height.toFloat() - 1.7f) > 0.1f) {
                return true
            }
        }
        return if ("MOT-XT788" == Build.MODEL && size.width == 1280 && size.height == 720) {
            true
        } else false
    }

    fun hasJellyBeanMR2(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
    }

    fun hasKitKat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }
}

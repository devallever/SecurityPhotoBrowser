package com.allever.security.photo.browser.util;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.absbase.utils.PlatformUtils;


/**
 * 机型判断接口
 * 
 */
public class PhoneInfo {
    
    /**
     * 是否是小米机型
     * 
     * @return
     */
    public static boolean isXiaoMi() {
        return Build.BRAND.equalsIgnoreCase("Xiaomi");
    }

	/**
	 * 是否是索尼机型
	 *
	 * @return
	 */
	public static boolean isSony() {
		return Build.BRAND.equalsIgnoreCase("SEMC") || Build.BRAND.equalsIgnoreCase("Sony");
	}
    
    /**
     * 设置分辨率是否需要重开摄像头
     * 
     * @return
     */
    public static boolean needToRestartCamera() {
        return Build.MODEL.equals("LG-P880");
    }
    

	/**
	 * 获取手机IMSI
	 *
	 * @param context
	 * @return
	 */
	public static String getPhoneIMSI(Context context) {
		String imsi = null;

    	try {
    		if( imsi == null) {
				TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				imsi =tm.getSubscriberId();
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		}

    	String simsi = null;
    	try{
	    	if( imsi != null && imsi.length() > 0) {
				simsi = MD5.getMD5Str(imsi); //单向加密
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}

    	return simsi;
	}

	/**
	 * 是否是需要剔除的分辨率
	 *
	 * @param size
	 * @return
	 */
	public static boolean isBadPictureSize(Camera.Size size) {
        if (!"SCH-I435".equals(Build.MODEL) || size == null) {
            return false;
        }
        if (size.width == 1920 && size.height == 1080) {
            return true;
        }
        return false;
	}

	public static boolean isBadVideoSize(Camera.Size size, boolean frontFacing) {
		// 不支持2K分辨率
		if (/*(isMeizu() || isMoto()) && */size.width * size.height >= 2560 * 1440) {
			return true;
		}
		if ("SM-N910F".equals(Build.MODEL) && frontFacing) {
			if (Math.abs(size.width / (float)size.height - 1.7f) > 0.1f) {
				return true;
			}
		}
		if ("MOT-XT788".equals(Build.MODEL) && size.width == 1280 && size.height == 720) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是三星机型
	 *
	 * @return
	 */
	public static boolean isSamsung() {
	    return "samsung".equalsIgnoreCase(Build.BRAND);
	}

	/**
	 * 是否OES支持异常机型
	 *
	 * @return
	 */
	public static boolean isNotSupportOES() {
		return
				"Galaxy Nexus".equals(Build.MODEL)
						|| (isSony() && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
						|| "GT-I8552".equals(Build.MODEL)
						|| "SM-T110".equals(Build.MODEL)
						|| "SM-T211".equals(Build.MODEL)
//						|| "GT-I9502".equals(Build.MODEL)
						|| "XT910".equals(Build.MODEL)
						|| "SM-G3818".equals(Build.MODEL);
	}

	/**
	 * 是否停止录像需要重启摄像头机型
	 *
	 * @return
	 */
	public static boolean isVideoIssueDevice() {
		return "SM-N9108V".equals(Build.MODEL);
	}

	/**
	 * 是否是录像不支持预览渲染机型
	 *
	 * @return
	 */
	public static boolean isNotSupportVideoRender() {
		if ("GT-I9300".equals(Build.MODEL)
				|| "HUAWEI C8950D".equals(Build.MODEL)
				|| Build.MODEL.contains("HUAWEI P6")
				|| Build.MODEL.contains("HTC Sensation XE")
				|| "C2305".equals(Build.MODEL)
				|| "GT-S7270".equals(Build.MODEL)
				|| "GT-S7275".equals(Build.MODEL)
				|| "GT-S7272".equals(Build.MODEL)
				|| "SM-T210".equals(Build.MODEL)
				|| "ST26a".equals(Build.MODEL)
				|| Build.MODEL.toLowerCase().contains("htc desire 310")
				|| ("Coolpad".equals(Build.BRAND) && "9900".equals(Build.MODEL))
				|| "XT910".equals(Build.MODEL)
				|| Build.MODEL.toLowerCase().contains("vs840")
				|| "SCH-I435".equals(Build.MODEL)
				|| ("SM-N910F".equals(Build.MODEL) && PlatformUtils.version() != PlatformUtils.VERSION_CODES.LOLLIPOP)) {
			return true;
		}
		return false;
	}

	public static boolean isSupportVideoFilter() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
				|| isNotSupportVideoRender() || isMeizu()) {
			return false;
		}
		return true;
	}

	public static boolean hasJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}
	
	/**
	 * 华硕
	 * @return
	 */
	public static boolean isASUS(){
		return "asus".equalsIgnoreCase(Build.BRAND);
	}

	/**
	 * 是否是魅族机型
	 *
	 * @return
	 */
	public static boolean isMeizu() {
		return "Meizu".equals(Build.BRAND);
	}

	/**
	 * 是否是Moto机型
	 *
	 * @return
	 */
	public static boolean isMoto() {
		return "motorola".equals(Build.BRAND);
	}

	/**
	 * 是否是魅族MX4机型
	 *
	 * @return
	 */
	public static boolean isMeizuMX4() {
		return isMeizu() && "MX4".equals(Build.MODEL);
	}

	/**
	 * 是否是MIUI
	 *
	 * @return
     */
	public static boolean isMiui() {
		return !TextUtils.isEmpty(SystemProperties.get("ro.miui.ui.version.name", null));
	}

	/**
	 * 是否是华为机型
	 *
	 * @return
     */
	public static boolean isHuawei() {
		return Build.BRAND.toLowerCase().contains("huawei");
	}

	/**
	 * 是否是奇酷手机
	 *
	 * @return
     */
	public static boolean isQiku() {
		return Build.BRAND.equalsIgnoreCase("Qiku");
	}

	/**
	 * 是否不支持锁屏机型
	 *
	 * @return
     */
	public static boolean isNotSupportLocker() {
		return true || isXiaoMi() || isMiui() || isHuawei() || isQiku();
	}

	/**
	 * 如果是5.0或者以上 则支持写外置SDCARD
	 * @return
	 */
	public static boolean isSupportWriteExtSdCard(){
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	/**
	 * 华为 MT7不支持从mount读取外置SDCARD的路径
	 * @return
     */
	public static boolean isNotSupportReadExtSdcardPathFromMount(){
		return (isHuawei() && Build.MODEL.toLowerCase().contains("mt7")) || (isSamsung() && Build.MODEL.contains("SM-G930P"));
	}
}

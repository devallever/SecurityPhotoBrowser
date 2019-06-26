package com.allever.security.photo.browser.util

import android.opengl.GLES20
import android.os.Environment
import android.text.TextUtils
import com.android.absbase.App

import java.io.File
import java.nio.IntBuffer
import java.util.concurrent.CopyOnWriteArrayList

object DataManager {

    /**
     * 数据是否已初始化
     */
    var isDataInit = false

    /**
     * 保存已下载资源
     */
    private val mInstalledRes = CopyOnWriteArrayList<String>()

    //如果是5.0及以上  外置sd卡是可以读写的
    //没有权限
    //如果当前路径不存在 或者不能读  或者不能写 则设置为默认路径
    var preferenceSaveLocation: String
        get() {
            var location = SPDataManager.getPhotoSaveLocation()
            if (PhoneInfo.isSupportWriteExtSdCard && ExtSdcardUtils.isExtSdcardPath(location)) {
                val hasPermission = ExtSdcardUtils.hasExtSdcardPermission()
                val f = File(location)
                if (!hasPermission || !f.exists()) {
                    location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera"
                    preferenceSaveLocation = location
                    return location
                }
            } else {
                val f = File(location)
                if (!f.exists() || !f.canRead() || !f.canWrite()) {
                    location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera"
                    preferenceSaveLocation = location
                    return location
                }
            }
            return location
        }
        set(location) = SPDataManager.setPhotoSaveLocation(location)

    /**
     * 获取最大纹理，0为未初始化
     *
     * @return
     */
    val maxTextureSize: Int
        get() = SPDataManager.getMaxTextureSize()

    val shareImageTool1PkgName: String
        get() = SPDataManager.getShareImageTool1PkgName()

    val shareImageTool1ActivityName: String
        get() = SPDataManager.getShareImageTool1ActivityName()

    val shareImageTool2PkgName: String
        get() = SPDataManager.getShareImageTool2PkgName()

    val shareImageTool2ActivityName: String
        get() = SPDataManager.getShareImageTool2ActivityName()

    val shareVideoTool1PkgName: String
        get() = SPDataManager.getShareImageTool1PkgName()

    val shareVideoTool1ActivityName: String
        get() = SPDataManager.getShareImageTool1ActivityName()

    val shareVideoTool2PkgName: String
        get() = SPDataManager.getShareImageTool2PkgName()

    val shareVideoTool2ActivityName: String
        get() = SPDataManager.getShareImageTool2ActivityName()

    fun release() {}

    fun setDataInitSuccess(success: Boolean) {
        isDataInit = success
    }

    /**
     * 初始化最大纹理，需要在GL线程执行
     */
    fun initMaxTextureSize() {
        var size = maxTextureSize
        if (size == 0) {
            try {
                val ib = IntBuffer.allocate(1)
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, ib)
                ib.position(0)
                size = ib.get()
            } catch (tr: Throwable) {
            }

            if (size >= 2048) {
                SPDataManager.setMaxTextureSize(size)
            }
        }
    }

    fun delInstalledRes(pkgName: String) {
        if (TextUtils.isEmpty(pkgName)) {
            return
        }
        mInstalledRes.remove(pkgName)
    }


    val TAG = DataManager::class.java.simpleName

    //    public static final String TEMP_DIR = FileUtils.getExternalCacheDir(App.getContext(),
    //            ".temp", true) + File.separator;
    //
    //    public static final String RESOURCE_DIR = FileUtils.getExternalCacheDir(App.getContext(),
    //            ".normal_res", true) + File.separator;
    //
    //    public static final String CONFIG_DIR = FileUtils.getExternalCacheDir(App.getContext(),
    //            ".config",true) + File.separator;
    //
    //    public static final String FONT_DIR = RESOURCE_DIR + "font" + File.separator;

    /*
config -> 2245023265AE4CF87D02C8B6BA991139
resource -> 96AB4E163F4EE03AAA4D1051AA51D204
font -> 47A282DFE68A42D302E22C4920ED9B5E
sticker -> 9173F7D7444D3161C4DEA50F35F5D15F
filter -> B2C97AE425DD751B0E48A3ACAE79CF4A
mixer -> 10B5E1E383FFF2F51F8A7C9FD67F2E03
background -> D229BBF31EAEEBC7C88897732D8B932D
tutorial -> 0575C8D592FB7B088226750ACEEC2B4E
config.json -> 06B2D3B23DCE96E1619D2B53D6C947EC
effects_info.json -> C2A6213D2C7F57DB3340E17BC71D5182
temp -> 3D801AA532C1CEC3EE82D87A99FDF63F
config -> 2245023265AE4CF87D02C8B6BA991139
effect -> BF5B17FAC5C60D745A593B5920372235
*/

    //temp
    private val TEMP_MD5 = "3D801AA532C1CEC3EE82D87A99FDF63F"
    //resource
    private val RESOURCE_MD5 = "96AB4E163F4EE03AAA4D1051AA51D204"
    //config as path
    private val CONFIG_DIR_MD5 = "2245023265AE4CF87D02C8B6BA991139"
    //config as fileName
    private val CONFIG_NAME_MD5 = CONFIG_DIR_MD5
    //    //config.json
    //    private static final String CONFIG_JSON_MD5 = "06B2D3B23DCE96E1619D2B53D6C947EC";
    //effect
    private val EFFECT_INFO_JSON_MD5 = "BF5B17FAC5C60D745A593B5920372235"
    //font
    private val FONT_MD5 = "47A282DFE68A42D302E22C4920ED9B5E"
    //sticker
    private val STICKER_MD5 = "9173F7D7444D3161C4DEA50F35F5D15F"
    //filter
    private val FILTER_MD5 = "B2C97AE425DD751B0E48A3ACAE79CF4A"
    //mixer
    private val MIXER_MD5 = "10B5E1E383FFF2F51F8A7C9FD67F2E03"
    //background
    private val BACKGROUND_MD5 = "D229BBF31EAEEBC7C88897732D8B932D"
    //tutorial
    private val TUTORIAL_MD5 = "0575C8D592FB7B088226750ACEEC2B4E"


    val INTERNAL_ROOT_DIR = App.getContext().filesDir.absolutePath

    val INTERNAL_RES_DIR = INTERNAL_ROOT_DIR + File.separator + RESOURCE_MD5

    val INTERNAL_FONT_DIR = INTERNAL_RES_DIR + File.separator + FONT_MD5


    val ROOT_DIR = (Environment.getExternalStorageDirectory().absolutePath
            + File.separator + "fantuan" + File.separator + "photoeditor" + File.separator + "files")

    val RESOURCE_DIR = ROOT_DIR + File.separator + RESOURCE_MD5

    val CONFIG_DIR = ROOT_DIR + File.separator + CONFIG_DIR_MD5

    val TEMP_DIR = ROOT_DIR + File.separator + TEMP_MD5

    val CONFIG_FILE_NAME = CONFIG_NAME_MD5
    val EFFECT_FILE_NAME = EFFECT_INFO_JSON_MD5

    val RES_FONT_DIR = RESOURCE_DIR + File.separator + FONT_MD5
    val RES_STICKER_DIR = RESOURCE_DIR + File.separator + STICKER_MD5
    val RES_BACKGROUND_DIR = RESOURCE_DIR + File.separator + BACKGROUND_MD5
    val RES_MIXER_DIR = RESOURCE_DIR + File.separator + MIXER_MD5

    val RES_TUTORIAL_DIR = RESOURCE_DIR + File.separator + TUTORIAL_MD5

    private var mInstance: DataManager? = null

}

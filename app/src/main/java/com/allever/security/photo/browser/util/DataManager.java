package com.allever.security.photo.browser.util;

import android.opengl.GLES20;
import android.os.Environment;
import android.text.TextUtils;
import com.android.absbase.App;

import java.io.File;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataManager {

    public static final String TAG = DataManager.class.getSimpleName();

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
    private static final String TEMP_MD5 = "3D801AA532C1CEC3EE82D87A99FDF63F";
    //resource
    private static final String RESOURCE_MD5 = "96AB4E163F4EE03AAA4D1051AA51D204";
    //config as path
    private static final String CONFIG_DIR_MD5 = "2245023265AE4CF87D02C8B6BA991139";
    //config as fileName
    private static final String CONFIG_NAME_MD5 = CONFIG_DIR_MD5;
    //    //config.json
//    private static final String CONFIG_JSON_MD5 = "06B2D3B23DCE96E1619D2B53D6C947EC";
    //effect
    private static final String EFFECT_INFO_JSON_MD5 = "BF5B17FAC5C60D745A593B5920372235";
    //font
    private static final String FONT_MD5 = "47A282DFE68A42D302E22C4920ED9B5E";
    //sticker
    private static final String STICKER_MD5 = "9173F7D7444D3161C4DEA50F35F5D15F";
    //filter
    private static final String FILTER_MD5 = "B2C97AE425DD751B0E48A3ACAE79CF4A";
    //mixer
    private static final String MIXER_MD5 = "10B5E1E383FFF2F51F8A7C9FD67F2E03";
    //background
    private static final String BACKGROUND_MD5 = "D229BBF31EAEEBC7C88897732D8B932D";
    //tutorial
    private static final String TUTORIAL_MD5 = "0575C8D592FB7B088226750ACEEC2B4E";


    public static final String INTERNAL_ROOT_DIR = App.getContext().getFilesDir().getAbsolutePath();

    public static final String INTERNAL_RES_DIR = INTERNAL_ROOT_DIR + File.separator + RESOURCE_MD5;

    public static final String INTERNAL_FONT_DIR = INTERNAL_RES_DIR + File.separator + FONT_MD5;


    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "fantuan" + File.separator + "photoeditor" + File.separator + "files";

    public static final String RESOURCE_DIR = ROOT_DIR + File.separator + RESOURCE_MD5;

    public static final String CONFIG_DIR = ROOT_DIR + File.separator + CONFIG_DIR_MD5;

    public static final String TEMP_DIR = ROOT_DIR + File.separator + TEMP_MD5;

    public static final String CONFIG_FILE_NAME = CONFIG_NAME_MD5;
    public static final String EFFECT_FILE_NAME = EFFECT_INFO_JSON_MD5;

    public static final String RES_FONT_DIR = RESOURCE_DIR + File.separator + FONT_MD5;
    public static final String RES_STICKER_DIR = RESOURCE_DIR + File.separator + STICKER_MD5;
    public static final String RES_BACKGROUND_DIR = RESOURCE_DIR + File.separator + BACKGROUND_MD5;
    public static final String RES_MIXER_DIR = RESOURCE_DIR + File.separator + MIXER_MD5;

    public static final String RES_TUTORIAL_DIR = RESOURCE_DIR + File.separator + TUTORIAL_MD5;

    /**
     * 数据是否已初始化
     */
    public boolean mIsDataInit = false;

    private static DataManager mInstance = null;

    /**
     * 保存已下载资源
     **/
    private List<String> mInstalledRes = new CopyOnWriteArrayList<>();

    public static DataManager getInstance() {
        if (mInstance == null) {
            synchronized (DataManager.class) {
                if (mInstance == null) {
                    mInstance = new DataManager();
                }
            }
        }
        return mInstance;
    }

    private DataManager() {
    }

    public void release() {
    }

    public boolean isDataInit() {
        return mIsDataInit;
    }

    public void setDataInitSuccess(boolean success) {
        mIsDataInit = success;
    }

    public void setPreferenceSaveLocation(String location) {
        SPDataManager.setPhotoSaveLocation(location);
    }

    public String getPreferenceSaveLocation() {
        String location = SPDataManager.getPhotoSaveLocation();
        if (PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(location)) {//如果是5.0及以上  外置sd卡是可以读写的
            boolean hasPermission = ExtSdcardUtils.hasExtSdcardPermission();
            File f = new File(location);
            if (!hasPermission || !f.exists()) {//没有权限
                location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera";
                setPreferenceSaveLocation(location);
                return location;
            }
        } else {
            File f = new File(location);
            if (!f.exists() || !f.canRead() || !f.canWrite()) {//如果当前路径不存在 或者不能读  或者不能写 则设置为默认路径
                location = FileUtil.DICM_ROOT_PATH + File.separator + "Camera";
                setPreferenceSaveLocation(location);
                return location;
            }
        }
        return location;
    }

    /**
     * 获取最大纹理，0为未初始化
     *
     * @return
     */
    public int getMaxTextureSize() {
        return SPDataManager.getMaxTextureSize();
    }

    /**
     * 初始化最大纹理，需要在GL线程执行
     */
    public void initMaxTextureSize() {
        int size = getMaxTextureSize();
        if (size == 0) {
            try {
                IntBuffer ib = IntBuffer.allocate(1);
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, ib);
                ib.position(0);
                size = ib.get();
            } catch (Throwable tr) {
            }
            if (size >= 2048) {
                SPDataManager.setMaxTextureSize(size);
            }
        }
    }

    public String getShareImageTool1PkgName() {
        return SPDataManager.getShareImageTool1PkgName();
    }

    public String getShareImageTool1ActivityName() {
        return SPDataManager.getShareImageTool1ActivityName();
    }

    public String getShareImageTool2PkgName() {
        return SPDataManager.getShareImageTool2PkgName();
    }

    public String getShareImageTool2ActivityName() {
        return SPDataManager.getShareImageTool2ActivityName();
    }

    public String getShareVideoTool1PkgName() {
        return SPDataManager.getShareImageTool1PkgName();
    }

    public String getShareVideoTool1ActivityName() {
        return SPDataManager.getShareImageTool1ActivityName();
    }

    public String getShareVideoTool2PkgName() {
        return SPDataManager.getShareImageTool2PkgName();
    }

    public String getShareVideoTool2ActivityName() {
        return SPDataManager.getShareImageTool2ActivityName();
    }

    public void delInstalledRes(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        mInstalledRes.remove(pkgName);
    }
}

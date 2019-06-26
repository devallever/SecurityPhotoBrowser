package com.allever.security.photo.browser.util;

import android.text.TextUtils;
import com.android.absbase.utils.SpUtils;

import java.io.File;

/**
 *
 */

public class SPDataManager {

    private static boolean sIsFirstStart = false;

    public static void init() {
        sIsFirstStart = SpUtils.obtain().getBoolean(SpConstant.INSTANCE.getIS_APP_FIRST_START(), true);
        SpUtils.obtain().save(SpConstant.INSTANCE.getIS_APP_FIRST_START(), false);
    }


    public static void setPhotoSaveLocation(String location) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getPHOTO_SAVE_LOCATION(), location);
    }

    public static String getPhotoSaveLocation() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getPHOTO_SAVE_LOCATION(), FileUtil.DICM_ROOT_PATH + File.separator + "Camera");
    }

    public static int getMaxTextureSize() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getMAX_TEXTURE_SIZE(), 0);
    }

    public static void setMaxTextureSize(int size) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getMAX_TEXTURE_SIZE(), size);
    }

    public static void setEditCount(int count) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getEDIT_COUNT(), count);
    }

    public static int getEditCount() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getEDIT_COUNT(), 0);
    }

    public static void setTakePictureCount(int count) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getTAKE_PICTURE_COUNT(), count);
    }

    public static int getTakePictureCount() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getTAKE_PICTURE_COUNT(), 0);
    }

    public static String getShareImageTool1PkgName() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL1_PKGNAME(), null);
    }

    public static String getShareImageTool1ActivityName() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME(), null);
    }

    public static String getShareImageTool2PkgName() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL2_PKGNAME(), null);
    }

    public static String getShareImageTool2ActivityName() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME(), null);
    }

    public static void setShareImageTool1PkgName(String name) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL1_PKGNAME(), name);
    }

    public static void setShareImageTool1ActivityName(String name) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME(), name);
    }

    public static void setShareImageTool2PkgName(String name) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL2_PKGNAME(), name);
    }

    public static void setShareImageTool2ActivityName(String name) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getLAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME(), name);
    }


    public static int getOpenAppCountToday() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getOPEN_APP_CNT_PER_DAY(), 0);
    }

    public static void setOpenAppCountToday(int count) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getOPEN_APP_CNT_PER_DAY(), count);
    }

    public static int getShareCountOfDay() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getSHARE_CNT_PER_DAY(), 0);
    }

    public static void setShareCountOfDay(int count) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getSHARE_CNT_PER_DAY(), count);
    }

    public static int getDownloadCountToday() {
        return SpUtils.obtain().getInt(SpConstant.INSTANCE.getDOWNLOAD_CNT_PER_DAY(), 0);
    }

    public static void setDownloadCountToday(int count) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getDOWNLOAD_CNT_PER_DAY(), count);
    }


    public static String getInsShareImgUri() {
        return SpUtils.obtain().getString(SpConstant.INSTANCE.getINS_SHARE_IMG_URI(), "");
    }

    public static void setInsShareImgUri(String imgUri) {
        if (TextUtils.isEmpty(imgUri)) {
            SpUtils.obtain().save(SpConstant.INSTANCE.getINS_SHARE_IMG_URI(), "");
        } else {
            SpUtils.obtain().save(SpConstant.INSTANCE.getINS_SHARE_IMG_URI(), getInsShareImgUri() + imgUri + "#");
        }
    }

    public static void setHasCheckPrivacy() {
        SpUtils.obtain().save(SpConstant.INSTANCE.getHAS_CHECK_PRIVACY(), true);
    }

    public static boolean hasCheckPrivacy() {
        return SpUtils.obtain().getBoolean(SpConstant.INSTANCE.getHAS_CHECK_PRIVACY(), false);
    }


    public static boolean isSaveVideoCollage() {
        return SpUtils.obtain().getBoolean(SpConstant.INSTANCE.getSAVE_VIDEO_COLLAGE(), false);
    }

    public static void setSaveVideoCollage(boolean save) {
        SpUtils.obtain().save(SpConstant.INSTANCE.getSAVE_VIDEO_COLLAGE(), save);
    }

}

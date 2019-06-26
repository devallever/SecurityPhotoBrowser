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
        sIsFirstStart = SpUtils.obtain().getBoolean(SpConstant.IS_APP_FIRST_START, true);
        SpUtils.obtain().save(SpConstant.IS_APP_FIRST_START, false);
    }


    public static void setPhotoSaveLocation(String location) {
        SpUtils.obtain().save(SpConstant.PHOTO_SAVE_LOCATION, location);
    }

    public static String getPhotoSaveLocation() {
        return SpUtils.obtain().getString(SpConstant.PHOTO_SAVE_LOCATION, FileUtil.DICM_ROOT_PATH + File.separator + "Camera");
    }

    public static int getMaxTextureSize() {
        return SpUtils.obtain().getInt(SpConstant.MAX_TEXTURE_SIZE, 0);
    }

    public static void setMaxTextureSize(int size) {
        SpUtils.obtain().save(SpConstant.MAX_TEXTURE_SIZE, size);
    }

    public static void setEditCount(int count) {
        SpUtils.obtain().save(SpConstant.EDIT_COUNT, count);
    }

    public static int getEditCount() {
        return SpUtils.obtain().getInt(SpConstant.EDIT_COUNT, 0);
    }

    public static void setTakePictureCount(int count) {
        SpUtils.obtain().save(SpConstant.TAKE_PICTURE_COUNT, count);
    }

    public static int getTakePictureCount() {
        return SpUtils.obtain().getInt(SpConstant.TAKE_PICTURE_COUNT, 0);
    }

    public static String getShareImageTool1PkgName() {
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL1_PKGNAME, null);
    }

    public static String getShareImageTool1ActivityName() {
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME, null);
    }

    public static String getShareImageTool2PkgName() {
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL2_PKGNAME, null);
    }

    public static String getShareImageTool2ActivityName() {
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME, null);
    }

    public static void setShareImageTool1PkgName(String name) {
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL1_PKGNAME, name);
    }

    public static void setShareImageTool1ActivityName(String name) {
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME, name);
    }

    public static void setShareImageTool2PkgName(String name) {
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL2_PKGNAME, name);
    }

    public static void setShareImageTool2ActivityName(String name) {
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME, name);
    }


    public static int getOpenAppCountToday() {
        return SpUtils.obtain().getInt(SpConstant.OPEN_APP_CNT_PER_DAY, 0);
    }

    public static void setOpenAppCountToday(int count) {
        SpUtils.obtain().save(SpConstant.OPEN_APP_CNT_PER_DAY, count);
    }

    public static int getShareCountOfDay() {
        return SpUtils.obtain().getInt(SpConstant.SHARE_CNT_PER_DAY, 0);
    }

    public static void setShareCountOfDay(int count) {
        SpUtils.obtain().save(SpConstant.SHARE_CNT_PER_DAY, count);
    }

    public static int getDownloadCountToday() {
        return SpUtils.obtain().getInt(SpConstant.DOWNLOAD_CNT_PER_DAY, 0);
    }

    public static void setDownloadCountToday(int count) {
        SpUtils.obtain().save(SpConstant.DOWNLOAD_CNT_PER_DAY, count);
    }


    public static String getInsShareImgUri() {
        return SpUtils.obtain().getString(SpConstant.INS_SHARE_IMG_URI, "");
    }

    public static void setInsShareImgUri(String imgUri) {
        if (TextUtils.isEmpty(imgUri)) {
            SpUtils.obtain().save(SpConstant.INS_SHARE_IMG_URI, "");
        } else {
            SpUtils.obtain().save(SpConstant.INS_SHARE_IMG_URI, getInsShareImgUri() + imgUri + "#");
        }
    }

    public static void setHasCheckPrivacy() {
        SpUtils.obtain().save(SpConstant.HAS_CHECK_PRIVACY, true);
    }

    public static boolean hasCheckPrivacy() {
        return SpUtils.obtain().getBoolean(SpConstant.HAS_CHECK_PRIVACY, false);
    }


    public static boolean isSaveVideoCollage() {
        return SpUtils.obtain().getBoolean(SpConstant.SAVE_VIDEO_COLLAGE, false);
    }

    public static void setSaveVideoCollage(boolean save) {
        SpUtils.obtain().save(SpConstant.SAVE_VIDEO_COLLAGE, save);
    }

}

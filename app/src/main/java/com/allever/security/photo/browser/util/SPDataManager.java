package com.allever.security.photo.browser.util;

import android.text.TextUtils;
import com.android.absbase.utils.SpUtils;

import java.io.File;

/**
 */

public class SPDataManager {

    private static boolean sIsFirstStart = false;

    public static void init() {
        sIsFirstStart = SpUtils.obtain().getBoolean(SpConstant.IS_APP_FIRST_START, true);
        SpUtils.obtain().save(SpConstant.IS_APP_FIRST_START, false);
    }

//    public static boolean isFirstStart() {
//        return sIsFirstStart;
//    }
//
//    public static boolean isInitDBDataSuccess() {
//        return SpUtils.obtain().getBoolean(SpConstant.INIT_DB_DATA_SUCCESS, false);
//    }
//
//    public static void setInitDBDataSuccess(boolean success) {
//        SpUtils.obtain().save(SpConstant.INIT_DB_DATA_SUCCESS, success);
//    }
//
//    public static void setLastScheduleServiceTime(long time) {
//        SpUtils.obtain().save(SpConstant.LAST_RUN_SCHEDULE_SERVICE_TIME, time);
//    }
//
//    public static long getLastScheduleServiceTime() {
//        return SpUtils.obtain().getLong(SpConstant.LAST_RUN_SCHEDULE_SERVICE_TIME, System.currentTimeMillis());
//    }
//
//    public static boolean isOpenScreenLocker() {
//        // 锁屏开关由服务器控制，买量默认开，自然默认会隐藏入口
//        return SpUtils.obtain().getBoolean(SpConstant.FLASH_SCREEN_LOCKER, true);
//    }
//
//    public static void setOpenScreenLocker(boolean isOpen) {
//        SpUtils.obtain().save(SpConstant.FLASH_SCREEN_LOCKER, isOpen);
//    }
//
//    /**
//     * 锁屏设置是否被用户修改过
//     * @return
//     */
//    public static boolean isScreenLockerModifyByUser() {
//        return SpUtils.obtain().getBoolean(SpConstant.SCREEN_LOCKER_MODIFY_BY_USER, false);
//    }
//
//    public static void setScreenLockerModifyByUser(boolean byUser) {
//        SpUtils.obtain().save(SpConstant.SCREEN_LOCKER_MODIFY_BY_USER, byUser);
//    }
//
//    public static boolean isTouchTakePhoto() {
//        return SpUtils.obtain().getBoolean(SpConstant.TOUCH_2_TAKE_PHOTO, false);
//    }
//
//    public static void setTouchTakePhoto(boolean able) {
//        SpUtils.obtain().save(SpConstant.TOUCH_2_TAKE_PHOTO, able);
//    }
//
//    public static boolean isCropSquare() {
//        return SpUtils.obtain().getBoolean(SpConstant.BITMAP_CROP_SQUARE, false);
//    }
//
//    public static void setCropSquare(boolean square) {
//        SpUtils.obtain().save(SpConstant.BITMAP_CROP_SQUARE, square);
//    }
//
//    public static boolean isCropRect() {
//        return SpUtils.obtain().getBoolean(SpConstant.BITMAP_CROP_RECT, true);
//    }
//
//    public static void setCropRect(boolean rect) {
//        SpUtils.obtain().save(SpConstant.BITMAP_CROP_RECT, rect);
//    }
//
//    public static int getOnlyTwoCollageRatio(int defaultValue) {
//        return SpUtils.obtain().getInt(SpConstant.ONLY_TWO_COLLAGE_RATIO, defaultValue);
//    }
//
//    public static void setOnlyTwoCollageRatio(int value) {
//        SpUtils.obtain().save(SpConstant.ONLY_TWO_COLLAGE_RATIO, value);
//    }

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

//    public static boolean isCollageWithFrame() {
//        return SpUtils.obtain().getBoolean(SpConstant.COLLAGE_WITH_FRAME, false);
//    }
//
//    public static void setCollageWithFrame(boolean value) {
//        SpUtils.obtain().save(SpConstant.COLLAGE_WITH_FRAME, value);
//    }
//
//    public static boolean isPlayShutterSound() {
//        return SpUtils.obtain().getBoolean(SpConstant.PLAY_SHUTTER_SOUND, true);
//    }
//
//    public static void setPlayShutterSounde(boolean play) {
//        SpUtils.obtain().save(SpConstant.PLAY_SHUTTER_SOUND, play);
//    }
//
//    public static int getTakeVideoCount() {
//        return SpUtils.obtain().getInt(SpConstant.TAKE_VIDEO_COUNT, 0);
//    }
//
//    public static void setTakeVideoCount(int count) {
//        SpUtils.obtain().save(SpConstant.TAKE_VIDEO_COUNT, count);
//    }
//
//    public static int getTakeMotionCount() {
//        return SpUtils.obtain().getInt(SpConstant.TAKE_MOTION_COUNT, 0);
//    }
//
//    public static void setTakeMotionCount(int count) {
//        SpUtils.obtain().save(SpConstant.TAKE_MOTION_COUNT, count);
//    }

    public static void setEditCount(int count) {
        SpUtils.obtain().save(SpConstant.EDIT_COUNT, count);
    }

    public static int getEditCount() {
        return SpUtils.obtain().getInt(SpConstant.EDIT_COUNT, 0);
    }

//    public static void setShareCount(int count) {
//        SpUtils.obtain().save(SpConstant.SHARE_COUNT, count);
//    }
//
//    public static int getShareCount() {
//        return SpUtils.obtain().getInt(SpConstant.SHARE_COUNT, 0);
//    }
//
//    public static void setShowLocation(boolean showLocation) {
//        SpUtils.obtain().save(SpConstant.TAKE_VIDEO_COUNT, showLocation);
//    }
//
//    public static boolean isShowLocation() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_LOCATION, false);
//    }
//
    public static void setTakePictureCount(int count) {
        SpUtils.obtain().save(SpConstant.TAKE_PICTURE_COUNT, count);
    }

    public static int getTakePictureCount() {
        return SpUtils.obtain().getInt(SpConstant.TAKE_PICTURE_COUNT, 0);
    }

//    public static boolean isHdrOn() {
//        return SpUtils.obtain().getBoolean(SpConstant.HDR_ON, false);
//    }
//
//    public static void setHdrOn(boolean on) {
//        SpUtils.obtain().save(SpConstant.HDR_ON, on);
//    }
//
//    public static boolean isVignetteOn() {
//        return SpUtils.obtain().getBoolean(SpConstant.VIGNETTE_ON, false);
//    }
//
//    public static void setVignetteOn(boolean on) {
//        SpUtils.obtain().save(SpConstant.VIGNETTE_ON, on);
//    }
//
//    public static int getBlurState() {
//        return SpUtils.obtain().getInt(SpConstant.BLUR_STATE, 0);
//    }
//
//    public static void setBlurState(int state) {
//        SpUtils.obtain().save(SpConstant.BLUR_STATE, state);
//    }
//
//
//    public static void setResolutionById(int cameraId, String resolution) {
//        SpUtils.obtain().save("camera_resolution_" + cameraId, resolution);
//    }
//
//    public static String getResolutionById(int cameraId) {
//        return SpUtils.obtain().getString("camera_resolution_" + cameraId, "");
//    }
//
//    public static String getVideoQualityById(int cameraId) {
//        return SpUtils.obtain().getString("video_quality_" + cameraId, "");
//    }
//
//    public static void setVideoQualityById(int cameraId, String quality) {
//        SpUtils.obtain().save("video_quality_" + cameraId, quality);
//    }
//
//    public static String getCameraSizesFingerPrint() {
//        return SpUtils.obtain().getString(SpConstant.CAMERA_SIZE_FINGERPRINT, "");
//    }
//
//    public static void setCameraSizesFingerPrint(String fingerPrint) {
//        SpUtils.obtain().save(SpConstant.CAMERA_SIZE_FINGERPRINT, fingerPrint);
//    }
//
//    public static String getVideoBitrate() {
//        return SpUtils.obtain().getString(SpConstant.VIDEO_BITRATE, "default");
//    }
//
//    public static void setVideoBitrate(String bitrage) {
//        SpUtils.obtain().save(SpConstant.VIDEO_BITRATE, bitrage);
//    }
//
//    public static String getVideoFps() {
//        return SpUtils.obtain().getString(SpConstant.VIDEO_FPS, "default");
//    }
//
//    public static void setVideoFps(String fps) {
//        SpUtils.obtain().save(SpConstant.VIDEO_FPS, fps);
//    }
//
//    public static String getPreviewSize() {
//        return SpUtils.obtain().getString(SpConstant.PREVIEW_SIZE, "preference_preview_size_from_display");
//        //return SpUtils.obtain().getString(SpConstant.PREVIEW_SIZE, "preference_preview_size_default");
//    }
//
//    public static void setPreviewSize(String size) {
//        SpUtils.obtain().save(SpConstant.PREVIEW_SIZE, size);
//    }
//
//    public static String getRotatePreview() {
//        return SpUtils.obtain().getString(SpConstant.ROTATE_PREVIEW, "0");
//    }
//
//    public static void setRotatePreview(String size) {
//        SpUtils.obtain().save(SpConstant.ROTATE_PREVIEW, size);
//    }
//
//    public static void setGridInfo(String gridInfo) {
//        SpUtils.obtain().save(SpConstant.GRID_INFO, gridInfo);
//    }
//
//    public static String getGridInfo() {
//        return SpUtils.obtain().getString(SpConstant.GRID_INFO, "");
//    }
//
//    public static void setShowMotionPressTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_MOTION_PRESS_TIP, show);
//    }
//
//    public static boolean getShowMotionPressTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_MOTION_PRESS_TIP, true);
//    }
//
//    public static String getFlashValue() {
//        return SpUtils.obtain().getString(SpConstant.FLASH_VALUE, "flash_off");
//    }
//
//    public static void setFlashValue(String value) {
//        SpUtils.obtain().save(SpConstant.FLASH_VALUE, value);
//    }
//
//    public static void setTakePhotoTimer(String time) {
//        SpUtils.obtain().save(SpConstant.TAKE_PHOTO_TIME, time);
//    }
//
//    public static String getTakePhotoTimer() {
//        return SpUtils.obtain().getString(SpConstant.TAKE_PHOTO_TIME, "0");
//    }
//
//    public static void setRecordAudioSrc(String src) {
//        SpUtils.obtain().save(SpConstant.RECORD_AUDIO_SRC, src);
//    }
//
//    public static String getRecordAudioSrc() {
//        return SpUtils.obtain().getString(SpConstant.RECORD_AUDIO_SRC, "audio_src_camcorder");
//    }
//
//    public static void setMirrorFrontCamera(boolean mirror) {
//        SpUtils.obtain().save(SpConstant.MIRROR_FRONT_CAMERA, mirror);
//    }
//
//    public static boolean getMirrorFrontCamera() {
//        return SpUtils.obtain().getBoolean(SpConstant.MIRROR_FRONT_CAMERA, true);
//    }
//
//    public static void setFrontCamera(boolean front) {
//        SpUtils.obtain().save(SpConstant.FRONT_CAMERA, front);
//    }
//
//    public static boolean isFrontCamera() {
//        return SpUtils.obtain().getBoolean(SpConstant.FRONT_CAMERA, false);
//    }
//
//    public static void setCollageDelayTime(long time) {
//        SpUtils.obtain().save(SpConstant.COLLAGE_DELAY_TIME, time);
//    }
//
//    public static long getCollageDelayTime() {
//        return SpUtils.obtain().getLong(SpConstant.COLLAGE_DELAY_TIME, 0);
//    }
//
//    public static void setShowThumbnailAnimation(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_THUMBNAIL_ANIMATION, show);
//    }
//
//    public static boolean getShowThumbnailAnimation() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_THUMBNAIL_ANIMATION, true);
//    }
//
//    public static void setGpsDirection(boolean show) {
//        SpUtils.obtain().save(SpConstant.GPS_DIRECTION, show);
//    }
//
//    public static boolean getGpsDirection() {
//        return SpUtils.obtain().getBoolean(SpConstant.GPS_DIRECTION, false);
//    }
//
//    public static void setImageQuality(String quality) {
//        SpUtils.obtain().save(SpConstant.IMAGE_QUALITY, quality);
//    }
//
//    public static String getImageQuality() {
//        return SpUtils.obtain().getString(SpConstant.IMAGE_QUALITY, "100");
//    }
//
//    public static void setShowTiltshiftClickTips(boolean show) {
//        SpUtils.obtain().save(SpConstant.TILTSHIFT_CLICK_TIPS, show);
//    }
//
//    public static boolean getShowTiltshiftClickTips() {
//        return SpUtils.obtain().getBoolean(SpConstant.TILTSHIFT_CLICK_TIPS, false);
//    }
//
//    public static void setHasNewFilter(boolean show) {
//        SpUtils.obtain().save(SpConstant.HAS_NEW_FILTER, show);
//    }
//
//    public static boolean getHasNewFilter() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_NEW_FILTER, true);
//    }
//
//    public static void setHasNewMarking(boolean show) {
//        SpUtils.obtain().save(SpConstant.HAS_NEW_MARKING, show);
//    }
//
//    public static boolean getHasNewMarking() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_NEW_MARKING, true);
//    }
//
//    public static void setFillInLight(boolean show) {
//        SpUtils.obtain().save(SpConstant.FILL_IN_LIGHT, show);
//    }
//
//    public static boolean getFillInLight() {
//        return SpUtils.obtain().getBoolean(SpConstant.FILL_IN_LIGHT, false);
//    }
//
//    public static void setBeautyHairColorNew(boolean show) {
//        SpUtils.obtain().save(SpConstant.BEAUTY_HAIR_COLOR_NEW, show);
//    }
//
//    public static boolean isBeautyHairColorNew() {
//        return SpUtils.obtain().getBoolean(SpConstant.BEAUTY_HAIR_COLOR_NEW, true);
//    }
//
//    public static void setShowSelectMultiPreviewTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_SELECT_MULTI_PREVIEW_TIP, show);
//    }
//
//    public static boolean isShowSelectMultiPreviewTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_SELECT_MULTI_PREVIEW_TIP, false);
//    }
//
//    public static void setShowSelectToEditPreviewTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_SELECT_TO_EDIT_PREVIEW_TIP, show);
//    }
//
//    public static boolean isShowSelectToEditPreviewTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_SELECT_TO_EDIT_PREVIEW_TIP, false);
//    }

    public static String getShareImageTool1PkgName(){
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL1_PKGNAME, null);
    }

    public static String getShareImageTool1ActivityName(){
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME, null);
    }

    public static String getShareImageTool2PkgName(){
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL2_PKGNAME, null);
    }

    public static String getShareImageTool2ActivityName(){
        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME, null);
    }

    public static void setShareImageTool1PkgName(String name){
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL1_PKGNAME, name);
    }

    public static void setShareImageTool1ActivityName(String name){
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME, name);
    }

    public static void setShareImageTool2PkgName(String name){
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL2_PKGNAME, name);
    }

    public static void setShareImageTool2ActivityName(String name){
        SpUtils.obtain().save(SpConstant.LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME, name);
    }

//    public static String getShareVideoTool1PkgName(){
//        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_VIDEO_TOOL1_PKGNAME, null);
//    }
//
//    public static String getShareVideoTool1ActivityName(){
//        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME, null);
//    }
//
//    public static String getShareVideoTool2PkgName(){
//        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_VIDEO_TOOL2_PKGNAME, null);
//    }
//
//    public static String getShareVideoTool2ActivityName(){
//        return SpUtils.obtain().getString(SpConstant.LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME, null);
//    }
//
//    public static void setShareVideoTool1PkgName(String name){
//        SpUtils.obtain().save(SpConstant.LAST_SHARE_VIDEO_TOOL1_PKGNAME, name);
//    }
//
//    public static void setShareVideoTool1ActivityName(String name){
//        SpUtils.obtain().save(SpConstant.LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME, name);
//    }
//
//    public static void setShareVideoTool2PkgName(String name){
//        SpUtils.obtain().save(SpConstant.LAST_SHARE_VIDEO_TOOL2_PKGNAME, name);
//    }
//
//    public static void setShareVideoTool2ActivityName(String name){
//        SpUtils.obtain().save(SpConstant.LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME, name);
//    }
//
//    public static void setCollageNew(boolean show) {
//        SpUtils.obtain().save(SpConstant.COLLAGE_NEW, show);
//    }
//
//    public static boolean getCollageNew() {
//        return SpUtils.obtain().getBoolean(SpConstant.COLLAGE_NEW, false);
//    }
//
//    public static void setEditMagazineNew(boolean show) {
//        SpUtils.obtain().save(SpConstant.EDIT_MAGAZINE_NEW, show);
//    }
//
//    public static boolean getEditMagazineNew() {
//        return SpUtils.obtain().getBoolean(SpConstant.EDIT_MAGAZINE_NEW, false);
//    }
//
//    public static void setShowOtherBeautyRedIcon(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_OTHER_BEAUTY_NEW_ICON, show);
//    }
//
//    public static boolean getShowOtherBeautyRedIcon() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_OTHER_BEAUTY_NEW_ICON, false);
//    }
//
//    public static void setEditShowHairColorTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.EDIT_SHOW_HAIR_COLOR_TIP, show);
//    }
//
//    public static boolean getEditShowHairColorTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.EDIT_SHOW_HAIR_COLOR_TIP, false);
//    }
//
//    public static void setShowPreviewEditRedIcon(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_PREVIEW_EDIT_RED_ICON, show);
//    }
//
//    public static boolean getShowPreviewEditRedIcon() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_PREVIEW_EDIT_RED_ICON, false);
//    }
//
//    public static void setShowImgPreviewDynamicTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_IMG_PREVIEW_DYNAMIC_TIP, show);
//    }
//
//    public static boolean getShowImgPreviewDynamicTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_IMG_PREVIEW_DYNAMIC_TIP, false);
//    }
//
//    public static void saveInitCameraId(int id) {
//        SpUtils.obtain().save(SpConstant.INIT_CAMERA_ID, id);
//    }
//
//    public static int getInitCameraId() {
//        return SpUtils.obtain().getInt(SpConstant.INIT_CAMERA_ID, 1);
//    }
//
//    /**
//     * 评分引导相关
//     *
//     */
//    public static long getFirstInTimeOfDay(){
//        return SpUtils.obtain().getLong(SpConstant.FIRST_IN_TIME_OF_DAY);
//    }
//
//    public static void setFirstInTimeOfDay(long time){
//        SpUtils.obtain().save(SpConstant.FIRST_IN_TIME_OF_DAY, time);
//    }

    public static int getOpenAppCountToday(){
        return SpUtils.obtain().getInt(SpConstant.OPEN_APP_CNT_PER_DAY, 0);
    }

    public static void setOpenAppCountToday(int count){
        SpUtils.obtain().save(SpConstant.OPEN_APP_CNT_PER_DAY, count);
    }

    public static int getShareCountOfDay(){
        return SpUtils.obtain().getInt(SpConstant.SHARE_CNT_PER_DAY, 0);
    }

    public static void setShareCountOfDay(int count){
        SpUtils.obtain().save(SpConstant.SHARE_CNT_PER_DAY, count);
    }

    public static int getDownloadCountToday(){
        return SpUtils.obtain().getInt(SpConstant.DOWNLOAD_CNT_PER_DAY, 0);
    }

    public static void setDownloadCountToday(int count){
        SpUtils.obtain().save(SpConstant.DOWNLOAD_CNT_PER_DAY, count);
    }

//    public static void setFilterBuy(String categoryId){
//        String buy = SpUtils.obtain().getString(SpConstant.ALREADY_BUY_FILTER_ID,"");
//        if(TextUtils.isEmpty(buy)){
//            buy = categoryId;
//        } else {
//            buy += "#" + categoryId;
//        }
//        SpUtils.obtain().save(SpConstant.ALREADY_BUY_FILTER_ID,buy);
//    }
//
//    public static String getFilterBuy(){
//        return SpUtils.obtain().getString(SpConstant.ALREADY_BUY_FILTER_ID,"");
//    }

    /**
     * 拍照或录制视频后，是否立即保持图片到相册
     * 1.03版本改成false，跳转到暂存编辑页面
     */
//    public static boolean getSaveImgImmediately(){
//        return SpUtils.obtain().getBoolean(SpConstant.SAVE_IMG_IMMEDIATELY, false);
//    }
//
//    public static void setSaveImgImmediately(boolean save){
//        SpUtils.obtain().save(SpConstant.SAVE_IMG_IMMEDIATELY, save);
//    }
//
//    public static void setHolidayDBVersion(int version){
//        SpUtils.obtain().save(SpConstant.HOLIDAY_DB_VERSION, version);
//    }
//
//    public static int getHolidayDBVersion(){
//        return SpUtils.obtain().getInt(SpConstant.HOLIDAY_DB_VERSION, 0);
//    }
//
//    public static String getGifRecently(){
//        return SpUtils.obtain().getString(SpConstant.GIF_RECENTLY, "");
//    }
//
//    public static void setGifRecently(String gifRecently){
//        SpUtils.obtain().save(SpConstant.GIF_RECENTLY, gifRecently);
//    }

    public static String getInsShareImgUri(){
        return SpUtils.obtain().getString(SpConstant.INS_SHARE_IMG_URI, "");
    }

    public static void setInsShareImgUri(String imgUri){
        if(TextUtils.isEmpty(imgUri)){
            SpUtils.obtain().save(SpConstant.INS_SHARE_IMG_URI, "");
        } else {
            SpUtils.obtain().save(SpConstant.INS_SHARE_IMG_URI, getInsShareImgUri() + imgUri + "#");
        }
    }

//    public static void setBasicVideoInit(){
//        SpUtils.obtain().save("BasicVideoInit", true);
//    }
//
//    public static boolean isBasicVideoInit(){
//        return SpUtils.obtain().getBoolean("BasicVideoInit", false);
//    }
//
//    public static void setHasSaveGif(){
//        SpUtils.obtain().save("HasSaveGif", true);
//    }
//
//    public static boolean hasSaveGif(){
//        return SpUtils.obtain().getBoolean("HasSaveGif", false);
//    }

    public static void setHasCheckPrivacy(){
        SpUtils.obtain().save(SpConstant.HAS_CHECK_PRIVACY, true);
    }

    public static boolean hasCheckPrivacy(){
        return SpUtils.obtain().getBoolean(SpConstant.HAS_CHECK_PRIVACY, false);
    }

//    public static boolean showMainFuncListGuide() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_MAIN_FUNC_LIST_GUIDE_TIP, true);
//    }
//
//    public static void setShowMainFuncListGuide(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_MAIN_FUNC_LIST_GUIDE_TIP, show);
//    }
//
//    public static int getDownloadCountOfDay() {
//        return SpUtils.obtain().getInt(SpConstant.STORE_DOWNLOAD_COUNT_OF_DAY, 0);
//    }
//
//    public static void addDownloadCountOfDay() {
//        int i = getDownloadCountOfDay();
//        SpUtils.obtain().save(SpConstant.STORE_DOWNLOAD_COUNT_OF_DAY, ++i);
//    }
//
//    public static void resetDownloadCountOfDay() {
//        SpUtils.obtain().save(SpConstant.STORE_DOWNLOAD_COUNT_OF_DAY, 0);
//    }
//
//    public static boolean hasUseBoobJob() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_BOOBJOB, false);
//    }
//
//    public static void setHasUseBoobJob(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_BOOBJOB, use);
//    }
//
//    public static boolean hasUseButtocks() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_BUTTOCKS, false);
//    }
//
//    public static void setHasUseButtocks(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_BUTTOCKS, use);
//    }
//
//    public static boolean hasUseCollage() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_COLLAGE, false);
//    }
//
//    public static void setHasUseCollage(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_COLLAGE, use);
//    }
//
//    public static boolean hasUseGifSticker() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_GIF_STICKER, false);
//    }
//
//    public static void setHasUseGifSticker(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_GIF_STICKER, use);
//    }
//
//    public static boolean canShowGifSticker() {
//        return SpUtils.obtain().getBoolean(SpConstant.CAN_SHOW_GIF_STICKER, false);
//    }
//
//    public static void setCanShowGifSticker(boolean can) {
//        SpUtils.obtain().save(SpConstant.CAN_SHOW_GIF_STICKER, can);
//    }
//
//    public static boolean hasUseSticker() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_STICKER, false);
//    }
//
//    public static void setHasUseSticker(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_STICKER, use);
//    }
//
//    public static boolean hasUseHealing() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_HEALING, false);
//    }
//
//    public static void setHasUseHealing(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_HEALING, use);
//    }
//
//    public static boolean hasUseWhiten() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_WHITEN, false);
//    }
//
//    public static void setHasUseWhiten(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_WHITEN, use);
//    }
//
//    public static boolean hasUseSmooth() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_SMOOTH, false);
//    }
//
//    public static void setHasUseSmooth(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_SMOOTH, use);
//    }
//
//    public static boolean hasUseGlow() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_GLOW, false);
//    }
//
//    public static void setHasUseGlow(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_GLOW, use);
//    }
//
//    public static boolean hasUseHairColor() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_HAIR_COLOR, false);
//    }
//
//    public static void setHasUseHairColor(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_HAIR_COLOR, use);
//    }
//
//    public static boolean hasUseLipstick() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_LIPSTICK, false);
//    }
//
//    public static void setHasUseLipstick(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_LIPSTICK, use);
//    }
//
//    public static boolean hasUseEyeTuner() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_EYE_TUNER, false);
//    }
//
//    public static void setHasUseEyeTuner(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_EYE_TUNER, use);
//    }
//
//    public static boolean hasUseTaller() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_TALLER, false);
//    }
//
//    public static void setHasUseTaller(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_TALLER, use);
//    }
//
//    public static void saveLastShowTimeMainGuide() {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_TIME_MAIN_GUIDE, System.currentTimeMillis());
//    }
//
//    public static long getLastShowTimeMainGuide() {
//        return SpUtils.obtain().getLong(SpConstant.LAST_SHOW_TIME_MAIN_GUIDE, 0);
//    }
//
//    public static boolean hasUseMixer() {
//        return SpUtils.obtain().getBoolean(SpConstant.HAS_USE_MIXER, false);
//    }
//
//    public static void setHasUseMixer(boolean use) {
//        SpUtils.obtain().save(SpConstant.HAS_USE_MIXER, use);
//    }
//
//    public static boolean needShowByond30sVideoTip() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_TIP_SEL_BEYOND_30S_VIDEO, true);
//    }
//
//    public static void setShowByond30sVideoTip(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_TIP_SEL_BEYOND_30S_VIDEO, show);
//    }
//
//    public static boolean notifyStoreUpdateNeedPush() {
//        return SpUtils.obtain().getBoolean(SpConstant.NOTIFY_STORE_UPDATE_NEED_PUSH, false);
//    }
//
//    public static void notifyStoreUpdateNeedPush(boolean need) {
//        SpUtils.obtain().save(SpConstant.NOTIFY_STORE_UPDATE_NEED_PUSH, need);
//    }
//
//    public static String getNotifyStoreUpdateKey() {
//        return SpUtils.obtain().getString(SpConstant.NOTIFY_STORE_UPDATE_KEY, "");
//    }
//
//    public static void setNotifyStoreUpdateKey(String key) {
//        SpUtils.obtain().save(SpConstant.NOTIFY_STORE_UPDATE_KEY, key);
//    }
//
//    public static String getNotifyStoreUpdateIcon() {
//        return SpUtils.obtain().getString(SpConstant.NOTIFY_STORE_UPDATE_LARGE_ICON, "");
//    }
//
//    public static void setNotifyStoreUpdateIcon(String icon) {
//        SpUtils.obtain().save(SpConstant.NOTIFY_STORE_UPDATE_LARGE_ICON, icon);
//    }
//
//    public static long getNotifyStoreUpdateLastShowTime() {
//        return SpUtils.obtain().getLong(SpConstant.NOTIFY_STORE_UPDATE_SHOW_TIME, 0);
//    }
//
//    public static void setNotifyStoreUpdateLastShowTime(long time) {
//        SpUtils.obtain().save(SpConstant.NOTIFY_STORE_UPDATE_SHOW_TIME, time);
//    }
//
//    public static long getLastUseAppTime() {
//        return SpUtils.obtain().getLong(SpConstant.LAST_USE_APP_TIME, System.currentTimeMillis());
//    }
//
//    public static void setLastUseAppTime(long time) {
//        SpUtils.obtain().save(SpConstant.LAST_USE_APP_TIME, time);
//    }
//
//    public static long getLastSlientUserNotifyTime() {
//        return SpUtils.obtain().getLong(SpConstant.LAST_SLIENT_USER_NOTIFY_TIME, System.currentTimeMillis());
//    }
//
//    public static void setLastSlientUserNotifyTime(long time) {
//        SpUtils.obtain().save(SpConstant.LAST_SLIENT_USER_NOTIFY_TIME, time);
//    }
//
//    public static boolean isClickCollageBtn() {
//        return SpUtils.obtain().getBoolean(SpConstant.CLICK_COLLAGE_BTN, false);
//    }
//
//    public static void setClickCollageBtn(boolean click) {
//        SpUtils.obtain().save(SpConstant.CLICK_COLLAGE_BTN, click);
//    }
//
//    public static int getNotifyCollageTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_COLLAGE_TIME, 0);
//    }
//
//    public static void setNotifyCollageTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_COLLAGE_TIME, time);
//    }
//
    public static boolean isSaveVideoCollage() {
        return SpUtils.obtain().getBoolean(SpConstant.SAVE_VIDEO_COLLAGE, false);
    }

    public static void setSaveVideoCollage(boolean save) {
        SpUtils.obtain().save(SpConstant.SAVE_VIDEO_COLLAGE, save);
    }
//
//    public static int getNotifyVideoCollageTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_VIDEO_COLLAGE_TIME, 0);
//    }
//
//    public static void setNotifyVideoCollageTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_VIDEO_COLLAGE_TIME, time);
//    }
//
//    public static boolean isClickFressStyleBtn() {
//        return SpUtils.obtain().getBoolean(SpConstant.CLICK_FREESTYLE_BTN, false);
//    }
//
//    public static void setClickFressStyleBtn(boolean click) {
//        SpUtils.obtain().save(SpConstant.CLICK_FREESTYLE_BTN, click);
//    }
//
//    public static int getNotifyFreeStyleTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_FREESTYLE_TIME, 0);
//    }
//
//    public static void setNotifyFreeStyleTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_FREESTYLE_TIME, time);
//    }
//
//    public static boolean isSaveDynamicTemplate() {
//        return SpUtils.obtain().getBoolean(SpConstant.SAVE_DYNAMIC_TEMPLATE, false);
//    }
//
//    public static void setSaveDynamicTemplate(boolean save) {
//        SpUtils.obtain().save(SpConstant.SAVE_DYNAMIC_TEMPLATE, save);
//    }
//
//    public static int getNotifyDynamicTemplateTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME, 0);
//    }
//
//    public static void setNotifyDynamicTemplateTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME, time);
//    }
//
//    public static boolean isClickCropBtn() {
//        return SpUtils.obtain().getBoolean(SpConstant.CLICK_CROP_BTN, false);
//    }
//
//    public static void setClickCropBtn(boolean click) {
//        SpUtils.obtain().save(SpConstant.CLICK_CROP_BTN, click);
//    }
//
//    public static int getNotifyCropTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_CROP_TIME, 0);
//    }
//
//    public static void setNotifyCropTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_CROP_TIME, time);
//    }
//
//    public static boolean isSaveGif() {
//        return SpUtils.obtain().getBoolean(SpConstant.SAVE_GIF, false);
//    }
//
//    public static void setSaveGif(boolean save) {
//        SpUtils.obtain().save(SpConstant.SAVE_GIF, save);
//    }
//
//    public static int getNotifySaveGifTime() {
//        return SpUtils.obtain().getInt(SpConstant.SHOW_NOTIFY_GIF_TIME, 0);
//    }
//
//    public static void setNotifySaveGifTime(int time) {
//        SpUtils.obtain().save(SpConstant.SHOW_NOTIFY_GIF_TIME, time);
//    }
//
//    public static boolean isShowVideoCollageGuideAgain() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_VIDEO_COLLAGE_GUIDE_AGAIN, false);
//    }
//
//    public static void setShowVideoCollageGuideAgain(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_VIDEO_COLLAGE_GUIDE_AGAIN, show);
//    }
//
//    public static boolean isShowGifGuideAgain() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_VIDEO_GIF_GUIDE_AGAIN, false);
//    }
//
//    public static void setShowGifGuideAgain(boolean show) {
//        SpUtils.obtain().save(SpConstant.SHOW_VIDEO_GIF_GUIDE_AGAIN, show);
//    }
//
//    public static void setShowHolidayNotifTime(long time) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_NOTIF_TIME, time);
//    }
//
//    public static long getLastShowHolidayNotifTime() {
//        return SpUtils.obtain().getLong(SpConstant.LAST_SHOW_HOLIDAY_NOTIF_TIME, 0);
//    }
//
//    public static void setShowHolidayNotifTitle(String title) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_TITLE, title);
//    }
//
//    public static String getShowHolidayNotifTitle() {
//        return SpUtils.obtain().getString(SpConstant.LAST_SHOW_HOLIDAY_TITLE, App.getContext().getResources().getString(R.string.notify_holiday_title));
//    }
//
//    public static void setShowHolidayNotifContent(String title) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_CONTENT, title);
//    }
//
//    public static String getShowHolidayNotifContent() {
//        return SpUtils.obtain().getString(SpConstant.LAST_SHOW_HOLIDAY_CONTENT, App.getContext().getResources().getString(R.string.notify_holiday_content));
//    }
//
//    public static void setShowHolidayNotifDayOfWeek(int dayOfWeek) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_DAY_OF_WEEK, dayOfWeek);
//    }
//
//    public static int getShowHolidayNotifDayOfWeek() {
//        return SpUtils.obtain().getInt(SpConstant.LAST_SHOW_HOLIDAY_DAY_OF_WEEK, 6);
//    }
//
//    public static void setShowHolidayNotifStartHour(int startHour) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_HOUR_START, startHour);
//    }
//
//    public static int getShowHolidayNotifStartHour() {
//        return SpUtils.obtain().getInt(SpConstant.LAST_SHOW_HOLIDAY_HOUR_START, 19);
//    }
//
//    public static void setShowHolidayNotifEndHour(int startHour) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_HOUR_END, startHour);
//    }
//
//    public static int getShowHolidayNotifEndHour() {
//        return SpUtils.obtain().getInt(SpConstant.LAST_SHOW_HOLIDAY_HOUR_END, 21);
//    }
//
//    public static void setShowHolidayNotifIcon(String iconUrl) {
//        SpUtils.obtain().save(SpConstant.LAST_SHOW_HOLIDAY_ICON, iconUrl);
//    }
//
//    public static String getShowHolidayNotifIcon() {
//        return SpUtils.obtain().getString(SpConstant.LAST_SHOW_HOLIDAY_ICON, "");
//    }
//
//    public static void setEnableNotifacation(boolean enable) {
//        SpUtils.obtain().save(SpConstant.ENABLE_NOTIFICATION, enable);
//    }
//
//    public static boolean enableNotifacation() {
//        return SpUtils.obtain().getBoolean(SpConstant.ENABLE_NOTIFICATION, true);
//    }
//    public static int getLastBannerPosition() {
//        return SpUtils.obtain().getInt(SpConstant.LAST_BANNER_POSITION, -1);
//    }
//
//    public static void setLastBannerPosition(int pos) {
//        SpUtils.obtain().save(SpConstant.LAST_BANNER_POSITION, pos);
//    }
//
//    public static boolean isShowDefaultBanner() {
//        return SpUtils.obtain().getBoolean(SpConstant.SHOW_DEFAULT_BANNER, false);
//    }
//
//    public static void setHasShowDefaultBanner() {
//        SpUtils.obtain().save(SpConstant.SHOW_DEFAULT_BANNER, false);
//    }
}

package com.allever.security.photo.browser.util;

/**
 *
 */

public class SpConstant {

    /**
     * 是否第一次启动APP
     */
    public static final String IS_APP_FIRST_START = "IS_APP_FIRST_START";

    /**
     * 初始化数据库是否成功
     */
    public static final String INIT_DB_DATA_SUCCESS = "INIT_DB_DATA_SUCCESS";

    /**
     * 程序安装时间,包括更新时间
     */
    public static final String APP_INSTALL_TIME = "APP_INSTALL_TIME";

    /**
     * 是否显示协议页
     */
    public static final String SHOW_PROTOCOL_PAGE = "SHOW_PROTOCOL_PAGE";

    /**
     * 最后一次检查更新的时间，milliSeconds
     */
    public static final String LAST_UPDATE_MILLISECONDS = "LAST_UPDATE_MILLISECONDS";

    /**
     * 最后一次更新的信息id
     */
    public static final String LAST_UPDATE_INFO_ID = "LAST_UPDATE_INFO_ID";

    /**
     * 上次定时service执行时间
     */
    public static final String LAST_RUN_SCHEDULE_SERVICE_TIME = "LAST_RUN_SCHEDULE_SERVICE_TIME";

    /**
     * 是否开启工具锁屏
     */
    public static final String FLASH_SCREEN_LOCKER = "FLASH_SCREEN_LOCKER";

    /**
     * 工具锁是否被用户修改过
     */
    public static final String SCREEN_LOCKER_MODIFY_BY_USER = "SCREEN_LOCKER_MODIFY_BY_USER";

    /// 评分引导相关
    /**
     * 已引导用户跳转到gp的版本号
     */
    public static final String RATE_DIALOG_CONFIRM_VERSION_NAME = "RATE_DIALOG_CONFIRM_VERSION_NAME";
    /**
     * 是否已引导用户跳转到ins主页
     */
    public static final String FOLLOW_INS_DIALOG_CONFIRM = "FOLLOW_INS_DIALOG_CONFIRM";
    /**
     * 是否已引导用户跳转分享
     */
    public static final String SHARE_DIALOG_CONFIRM = "SHARE_DIALOG_CONFIRM";

    /**
     * 评分引导弹框条件 每天起始时间为凌晨0点
     */
    public static final String CURRENT_DAY = "CURRENT_DAY";

    /**
     * 评分引导弹框条件 每天进入应用次数
     */
    public static final String RATE_DIALOG_SHOWN_TIMES = "RATE_DIALOG_SHOWN_TIMES";

    /**
     * 评分引导弹框最后一次弹框时间
     */
    public static final String RATE_DIALOG_LAST_SHOWN_TIME = "RATE_DIALOG_LAST_SHOWN_TIME";

    /**
     * 评分引导弹框条件 每天进入应用次数
     */
    public static final String OPEN_APP_CNT_PER_DAY = "OPEN_APP_CNT_PER_DAY";

    /**
     * 评分引导弹框条件 每天分享次数
     */
    public static final String SHARE_CNT_PER_DAY = "OPERATE_APP_CNT_PER_DAY";

    /**
     * 评分引导弹框条件 每天下载次数
     */
    public static final String DOWNLOAD_CNT_PER_DAY = "DOWNLOAD_CNT_PER_DAY";

    /**
     * 当天第一次进入应用的时间
     */
    public static final String FIRST_IN_TIME_OF_DAY = "FIRST_IN_TIME_OF_DAY";

    /**
     * 评分引导弹框条件 每天优化次数
     */
    public static final String OPTIMIZE_TIMES_CNT_PER_DAY = "OPTIMIZE_TIMES_CNT_PER_DAY";

    /**
     * 子包评分引导最后一次展示的时间
     */
    public static final String RATE_SUBPACKAGE_LAST_SHOWN_TIMES = "RATE_SUBPACKAGE_LAST_SHOWN_TIMES";

    /**
     * 子包评分引导展示次数后缀
     */
    public static final String RATE_SUBPACKAGE_SHOWN_TIMES_SUFFIX = "_SHOWN_TIMES";

    /**
     * 子包评分引导点击过跳转gp的版本，有值说明点击过跳转
     */
    public static final String RATE_SUBPACKAGE_CLICK_GIVE_5_STARS_VERSION_SUFFIX = "_CLICK_TO_GP_VERSION";

    /**
     * 是否点击拍照片
     */
    public static final String TOUCH_2_TAKE_PHOTO = "TOUCH_2_TAKE_PHOTO";

    /**
     * 是否裁剪成正方形
     */
    public static final String BITMAP_CROP_SQUARE = "BITMAP_CROP_SQUARE";

    /**
     * 是否裁剪成矩形
     */
    public static final String BITMAP_CROP_RECT = "BITMAP_CROP_RECT";

    public static final String ONLY_TWO_COLLAGE_RATIO = "ONLY_TWO_COLLAGE_RATIO";

    /**
     * 拍照图片保存路径
     */
    public static final String PHOTO_SAVE_LOCATION = "PHOTO_SAVE_LOCATION";

    /**
     * 获取最大纹理，0为未初始化
     */
    public static final String MAX_TEXTURE_SIZE = "MAX_TEXTURE_SIZE";

    public static final String COLLAGE_WITH_FRAME = "COLLAGE_WITH_FRAME";

    public static final String PLAY_SHUTTER_SOUND = "PLAY_SHUTTER_SOUND";

    public static final String TAKE_VIDEO_COUNT = "TAKE_VIDEO_COUNT";

    public static final String TAKE_MOTION_COUNT = "TAKE_MOTION_COUNT";

    public static final String EDIT_COUNT = "EDIT_COUNT";

    public static final String SHARE_COUNT = "SHARE_COUNT";

    public static final String SHOW_LOCATION = "SHOW_LOCATION";

    public static final String TAKE_PICTURE_COUNT = "TAKE_PICTURE_COUNT";

    public static final String HDR_ON = "HDR_ON";

    public static final String VIGNETTE_ON = "VIGNETTE_ON";

    public static final String BLUR_STATE = "BLUR_STATE";

    public static final String CAMERA_SIZE_FINGERPRINT = "CAMERA_SIZE_FINGERPRINT";

    public static final String VIDEO_BITRATE = "VIDEO_BITRATE";

    public static final String VIDEO_FPS = "VIDEO_FPS";

    public static final String PREVIEW_SIZE = "PREVIEW_SIZE";

    public static final String ROTATE_PREVIEW = "ROTATE_PREVIEW";

    public static final String GRID_INFO = "GRID_INFO";

    public static final String SHOW_MOTION_PRESS_TIP = "SHOW_MOTION_PRESS_TIP";

    public static final String FLASH_VALUE = "FLASH_VALUE";

    public static final String TAKE_PHOTO_TIME = "TAKE_PHOTO_TIME";

    public static final String RECORD_AUDIO_SRC = "RECORD_AUDIO_SRC";

    public static final String MIRROR_FRONT_CAMERA = "MirrorFrontCamera";

    public static final String FRONT_CAMERA = "FrontCamera";

    public static final String COLLAGE_DELAY_TIME = "COLLAGE_DELAY_TIME";

    public static final String SHOW_THUMBNAIL_ANIMATION = "SHOW_THUMBNAIL_ANIMATION";

    public static final String GPS_DIRECTION = "GpsDirection";

    public static final String IMAGE_QUALITY = "IMAGE_QUALITY";

    /**
     * 是否显示移轴提示
     */
    public static final String TILTSHIFT_CLICK_TIPS = "TILTSHIFT_CLICK_TIPS";

    public static final String HAS_NEW_FILTER = "HAS_NEW_FILTER";

    public static final String HAS_NEW_MARKING = "HAS_NEW_MARKING";

    public static final String FILL_IN_LIGHT = "FILL_IN_LIGHT";

    public static final String SHOW_MOTION_PLAY_TIP = "ShowMotionPlayTip";

    public static final String BEAUTY_HAIR_COLOR_NEW = "BEAUTY_HAIR_COLOR_NEW";

    public static final String SHOW_SELECT_MULTI_PREVIEW_TIP = "SHOW_SELECT_MULTI_PREVIEW_TIP";

    public static final String SHOW_SELECT_TO_EDIT_PREVIEW_TIP = "SHOW_SELECT_TO_EDIT_PREVIEW_TIP";

    public static final String LAST_SHARE_IMAGE_TOOL1_PKGNAME = "LAST_SHARE_IMAGE_TOOL_ONE_PKGNAME";
    public static final String LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME = "LAST_SHARE_IMAGE_TOOL_ONE_ACTIVITY_NAME";
    public static final String LAST_SHARE_IMAGE_TOOL2_PKGNAME = "LAST_SHARE_IMAGE_TOOL_TWO_PKGNAME";
    public static final String LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME = "LAST_SHARE_IMAGE_TOOL_TWO_ACTIVITY_NAME";

    public static final String LAST_SHARE_VIDEO_TOOL1_PKGNAME = "LAST_SHARE_VIDEO_TOOL1_PKGNAME";
    public static final String LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME = "LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME";
    public static final String LAST_SHARE_VIDEO_TOOL2_PKGNAME = "LAST_SHARE_VIDEO_TOOL2_PKGNAME";
    public static final String LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME = "LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME";

    public static final String COLLAGE_NEW = "COLLAGE_NEW";

    public static final String EDIT_PICTURE_COUNT = "EDIT_PICTURE_COUNT";

    public static final String EDIT_MAGAZINE_NEW = "EDIT_MAGAZINE_NEW";

    public static final String SHOW_OTHER_BEAUTY_NEW_ICON = "SHOW_OTHER_BEAUTY_NEW_ICON";

    public static final String EDIT_SHOW_HAIR_COLOR_TIP = "EDIT_SHOW_HAIR_COLOR_TIP";

    public static final String SHOW_PREVIEW_EDIT_RED_ICON = "SHOW_PREVIEW_EDIT_RED_ICON";

    public static final String SHOW_IMG_PREVIEW_DYNAMIC_TIP = "SHOW_IMG_PREVIEW_DYNAMIC_TIP";

    public static final String INIT_CAMERA_ID = "INIT_CAMERA_ID";

    public static final String FIRST_CAMERA_RADIO = "FIRST_CAMERA_RADIO";

    public static final String ALREADY_BUY_FILTER_ID = "ALREADY_BUY_FILTER_ID";

    /**
     * 拍照或录制视频后，是否立即保持图片到相册
     */
    public static final String SAVE_IMG_IMMEDIATELY = "SAVE_IMG_IMMEDIATELY";
    public static final String HOLIDAY_DB_VERSION = "HOLIDAY_DB_VERSION";

    //最近使用的gif
    public static final String GIF_RECENTLY = "GIF_RECENTLY";

    //ins分享过的图片uri 1:1  用于删除
    public static final String INS_SHARE_IMG_URI = "INS_SHARE_IMG_URI";

    public static final String HAS_CHECK_PRIVACY = "ALREADY_CHECK_PRIVACY";

    public static final String SHOW_MAIN_FUNC_LIST_GUIDE_TIP = "SHOW_MAIN_FUNC_LIST_GUIDE_TIP";

    public static final String STORE_DOWNLOAD_COUNT_OF_DAY = "STORE_DOWNLOAD_COUNT_OF_DAY";

    public static final String HAS_USE_BOOBJOB = "HAS_USE_BOOBJOB";

    public static final String HAS_USE_BUTTOCKS = "HAS_USE_BUTTOCKS";

    public static final String HAS_USE_COLLAGE = "HAS_USE_COLLAGE";

    public static final String HAS_USE_GIF_STICKER = "HAS_USE_GIF_STICKER";
    public static final String CAN_SHOW_GIF_STICKER = "CAN_SHOW_GIF_STICKER";

    public static final String HAS_USE_STICKER = "HAS_USE_STICKER";
    //美颜引导
    public static final String HAS_USE_HEALING = "HAS_USE_HEALING";
    public static final String HAS_USE_WHITEN = "HAS_USE_WHITEN";
    public static final String HAS_USE_SMOOTH = "HAS_USE_SMOOTH";
    public static final String HAS_USE_GLOW = "HAS_USE_GLOW";
    public static final String HAS_USE_HAIR_COLOR = "HAS_USE_HAIR_COLOR";
    public static final String HAS_USE_LIPSTICK = "HAS_USE_LIPSTICK";
    public static final String HAS_USE_EYE_TUNER = "HAS_USE_EYE_TUNER";
    public static final String HAS_USE_TALLER = "HAS_USE_TALLER";

    public static final String HAS_USE_MIXER = "HAS_USE_MIXER";

    public static final String LAST_SHOW_TIME_MAIN_GUIDE = "LAST_SHOW_TIME_MAIN_GUIDE";

//    public static final String IS_FORCE_UPDATE = "IS_FORCE_UPDATE";
//    public static final String FORCE_UPDATE_PKGNAME = "FORCE_UPDATE_PKGNAME";
//    public static final String FORCE_UPDATE_TITLE = "FORCE_UPDATE_TITLE";
//    public static final String FORCE_UPDATE_CONTENT = "FORCE_UPDATE_CONTENT";
//    public static final String FORCE_UPDATE_URL = "FORCE_UPDATE_URL";
//    public static final String FORCE_UPDATE_VERSION = "FORCE_UPDATE_VERSION";

    public static final String SHOW_TIP_SEL_BEYOND_30S_VIDEO = "SHOW_TIP_SEL_BEYOND_30S_VIDEO";

    public static final String NOTIFY_STORE_UPDATE_SHOW_TIME = "NOTIFY_STORE_UPDATE_SHOW_TIME";
    public static final String NOTIFY_STORE_UPDATE_NEED_PUSH = "NOTIFY_STORE_UPDATE_NEED_PUSH";
    public static final String NOTIFY_STORE_UPDATE_KEY = "NOTIFY_STORE_UPDATE_KEY";
    public static final String NOTIFY_STORE_UPDATE_LARGE_ICON = "NOTIFY_STORE_UPDATE_LARGE_ICON";

    public static final String LAST_USE_APP_TIME = "LAST_USE_APP_TIME";

    public static final String LAST_SLIENT_USER_NOTIFY_TIME = "LAST_SLIENT_USER_NOTIFY_TIME";
    public static final String CLICK_COLLAGE_BTN = "CLICK_COLLAGE_BTN";
    public static final String SHOW_NOTIFY_COLLAGE_TIME = "SHOW_NOTIFY_COLLAGE_TIME";
    public static final String SAVE_VIDEO_COLLAGE = "SAVE_VIDEO_COLLAGE";
    public static final String SHOW_NOTIFY_VIDEO_COLLAGE_TIME = "SHOW_NOTIFY_VIDEO_COLLAGE_TIME";
    public static final String CLICK_FREESTYLE_BTN = "CLICK_FREESTYLE_BTN";
    public static final String SHOW_NOTIFY_FREESTYLE_TIME = "SHOW_NOTIFY_FREESTYLE_TIME";
    public static final String SAVE_DYNAMIC_TEMPLATE = "SAVE_DYNAMIC_TEMPLATE";
    public static final String SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME = "SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME";
    public static final String CLICK_CROP_BTN = "CLICK_CROP_BTN";
    public static final String SHOW_NOTIFY_CROP_TIME = "SHOW_NOTIFY_CROP_TIME";
    public static final String SAVE_GIF = "SAVE_GIF";
    public static final String SHOW_NOTIFY_GIF_TIME = "SHOW_NOTIFY_GIF_TIME";

    public static final String SHOW_VIDEO_COLLAGE_GUIDE_AGAIN = "SHOW_VIDEO_COLLAGE_GUIDE_AGAIN";
    public static final String SHOW_VIDEO_GIF_GUIDE_AGAIN = "SHOW_VIDEO_GIF_GUIDE_AGAIN";

    public static final String LAST_SHOW_HOLIDAY_NOTIF_TIME = "LAST_SHOW_HOLIDAY_NOTIF_TIME";
    public static final String LAST_SHOW_HOLIDAY_TITLE = "LAST_SHOW_HOLIDAY_TITLE";
    public static final String LAST_SHOW_HOLIDAY_CONTENT = "LAST_SHOW_HOLIDAY_CONTENT";
    public static final String LAST_SHOW_HOLIDAY_DAY_OF_WEEK = "LAST_SHOW_HOLIDAY_DAY_OF_WEEK";
    public static final String LAST_SHOW_HOLIDAY_HOUR_START = "LAST_SHOW_HOLIDAY_HOUR_START";
    public static final String LAST_SHOW_HOLIDAY_HOUR_END = "LAST_SHOW_HOLIDAY_HOUR_END";
    public static final String LAST_SHOW_HOLIDAY_ICON = "LAST_SHOW_HOLIDAY_ICON";

    public static final String ENABLE_NOTIFICATION = "ENABLE_NOTIFICATION";

    public static final String LAST_BANNER_POSITION = "LAST_BANNER_POSITION";
    public static final String SHOW_DEFAULT_BANNER = "SHOW_DEFAULT_BANNER";
}

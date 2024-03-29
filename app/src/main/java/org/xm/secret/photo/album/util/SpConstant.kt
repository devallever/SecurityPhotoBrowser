package org.xm.secret.photo.album.util

/**
 *
 */

object SpConstant {

    /**
     * 是否第一次启动APP
     */
    val IS_APP_FIRST_START = "IS_APP_FIRST_START"

    /**
     * 初始化数据库是否成功
     */
    val INIT_DB_DATA_SUCCESS = "INIT_DB_DATA_SUCCESS"

    /**
     * 程序安装时间,包括更新时间
     */
    val APP_INSTALL_TIME = "APP_INSTALL_TIME"

    /**
     * 是否显示协议页
     */
    val SHOW_PROTOCOL_PAGE = "SHOW_PROTOCOL_PAGE"

    /**
     * 最后一次检查更新的时间，milliSeconds
     */
    val LAST_UPDATE_MILLISECONDS = "LAST_UPDATE_MILLISECONDS"

    /**
     * 最后一次更新的信息id
     */
    val LAST_UPDATE_INFO_ID = "LAST_UPDATE_INFO_ID"

    /**
     * 上次定时service执行时间
     */
    val LAST_RUN_SCHEDULE_SERVICE_TIME = "LAST_RUN_SCHEDULE_SERVICE_TIME"

    /**
     * 是否开启工具锁屏
     */
    val FLASH_SCREEN_LOCKER = "FLASH_SCREEN_LOCKER"

    /**
     * 工具锁是否被用户修改过
     */
    val SCREEN_LOCKER_MODIFY_BY_USER = "SCREEN_LOCKER_MODIFY_BY_USER"

    /// 评分引导相关
    /**
     * 已引导用户跳转到gp的版本号
     */
    val RATE_DIALOG_CONFIRM_VERSION_NAME = "RATE_DIALOG_CONFIRM_VERSION_NAME"
    /**
     * 是否已引导用户跳转到ins主页
     */
    val FOLLOW_INS_DIALOG_CONFIRM = "FOLLOW_INS_DIALOG_CONFIRM"
    /**
     * 是否已引导用户跳转分享
     */
    val SHARE_DIALOG_CONFIRM = "SHARE_DIALOG_CONFIRM"

    /**
     * 评分引导弹框条件 每天起始时间为凌晨0点
     */
    val CURRENT_DAY = "CURRENT_DAY"

    /**
     * 评分引导弹框条件 每天进入应用次数
     */
    val RATE_DIALOG_SHOWN_TIMES = "RATE_DIALOG_SHOWN_TIMES"

    /**
     * 评分引导弹框最后一次弹框时间
     */
    val RATE_DIALOG_LAST_SHOWN_TIME = "RATE_DIALOG_LAST_SHOWN_TIME"

    /**
     * 评分引导弹框条件 每天进入应用次数
     */
    val OPEN_APP_CNT_PER_DAY = "OPEN_APP_CNT_PER_DAY"

    /**
     * 评分引导弹框条件 每天分享次数
     */
    val SHARE_CNT_PER_DAY = "OPERATE_APP_CNT_PER_DAY"

    /**
     * 评分引导弹框条件 每天下载次数
     */
    val DOWNLOAD_CNT_PER_DAY = "DOWNLOAD_CNT_PER_DAY"

    /**
     * 当天第一次进入应用的时间
     */
    val FIRST_IN_TIME_OF_DAY = "FIRST_IN_TIME_OF_DAY"

    /**
     * 评分引导弹框条件 每天优化次数
     */
    val OPTIMIZE_TIMES_CNT_PER_DAY = "OPTIMIZE_TIMES_CNT_PER_DAY"

    /**
     * 子包评分引导最后一次展示的时间
     */
    val RATE_SUBPACKAGE_LAST_SHOWN_TIMES = "RATE_SUBPACKAGE_LAST_SHOWN_TIMES"

    /**
     * 子包评分引导展示次数后缀
     */
    val RATE_SUBPACKAGE_SHOWN_TIMES_SUFFIX = "_SHOWN_TIMES"

    /**
     * 子包评分引导点击过跳转gp的版本，有值说明点击过跳转
     */
    val RATE_SUBPACKAGE_CLICK_GIVE_5_STARS_VERSION_SUFFIX = "_CLICK_TO_GP_VERSION"

    /**
     * 是否点击拍照片
     */
    val TOUCH_2_TAKE_PHOTO = "TOUCH_2_TAKE_PHOTO"

    /**
     * 是否裁剪成正方形
     */
    val BITMAP_CROP_SQUARE = "BITMAP_CROP_SQUARE"

    /**
     * 是否裁剪成矩形
     */
    val BITMAP_CROP_RECT = "BITMAP_CROP_RECT"

    val ONLY_TWO_COLLAGE_RATIO = "ONLY_TWO_COLLAGE_RATIO"

    /**
     * 拍照图片保存路径
     */
    val PHOTO_SAVE_LOCATION = "PHOTO_SAVE_LOCATION"

    /**
     * 获取最大纹理，0为未初始化
     */
    val MAX_TEXTURE_SIZE = "MAX_TEXTURE_SIZE"

    val COLLAGE_WITH_FRAME = "COLLAGE_WITH_FRAME"

    val PLAY_SHUTTER_SOUND = "PLAY_SHUTTER_SOUND"

    val TAKE_VIDEO_COUNT = "TAKE_VIDEO_COUNT"

    val TAKE_MOTION_COUNT = "TAKE_MOTION_COUNT"

    val EDIT_COUNT = "EDIT_COUNT"

    val SHARE_COUNT = "SHARE_COUNT"

    val SHOW_LOCATION = "SHOW_LOCATION"

    val TAKE_PICTURE_COUNT = "TAKE_PICTURE_COUNT"

    val HDR_ON = "HDR_ON"

    val VIGNETTE_ON = "VIGNETTE_ON"

    val BLUR_STATE = "BLUR_STATE"

    val CAMERA_SIZE_FINGERPRINT = "CAMERA_SIZE_FINGERPRINT"

    val VIDEO_BITRATE = "VIDEO_BITRATE"

    val VIDEO_FPS = "VIDEO_FPS"

    val PREVIEW_SIZE = "PREVIEW_SIZE"

    val ROTATE_PREVIEW = "ROTATE_PREVIEW"

    val GRID_INFO = "GRID_INFO"

    val SHOW_MOTION_PRESS_TIP = "SHOW_MOTION_PRESS_TIP"

    val FLASH_VALUE = "FLASH_VALUE"

    val TAKE_PHOTO_TIME = "TAKE_PHOTO_TIME"

    val RECORD_AUDIO_SRC = "RECORD_AUDIO_SRC"

    val MIRROR_FRONT_CAMERA = "MirrorFrontCamera"

    val FRONT_CAMERA = "FrontCamera"

    val COLLAGE_DELAY_TIME = "COLLAGE_DELAY_TIME"

    val SHOW_THUMBNAIL_ANIMATION = "SHOW_THUMBNAIL_ANIMATION"

    val GPS_DIRECTION = "GpsDirection"

    val IMAGE_QUALITY = "IMAGE_QUALITY"

    /**
     * 是否显示移轴提示
     */
    val TILTSHIFT_CLICK_TIPS = "TILTSHIFT_CLICK_TIPS"

    val HAS_NEW_FILTER = "HAS_NEW_FILTER"

    val HAS_NEW_MARKING = "HAS_NEW_MARKING"

    val FILL_IN_LIGHT = "FILL_IN_LIGHT"

    val SHOW_MOTION_PLAY_TIP = "ShowMotionPlayTip"

    val BEAUTY_HAIR_COLOR_NEW = "BEAUTY_HAIR_COLOR_NEW"

    val SHOW_SELECT_MULTI_PREVIEW_TIP = "SHOW_SELECT_MULTI_PREVIEW_TIP"

    val SHOW_SELECT_TO_EDIT_PREVIEW_TIP = "SHOW_SELECT_TO_EDIT_PREVIEW_TIP"

    val LAST_SHARE_IMAGE_TOOL1_PKGNAME = "LAST_SHARE_IMAGE_TOOL_ONE_PKGNAME"
    val LAST_SHARE_IMAGE_TOOL1_ACTIVITY_NAME = "LAST_SHARE_IMAGE_TOOL_ONE_ACTIVITY_NAME"
    val LAST_SHARE_IMAGE_TOOL2_PKGNAME = "LAST_SHARE_IMAGE_TOOL_TWO_PKGNAME"
    val LAST_SHARE_IMAGE_TOOL2_ACTIVITY_NAME = "LAST_SHARE_IMAGE_TOOL_TWO_ACTIVITY_NAME"

    val LAST_SHARE_VIDEO_TOOL1_PKGNAME = "LAST_SHARE_VIDEO_TOOL1_PKGNAME"
    val LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME = "LAST_SHARE_VIDEO_TOOL1_ACTIVITY_NAME"
    val LAST_SHARE_VIDEO_TOOL2_PKGNAME = "LAST_SHARE_VIDEO_TOOL2_PKGNAME"
    val LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME = "LAST_SHARE_VIDEO_TOOL2_ACTIVITY_NAME"

    val COLLAGE_NEW = "COLLAGE_NEW"

    val EDIT_PICTURE_COUNT = "EDIT_PICTURE_COUNT"

    val EDIT_MAGAZINE_NEW = "EDIT_MAGAZINE_NEW"

    val SHOW_OTHER_BEAUTY_NEW_ICON = "SHOW_OTHER_BEAUTY_NEW_ICON"

    val EDIT_SHOW_HAIR_COLOR_TIP = "EDIT_SHOW_HAIR_COLOR_TIP"

    val SHOW_PREVIEW_EDIT_RED_ICON = "SHOW_PREVIEW_EDIT_RED_ICON"

    val SHOW_IMG_PREVIEW_DYNAMIC_TIP = "SHOW_IMG_PREVIEW_DYNAMIC_TIP"

    val INIT_CAMERA_ID = "INIT_CAMERA_ID"

    val FIRST_CAMERA_RADIO = "FIRST_CAMERA_RADIO"

    val ALREADY_BUY_FILTER_ID = "ALREADY_BUY_FILTER_ID"

    /**
     * 拍照或录制视频后，是否立即保持图片到相册
     */
    val SAVE_IMG_IMMEDIATELY = "SAVE_IMG_IMMEDIATELY"
    val HOLIDAY_DB_VERSION = "HOLIDAY_DB_VERSION"

    //最近使用的gif
    val GIF_RECENTLY = "GIF_RECENTLY"

    //ins分享过的图片uri 1:1  用于删除
    val INS_SHARE_IMG_URI = "INS_SHARE_IMG_URI"

    val HAS_CHECK_PRIVACY = "ALREADY_CHECK_PRIVACY"

    val SHOW_MAIN_FUNC_LIST_GUIDE_TIP = "SHOW_MAIN_FUNC_LIST_GUIDE_TIP"

    val STORE_DOWNLOAD_COUNT_OF_DAY = "STORE_DOWNLOAD_COUNT_OF_DAY"

    val HAS_USE_BOOBJOB = "HAS_USE_BOOBJOB"

    val HAS_USE_BUTTOCKS = "HAS_USE_BUTTOCKS"

    val HAS_USE_COLLAGE = "HAS_USE_COLLAGE"

    val HAS_USE_GIF_STICKER = "HAS_USE_GIF_STICKER"
    val CAN_SHOW_GIF_STICKER = "CAN_SHOW_GIF_STICKER"

    val HAS_USE_STICKER = "HAS_USE_STICKER"
    //美颜引导
    val HAS_USE_HEALING = "HAS_USE_HEALING"
    val HAS_USE_WHITEN = "HAS_USE_WHITEN"
    val HAS_USE_SMOOTH = "HAS_USE_SMOOTH"
    val HAS_USE_GLOW = "HAS_USE_GLOW"
    val HAS_USE_HAIR_COLOR = "HAS_USE_HAIR_COLOR"
    val HAS_USE_LIPSTICK = "HAS_USE_LIPSTICK"
    val HAS_USE_EYE_TUNER = "HAS_USE_EYE_TUNER"
    val HAS_USE_TALLER = "HAS_USE_TALLER"

    val HAS_USE_MIXER = "HAS_USE_MIXER"

    val LAST_SHOW_TIME_MAIN_GUIDE = "LAST_SHOW_TIME_MAIN_GUIDE"

    //    public static final String IS_FORCE_UPDATE = "IS_FORCE_UPDATE";
    //    public static final String FORCE_UPDATE_PKGNAME = "FORCE_UPDATE_PKGNAME";
    //    public static final String FORCE_UPDATE_TITLE = "FORCE_UPDATE_TITLE";
    //    public static final String FORCE_UPDATE_CONTENT = "FORCE_UPDATE_CONTENT";
    //    public static final String FORCE_UPDATE_URL = "FORCE_UPDATE_URL";
    //    public static final String FORCE_UPDATE_VERSION = "FORCE_UPDATE_VERSION";

    val SHOW_TIP_SEL_BEYOND_30S_VIDEO = "SHOW_TIP_SEL_BEYOND_30S_VIDEO"

    val NOTIFY_STORE_UPDATE_SHOW_TIME = "NOTIFY_STORE_UPDATE_SHOW_TIME"
    val NOTIFY_STORE_UPDATE_NEED_PUSH = "NOTIFY_STORE_UPDATE_NEED_PUSH"
    val NOTIFY_STORE_UPDATE_KEY = "NOTIFY_STORE_UPDATE_KEY"
    val NOTIFY_STORE_UPDATE_LARGE_ICON = "NOTIFY_STORE_UPDATE_LARGE_ICON"

    val LAST_USE_APP_TIME = "LAST_USE_APP_TIME"

    val LAST_SLIENT_USER_NOTIFY_TIME = "LAST_SLIENT_USER_NOTIFY_TIME"
    val CLICK_COLLAGE_BTN = "CLICK_COLLAGE_BTN"
    val SHOW_NOTIFY_COLLAGE_TIME = "SHOW_NOTIFY_COLLAGE_TIME"
    val SAVE_VIDEO_COLLAGE = "SAVE_VIDEO_COLLAGE"
    val SHOW_NOTIFY_VIDEO_COLLAGE_TIME = "SHOW_NOTIFY_VIDEO_COLLAGE_TIME"
    val CLICK_FREESTYLE_BTN = "CLICK_FREESTYLE_BTN"
    val SHOW_NOTIFY_FREESTYLE_TIME = "SHOW_NOTIFY_FREESTYLE_TIME"
    val SAVE_DYNAMIC_TEMPLATE = "SAVE_DYNAMIC_TEMPLATE"
    val SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME = "SHOW_NOTIFY_DYNAMIC_TEMPLATE_TIME"
    val CLICK_CROP_BTN = "CLICK_CROP_BTN"
    val SHOW_NOTIFY_CROP_TIME = "SHOW_NOTIFY_CROP_TIME"
    val SAVE_GIF = "SAVE_GIF"
    val SHOW_NOTIFY_GIF_TIME = "SHOW_NOTIFY_GIF_TIME"

    val SHOW_VIDEO_COLLAGE_GUIDE_AGAIN = "SHOW_VIDEO_COLLAGE_GUIDE_AGAIN"
    val SHOW_VIDEO_GIF_GUIDE_AGAIN = "SHOW_VIDEO_GIF_GUIDE_AGAIN"

    val LAST_SHOW_HOLIDAY_NOTIF_TIME = "LAST_SHOW_HOLIDAY_NOTIF_TIME"
    val LAST_SHOW_HOLIDAY_TITLE = "LAST_SHOW_HOLIDAY_TITLE"
    val LAST_SHOW_HOLIDAY_CONTENT = "LAST_SHOW_HOLIDAY_CONTENT"
    val LAST_SHOW_HOLIDAY_DAY_OF_WEEK = "LAST_SHOW_HOLIDAY_DAY_OF_WEEK"
    val LAST_SHOW_HOLIDAY_HOUR_START = "LAST_SHOW_HOLIDAY_HOUR_START"
    val LAST_SHOW_HOLIDAY_HOUR_END = "LAST_SHOW_HOLIDAY_HOUR_END"
    val LAST_SHOW_HOLIDAY_ICON = "LAST_SHOW_HOLIDAY_ICON"

    val ENABLE_NOTIFICATION = "ENABLE_NOTIFICATION"

    val LAST_BANNER_POSITION = "LAST_BANNER_POSITION"
    val SHOW_DEFAULT_BANNER = "SHOW_DEFAULT_BANNER"
}

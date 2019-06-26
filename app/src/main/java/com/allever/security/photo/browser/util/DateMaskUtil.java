package com.allever.security.photo.browser.util;

import android.text.TextUtils;
import com.android.absbase.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片的日期标记的公共类
 */
public class DateMaskUtil {

    //存在标记的时候多一个后缀
    private static final String END_PENDING = "_DateMark";

    /**
     * 用于存储当前标记是否开启
     */
    public static final String PREF_DATE_MARK_OPEN = "sp_date_mask_open";

    /**
     * 用于存储当前的标记的格式
     */
    public static final String PREF_CUR_FORMAT_STRING = "sp_current_format_string";

    /**
     * 所有的格式列表
     */
    public static final String[] DEFAULT_FORMAT = new String[]{
            "MM-dd-yy hh:mm a",
            "dd-MM-yy hh:mm a",
            "yyyy-MM-dd hh:mm a",
            "dd/MM/yy hh:mm a",
            "dd/MM/yyyy hh:mm a",
            "dd.MM.yyyy hh:mm a"
    };

    /**
     * 获取当前显示的String
     *
     * @return
     */
    public static String[] getNowSetting(Date date) {
        String template = "";
        int count = DEFAULT_FORMAT.length;
        for (int i = 0; i < count; i++) {
            template += (DEFAULT_FORMAT[i] + '&');
        }
        template = template.substring(0, template.length() - 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(template, Locale.US);
        String data = simpleDateFormat.format(date);
        String[] result = data.split("&");
        return result;
    }

    /**
     * 获取当前设置的格式化后的日期串
     *
     * @param date
     * @return
     */
    public static String getCurSettingFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getCurFormat(), Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前设置的格式化后的日期串
     *
     * @return
     */
    public static String getCurSettingFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getCurFormat(), Locale.US);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 保存当前的格式
     *
     * @param format
     */
    public static void saveCurFormat(String format) {
        SpUtils.obtainDefault().save(PREF_CUR_FORMAT_STRING, format);
    }

    /**
     * 保存当前的格式
     *
     * @param index
     */
    public static void saveCurFormat(int index) {
        SpUtils.obtainDefault().save(PREF_CUR_FORMAT_STRING, DEFAULT_FORMAT[index]);
    }

    /**
     * 获取当前的格式
     *
     * @return
     */
    public static String getCurFormat() {
        return SpUtils.obtainDefault().get(PREF_CUR_FORMAT_STRING, DEFAULT_FORMAT[0]);
    }

    /**
     * 设置当前日期标识是否开启
     *
     * @param open
     */
    public static void setDataMarkOpen(boolean open) {
        SpUtils.obtainDefault().save(PREF_DATE_MARK_OPEN, open);
    }

    /**
     * 获取当前是否开启日期标识
     *
     * @return
     */
    public static boolean getDataMarkOpen() {
        return SpUtils.obtainDefault().getBoolean(PREF_DATE_MARK_OPEN, false);
    }

    /**
     * 获取当前选中的日期格式的Index
     * 如果不正确则恢复默认0
     * 注意如果没有匹配到  会恢复到默认的
     *
     * @return
     */
    public static int getSelectIndex() {
        String cur = getCurFormat();
        int count = DEFAULT_FORMAT.length;
        for (int i = 0; i < count; i++) {
            if (DEFAULT_FORMAT[i].equals(cur)) {
                return i;
            }
        }
        saveCurFormat(0);
        return 0;
    }

    /**
     * 是否有日期标记
     *
     * @param path
     * @return
     */
    public static boolean hasDateMark(String path) {
        if (TextUtils.isEmpty(path)) return false;
        int index = path.indexOf('.');
        if (index == -1) return false;
        String tempStr = path.substring(0, index);
        return tempStr.endsWith(END_PENDING);
    }

    /**
     * 增加有日期标记的标识
     *
     * @param fileName or path 或者是没有后缀的名字也可以
     * @return
     */
    public static String addDateMark(String fileName) {
        if (TextUtils.isEmpty(fileName)) return fileName;
        int index = fileName.indexOf('.');
        if (index == -1) return (fileName + END_PENDING);
        String tempStr = fileName.substring(0, index);
        String end = fileName.substring(index, fileName.length());
        return tempStr + END_PENDING + end;
    }
}

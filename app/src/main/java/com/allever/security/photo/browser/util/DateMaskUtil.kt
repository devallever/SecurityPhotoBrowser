package com.allever.security.photo.browser.util

import android.text.TextUtils
import com.android.absbase.utils.SpUtils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 图片的日期标记的公共类
 */
object DateMaskUtil {

    //存在标记的时候多一个后缀
    private val END_PENDING = "_DateMark"

    /**
     * 用于存储当前标记是否开启
     */
    val PREF_DATE_MARK_OPEN = "sp_date_mask_open"

    /**
     * 用于存储当前的标记的格式
     */
    val PREF_CUR_FORMAT_STRING = "sp_current_format_string"

    /**
     * 所有的格式列表
     */
    val DEFAULT_FORMAT = arrayOf(
        "MM-dd-yy hh:mm a",
        "dd-MM-yy hh:mm a",
        "yyyy-MM-dd hh:mm a",
        "dd/MM/yy hh:mm a",
        "dd/MM/yyyy hh:mm a",
        "dd.MM.yyyy hh:mm a"
    )

    /**
     * 获取当前设置的格式化后的日期串
     *
     * @return
     */
    val curSettingFormat: String
        get() {
            val simpleDateFormat = SimpleDateFormat(curFormat, Locale.US)
            return simpleDateFormat.format(Date())
        }

    /**
     * 获取当前的格式
     *
     * @return
     */
    val curFormat: String?
        get() = SpUtils.obtainDefault()[PREF_CUR_FORMAT_STRING, DEFAULT_FORMAT[0]]

    /**
     * 获取当前是否开启日期标识
     *
     * @return
     */
    /**
     * 设置当前日期标识是否开启
     *
     * @param open
     */
    var dataMarkOpen: Boolean
        get() = SpUtils.obtainDefault().getBoolean(PREF_DATE_MARK_OPEN, false)
        set(open) = SpUtils.obtainDefault().save(PREF_DATE_MARK_OPEN, open)

    /**
     * 获取当前选中的日期格式的Index
     * 如果不正确则恢复默认0
     * 注意如果没有匹配到  会恢复到默认的
     *
     * @return
     */
    val selectIndex: Int
        get() {
            val cur = curFormat
            val count = DEFAULT_FORMAT.size
            for (i in 0 until count) {
                if (DEFAULT_FORMAT[i] == cur) {
                    return i
                }
            }
            saveCurFormat(0)
            return 0
        }

    /**
     * 获取当前显示的String
     *
     * @return
     */
    fun getNowSetting(date: Date): Array<String> {
        var template = ""
        val count = DEFAULT_FORMAT.size
        for (i in 0 until count) {
            template += DEFAULT_FORMAT[i] + '&'
        }
        template = template.substring(0, template.length - 1)
        val simpleDateFormat = SimpleDateFormat(template, Locale.US)
        val data = simpleDateFormat.format(date)
        val result = data.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return result
    }

    /**
     * 获取当前设置的格式化后的日期串
     *
     * @param date
     * @return
     */
    fun getCurSettingFormat(date: Date): String {
        val simpleDateFormat = SimpleDateFormat(curFormat, Locale.US)
        return simpleDateFormat.format(date)
    }

    /**
     * 保存当前的格式
     *
     * @param format
     */
    fun saveCurFormat(format: String) {
        SpUtils.obtainDefault().save(PREF_CUR_FORMAT_STRING, format)
    }

    /**
     * 保存当前的格式
     *
     * @param index
     */
    fun saveCurFormat(index: Int) {
        SpUtils.obtainDefault().save(PREF_CUR_FORMAT_STRING, DEFAULT_FORMAT[index])
    }

    /**
     * 是否有日期标记
     *
     * @param path
     * @return
     */
    fun hasDateMark(path: String): Boolean {
        if (TextUtils.isEmpty(path)) return false
        val index = path.indexOf('.')
        if (index == -1) return false
        val tempStr = path.substring(0, index)
        return tempStr.endsWith(END_PENDING)
    }

    /**
     * 增加有日期标记的标识
     *
     * @param fileName or path 或者是没有后缀的名字也可以
     * @return
     */
    fun addDateMark(fileName: String): String {
        if (TextUtils.isEmpty(fileName)) return fileName
        val index = fileName.indexOf('.')
        if (index == -1) return fileName + END_PENDING
        val tempStr = fileName.substring(0, index)
        val end = fileName.substring(index, fileName.length)
        return tempStr + END_PENDING + end
    }
}

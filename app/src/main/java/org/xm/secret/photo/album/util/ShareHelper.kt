package org.xm.secret.photo.album.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import android.text.TextUtils
import org.xm.secret.photo.album.BuildConfig
import org.xm.secret.photo.album.R

import org.xm.secret.photo.album.bean.ShareData
import com.android.absbase.utils.ToastUtils
import java.io.File
import java.util.*

object ShareHelper {

    public const val SHARE_CONTENT_TYPE_TEXT = 0
    public const val SHARE_CONTENT_TYPE_IMAGE = 1
    public const val SHARE_CONTENT_TYPE_VEDIO = 2

    private const val FACEBOOK_PKG = "com.facebook.katana"
    private const val FACEBOOK_ACTIVITY_NAME =
        "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias"

    private const val WHATSAPP_PKG = "com.whatsapp"
    private const val WHATSAPP_ACTIVITY_NAME = "com.whatsapp.ContactPicker"

    private const val INSTAGRAM_PKG = "com.instagram.android"
    private const val INSTAGRAM_ACTIVITY_NAME = "com.instagram.android.activity.ShareHandlerActivity"

    private const val YOUTUBE_PKG = "com.google.android.youtube"
    private const val YOUTUBE_ACTIVITY_NAME = " com.google.android.apps.youtube.app.WatchWhileActivity"

    //不填ACTIVITY_NAME也能分享?
    private const val TIKTOK_PKG_INTERNATIONAL = "com.zhiliaoapp.musically"
    private const val TIKTOK_PKG = "com.ss.android.ugc.aweme"
    private const val TIKTOK_ACTIVITY_NAME = ""

    const val TYPE_IMAGE = 0
    const val TYPE_VIDEO = 1

    fun shareTo(context: Context, path: String?, type: Int){
        val intent = Intent()
        val file = File(path)
        val fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //解决调用相册不显示图片的问题
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        if (type == TYPE_IMAGE){
            intent.type = "image/*"
        }else if (type == TYPE_VIDEO){
            intent.type = "video/*"
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)))
    }

    private fun shareTextTo(context: Context, pkg: String, activityName: String, content: String): Boolean {
        var flag = true
        val shareIntent = Intent(Intent.ACTION_SEND)
        if (!TextUtils.isEmpty(pkg)) {
            shareIntent.component = ComponentName(pkg, activityName)
        } else {
            shareIntent.setPackage(pkg)
        }
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, content)
        try {
            context.startActivity(shareIntent)
        } catch (e: Throwable) {//增加保护
            e.printStackTrace()
            flag = false
            val result = getAllShareTextTools(context)

            for (data in result) {
                if (data.pkg == pkg) {
                    shareIntent.component = ComponentName(data.pkg, data.activityName)
                    flag = true
                    break
                }
            }
            try {
                context.startActivity(shareIntent)
            } catch (e1: Throwable) {
                flag = false
            }

        }

        return flag
    }

    private fun shareTo(context: Context, pkg: String, activityName: String, path: String, isImage: Boolean) {
        var flag = true
        val shareIntent = Intent(Intent.ACTION_SEND)
        if (TextUtils.isEmpty(activityName)) {
            shareIntent.setPackage(pkg)
        } else {
            shareIntent.component = ComponentName(pkg, activityName)
        }
        if (isImage) {
            shareIntent.type = "image/*"
        } else {
            shareIntent.type = "video/*"
        }
        val uri = Uri.fromFile(File(path))
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            context.startActivity(shareIntent)
        } catch (e: Throwable) {//增加保护
            flag = false
            val result = getAllShareTools(context, isImage)
            for (data in result) {
                if (data.pkg == pkg) {
                    shareIntent.component = ComponentName(data.pkg, data.activityName)
                    flag = true
                    break
                }
            }
            try {
                if (flag) {
                    context.startActivity(shareIntent)
                }
            } catch (e1: Throwable) {
                flag = false
            }

        }

        return
    }


//    fun shareImage(path: String) {
//
//    }
//
//    fun shareVideo(path: String) {
//
//    }

    fun shareToYoutube(context: Context, any: Any?, type: Int) {
        shareTo(context, YOUTUBE_PKG, YOUTUBE_ACTIVITY_NAME, any, type)
    }

    fun shareToTikTok(context: Context, any: Any?, type: Int) {
//        shareTo(context, TIKTOK_PKG, TIKTOK_ACTIVITY_NAME, any, type)
        shareTo(
            context, TIKTOK_PKG_INTERNATIONAL, TIKTOK_ACTIVITY_NAME,
            TIKTOK_PKG, TIKTOK_ACTIVITY_NAME,
            any, type
        )
    }

    fun shareToFacebook(context: Context, any: Any?, type: Int) {
        shareTo(context, FACEBOOK_PKG, FACEBOOK_ACTIVITY_NAME, any, type)
    }

    fun shareToWhatsapp(context: Context, any: Any?, type: Int) {
        shareTo(context, WHATSAPP_PKG, WHATSAPP_ACTIVITY_NAME, any, type)
    }

    fun shareToIns(context: Context, path: String?, content: String = "", type: Int): Boolean {
        if (!isInstall(context, INSTAGRAM_PKG)) {
            ToastUtils.show(context.getString(R.string.not_install))
        }
        var isImage = false
        var flag = true
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.component = ComponentName(INSTAGRAM_PKG, INSTAGRAM_ACTIVITY_NAME)
        if (type == SHARE_CONTENT_TYPE_TEXT) {
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, content)
            isImage = false
        } else {
            if (type == SHARE_CONTENT_TYPE_IMAGE) {
                shareIntent.type = "image/*"
                isImage = true
            } else {
                shareIntent.type = "video/*"
                isImage = false
            }

            val uri = Uri.fromFile(File(path))
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "#zcamera")
        }

        try {
            context.startActivity(shareIntent)
        } catch (e: Throwable) {//增加保护
            flag = false
            val result = getAllShareTools(context, isImage)
            for (data in result) {
                if (data.pkg == INSTAGRAM_PKG) {
                    shareIntent.component = ComponentName(data.pkg, data.activityName)
                    flag = true
                    break
                }
            }
            try {
                if (flag) {
                    context.startActivity(shareIntent)
                }
            } catch (e1: Throwable) {
                flag = false
            }

        }

        return flag
    }

    fun shareMore(context: Context?, any: Any?, type: Int) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND

        if (type == SHARE_CONTENT_TYPE_TEXT) {
            val content = any.toString()
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, content)
        } else {
            if (type == SHARE_CONTENT_TYPE_IMAGE) {
                intent.type = "image/*"
            } else {
                intent.type = "video/*"
            }
            val path = any.toString()
            val uri = Uri.fromFile(File(path))
            intent.putExtra(Intent.EXTRA_STREAM, uri)
        }
        context?.startActivity(Intent.createChooser(intent, context.getString(R.string.share_to)))
    }

    private fun shareTo(context: Context, pkg: String, activityName: String, any: Any?, type: Int) {
        if (isInstall(context, pkg)) {
            if (type == SHARE_CONTENT_TYPE_TEXT) {
                val content = any.toString()
                shareTextTo(context, pkg, activityName, content)
            } else {
                val path = any.toString()
                if (type == SHARE_CONTENT_TYPE_IMAGE) {
                    shareTo(context, pkg, activityName, path, true)
                } else {
                    shareTo(context, pkg, activityName, path, false)
                }
            }
        } else {
            ToastUtils.show(context.getString(R.string.not_install))
        }
    }

    private fun shareTo(
        context: Context,
        pkg1: String, activityName1: String,
        pkg2: String, activityName2: String,
        any: Any?, type: Int
    ) {
        var pkg: String? = null
        var activityName = ""
        if (isInstall(context, pkg1)) {
            pkg = pkg1
            activityName = activityName1
        } else if (isInstall(context, pkg2)) {
            pkg = pkg2
            activityName = activityName2
        }
        if (pkg != null) {
            if (type == SHARE_CONTENT_TYPE_TEXT) {
                val content = any.toString()
                shareTextTo(context, pkg, activityName, content)
            } else {
                val path = any.toString()
                if (type == SHARE_CONTENT_TYPE_IMAGE) {
                    shareTo(context, pkg, activityName, path, true)
                } else {
                    shareTo(context, pkg, activityName, path, false)
                }
            }
        } else {
            ToastUtils.show(context.getString(R.string.not_install))
        }
    }

    private fun isInstall(context: Context, pkg: String): Boolean {
        if (TextUtils.isEmpty(pkg)) return false

        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(pkg, 0)
            if (packageInfo != null) {
                return true
            }
        } catch (e: Throwable) {
        }

        return false
    }

    private fun getAllShareTools(context: Context, isImage: Boolean): List<ShareData> {
        val result = ArrayList<ShareData>()
        val pm = context.packageManager
        val list = getAllSendImageOrVideoTools(context, isImage)
        for (ri in list) {
            if (ri.activityInfo.packageName != FileUtil.PACKAGE_NAME) {
                result.add(
                    ShareData(
                        ri.activityInfo.packageName,
                        ri.activityInfo.name
                    )
                )
            }
        }
        return result
    }

    private fun getAllSendImageOrVideoTools(context: Context, isImage: Boolean): List<ResolveInfo> {
        val pm = context.packageManager
        val shareIntent = Intent(Intent.ACTION_SEND)
        if (isImage) {
            shareIntent.type = "image/*"
        } else {
            shareIntent.type = "video/*"
        }
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
        return pm.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    private fun getAllShareTextTools(context: Context): List<ShareData> {
        val result = ArrayList<ShareData>()
        val pm = context.packageManager
        val list = getAllSendTextTools(context)
        for (ri in list) {
            if (ri.activityInfo.packageName != FileUtil.PACKAGE_NAME) {
                result.add(
                    ShareData(
                        ri.activityInfo.packageName,
                        ri.activityInfo.name
                    )
                )
            }
        }
        return result
    }

    private fun getAllSendTextTools(context: Context): List<ResolveInfo> {
        val pm = context.packageManager
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/*"
        shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
        return pm.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
    }


}
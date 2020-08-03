package org.xm.secret.photo.album.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.appcompat.app.AlertDialog
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.bean.ThumbnailBean
import org.xm.secret.photo.album.media.IDeleteListener
import com.allever.lib.common.app.App
import com.android.absbase.utils.SpUtils

import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStream
import java.util.ArrayList


@TargetApi(21)
object ExtSdcardUtils {

    //存储的treeUri
    val PREF_SAVED_EXT_SDCARD_URI = "SP_SAVED_URI"

    /**
     * 保存JPG或者视频的CACHE路径
     */
    val SAVE_CACHE_PATH = FileUtil.DICM_ROOT_PATH + File.separator + "Camera"

    /**
     * 获取tree URI
     *
     * @return
     */
    val savedExtSdcardUri: Uri?
        get() {
            val uri = SpUtils.obtainDefault().getString(PREF_SAVED_EXT_SDCARD_URI, null)
            if (uri != null) {
                try {
                    return Uri.parse(uri)
                } catch (ignored: Throwable) {

                }

            }
            return null
        }

    /**
     * 是否有外置SD卡的权限
     *
     * @return
     */
    fun hasExtSdcardPermission(): Boolean {
        var result = false
        val uri = savedExtSdcardUri
        if (uri != null) {
            val uriPermissions = App.context.contentResolver.persistedUriPermissions
            if (uriPermissions != null && uriPermissions.size > 0) {
                val size = uriPermissions.size
                for (i in 0 until size) {
                    val permission = uriPermissions[i]
                    if (uri == permission.uri && permission.isReadPermission && permission.isWritePermission) {
                        result = true
                        break
                    }
                }
            }
        }
        return result
    }

    /**
     * 调用这个请求权限
     *
     * @param activity
     * @param requestCode
     */
    fun requestExtSdcardPermission(activity: Activity, requestCode: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setPositiveButton(R.string.confirm) { dialog, which ->
            try {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                activity.startActivityForResult(intent, requestCode)
            } catch (e: Throwable) {
            }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.setTitle(R.string.request_ext_sdcard_permission_tip)
        val res = App.context.resources
        val msg1 = res.getString(R.string.request_ext_sdcard_permission_msg1)
        val msg2 = res.getString(R.string.request_ext_sdcard_permission_msg2)
        val msg3 = res.getString(R.string.request_ext_sdcard_permission_msg3)
        val spannableString = SpannableString(msg1 + msg2 + msg3)
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            msg1.length,
            msg1.length + msg2.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setMessage(spannableString)
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * 这个方法需要在onActivityresult中执行
     *
     * @param activity
     * @param data
     * @return 返回值是是否是外置SD卡根目录
     */
    fun dealOnActivityResult(activity: Activity, data: Intent): Boolean {
        try {
            val u = data.data

            val documentFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(activity, u!!)//根目录文件的
            if (isExtSdcardRootUri(documentFile!!)) {
                saveExtSdcardUri(u)
                val takeFlags =
                    data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                activity.contentResolver.takePersistableUriPermission(u, takeFlags)//持久化权限
                return true
            }
        } catch (e: Throwable) {
        }

        return false
    }

    fun saveExtSdcardUri(uri: Uri?) {
        SpUtils.obtainDefault().save(PREF_SAVED_EXT_SDCARD_URI, uri?.toString() ?: "")
    }

    /**
     * 这个是用于判断是不是外置SD卡根目录
     *
     * @param documentFile
     * @return
     */
    fun isExtSdcardRootUri(documentFile: androidx.documentfile.provider.DocumentFile): Boolean {
        val documentId = DocumentsContract.getDocumentId(documentFile.uri)
        if (!TextUtils.isEmpty(documentId)) {
            val part = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (part != null && part.size == 1 && part[0] != "primary") {
                return true
            }
        }
        return false
    }

    /**
     * 获取根目录的Id
     *
     * @param context
     * @param treeUri
     * @return
     */
    fun getRootDocumentId(context: Context, treeUri: Uri?): String? {
        if (treeUri != null) {
            val documentFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
            return DocumentsContract.getDocumentId(documentFile!!.uri)
        }
        return null
    }

    /**
     * 这个方法如果文件不存在会创建新的文件
     *
     * @param context
     * @param pathName
     * @param treeUri
     * @param mimeType
     * @return 返回的都是SingleDocumentFile不能进行文件创建
     */
    fun getDocumentFile(context: Context, pathName: String, treeUri: Uri?, mimeType: String): androidx.documentfile.provider.DocumentFile? {
        val documentFile = getDocumentFile(context, pathName, treeUri)
        if (documentFile == null) {
            return null
        } else if (documentFile.exists()) {
            return documentFile
        } else {
            createDocumentFile(context, documentFile, treeUri, pathName, mimeType)
            return documentFile
        }
    }

    /**
     * 获取一个documentFile的父目录的DocumentFile
     *
     * @param documentFile
     * @return SingleDocumentFile
     */
    private fun getParentDocument(context: Context, documentFile: androidx.documentfile.provider.DocumentFile?, treeUri: Uri?): androidx.documentfile.provider.DocumentFile? {
        if (documentFile == null) {
            return null
        }
        var parentDocumentFile = documentFile.parentFile
        if (parentDocumentFile == null) {
            val documentId = DocumentsContract.getDocumentId(documentFile.uri)
            val parentDocumentId: String
            val index = documentId.lastIndexOf(File.separator)
            if (index == -1) {//没有/则代表是root目录了
                if (treeUri != null) {
                    parentDocumentFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, treeUri)
                }
            } else {
                parentDocumentId = documentId.substring(0, index)
                parentDocumentFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(
                    context,
                    DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocumentId)
                )
                //                parentDocumentFile = DocumentFile.fromTreeUri(context, DocumentsContract.buildTreeDocumentUri(treeUri.getAuthority(), parentDocumentId));
            }
        }
        return parentDocumentFile
    }

    /**
     * 创建
     *
     * @param context
     * @param documentFile
     * @param treeUri
     * @param pathName
     * @param mimeType
     * @return
     */
    private fun createDocumentFile(
        context: Context,
        documentFile: androidx.documentfile.provider.DocumentFile,
        treeUri: Uri?,
        pathName: String,
        mimeType: String
    ): Uri? {
        val parentDocument = getParentDocument(context, documentFile, treeUri)
        val displayName = FileUtil.getFileName(pathName)
        if (parentDocument == null) return null
        if (parentDocument.exists()) {
            try {
                return DocumentsContract.createDocument(
                    context.contentResolver,
                    parentDocument.uri,
                    mimeType,
                    displayName
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        } else {
            mkdirExtSdcardPath(context, FileUtil.getParentFilePath(pathName), treeUri)//创建目录然后创建文件
            try {
                return DocumentsContract.createDocument(
                    context.contentResolver,
                    parentDocument.uri,
                    mimeType,
                    displayName
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
        return null
    }


    /**
     * 这个方法如果文件不存在不会创建新的文件
     *
     * @param context
     * @param pathName
     * @param treeUri
     * @Return SingleDocumentFile
     */
    fun getDocumentFile(context: Context, pathName: String?, treeUri: Uri?): androidx.documentfile.provider.DocumentFile? {
        if (treeUri != null) {
            val sdcardPath = FolderUtil.getAllSDPath(context)
            if (sdcardPath != null && sdcardPath.size >= 2) {
                val extSdcardPath = sdcardPath[1]
                val truePath = pathName!!.substring(extSdcardPath.length + 1, pathName.length)//除去root之外的
                val rootDocumentId = getRootDocumentId(context, treeUri)
                val documentId = rootDocumentId!! + truePath
                //这里存在两种方式 一种会生成singleDocumentFile 另一种生成TreeDocumentFile
                //            DocumentFile documentFile = DocumentFile.fromTreeUri(context, DocumentsContract.buildTreeDocumentUri(treeUri.getAuthority(), documentId));
                val documentFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(
                    context,
                    DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)
                )
                return documentFile
            }
        }
        return null
    }


    /**
     * 会创建新的文件夹
     *
     * @param context
     * @param path    必须是一个目录  不能是文件
     * @param treeUri
     */
    fun getDocumentDirectoryFile(context: Context, path: String, treeUri: Uri): androidx.documentfile.provider.DocumentFile? {
        val documentFile = getDocumentFile(context, path, treeUri)
        if (documentFile == null) {
            return null
        } else if (documentFile.exists()) {
            return documentFile
        } else {
            mkdirExtSdcardPath(context, path, treeUri)
            return documentFile
        }
    }


    /**
     * @param context
     * @param path    必须是一个目录  不能是文件
     * @param treeUri
     */
    fun mkdirExtSdcardPath(context: Context, path: String?, treeUri: Uri?): Boolean {
        val documentFile = getDocumentFile(context, path, treeUri)
        val parentDocument = getParentDocument(context, documentFile, treeUri)//拿到父文件夹
        if (parentDocument == null) {
            return false
        } else if (parentDocument.exists()) {
            //直接这样创建是有错误的 SingleDocumentFile不能创建文件或者文件夹
            //            documentFile =  parentDocument.createDirectory(FileUtil.getFileName(path));
            try {
                DocumentsContract.createDocument(
                    context.contentResolver,
                    parentDocument.uri,
                    DocumentsContract.Document.MIME_TYPE_DIR,
                    FileUtil.getFileName(path)
                )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return if (documentFile!!.exists()) {
                true
            } else {
                false
            }
        } else {
            val flag = mkdirExtSdcardPath(context, FileUtil.getParentFilePath(path), treeUri)
            if (flag) {
                //                documentFile =  parentDocument.createDirectory(FileUtil.getFileName(path));
                try {
                    DocumentsContract.createDocument(
                        context.contentResolver,
                        parentDocument.uri,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        FileUtil.getFileName(path)
                    )
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }

                return if (documentFile!!.exists()) {
                    true
                } else {
                    false
                }
            } else {
                return false
            }
        }
    }


    /**
     * 获取某个路径下文件的OutputStream
     *
     * @param context
     * @param pathName
     * @param mimeType
     * @return
     */
    fun getExtCardOutputStream(context: Context, pathName: String, mimeType: String): OutputStream? {
        try {
            val documentFile = getDocumentFile(context, pathName, savedExtSdcardUri, mimeType)
            val result = context.contentResolver.openOutputStream(documentFile!!.uri)
            return result
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取外置SDCARD文件的大小
     *
     * @param context
     * @param pathName
     * @return
     */
    fun getExtCardFileLength(context: Context, pathName: String): Long {
        val documentFile = getDocumentFile(context, pathName, savedExtSdcardUri)
        return if (documentFile != null && documentFile.exists()) {
            documentFile.length()
        } else 0
    }

    /**
     * 判断原理是如果不是内置的就是外置的
     *
     * @param path
     * @return
     */
    fun isExtSdcardPath(path: String): Boolean {
        return !path.startsWith(Environment.getExternalStorageDirectory().absolutePath)
    }

    /**
     * 删除外置SDCARD的单个文件
     *
     * @param context
     * @param path
     * @return
     */
    fun deleteExtFile(context: Context, path: String): Boolean {
        val hasPermission = hasExtSdcardPermission()
        if (hasPermission) {
            val documentFile = getDocumentFile(context, path, savedExtSdcardUri)
            return if (documentFile != null) {
                if (documentFile.exists()) {
                    documentFile.delete()
                } else {//不存在认为删除成功了
                    true
                }
            } else {
                false
            }
        } else {
            return false
        }
    }

    /**
     * 用于删除directoryPath目录下的某些文件
     *
     * @param context  //     * @param directoryPath  父目录的地址
     * @param files    父目录下需要删除的图片
     * @param listener
     * @return
     */
    fun deleteDirectoryFile(
        context: Context/*, String directoryPath*/,
        files: ArrayList<ThumbnailBean>?,
        listener: IDeleteListener<ThumbnailBean>?
    ): Int {
        if (files == null || files.size == 0) return 0
        val cr = context.contentResolver
        val size = files.size
        var successed = 0
        var failed = 0
        val treeUri = savedExtSdcardUri
        val hasPermission = hasExtSdcardPermission()
        for (i in 0 until size) {
            val file = files[i]
            if (hasPermission) {
                val childDocumentFile = getDocumentFile(context, file.path, treeUri)
                if (childDocumentFile != null) {
                    if (childDocumentFile.exists()) {//存在
                        if (childDocumentFile.delete()) {//删除成功的时候才进行删除数据库记录的操作
                            cr.delete(file.uri, null, null)
                            successed++
                            listener?.onDeleteFile(file, true)
                        } else {//删除文件失败 不处理数据库记录
                            failed++
                            listener?.onDeleteFile(file, false)
                        }
                    } else {//不存在 认为成功了
                        cr.delete(file.uri, null, null)
                        successed++
                        listener?.onDeleteFile(file, true)
                    }
                } else {
                    failed++
                    listener?.onDeleteFile(file, false)
                }
            } else {
                failed++
                listener?.onDeleteFile(file, false)
            }
        }
        return successed
    }
}

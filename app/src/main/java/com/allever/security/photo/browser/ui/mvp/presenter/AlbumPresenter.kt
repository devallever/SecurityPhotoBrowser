package com.allever.security.photo.browser.ui.mvp.presenter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.allever.lib.common.app.App
import com.allever.lib.common.mvp.BasePresenter
import com.allever.lib.common.util.DLog
import com.allever.lib.common.util.ToastUtils
import com.allever.lib.permission.PermissionListener
import com.allever.lib.permission.PermissionManager
import com.allever.security.photo.browser.AlbumActivity
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.LocalThumbnailBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.ui.mvp.view.AlbumView
import com.allever.security.photo.browser.util.DialogHelper
import com.allever.security.photo.browser.util.SharePreferenceUtil
import java.io.File

class AlbumPresenter : BasePresenter<AlbumView>() {

    private val mAlbumImageFolderMap = LinkedHashMap<String, ImageFolder>()
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private lateinit var mAlbumDataTask: PrivateAlbumDataTask

    fun requestPermission(activity: Activity, task: Runnable? = null) {
        PermissionManager.request(object : PermissionListener {
            override fun onGranted(grantedList: MutableList<String>) {
                task?.run()
            }

            override fun onDenied(deniedList: MutableList<String>) {

            }

            override fun alwaysDenied(deniedList: MutableList<String>) {
                PermissionManager.jumpPermissionSetting(activity, 0,
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog?.dismiss()
                    })
            }

        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun getPrivateAlbumData() {
        mAlbumDataTask = PrivateAlbumDataTask()
        mAlbumDataTask.execute()
    }

    fun cancelTask() {
        mAlbumDataTask.cancel(true)
    }

    fun createAlbum(albumName: String) {
        if (TextUtils.isEmpty(albumName)) {
            ToastUtils.show(App.context.getString(R.string.tips_please_input_album_name))
            return
        }

        val imageFolder = ImageFolder()
        imageFolder.name = (albumName)
        imageFolder.dir = (PrivateHelper.PATH_ALBUM + File.separator + albumName)
        imageFolder.data = ArrayList()
        imageFolder.count = (0)

        //创建相册
        handleAddAlbum(albumName)

        val albumPath = PrivateHelper.PATH_ALBUM + File.separator + albumName
        mAlbumImageFolderMap[albumPath] = imageFolder

        mViewRef.get()?.addAlbum(imageFolder)
    }

    fun handleAddAlbum(activity: Activity) {
        val builder = DialogHelper.Builder()
            .setTitleContent(App.context.getString(R.string.add_album))
            .isShowMessage(false)
            .isShowEditText(true)
            .setOkContent(App.context.getString(R.string.save))
            .setCancelContent(App.context.getString(R.string.cancel))

        val addAlbumDialog = DialogHelper.createEditTextDialog(
            activity,
            builder,
            object : DialogHelper.EditDialogCallback {
                override fun onOkClick(dialog: AlertDialog, etContent: String) {
                    createAlbum(etContent)
                    dialog.dismiss()
                }

                override fun onCancelClick(dialog: AlertDialog) {}
            })
        addAlbumDialog.show()
    }

    /***
     * 创建相册
     * @param album 相册名
     */
    private fun handleAddAlbum(album: String) {
        val albumFile = File(PrivateHelper.PATH_ALBUM + File.separator + album)
        if (!albumFile.exists()) {
            albumFile.mkdirs()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class PrivateAlbumDataTask : AsyncTask<Void, Void, MutableList<ImageFolder>>() {
        override fun doInBackground(vararg params: Void?): MutableList<ImageFolder>? {

            //遍历Album
            val rootDir = File(PrivateHelper.PATH_ALBUM)
            if (rootDir.exists() && rootDir.isDirectory) {
                val list = rootDir.list()
                if (list != null && list.isNotEmpty()) {
                    mAlbumImageFolderMap.clear()
                    val fileList = rootDir.listFiles() ?: return null
                    //遍历相册目录
                    for (albumDir in fileList) {
                        DLog.d("album name = ${albumDir.name}")
                        if (!albumDir.isDirectory) {
                            continue
                        }

                        val albumDirPath = albumDir.absolutePath
                        var thumbnailBeans = ArrayList<ThumbnailBean>()
                        val files = albumDir.listFiles()
                        val imageFolder = ImageFolder()

                        if (files == null) {
                            continue
                        }

                        //遍历目录内的链接文件
                        for (filepath in files) {
                            val name = filepath.name
                            val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
                            DLog.d("file name = $name")

                            if (!file.exists()) {
                                continue
                            }

                            val obj = SharePreferenceUtil.getObjectFromShare(App.context, name)
                            if (obj is LocalThumbnailBean) {
                                val thumb = obj as LocalThumbnailBean
                                val thumbnailBean =
                                    PrivateHelper.changeLocalThumbnailBean2ThumbnailBean(thumb)
                                if (!thumbnailBean.isInvalid) {
                                    thumbnailBeans.add(thumbnailBean)
                                }
                                thumbnailBean.isChecked = false
                            }

                        }

                        imageFolder.dir = albumDirPath
                        imageFolder.name = albumDir.name

                        val sortThumbnailBeans = ArrayList(thumbnailBeans)
                        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
                            java.lang.Long.compare(arg1.date, arg0.date)
                        })

                        thumbnailBeans = sortThumbnailBeans

                        imageFolder.data = (thumbnailBeans)
                        imageFolder.dir = (albumDirPath)
                        imageFolder.name = (albumDir.name)
                        imageFolder.count = (imageFolder.data?.size ?: 0)
                        mAlbumImageFolderMap[albumDirPath] = imageFolder
                    }
                }
            }
            return ArrayList(mAlbumImageFolderMap.values)
        }

        override fun onPostExecute(result: MutableList<ImageFolder>) {
            mImageFolderList = result
            mViewRef.get()?.updateAlbumList(mImageFolderList)
        }
    }
}
package org.xm.secret.photo.album.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.MediaColumns
import android.provider.MediaStore.Video
import android.text.TextUtils
import android.util.Log
import com.allever.lib.common.app.App
import com.android.absbase.utils.FileUtils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 文件夹帮助类
 */
object FolderHelper {
    private val TAG = FolderHelper::class.java.name

    val MEDIA_TYPE_IMAGE = 1

    val MEDIA_TYPE_VIDEO = 2

    val MEDIA_TYPE_DYNAMIC = 3

    val MEDIA_TYPE_GIF = 4

    private val VIDEO_BASE_URI = "content://media/external/video/media"

    val MEDIA_TEMP_FOLDER_PATH = FileUtils.getExternalCacheDir(App.context, "MediaTemp")

    val imageFolder: File
        get() {
            val folderName = DataManager.preferenceSaveLocation
            return getImageFolder(folderName)
        }

    val tempFolder: File
        get() = File(MEDIA_TEMP_FOLDER_PATH)

    // return free memory in MB
    // cast to long to avoid overflow!
    // can fail on emulator, at least!
    val freeMemory: Long
        get() {
            try {
                val imageFolder = imageFolder
                val statFs = StatFs(imageFolder.absolutePath)
                val blocks = statFs.availableBlocks.toLong()
                val size = statFs.blockSize.toLong()
                val free = blocks * size / 1048576
                return free
            } catch (e: IllegalArgumentException) {
                return -1
            }

        }

    //            if (PhoneInfo.isSupportWriteExtSdCard() && ExtSdcardUtils.isExtSdcardPath(location)) {
    ////                ExtSdcardUtils.mkdirExtSdcardPath(App.context, location, ExtSdcardUtils.getSavedExtSdcardUri());
    //                File f = new File(location);
    //                if (!f.exists()) {
    //                    f.mkdirs();
    //                }
    //            }
    val orCreateSaveLocation: String?
        get() {
            var location: String? = null
            try {
                location = DataManager.preferenceSaveLocation
                val f = File(location)
                if (!f.exists()) {
                    f.mkdirs()
                }
            } catch (e: Throwable) {
            }

            return location
        }

    val isExtSdcardImagePath: Boolean
        get() = PhoneInfo.isSupportWriteExtSdCard && ExtSdcardUtils.isExtSdcardPath(FolderHelper.imageFolder.absolutePath)


    fun getImageFolder(folderName: String): File {
        var folderName = folderName
        var file: File? = null
        if (folderName.length > 0 && folderName.lastIndexOf('/') == folderName.length - 1) {
            // ignore final '/' character
            folderName = folderName.substring(0, folderName.length - 1)
        }
        if (folderName.startsWith("/")) {
            file = File(folderName)
        } else {
            file = File(FileUtil.DICM_ROOT_PATH, folderName)
        }
        return file
    }

    /**
     * Create a File for saving an image or video
     */
    @SuppressLint("SimpleDateFormat")
    fun getOutputMediaFile(context: Context, type: Int, cache: Boolean): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        var mediaStorageDir = imageFolder

        if (type == MEDIA_TYPE_VIDEO || type == MEDIA_TYPE_DYNAMIC || type == MEDIA_TYPE_GIF) {
            if (PhoneInfo.isSupportWriteExtSdCard && ExtSdcardUtils.isExtSdcardPath(mediaStorageDir.absolutePath) || cache) {
                mediaStorageDir = tempFolder
            }
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(Date())
        var index = ""
        var mediaFile: File? = null
        val hasDateMask = DateMaskUtil.dataMarkOpen
        for (count in 1..100) {
            if (type == MEDIA_TYPE_IMAGE) {
                var name = (mediaStorageDir.path + File.separator + "IMG_"
                        + timeStamp + index + ".jpg")
                if (hasDateMask) {
                    name = DateMaskUtil.addDateMark(name)
                }
                mediaFile = File(name)
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = File(
                    mediaStorageDir.path + File.separator + "VID_"
                            + timeStamp + index + ".mp4"
                )
            } else if (type == MEDIA_TYPE_DYNAMIC) {
                mediaFile = File(
                    mediaStorageDir.path + File.separator + EncryptUtil.DYNAMIC_START
                            + timeStamp + index + ".mp4"
                )
            } else if (type == MEDIA_TYPE_GIF) {
                mediaFile = File(
                    mediaStorageDir.path + File.separator + "IMG_"
                            + timeStamp + index + ".gif"
                )
            } else {
                return null
            }
            if (!mediaFile.exists()) {
                break
            }
            index = "_$count" // try to find a unique filename
        }

        return mediaFile
    }


    fun broadcastFile(
        context: Context, file: File, isNewPicture: Boolean,
        isNewVideo: Boolean, orientation: Int, listener: OnScanCompletedListener?
    ) {
        // note that the new method means that the new folder shows up as a file
        // when connected to a PC via MTP (at least tested on Windows 8)
        if (file.isDirectory) {
            // this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            // Uri.fromFile(file)));
            // ACTION_MEDIA_MOUNTED no longer allowed on Android 4.4! Gives:
            // SecurityException: Permission Denial: not allowed to send
            // broadcast android.intent.action.MEDIA_MOUNTED
            // note that we don't actually need to broadcast anything, the
            // folder and contents appear straight away (both in Gallery on
            // device, and on a PC when connecting via MTP)
            // also note that we definitely don't want to broadcast
            // ACTION_MEDIA_SCANNER_SCAN_FILE or use scanFile() for folders, as
            // this means the folder shows up as a file on a PC via MTP (and
            // isn't fixed by rebooting!)
            listener?.onScanCompleted(file.absolutePath, null, orientation)
        } else {
            // both of these work fine, but using
            // MediaScannerConnection.scanFile() seems to be preferred over
            // sending an intent
            // this.sendBroadcast(new
            // Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
            // Uri.fromFile(file)));
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null) { path, uri ->
                if (isNewPicture) {
                    // note, we reference the string directly rather than
                    // via Camera.ACTION_NEW_PICTURE, as the latter class is
                    // now deprecated - but we still need to broadcase the
                    // string for other apps
                    context.sendBroadcast(Intent("android.hardware.action.NEW_PICTURE", uri))
                    // for compatibility with some apps - apparently this is
                    // what used to be broadcast on Android?
                    context.sendBroadcast(Intent("com.android.camera.NEW_PICTURE", uri))
                } else if (isNewVideo) {
                    context.sendBroadcast(Intent("android.hardware.action.NEW_VIDEO", uri))
                }
                try {
                    val cr = context.contentResolver
                    val cv = ContentValues()
                    cv.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis())
                    cr.update(uri, cv, null, null)
                } catch (e: Throwable) {
                }

                listener?.onScanCompleted(path, uri, orientation)
            }
        }
    }

    fun broadcastVideoFile(
        context: Context, fileSaved: File, width: Int, height: Int,
        rotation: Int, duration: Long, location: Array<String>?,
        listener: FolderHelper.OnScanCompletedListener?
    ) {
        try {
            var title: String? = fileSaved.name
            if (title != null) {
                val index = title.lastIndexOf(".")
                if (index > 0) {
                    title = title.substring(0, index)
                }
            }
            val dateTaken = System.currentTimeMillis()
            val values = ContentValues()
            values.put(Video.Media.TITLE, title)
            values.put(Video.Media.DISPLAY_NAME, fileSaved.name)
            values.put(Video.Media.DATE_TAKEN, dateTaken)
            values.put(Video.Media.DATE_MODIFIED, dateTaken / 1000)
            values.put(Video.Media.MIME_TYPE, "video/mp4")
            values.put(Video.Media.DATA, fileSaved.absolutePath)
            if ("90".toInt() == rotation || "270".toInt() == rotation) {
                values.put(Video.Media.RESOLUTION, height.toString() + "x" + width)
                setVideoSize(values, height, width)
            } else {
                values.put(Video.Media.RESOLUTION, width.toString() + "x" + height)
                setVideoSize(values, width, height)
            }
            if (location != null) {
                values.put(Video.Media.LONGITUDE, location[0])
                values.put(Video.Media.LATITUDE, location[1])
            }
            values.put(Video.Media.SIZE, fileSaved.length())
            values.put(Video.Media.DURATION, duration)
            val uri = context.contentResolver
                .insert(Uri.parse(VIDEO_BASE_URI), values)
            if (uri != null) {
                listener?.onScanCompleted(fileSaved.absolutePath, uri, 0)
            } else {
                throw RuntimeException()
            }
        } catch (tr: Throwable) {
            // need to scan when finished, so we update for the
            // completed file
            FolderHelper.broadcastFile(
                context, fileSaved, false,
                true, 0, listener
            )
        }

    }

    fun broadcastVideoFile(
        context: Context, fileSaved: File, location: Location?,
        listener: FolderHelper.OnScanCompletedListener?
    ) {
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(fileSaved.absolutePath)
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            val height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            mmr.release()
            var title: String? = fileSaved.name
            if (title != null) {
                val index = title.lastIndexOf(".")
                if (index > 0) {
                    title = title.substring(0, index)
                }
            }
            val dateTaken = System.currentTimeMillis()
            val values = ContentValues()
            values.put(Video.Media.TITLE, title)
            values.put(Video.Media.DISPLAY_NAME, fileSaved.name)
            values.put(Video.Media.DATE_TAKEN, dateTaken)
            values.put(Video.Media.DATE_MODIFIED, dateTaken / 1000)
            values.put(Video.Media.MIME_TYPE, "video/mp4")
            values.put(Video.Media.DATA, fileSaved.absolutePath)
            if ("90" == rotation || "270" == rotation) {
                values.put(Video.Media.RESOLUTION, height + "x" + width)
                setVideoSize(values, height!!, width!!)
            } else {
                values.put(Video.Media.RESOLUTION, width + "x" + height)
                setVideoSize(values, width!!, height!!)
            }
            if (location != null) {
                values.put(Video.Media.LATITUDE, location.latitude)
                values.put(Video.Media.LONGITUDE, location.longitude)
            }
            values.put(Video.Media.SIZE, fileSaved.length())
            values.put(Video.Media.DURATION, duration)
            val uri = context.contentResolver
                .insert(Uri.parse(VIDEO_BASE_URI), values)
            if (uri != null) {
                listener?.onScanCompleted(fileSaved.absolutePath, uri, 0)
            } else {
                throw RuntimeException()
            }
        } catch (tr: Throwable) {
            // need to scan when finished, so we update for the
            // completed file
            FolderHelper.broadcastFile(
                context, fileSaved, false,
                true, 0, listener
            )
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setVideoSize(values: ContentValues, width: String, height: String) {
        // The two fields are available since ICS but got published in JB
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            values.put(Video.Media.WIDTH, width)
            values.put(Video.Media.HEIGHT, height)
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setVideoSize(values: ContentValues, width: Int, height: Int) {
        // The two fields are available since ICS but got published in JB
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            values.put(Video.Media.WIDTH, width)
            values.put(Video.Media.HEIGHT, height)
        }
    }

    fun asynAddImage(
        context: Context, fileName: String, mimeType: String,
        date: Long, location: Location, orientation: Int, jpegLength: Int,
        path: String, width: Int, height: Int,
        listener: OnScanCompletedListener?
    ) {
        object : Thread() {

            override fun run() {
                super.run()
                val uri = addImage(
                    context, fileName, mimeType, date, location, orientation,
                    jpegLength, path, width, height
                )
                listener?.onScanCompleted(path, uri, orientation)
            }

        }.start()
    }

    fun asynAddImage(
        context: Context, fileName: String, mimeType: String,
        date: Long, latitude: String, longitude: String, orientation: Int, jpegLength: Int,
        path: String, width: Int, height: Int,
        listener: OnScanCompletedListener?
    ) {
        object : Thread() {

            override fun run() {
                super.run()
                val uri = addImage(
                    context, fileName, mimeType, date, latitude, longitude, orientation,
                    jpegLength, path, width, height
                )
                if (listener != null && uri != null) {
                    listener.onScanCompleted(path, uri, orientation)
                }
            }

        }.start()
    }

    /**
     * Add the image to media store.
     */
    fun addImage(
        context: Context, fileName: String, mimeType: String,
        date: Long, location: Location?, orientation: Int, jpegLength: Int,
        path: String, width: Int, height: Int
    ): Uri? {
        var title: String? = fileName
        if (title != null) {
            val index = title.lastIndexOf(".")
            if (index > 0) {
                title = title.substring(0, index)
            }
        }
        // Insert into MediaStore.
        val values = ContentValues(9)
        values.put(ImageColumns.TITLE, title)
        values.put(ImageColumns.DISPLAY_NAME, fileName)
        values.put(ImageColumns.DATE_TAKEN, date)
        values.put(ImageColumns.MIME_TYPE, mimeType)
        // Clockwise rotation in degrees. 0, 90, 180, or 270.
        values.put(ImageColumns.ORIENTATION, orientation)
        values.put(ImageColumns.DATA, path)
        values.put(ImageColumns.SIZE, jpegLength)

        setImageSize(values, width, height)

        if (location != null) {
            values.put(ImageColumns.LATITUDE, location.latitude)
            values.put(ImageColumns.LONGITUDE, location.longitude)
        }

        var uri: Uri? = null
        try {
            uri = context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (th: Throwable) {
            // This can happen when the external volume is already mounted, but
            // MediaScanner has not notify MediaProvider to add that volume.
            // The picture is still safe and MediaScanner will find it and
            // insert it into MediaProvider. The only problem is that the user
            // cannot click the thumbnail to review the picture.
            Log.e(TAG, "Failed to write MediaStore$th")
        }

        return uri
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setImageSize(values: ContentValues, width: Int, height: Int) {
        // The two fields are available since ICS but got published in JB
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            values.put(MediaColumns.WIDTH, width)
            values.put(MediaColumns.HEIGHT, height)
        }
    }

    interface OnScanCompletedListener {
        fun onScanCompleted(path: String, uri: Uri?, orientation: Int)
    }

    /**
     * Add the image to media store
     */
    fun addImage(
        context: Context, fileName: String, mimeType: String,
        date: Long, latitude: String, longitude: String, orientation: Int, jpegLength: Int,
        path: String, width: Int, height: Int
    ): Uri? {
        var title: String? = fileName
        if (title != null) {
            val index = title.lastIndexOf(".")
            if (index > 0) {
                title = title.substring(0, index)
            }
        }
        // Insert into MediaStore.
        val values = ContentValues(9)
        values.put(ImageColumns.TITLE, title)
        values.put(ImageColumns.DISPLAY_NAME, fileName)
        values.put(ImageColumns.DATE_TAKEN, date)
        values.put(ImageColumns.MIME_TYPE, mimeType)
        // Clockwise rotation in degrees. 0, 90, 180, or 270.
        values.put(ImageColumns.ORIENTATION, orientation)
        values.put(ImageColumns.DATA, path)
        values.put(ImageColumns.SIZE, jpegLength)

        setImageSize(values, width, height)

        values.put(ImageColumns.LATITUDE, latitude)
        values.put(ImageColumns.LONGITUDE, longitude)

        var uri: Uri? = null
        try {
            uri = context.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)
        } catch (th: Throwable) {
            // This can happen when the external volume is already mounted, but
            // MediaScanner has not notify MediaProvider to add that volume.
            // The picture is still safe and MediaScanner will find it and
            // insert it into MediaProvider. The only problem is that the user
            // cannot click the thumbnail to review the picture.
            Log.e(TAG, "Failed to write MediaStore$th")
        }

        return uri
    }

    /**
     * Add the video to media store.
     */
    fun addVideo(
        context: Context, fileName: String, mimeType: String,
        date: Long, dataModify: Long, latitude: String, longitude: String, videoLength: Long, resolution: String,
        path: String, width: Int, height: Int, duration: Long
    ): Uri? {
        var title: String? = fileName
        if (title != null) {
            val index = title.lastIndexOf(".")
            if (index > 0) {
                title = title.substring(0, index)
            }
        }

        val values = ContentValues()
        values.put(Video.Media.TITLE, title)
        values.put(Video.Media.DISPLAY_NAME, fileName)
        values.put(Video.Media.DATE_TAKEN, date)
        values.put(Video.Media.DATE_MODIFIED, dataModify)
        values.put(Video.Media.MIME_TYPE, mimeType)
        values.put(Video.Media.DATA, path)
        values.put(Video.Media.RESOLUTION, resolution)
        values.put(Video.Media.LATITUDE, latitude)
        values.put(Video.Media.LONGITUDE, longitude)
        values.put(Video.Media.SIZE, videoLength)
        values.put(Video.Media.DURATION, duration)
        setVideoSize(values, width, height)
        var uri: Uri? = null
        try {
            uri = context.contentResolver.insert(Video.Media.EXTERNAL_CONTENT_URI, values)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to write MediaStore" + e.message)
        }

        return uri
    }

    fun getOrCreateEditCachePath(context: Context): File {
        val path = File(getCachePath(context).toString() + "/.edittemp")
        if (!path.exists()) {
            if (path.mkdirs()) {
                try {
                    File(path, ".nomedia").createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return path
    }

    fun getCachePath(context: Context): File {
        var externalCacheDir = context.externalCacheDir
        if (externalCacheDir != null && externalCacheDir.exists()) {
            return externalCacheDir
        }
        externalCacheDir = context.cacheDir
        return if (externalCacheDir != null && externalCacheDir.exists()) {
            externalCacheDir
        } else File(Environment.getExternalStorageDirectory().path + ("/Android/data/" + context.packageName + "/cache/"))
    }

    fun isDynamicMediaFile(fileName: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false
        }
        if (TextUtils.isEmpty(fileName)) {
            return false
        }
        return if (fileName.startsWith(EncryptUtil.DYNAMIC_START) && fileName.endsWith(".mp4")) {
            true
        } else false
    }

    fun isDynamicMediaFilePath(filePath: String): Boolean {
        var filePath = filePath
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        val index = filePath.lastIndexOf('/')
        if (index >= 0 && index < filePath.length - 1) {
            filePath = filePath.substring(index + 1)
        }
        return isDynamicMediaFile(filePath)
    }

    /**
     * 保存图片Exif信息
     *
     * @param imgFile
     * @param width
     * @param height
     * @param datetime
     * @param location
     */
    //    public static void setExif(File imgFile, int width, int height, int orientation, long datetime, Location location) {
    //        if (imgFile == null) {
    //            return;
    //        }
    //        try {
    //            ExifInterface exif = new ExifInterface(imgFile.getCanonicalPath());
    //
    //            if (location != null) {
    //                // String latitudeStr = "90/1,12/1,30/1";
    //                double lat = location.getLatitude();
    //                double alat = Math.abs(lat);
    //                String dms = Location.convert(alat, Location.FORMAT_SECONDS);
    //                String[] splits = dms.split(":");
    //                String[] secnds = (splits[2]).split("\\.");
    //                String seconds;
    //                if (secnds.length == 0) {
    //                    seconds = splits[2];
    //                } else {
    //                    seconds = secnds[0];
    //                }
    //
    //                String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    //                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);
    //
    //                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat > 0 ? "N" : "S");
    //
    //                double lon = location.getLongitude();
    //                double alon = Math.abs(lon);
    //
    //                dms = Location.convert(alon, Location.FORMAT_SECONDS);
    //                splits = dms.split(":");
    //                secnds = (splits[2]).split("\\.");
    //
    //                if (secnds.length == 0) {
    //                    seconds = splits[2];
    //                } else {
    //                    seconds = secnds[0];
    //                }
    //                String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    //
    //                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
    //                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon > 0 ? "E" : "W");
    //            }
    //            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(
    //                    com.android.gallery3d.exif.ExifInterface.getOrientationValueForRotation(orientation)));
    //            exif.setAttribute(ExifInterface.TAG_IMAGE_WIDTH, String.valueOf(width));
    //            exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH, String.valueOf(height));
    //
    //            if (datetime > 0) {
    //                String timeStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(new Date(datetime));
    //                exif.setAttribute(ExifInterface.TAG_DATETIME, timeStamp);
    //            }
    //            exif.setAttribute(ExifInterface.TAG_MAKE, ImageUtil.EXIF_TAG_MAKE);
    //            exif.saveAttributes();
    //        } catch (Throwable tr) {
    //            tr.printStackTrace();
    //        }
    //    }

    /**
     * 保存图片Exif信息
     *
     * @param imgFile
     * @param datetime
     */
    fun setExif(imgFile: File?, datetime: Long) {
        if (imgFile == null) {
            return
        }
        try {
            val exif = ExifInterface(imgFile.canonicalPath)
            if (datetime > 0) {
                val timeStamp = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(Date(datetime))
                exif.setAttribute(ExifInterface.TAG_DATETIME, timeStamp)
            }
            exif.setAttribute(ExifInterface.TAG_MAKE, ImageUtil.EXIF_TAG_MAKE)
            exif.saveAttributes()
        } catch (tr: Throwable) {
            tr.printStackTrace()
        }

    }

    /**
     * 检查是否需要移动到位置存储卡
     *
     * @param context
     * @param fileSaved
     * @param mimeType
     * @return 返回新的文件位置，如果不需要移动则返回原位置
     */
    fun checkMoveToExtSdcard(context: Context, fileSaved: File?, mimeType: String): File? {
        if (fileSaved == null) {
            return null
        }
        if (MEDIA_TEMP_FOLDER_PATH == fileSaved.parent) {
            val fileName = fileSaved.name
            val moveToFilePath = imageFolder.absolutePath + File.separator + fileName
            val os = ExtSdcardUtils.getExtCardOutputStream(context, moveToFilePath, mimeType)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(fileSaved)
                val buffer = ByteArray(1024)
                var count = 0

                while (fis.read(buffer).also { count = it } != -1) {
                    os?.write(buffer, 0, count)
                }

                os?.flush()
                fileSaved.delete()
                return File(moveToFilePath)
            } catch (tr: Throwable) {
                tr.printStackTrace()
            } finally {
                if (os != null) {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
        return fileSaved
    }

    /**
     * 保存图片Exif信息
     * 去除其他相机的Exif
     *
     * @param imgFile
     */
    fun removeModelExif(imgFile: File?) {
        if (imgFile == null) {
            return
        }
        try {
            val exif = ExifInterface(imgFile.canonicalPath)
            exif.setAttribute(ExifInterface.TAG_MAKE, ImageUtil.EXIF_TAG_MAKE)
            exif.setAttribute(ExifInterface.TAG_MODEL, ImageUtil.EXIF_TAG_MAKE)
            exif.saveAttributes()
        } catch (tr: Throwable) {
            tr.printStackTrace()
        }

    }
}

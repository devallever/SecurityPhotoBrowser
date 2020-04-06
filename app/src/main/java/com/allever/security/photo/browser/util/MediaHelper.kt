package com.allever.security.photo.browser.util

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.allever.lib.common.app.App
import com.allever.lib.common.util.log
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.ThumbnailBean
import java.io.File
import java.util.HashMap
import java.util.HashSet

object MediaHelper {

    val TYPE_IMAGE = "TYPE_IMAGE"
    val TYPE_VIDEO = "TYPE_VIDEO"

    private val COLUMN_BUCKET_ID = "bucket_id"
    private val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
    private val COLUMN_DATE_TAKEN = "datetaken"
    private val COLUMN_DATA = "_data"
    val COLUMN_URI = "uri"
    val COLUMN_COUNT = "count"
    private val QUERY_URI = MediaStore.Files.getContentUri("external")

    private val COLUMNS = arrayOf(
        MediaStore.Files.FileColumns._ID,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        COLUMN_URI,
        COLUMN_COUNT
    )

    private val PROJECTION_29 = arrayOf(
        MediaStore.Files.FileColumns._ID,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        COLUMN_DATE_TAKEN,
        COLUMN_DATA,
        MediaStore.MediaColumns.MIME_TYPE
    )

    // === params for showSingleMediaType: false ===
    private val SELECTION = (
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id")
    private val SELECTION_29 = (
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val SELECTION_ARGS = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    private val SELECTION_ARGS_IMAGE = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()
    )

    private val SELECTION_ARGS_VIDEO = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    // =============================================

    // === params for showSingleMediaType: true ===
    private val SELECTION_FOR_SINGLE_MEDIA_TYPE = (
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id")
    private val SELECTION_FOR_SINGLE_MEDIA_TYPE_29 = (
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
        return arrayOf(mediaType.toString())
    }
    // =============================================

    // === params for showSingleMediaType: true ===
    private val SELECTION_FOR_SINGLE_MEDIA_GIF_TYPE = (
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + "=?"
                    + ") GROUP BY (bucket_id")
    private val SELECTION_FOR_SINGLE_MEDIA_GIF_TYPE_29 = (
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + "=?")

    private fun getSelectionArgsForSingleMediaGifType(mediaType: Int): Array<String> {
        return arrayOf(mediaType.toString(), "image/gif")
    }
    // =============================================

    private val BUCKET_ORDER_BY = "$COLUMN_DATE_TAKEN DESC"

    fun getAlbumInfo(type: String, includeGif: Boolean): MutableList<ImageFolder> {

        val imageFolderList = mutableListOf<ImageFolder>()
        var cursor: Cursor? = null
        try {
            val contentResolver = App.context.contentResolver

            val selectionArgs = if (type == TYPE_IMAGE) {
                SELECTION_ARGS_IMAGE
            } else {
                SELECTION_ARGS_VIDEO
            }
            cursor = contentResolver.query(
                QUERY_URI,
                PROJECTION_29,
                SELECTION_FOR_SINGLE_MEDIA_TYPE_29,
                selectionArgs,
                BUCKET_ORDER_BY
            )

            // Pseudo GROUP BY
            val countMap = HashMap<Long, Long>()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val bucketId = cursor.getLong(cursor.getColumnIndex(COLUMN_BUCKET_ID))

                    var count = countMap[bucketId]
                    if (count == null) {
                        count = 1L
                    } else {
                        count++
                    }
                    countMap[bucketId] = count
                }
            }

            if (cursor != null) {
                if (cursor.moveToFirst()) {

                    val done = HashSet<Long>()

                    do {
                        val bucketId = cursor.getLong(cursor.getColumnIndex(COLUMN_BUCKET_ID))
                        log("bucketId = $bucketId")
                        if (done.contains(bucketId)) {
                            continue
                        }

                        val fileId = cursor.getLong(
                            cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                        )
                        log("id = $fileId")
                        val bucketDisplayName = cursor.getString(
                            cursor.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME)
                        )
                        log("displayName = $bucketDisplayName")
                        val mimeType = cursor.getString(
                            cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                        )
                        log("mimeType = $mimeType")
                        val uri = getUri(cursor)
                        log("uri = $uri")
                        val count = countMap[bucketId]!!
                        log("count = $count")
                        val date = cursor.getLong(
                            cursor.getColumnIndex(COLUMN_DATE_TAKEN)
                        )
                        log("date = $date")
                        val filePath = cursor.getString(
                            cursor.getColumnIndex(COLUMN_DATA)
                        )
                        val path = filePath.substring(0, filePath.lastIndexOf(File.separatorChar))
                        log("path = $path")

                        //-----------------------------------------
                        val imageFolder = ImageFolder()
                        imageFolder.count = count.toInt()
                        val bean = ThumbnailBean()
                        bean.date = date
                        bean.path = filePath

                        bean.uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            fileId
                        )
                        if (bucketDisplayName == null) {
                            imageFolder.setDirAndName(path)
                        } else {
                            imageFolder.dir = path
                            imageFolder.name = bucketDisplayName
                        }
                        imageFolder.bucketId = bucketId.toString()

                        if (type == TYPE_IMAGE) {
                            when {
                                MediaFile.isGifFileType(bean.path) -> bean.type =
                                    MediaTypeUtil.TYPE_GIF
                                MediaFile.isJPGFileType(bean.path) -> bean.type =
                                    MediaTypeUtil.TYPE_JPG
                                MediaFile.isPNGFileType(bean.path) -> bean.type =
                                    MediaTypeUtil.TYPE_PNG
                                else -> bean.type = MediaTypeUtil.TYPE_OTHER_IMAGE
                            }

                            if (!includeGif) {
                                val imgExcludeGif =
                                    ImageHelper.getImageThumbnailBeanFromPathExcludeGif(
                                        App.context,
                                        path
                                    )
                                if (imgExcludeGif != null && imgExcludeGif.size > 0) {
                                    imageFolder.setFirstImageBean(imgExcludeGif[0])
                                    imageFolder.count = imgExcludeGif.size
                                    imageFolderList.add(imageFolder)
                                }
                            } else {
                                imageFolder.setFirstImageBean(bean)
                                imageFolderList.add(imageFolder)
                            }
                        } else {
                            bean.type = MediaTypeUtil.TYPE_VIDEO
                            imageFolder.setFirstImageBean(bean)
                            imageFolderList.add(imageFolder)
                        }

                        done.add(bucketId)

                        log("-----------------------------------------\n\n")
                    } while (cursor.moveToNext())
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return imageFolderList
    }

    private fun getUri(cursor: Cursor): Uri {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
        val mimeType = cursor.getString(
            cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
        )
        val contentUri: Uri

        contentUri = when {
            MimeType.isImage(mimeType) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            MimeType.isVideo(mimeType) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> // ?
                MediaStore.Files.getContentUri("external")
        }

        return ContentUris.withAppendedId(contentUri, id)
    }


    fun getImageFloderInfo() {
        val contentResolver = App.context.contentResolver
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            "count(" + MediaStore.Images.ImageColumns.BUCKET_ID + ") as count",
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.ImageColumns.BUCKET_ID
        )
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                uri,
                projection,
                ") group by (\"${MediaStore.Images.ImageColumns.BUCKET_ID}\"",
                null,
                null
            )
            if (cursor?.moveToFirst() == true) {
                val idIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                val pathIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                val dateIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)
                val nameIndex =
                    cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
                val numIndex = cursor.getColumnIndex("count")
                val bucketIdIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)
                do {
                    val imageFolder = ImageFolder()

                    val count = cursor.getInt(numIndex)
                    log("count = $count")
                    val name = cursor.getString(nameIndex)
                    log("name = $name")
                    val bean = ThumbnailBean()
                    bean.date = cursor.getLong(dateIndex)
                    log("date = ${bean.date}")
//                    bean.setDegree(c1.getInt(degreeIndex));
                    bean.path = cursor.getString(pathIndex)
                    log("path = ${bean.path}")
                    when {
                        MediaFile.isGifFileType(bean.path) -> bean.type = MediaTypeUtil.TYPE_GIF
                        MediaFile.isJPGFileType(bean.path) -> bean.type = MediaTypeUtil.TYPE_JPG
                        MediaFile.isPNGFileType(bean.path) -> bean.type = MediaTypeUtil.TYPE_PNG
                        else -> bean.type = MediaTypeUtil.TYPE_OTHER_IMAGE
                    }
                    val id = cursor.getInt(idIndex).toLong()
                    log("id = $id")
                    bean.uri =
                        ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                    val dir = bean.path.substring(0, bean.path.lastIndexOf(File.separator))
                    if (name == null) {
                        imageFolder.setDirAndName(dir)
                    } else {
                        imageFolder.dir = dir
                        imageFolder.name = name
                    }
                    val buckedId = cursor.getString(bucketIdIndex)
                    log("buckedId = $buckedId")
                    imageFolder.bucketId = buckedId

                    log("-----------------------------------------------------------\n\n")
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

    }
}
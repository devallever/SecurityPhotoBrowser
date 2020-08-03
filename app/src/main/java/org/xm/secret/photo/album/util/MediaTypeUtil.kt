package org.xm.secret.photo.album.util


object MediaTypeUtil {
    /**
     * png jpg
     */
    val TYPE_OTHER_IMAGE = 0

    /**
     * GIF
     */
    val TYPE_GIF = 1

    /**
     * png
     */
    val TYPE_PNG = 2

    /**
     * jpg
     */
    val TYPE_JPG = 3

    /**
     * video
     */
    val TYPE_VIDEO = 4

    /**
     * text
     */
    val TYPE_TEXT = 5

    fun isGif(type: Int): Boolean {
        return type == TYPE_GIF
    }

    fun isPNG(type: Int): Boolean {
        return type == TYPE_PNG
    }

    fun isJPG(type: Int): Boolean {
        return type == TYPE_JPG
    }

    fun isJPGOrPNG(type: Int): Boolean {
        return type == TYPE_JPG || type == TYPE_PNG
    }

    fun isOtherImage(type: Int): Boolean {
        return type == TYPE_OTHER_IMAGE
    }

    fun isImage(type: Int): Boolean {
        return type == TYPE_OTHER_IMAGE || type == TYPE_GIF || type == TYPE_PNG || type == TYPE_JPG
    }

    fun isVideo(type: Int): Boolean {
        return type == TYPE_VIDEO
    }

    fun isText(type: Int): Boolean {
        return type == TYPE_TEXT
    }
}

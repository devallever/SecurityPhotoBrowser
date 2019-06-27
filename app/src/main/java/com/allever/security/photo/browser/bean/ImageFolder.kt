package com.allever.security.photo.browser.bean

import java.util.ArrayList

class ImageFolder {
    /**
     * 图片的文件夹路径(包含名称)
     */
    var dir: String? = null

    /**
     * 文件夹的名称
     */
    var name: String? = null

    /**
     * 文件夹的名称
     */
    var bucketId: String? = null

    /**
     * 所有ThumbnailBean
     */
    var data: ArrayList<ThumbnailBean>? = null

    /**
     * Photo ThumbnailBean
     */
    var photoThumbnailBeans: ArrayList<ThumbnailBean>? = null

    /**
     * Video ThumbnailBean
     */
    var videoThumbnailBeans: ArrayList<ThumbnailBean>? = null

    /**
     * 图片的数量
     */
    var count: Int = 0

    var photoCount: Int = 0

    var videoCount: Int = 0

    /**
     * 第一张图片的ThumbnailBean
     */
    var firstThumbnailBean: ThumbnailBean? = null
        private set

    var isNeedRefresh: Boolean = false

    val isDataInit: Boolean
        get() = data != null

    var isChecked: Boolean = false
//        get() = firstThumbnailBean!!.isChecked()
//        set(flag) {
//            firstThumbnailBean!!.setChecked(flag)
//        }

    constructor() {
        count = 0
    }

    constructor(count: Int) {
        this.count = count
    }

    fun setDirAndName(dir: String) {
        this.dir = dir
        val lastIndexOf = this.dir!!.lastIndexOf("/")
        this.name = this.dir!!.substring(lastIndexOf)
    }

    fun setFirstImageBean(thumbnailBean: ThumbnailBean) {
        this.firstThumbnailBean = thumbnailBean
    }
}

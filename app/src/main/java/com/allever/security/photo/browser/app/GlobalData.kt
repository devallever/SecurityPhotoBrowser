package com.allever.security.photo.browser.app

import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.util.MediaTypeUtil


object GlobalData {
    //相册列表数据
    var albumData = mutableListOf<ImageFolder>()

    fun cloneAlbumData(data: MutableList<ImageFolder>) {
        albumData.clear()
        for (imageFolder in data) {
            val cloneImageFolder = ImageFolder()
            cloneImageFolder.name = imageFolder.name
            cloneImageFolder.bucketId = imageFolder.bucketId
            cloneImageFolder.dir = imageFolder.dir
            cloneImageFolder.count = imageFolder.count
            cloneImageFolder.setFirstImageBean(imageFolder.firstThumbnailBean)
            cloneImageFolder.isChecked = false
            cloneImageFolder.isNeedRefresh = imageFolder.isNeedRefresh
            cloneImageFolder.videoCount = imageFolder.videoCount

            //all data
            val thumbnailList = ArrayList<ThumbnailBean>()
            //image
            val imageThumbnailList = ArrayList<ThumbnailBean>()
            //video
            val videoThumbnailList = ArrayList<ThumbnailBean>()

            for (thumbnail in imageFolder.data!!) {
                val cloneThumbnail = cloneThumbnail(thumbnail)
                thumbnailList.add(cloneThumbnail)

                if (MediaTypeUtil.isImage(cloneThumbnail.type)) {
                    imageThumbnailList.add(cloneThumbnail)
                } else if (MediaTypeUtil.isVideo(cloneThumbnail.type)) {
                    videoThumbnailList.add(cloneThumbnail)
                }
            }
            cloneImageFolder.data = thumbnailList

            cloneImageFolder.photoThumbnailBeans = imageThumbnailList
            cloneImageFolder.videoThumbnailBeans = videoThumbnailList

            albumData.add(cloneImageFolder)
        }
    }

    private fun cloneThumbnail(thumbnail: ThumbnailBean): ThumbnailBean {
        val cloneThumbnail = ThumbnailBean()
//        cloneThumbnail.isChecked = false
        cloneThumbnail.path = thumbnail.path
        cloneThumbnail.uri = thumbnail.uri
        cloneThumbnail.type = thumbnail.type
        cloneThumbnail.date = thumbnail.date
//        cloneThumbnail.degree = thumbnail.degree
        cloneThumbnail.duration = thumbnail.duration
        cloneThumbnail.selectCount = thumbnail.selectCount
        cloneThumbnail.sourceType = thumbnail.sourceType
        cloneThumbnail.tempPath = thumbnail.tempPath
        return cloneThumbnail
    }
}
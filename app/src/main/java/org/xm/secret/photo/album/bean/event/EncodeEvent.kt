package org.xm.secret.photo.album.bean.event

import org.xm.secret.photo.album.bean.ThumbnailBean

class EncodeEvent {
    var albumName: String? = null
    var thumbnailBeanList = mutableListOf<ThumbnailBean>()
}
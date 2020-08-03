package org.xm.secret.photo.album.bean.event

import org.xm.secret.photo.album.bean.ThumbnailBean

class DecodeEvent {
    var needRefresh = true
    var indexList = mutableListOf<Int>()
    var index = 0
    var thumbnailBeanList = mutableListOf<ThumbnailBean>()
}
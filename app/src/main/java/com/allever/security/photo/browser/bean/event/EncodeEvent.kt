package com.allever.security.photo.browser.bean.event

import com.allever.security.photo.browser.bean.ThumbnailBean

class EncodeEvent {
    var albumName: String? = null
    var thumbnailBeanList = mutableListOf<ThumbnailBean>()
}
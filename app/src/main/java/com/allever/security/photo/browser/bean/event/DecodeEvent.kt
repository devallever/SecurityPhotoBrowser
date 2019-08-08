package com.allever.security.photo.browser.bean.event

import com.allever.security.photo.browser.bean.ThumbnailBean

class DecodeEvent {
    var needRefresh = true
    var indexList = mutableListOf<Int>()
    var index = 0
    var thumbnailBeanList = mutableListOf<ThumbnailBean>()
}
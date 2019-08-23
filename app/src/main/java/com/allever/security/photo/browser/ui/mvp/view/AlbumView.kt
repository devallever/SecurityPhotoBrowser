package com.allever.security.photo.browser.ui.mvp.view

import com.allever.security.photo.browser.bean.ImageFolder

interface AlbumView {
    fun updateAlbumList(data: MutableList<ImageFolder>)
    fun updateAddAlbum(data: ImageFolder)
    fun updateDeleteAlbum()
    fun updateRenameAlbum(albumName: String, dir: String)

    fun showBottomDialog()
    fun hideBottomDialog()
}
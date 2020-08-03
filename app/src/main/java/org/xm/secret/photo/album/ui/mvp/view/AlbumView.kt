package org.xm.secret.photo.album.ui.mvp.view

import org.xm.secret.photo.album.bean.ImageFolder

interface AlbumView {
    fun updateAlbumList(data: MutableList<ImageFolder>)
    fun updateAddAlbum(data: ImageFolder)
    fun updateDeleteAlbum(position: Int)
    fun updateRenameAlbum(position: Int, albumName: String, dir: String)

    fun showBottomDialog()
    fun hideBottomDialog()
}
package org.xm.secret.photo.album.ui.mvp.view

interface PreviewView {
    fun showLoading()
    fun hideLoading()

    fun showDeleteDialog()
    fun hideDeleteDialog()
}
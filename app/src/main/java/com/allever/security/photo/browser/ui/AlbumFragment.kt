package com.allever.security.photo.browser.ui

import android.view.View
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Fragment
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.ui.mvp.presenter.AlbumPresenter
import com.allever.security.photo.browser.ui.mvp.view.AlbumView

class AlbumFragment: Base2Fragment<AlbumView, AlbumPresenter>(), AlbumView {
    override fun getContentView(): Int = R.layout.fragment_album

    override fun initView(root: View) {
    }

    override fun initData() {
    }

    override fun createPresenter(): AlbumPresenter = AlbumPresenter()

    override fun updateAlbumList(data: MutableList<ImageFolder>) {
    }

    override fun updateAddAlbum(data: ImageFolder) {
    }

    override fun updateDeleteAlbum(position: Int) {
    }

    override fun updateRenameAlbum(position: Int, albumName: String, dir: String) {
    }

    override fun showBottomDialog() {
    }

    override fun hideBottomDialog() {
    }
}
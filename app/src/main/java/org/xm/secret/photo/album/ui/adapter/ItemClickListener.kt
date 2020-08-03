package org.xm.secret.photo.album.ui.adapter

interface ItemClickListener {
    fun onItemClick(position: Int)
    fun onItemLongClick(position: Int, item: Any)
}
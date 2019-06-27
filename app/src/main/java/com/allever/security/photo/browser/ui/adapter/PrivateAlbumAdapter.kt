package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.widget.ImageView

import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.R

import com.allever.security.photo.browser.bean.ImageFolder
import com.bumptech.glide.Glide

class PrivateAlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {

    var listener: ItemClickListener? = null

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {
        val albumName = item.name ?: "未命名"
        val totalCount = item.count
        holder.setText(R.id.item_tv_album_name, "$albumName($totalCount)")
        holder.setText(R.id.item_tv_album_video_count, item.videoCount.toString())
        holder.setText(R.id.item_tv_album_image_count, item.photoCount.toString())

        Glide.with(mContext).load(item.firstThumbnailBean?.uri).into(holder.getView(R.id.iv_image)!!)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}
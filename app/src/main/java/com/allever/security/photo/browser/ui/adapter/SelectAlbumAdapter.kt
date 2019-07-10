package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ImageFolder
import com.bumptech.glide.Glide

class SelectAlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {
    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {
        Glide.with(mContext).load(item.firstThumbnailBean?.path).into(holder.getView(R.id.iv_image)!!)
        holder.setText(R.id.tv_album_name, item.name ?: "")
        holder.setText(R.id.tv_video_count, item.videoCount.toString())
        holder.setText(R.id.tv_photo_count, item.photoCount.toString())
    }
}
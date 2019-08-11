package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView

import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.R

import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.util.ImageUtil
import com.allever.security.photo.browser.util.MediaTypeUtil
import com.bumptech.glide.Glide

class PrivateAlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {

    var listener: ItemClickListener? = null

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {
        val albumName = item.name ?: "未命名"
        val totalCount = item.count
        holder.setText(R.id.item_tv_album_name, "$albumName($totalCount)")

        //相册信息
        var videoCount = 0
        var imageCount = 0
        //获取图片数和视频数
        item.data?.forEach { bean ->
            if (MediaTypeUtil.isVideo(bean.type)) {
                videoCount++
            } else {
                imageCount++
            }
        }

        holder.setText(R.id.item_tv_album_video_count, videoCount.toString())
        holder.setText(R.id.item_tv_album_image_count, imageCount.toString())

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }

        holder.setOnClickListener(R.id.item_private_album_iv_menu,
            View.OnClickListener {
                listener?.onMoreClick(position)
            }
        )

        holder.setVisible(R.id.iv_image, item.data?.isNotEmpty() == true)
        holder.setVisible(R.id.id_iv_default_cover, item.data?.isEmpty() == true)
        if (item.data?.isNotEmpty() == true) {
            ImageUtil.loadEncodeImage(mContext, item.data!![0], holder.getView(R.id.iv_image)!!)
        }
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
        fun onMoreClick(position: Int){}
    }
}
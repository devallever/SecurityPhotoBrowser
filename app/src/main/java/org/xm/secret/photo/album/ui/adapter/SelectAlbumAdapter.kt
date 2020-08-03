package org.xm.secret.photo.album.ui.adapter

import android.content.Context
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import org.xm.secret.photo.album.R
import org.xm.secret.photo.album.bean.ImageFolder
import com.bumptech.glide.Glide

class SelectAlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {
    private var mListener: OnItemClickListener? = null
    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {
        Glide.with(mContext).load(item.firstThumbnailBean?.path).into(holder.getView(R.id.iv_image)!!)
        holder.setText(R.id.tv_album_name, item.name ?: "")
        holder.setText(R.id.tv_video_count, item.videoCount.toString())
        holder.setText(R.id.tv_photo_count, item.photoCount.toString())
        holder.itemView.setOnClickListener {
            mListener?.onChooseAlbumAdapterItemClick(item, position)
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mListener = onItemClickListener
    }

    public interface OnItemClickListener {
        fun onChooseAlbumAdapterItemClick(imageFolder: ImageFolder, position: Int)
    }
}
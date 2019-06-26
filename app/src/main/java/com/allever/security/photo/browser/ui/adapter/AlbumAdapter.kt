package com.allever.security.photo.browser.ui.adapter

import android.content.Context

import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder

import com.allever.security.photo.browser.bean.ImageFolder

class AlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {

    var listener: ItemClickListener? = null

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}
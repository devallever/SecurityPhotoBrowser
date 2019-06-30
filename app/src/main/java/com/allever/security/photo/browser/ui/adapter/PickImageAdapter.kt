package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.bumptech.glide.Glide

class PickImageAdapter(context: Context, layoutResId: Int, data: MutableList<ThumbnailBean>) :
    BaseRecyclerViewAdapter<ThumbnailBean>(context, layoutResId, data) {

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ThumbnailBean) {
        Glide.with(mContext).load(item.path).into(holder.getView(R.id.pick_iv_image)!!)
    }
}
package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.bean.ThumbnailBean

class PickImageAdapter(context: Context, layoutResId: Int, data: MutableList<ThumbnailBean>) :
    BaseRecyclerViewAdapter<ThumbnailBean>(context, layoutResId, data) {

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ThumbnailBean) {

    }
}
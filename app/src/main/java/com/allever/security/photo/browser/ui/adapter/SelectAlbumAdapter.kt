package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.bean.ImageFolder

class SelectAlbumAdapter(context: Context, layoutResId: Int, data: MutableList<ImageFolder>) :
    BaseRecyclerViewAdapter<ImageFolder>(context, layoutResId, data) {
    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ImageFolder) {

    }
}
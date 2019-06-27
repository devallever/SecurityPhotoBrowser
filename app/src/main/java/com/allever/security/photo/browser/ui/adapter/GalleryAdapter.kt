package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.MultiItemCommonAdapter
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport

class GalleryAdapter(context: Context, data: MutableList<Any>, multiItemTypeSupport: MultiItemTypeSupport<Any>) :
    MultiItemCommonAdapter<Any>(context, data, multiItemTypeSupport) {

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: Any) {

    }

}
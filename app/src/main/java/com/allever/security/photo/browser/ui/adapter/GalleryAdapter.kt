package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.MultiItemCommonAdapter
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.android.absbase.utils.DeviceUtils

class GalleryAdapter(context: Context, data: MutableList<Any>, val multiItemTypeSupport: MultiItemTypeSupport<Any>) :
    MultiItemCommonAdapter<Any>(context, data, multiItemTypeSupport) {

    var itemClickListener: ItemClickListener? = null

    private var mItemWidth = 0
    init {
        val screenWidth = DeviceUtils.SCREEN_WIDTH_PX.toFloat()
        val margin = DeviceUtils.dip2px(4f)
        mItemWidth = Math.round((screenWidth - margin * 4) / 3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (viewType == GALLERY_ITEM_TYPE_IMAGE) {
            val itemView = LayoutInflater.from(mContext).inflate(multiItemTypeSupport.getLayoutId(0), parent,
                false)
            val holder = BaseViewHolder(mContext, itemView)
            val lp = itemView.layoutParams
            //根据宽高决定高度
            lp.height = mItemWidth
            itemView.layoutParams = lp
            itemView.tag = holder
            return holder
        }

        return super.onCreateViewHolder(parent, viewType)
    }


    override fun bindHolder(holder: BaseViewHolder, position: Int, item: Any) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            itemClickListener?.onItemLongClick(position)
            return@setOnLongClickListener true
        }
    }

    companion object {
        const val GALLERY_ITEM_TYPE_NONE = -1
        const val GALLERY_ITEM_TYPE_IMAGE = 0
        const val GALLERY_ITEM_TYPE_SEPARATOR = 1
    }

}
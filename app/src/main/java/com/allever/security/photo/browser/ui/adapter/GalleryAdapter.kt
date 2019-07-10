package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.allever.lib.common.app.App.Companion.context
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.lib.common.ui.widget.recycler.MultiItemCommonAdapter
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.function.endecode.DecodeListener
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.util.ImageUtil
import com.allever.security.photo.browser.util.MD5
import com.android.absbase.utils.DeviceUtils
import com.bumptech.glide.Glide
import java.io.File

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
            val itemView = LayoutInflater.from(mContext).inflate(
                multiItemTypeSupport.getLayoutId(0), parent,
                false
            )
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
        if (item is ThumbnailBean) {
            ImageUtil.loadEncodeImage(context, item, holder.getView(R.id.gallery_iv_image)!!)
        }

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
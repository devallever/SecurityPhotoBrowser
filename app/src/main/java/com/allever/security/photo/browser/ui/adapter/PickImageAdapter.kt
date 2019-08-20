package com.allever.security.photo.browser.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.allever.lib.common.ui.widget.recycler.BaseRecyclerViewAdapter
import com.allever.lib.common.ui.widget.recycler.BaseViewHolder
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.android.absbase.utils.DeviceUtils
import com.bumptech.glide.Glide

class PickImageAdapter(context: Context, val layoutResId: Int, data: MutableList<ThumbnailBean>) :
    BaseRecyclerViewAdapter<ThumbnailBean>(context, layoutResId, data) {
    private var mItemWidth = 0

    init {
        val screenWidth = DeviceUtils.SCREEN_WIDTH_PX.toFloat()
        val margin = DeviceUtils.dip2px(4f)
        mItemWidth = Math.round((screenWidth - margin * 5) / 4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(
            layoutResId, parent,
            false
        )
        val holder = BaseViewHolder(mContext, itemView)
        val lp = itemView.layoutParams
//        lp.width = mItemWidth.toInt()
        //根据宽高决定高度
        lp.height = mItemWidth
        itemView.layoutParams = lp
        itemView.tag = holder
        return holder
    }

    override fun bindHolder(holder: BaseViewHolder, position: Int, item: ThumbnailBean) {
        Glide.with(mContext).load(item.path).into(holder.getView(R.id.pick_iv_image)!!)
        if (item.isChecked) {
            holder.setImageResource(R.id.pick_iv_select, R.drawable.icon_album_select)
        } else {
            holder.setImageResource(R.id.pick_iv_select, R.drawable.icon_album_unselected)
        }

    }

}
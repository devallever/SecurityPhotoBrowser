package com.allever.security.photo.browser.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allever.lib.common.app.BaseFragment
import com.allever.lib.common.app.App
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.ui.adapter.PickImageAdapter
import com.android.absbase.utils.ToastUtils

class PickFragment : BaseFragment() {

    var callback: PickCallback? = null
    var type: TabModel.Tab? = null

    private lateinit var mRecyclerView: RecyclerView
    private var mPickImageAdapter: PickImageAdapter? = null
    private val mData = mutableListOf<ThumbnailBean>()

    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = LayoutInflater.from(context).inflate(R.layout.fragment, container, false)
        initView()
        initData()
        return mView
    }

    private fun initView() {
        DLog.d("initView")
        mRecyclerView = mView.findViewById(R.id.pick_recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(activity, 4)

        mPickImageAdapter = PickImageAdapter(App.context, R.layout.item_pick, mData)
        mRecyclerView.adapter = mPickImageAdapter
        mPickImageAdapter?.optionListener = object : PickImageAdapter.OptionListener {
            override fun onItemClick(position: Int, item: ThumbnailBean) {
                ToastUtils.show("onItem click ${item.path}")
                callback?.onPickItemClick(item)
            }

            override fun onItemLongClick(position: Int, item: ThumbnailBean): Boolean {
                ToastUtils.show("onItem onPickItemLongClick ${item.path}")
                callback?.onPickItemLongClick(item)
                return true
            }

        }
//        val emptyView = mView.findViewById<View>(R.id.empty_view)
//        val emptyIcon = mView.findViewById<ImageView>(R.id.iv_empty_type)
//        val resId = type?.emptyIconResId
//        if(resId != null){
//            emptyIcon.setImageResource(resId)
//        }
    }

    private fun initData() {

    }


    fun updateData(data: MutableList<ThumbnailBean>?) {
        data ?: return
        mData.clear()
        mData.addAll(data)
        mPickImageAdapter?.notifyDataSetChanged()
    }


    fun updateData(data: ThumbnailBean?) {
        data ?: return
        val index = mData.indexOf(data)
        mPickImageAdapter?.notifyItemChanged(index, index)
    }

    interface PickCallback {
        fun onPickItemClick(thumbnailBean: ThumbnailBean)
        fun onPickItemLongClick(thumbnailBean: ThumbnailBean)
    }
}
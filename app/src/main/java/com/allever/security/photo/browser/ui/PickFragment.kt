package com.allever.security.photo.browser.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allever.lib.common.app.BaseFragment
import com.allever.lib.common.app.App
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.ui.adapter.PickImageAdapter

class PickFragment : BaseFragment() {

    var callback: PickCallback? = null
    var type: TabModel.Tab? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPickImageAdapter: PickImageAdapter
    private val mData = mutableListOf<ThumbnailBean>()

    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //test
        for (index in 0..30) {
            val thumbnailBean = ThumbnailBean()
            mData.add(thumbnailBean)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = LayoutInflater.from(context).inflate(R.layout.fragment, container, false)
        initView()
        initData()
        return mView
    }

    private fun initView() {
        mRecyclerView = mView.findViewById(R.id.pick_recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(activity, 4)

        mPickImageAdapter = PickImageAdapter(App.context, R.layout.item_pick, mData)
        mRecyclerView.adapter = mPickImageAdapter

//        val emptyView = mView.findViewById<View>(R.id.empty_view)
//        val emptyIcon = mView.findViewById<ImageView>(R.id.iv_empty_type)
//        val resId = type?.emptyIconResId
//        if(resId != null){
//            emptyIcon.setImageResource(resId)
//        }
    }

    private fun initData() {

    }

    interface PickCallback {

    }
}
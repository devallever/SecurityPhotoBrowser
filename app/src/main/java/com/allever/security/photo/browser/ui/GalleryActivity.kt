package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.bean.SeparatorBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.ui.adapter.GalleryAdapter

class GalleryActivity: BaseActivity(), View.OnClickListener {


    private lateinit var mBtnPick: View

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGalleryAdapter: GalleryAdapter
    private val mGalleryData = mutableListOf<Any>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        initTestData()

        initView()

        initData()
    }

    private fun initView() {
        mBtnPick = findViewById(R.id.gallery_btn_pick)
        mBtnPick.setOnClickListener(this)
        mRecyclerView = findViewById(R.id.gallery_recycler_view)
        val gridLayoutManager = GridLayoutManager(this, 3)
        //解决最后单个跨列问题
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemType = mGalleryAdapter.getItemViewType(position)
                return if (itemType == GALLERY_ITEM_TYPE_SEPARATOR){
                    3
                }else{
                    1
                }
            }
        }
        mRecyclerView.layoutManager = gridLayoutManager
        mGalleryAdapter = GalleryAdapter(this, mGalleryData, object : MultiItemTypeSupport<Any> {
            override fun getLayoutId(itemType: Int): Int {
                return when(itemType) {
                    GALLERY_ITEM_TYPE_IMAGE -> {
                        R.layout.item_gallery
                    }
                    GALLERY_ITEM_TYPE_SEPARATOR -> {
                        R.layout.item_seperator
                    }
                    else -> {
                        0
                    }
                }
            }

            override fun getItemViewType(position: Int, t: Any): Int {
                val obj = mGalleryData[position]
                return when(obj) {
                    is ThumbnailBean -> {
                        GALLERY_ITEM_TYPE_IMAGE
                    }
                    is  SeparatorBean-> {
                        GALLERY_ITEM_TYPE_SEPARATOR

                    }
                    else -> {
                        GALLERY_ITEM_TYPE_NONE
                    }
                }
            }
        })

        mRecyclerView.adapter = mGalleryAdapter
    }

    private fun initData() {

    }

    override fun onClick(v: View?) {
        when(v) {
            mBtnPick -> {
                PickActivity.start(this)
            }
        }
    }


    private fun initTestData() {
        val separatorBean = SeparatorBean()
        val thumbnailBean = ThumbnailBean()
        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)

        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)
        mGalleryData.add(thumbnailBean)

        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)

        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)

        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)

        mGalleryData.add(separatorBean)
        mGalleryData.add(thumbnailBean)

    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, GalleryActivity::class.java)
            context.startActivity(intent)
        }

        private const val GALLERY_ITEM_TYPE_NONE = -1
        private const val GALLERY_ITEM_TYPE_IMAGE = 0
        private const val GALLERY_ITEM_TYPE_SEPARATOR = 1

    }
}
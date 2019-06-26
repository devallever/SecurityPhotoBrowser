package com.allever.security.photo.browser

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import com.allever.lib.common.app.BaseActivity
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.ui.PickActivity
import com.allever.security.photo.browser.ui.adapter.AlbumAdapter
import com.android.absbase.ui.widget.RippleImageView
import com.android.absbase.utils.ToastUtils

class AlbumActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAlbumAdapter: AlbumAdapter
    private lateinit var mImageFolderList: MutableList<ImageFolder>
    private lateinit var mIvSetting: RippleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        initView()

        initData()
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.album_recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mIvSetting = findViewById(R.id.album_iv_setting)
        mIvSetting.setOnClickListener(this)

    }

    private fun initData() {
        mImageFolderList = mutableListOf()
        for (i in 0 .. 10) {
            val imageFolder = ImageFolder()
            mImageFolderList.add(imageFolder)
        }
        mAlbumAdapter = AlbumAdapter(this, R.layout.item_album, mImageFolderList)
        mRecyclerView.adapter = mAlbumAdapter
        mAlbumAdapter.listener = object : AlbumAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                ToastUtils.show("position = $position")
                PickActivity.start(this@AlbumActivity)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            mIvSetting -> {
                ToastUtils.show("setting")
            }
        }
    }


}

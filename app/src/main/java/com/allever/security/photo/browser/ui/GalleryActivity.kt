package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.allever.lib.common.app.App
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.allever.lib.common.util.DLog
import com.allever.lib.common.util.ToastUtils
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.SeparatorBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeEvent
import com.allever.security.photo.browser.bean.event.EncodeEvent
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.endecode.UnLockAndRestoreListener
import com.allever.security.photo.browser.function.endecode.UnLockListListener
import com.allever.security.photo.browser.ui.adapter.GalleryAdapter
import com.allever.security.photo.browser.ui.adapter.ItemClickListener
import com.allever.security.photo.browser.util.MD5
import com.allever.security.photo.browser.util.SharePreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class GalleryActivity : Base2Activity(), View.OnClickListener {

    private lateinit var mBtnExport: View
    private lateinit var mBtnPick: View
    private lateinit var mTvTitle: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGalleryAdapter: GalleryAdapter
    private val mGalleryData = mutableListOf<Any>()
    private val mThumbnailBeanList = mutableListOf<ThumbnailBean>()
    //多选导出
    private val mExportThumbnailBeanList = mutableListOf<ThumbnailBean>()

    private var mAlbumName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        mAlbumName = intent.getStringExtra(EXTRA_ALBUM_NAME)

        var listData: MutableList<ThumbnailBean>? = null
        listData = intent.getParcelableArrayListExtra(EXTRA_DATA)
        mGalleryData.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mGalleryData.map {
            if (it is ThumbnailBean) {
                mThumbnailBeanList.add(it)
            }
        }
        initView()

        initData()

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initView() {
        mTvTitle = findViewById(R.id.gallery_tv_album_name)
        mTvTitle.text = mAlbumName
        mBtnPick = findViewById(R.id.gallery_btn_pick)
        mBtnPick.setOnClickListener(this)
        mBtnExport = findViewById(R.id.gallery_iv_export)
        mBtnExport.setOnClickListener(this)
        mRecyclerView = findViewById(R.id.gallery_recycler_view)
        val gridLayoutManager = GridLayoutManager(this, 3)
        //解决最后单个跨列问题
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemType = mGalleryAdapter.getItemViewType(position)
                return if (itemType == GALLERY_ITEM_TYPE_SEPARATOR) {
                    3
                } else {
                    1
                }
            }
        }
        mRecyclerView.layoutManager = gridLayoutManager
        mGalleryAdapter = GalleryAdapter(this, mGalleryData, object : MultiItemTypeSupport<Any> {
            override fun getLayoutId(itemType: Int): Int {
                return when (itemType) {
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
                return when (obj) {
                    is ThumbnailBean -> {
                        GALLERY_ITEM_TYPE_IMAGE
                    }
                    is SeparatorBean -> {
                        GALLERY_ITEM_TYPE_SEPARATOR

                    }
                    else -> {
                        GALLERY_ITEM_TYPE_NONE
                    }
                }
            }
        })

        mGalleryAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(position: Int) {
                val thumbnailBeanPosition = mThumbnailBeanList.indexOf(mGalleryData[position])
                PreviewActivity.start(this@GalleryActivity, ArrayList(mThumbnailBeanList), thumbnailBeanPosition)
            }

            override fun onItemLongClick(position: Int, item: Any) {
                if (item is ThumbnailBean) {
                    if (item.isChecked) {
                        //add
                        mExportThumbnailBeanList.add(item)
                    } else {
                        //remove
                        mExportThumbnailBeanList.remove(item)
                    }

                    if (mExportThumbnailBeanList.size > 0) {
                        //show
                        mBtnExport.visibility = View.VISIBLE
                    } else {
                        //hide
                        mBtnExport.visibility = View.GONE
                    }
                }
            }
        }

        mRecyclerView.adapter = mGalleryAdapter
    }

    private fun initData() {

    }

    override fun onClick(v: View?) {
        when (v) {
            mBtnPick -> {
                PickActivity.start(this, mAlbumName!!)
            }

            mBtnExport -> {
                ToastUtils.show("Export")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveDecodeEvent(decodeEvent: DecodeEvent) {
        val removeBean = mThumbnailBeanList[decodeEvent.index]
        mThumbnailBeanList.remove(removeBean)
        mGalleryData.remove(removeBean)
        mGalleryAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveEncodeEvent(encodeEvent: EncodeEvent) {
        encodeEvent.thumbnailBeanList.map {
            mGalleryData.add(it)
            mThumbnailBeanList.add(it)
        }

        //排序
        val sortThumbnailBeans = ArrayList<Any>()
        sortThumbnailBeans.addAll(mGalleryData)
        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
            if (arg0 is ThumbnailBean && arg1 is ThumbnailBean) {
                java.lang.Long.compare(arg1.date, arg0.date)
            } else {
                //不作处理
                java.lang.Long.compare(0, 0)
            }
        })

        mGalleryData.clear()
        mGalleryData.addAll(sortThumbnailBeans)

        mGalleryData.map {
            if (it is ThumbnailBean) {
                mThumbnailBeanList.add(it)
            }
        }

        mGalleryAdapter.notifyDataSetChanged()
    }

    /**
     * 移除加密资源
     */
    private fun restoreResourceList(beanList: MutableList<ThumbnailBean>) {
        val privateBeanList = mutableListOf<PrivateBean>()
        beanList.map {
            val privateBean = PrivateBean()
            val name = MD5.getMD5Str(it.path)
            val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
            if (privateBean.resolveHead(file.absolutePath)) {
                privateBeanList.add(privateBean)
            }
        }

        PrivateHelper.unLockList(privateBeanList, object : UnLockListListener{
            override fun onStart() {}

            override fun onSuccess() {
                beanList.map {
                    SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(it.path), null)
                }
            }

            override fun onFailed(errors: List<PrivateBean>) {
                privateBeanList.mapIndexed { index, privateBean ->

                }
            }

        })
    }

    /**
     * 移除加密资源
     */
    private fun restoreResource(bean: ThumbnailBean) {
        val privateBean = PrivateBean()
        val name = MD5.getMD5Str(bean.path)
        val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
        if (privateBean.resolveHead(file.absolutePath)) {
            PrivateHelper.unLockAndRestore(privateBean, object : UnLockAndRestoreListener {
                override fun onStart() {
                    DLog.d("export onStart")
                }

                override fun onSuccess() {
                    SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(bean.path), null)
                    finish()
                    DLog.d("export onSuccess")
                }

                override fun onFailed(msg: String) {
                    DLog.d("export onFailed")
                }
            })
        }

    }



    companion object {
        fun start(context: Context, albumName: String, data: ArrayList<ThumbnailBean>?) {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(EXTRA_ALBUM_NAME, albumName)
            intent.putParcelableArrayListExtra(EXTRA_DATA, data)
            context.startActivity(intent)
        }

        private const val GALLERY_ITEM_TYPE_NONE = -1
        private const val GALLERY_ITEM_TYPE_IMAGE = 0
        private const val GALLERY_ITEM_TYPE_SEPARATOR = 1

        private const val EXTRA_ALBUM_NAME = "EXTRA_ALBUM_NAME"
        private const val EXTRA_DATA = "EXTRA_DATA"
    }
}
package com.allever.security.photo.browser

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.os.HandlerCompat.postDelayed
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View

import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.util.DLog
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.LocalThumbnailBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.ui.GalleryActivity
import com.allever.security.photo.browser.ui.adapter.PrivateAlbumAdapter
import com.allever.security.photo.browser.util.DialogHelper
import com.allever.security.photo.browser.util.SharePreferenceUtil
import com.android.absbase.App
import com.android.absbase.ui.widget.RippleImageView
import com.android.absbase.utils.ToastUtils
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AlbumActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private lateinit var mIvSetting: RippleImageView
    private lateinit var mBtnAddAlbum: View

    private lateinit var mAlbumDataTask: PrivateAlbumDataTask
    private val mAlbumImageFolderMap = ConcurrentHashMap<String, ImageFolder>()
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private var mAddAlbumDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        initView()

        initData()

        //todo 获取数据
        getPrivateAlbumData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlbumDataTask.cancel(true)
    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.album_recycler_view)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager?

        mIvSetting = findViewById(R.id.album_iv_setting)
        mIvSetting.setOnClickListener(this)
        mBtnAddAlbum = findViewById(R.id.album_btn_add_album)
        mBtnAddAlbum.setOnClickListener(this)


    }

    private fun initData() {
        mPrivateAlbumAdapter = PrivateAlbumAdapter(this, R.layout.item_private_album, mImageFolderList)
        mRecyclerView.adapter = mPrivateAlbumAdapter
        mPrivateAlbumAdapter.listener = object : PrivateAlbumAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                GalleryActivity.start(this@AlbumActivity)
            }
        }
    }

    private fun getPrivateAlbumData() {
        mAlbumDataTask = PrivateAlbumDataTask()
        mAlbumDataTask.execute()
    }

    override fun onClick(v: View?) {
        when (v) {
            mIvSetting -> {
                ToastUtils.show("setting")
            }
            mBtnAddAlbum -> {
                val builder = DialogHelper.Builder()
                    .setTitleContent(App.getContext().getString(R.string.add_album))
                    .isShowMessage(false)
                    .isShowEditText(true)
                    .setOkContent(App.getContext().getString(R.string.save))
                    .setCancelContent(App.getContext().getString(R.string.cancel))

                mAddAlbumDialog = DialogHelper.createEditTextDialog(
                    this,
                    builder,
                    object : DialogHelper.EditDialogCallback {
                        override fun onOkClick(dialog: AlertDialog, etContent: String) {
                            if (TextUtils.isEmpty(etContent)) {
                                ToastUtils.show(App.getContext().getString(R.string.tips_please_input_album_name))
                                return
                            }

                            val imageFolder = ImageFolder()
                            imageFolder.name = (etContent)
                            imageFolder.dir = (PrivateHelper.PATH_ALBUM + File.separator + etContent)
                            imageFolder.data = ArrayList()
                            imageFolder.count = (0)

                            //创建相册
                            createAlbum(etContent)

                            //刷新数据
                            mPrivateAlbumAdapter.addData(mImageFolderList.size, imageFolder)
                            val albumPath = PrivateHelper.PATH_ALBUM + File.separator + etContent
                            mAlbumImageFolderMap[albumPath] = imageFolder

                            //滚动到底部
                            mHandler.postDelayed({
                                mRecyclerView.smoothScrollToPosition(mImageFolderList.size)
                            }, 200)
                            mAddAlbumDialog?.dismiss()

                        }

                        override fun onCancelClick(dialog: AlertDialog) {

                        }
                    })
                mAddAlbumDialog?.show()
            }
        }
    }

    /***
     * 创建相册
     * @param album 相册名
     */
    private fun createAlbum(album: String) {
        val albumFile = File(PrivateHelper.PATH_ALBUM + File.separator + album)
        if (!albumFile.exists()) {
            albumFile.mkdirs()
        }
    }

    private inner class PrivateAlbumDataTask : AsyncTask<Void, Void, MutableList<ImageFolder>>() {
        override fun doInBackground(vararg params: Void?): MutableList<ImageFolder>? {

            //遍历Album
            val rootDir = File(PrivateHelper.PATH_ALBUM)
            if (rootDir.exists() && rootDir.isDirectory) {
                val list = rootDir.list()
                if (list != null && list.isNotEmpty()) {
                    mAlbumImageFolderMap.clear()
                    val fileList = rootDir.listFiles() ?: return null
                    //遍历相册目录
                    for (albumDir in fileList) {
                        if (!albumDir.isDirectory) {
                            continue
                        }

                        val albumDirPath = albumDir.absolutePath
                        var thumbnailBeans = ArrayList<ThumbnailBean>()
                        val files = albumDir.listFiles()
                        val imageFolder = ImageFolder()

                        if (files == null) {
                            continue
                        }

                        //遍历目录内的链接文件
                        for (filepath in files) {
                            val name = filepath.name
                            val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
                            DLog.d("file name = $name")

                            if (!file.exists()) {
                                continue
                            }

                            val obj = SharePreferenceUtil.getObjectFromShare(applicationContext, name)
                            if (obj is LocalThumbnailBean) {
                                val thumb = obj as LocalThumbnailBean
                                val thumbnailBean =
                                    PrivateHelper.changeLocalThumbnailBean2ThumbnailBean(thumb)
                                if (!thumbnailBean.isInvalid) {
                                    thumbnailBeans.add(thumbnailBean)
                                }
                            }

                        }

                        imageFolder.dir = albumDirPath
                        imageFolder.name = albumDir.name

                        val sortThumbnailBeans = ArrayList(thumbnailBeans)
                        Collections.sort(
                            sortThumbnailBeans
                        ) { arg0, arg1 -> java.lang.Long.compare(arg1.date, arg0.date) }

                        thumbnailBeans = sortThumbnailBeans

                        imageFolder.data = (thumbnailBeans)
                        imageFolder.dir = (albumDirPath)
                        imageFolder.name = (albumDir.name)
                        imageFolder.count = (imageFolder.data?.size ?: 0)
                        mAlbumImageFolderMap[albumDirPath] = imageFolder
                    }
                }
            }
            return ArrayList(mAlbumImageFolderMap.values)
        }

        override fun onPostExecute(result: MutableList<ImageFolder>) {
            mImageFolderList = result
            mPrivateAlbumAdapter.setData(mImageFolderList)
        }
    }
}

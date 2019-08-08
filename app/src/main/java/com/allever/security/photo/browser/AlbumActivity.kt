package com.allever.security.photo.browser

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.allever.lib.common.app.App

import com.allever.lib.common.util.DLog
import com.allever.lib.common.util.ToastUtils
import com.allever.lib.permission.PermissionListener
import com.allever.lib.permission.PermissionManager

import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.LocalThumbnailBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeEvent
import com.allever.security.photo.browser.bean.event.EncodeEvent
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.password.PasswordConfig
import com.allever.security.photo.browser.ui.GalleryActivity
import com.allever.security.photo.browser.ui.adapter.PrivateAlbumAdapter
import com.allever.security.photo.browser.util.DialogHelper
import com.allever.security.photo.browser.util.SharePreferenceUtil

import com.android.absbase.ui.widget.RippleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class AlbumActivity : Base2Activity(), View.OnClickListener {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPrivateAlbumAdapter: PrivateAlbumAdapter
    private lateinit var mIvSetting: RippleImageView
    private lateinit var mBtnAddAlbum: View

    private lateinit var mAlbumDataTask: PrivateAlbumDataTask
    private val mAlbumImageFolderMap = LinkedHashMap<String, ImageFolder>()
    private var mImageFolderList = mutableListOf<ImageFolder>()
    private var mAddAlbumDialog: AlertDialog? = null

    private var mClickAlbumPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        EventBus.getDefault().register(this)

        //清除私密相册密码验证记录
        PasswordConfig.secretCheckPass = false

        initView()

        initData()

        PermissionManager.request(object : PermissionListener {
            override fun onGranted(grantedList: MutableList<String>) {
                getPrivateAlbumData()
            }

            override fun onDenied(deniedList: MutableList<String>) {

            }

            override fun alwaysDenied(deniedList: MutableList<String>) {
                PermissionManager.jumpPermissionSetting(this@AlbumActivity, 0,
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog?.dismiss()
                    })
            }

        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlbumDataTask.cancel(true)
        EventBus.getDefault().unregister(this)
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
                GalleryActivity.start(
                    this@AlbumActivity,
                    mImageFolderList[position].name!!,
                    ArrayList(mImageFolderList[position].data)
                )
                mClickAlbumPosition = position
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
                PermissionManager.request(object : PermissionListener {
                    override fun onGranted(grantedList: MutableList<String>) {
                        handleAddAlbum()
                    }

                    override fun alwaysDenied(deniedList: MutableList<String>) {
                        PermissionManager.jumpPermissionSetting(this@AlbumActivity, 1,
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog?.dismiss()
                            })
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }
        }
    }

    private fun handleAddAlbum() {
        val builder = DialogHelper.Builder()
            .setTitleContent(App.context.getString(R.string.add_album))
            .isShowMessage(false)
            .isShowEditText(true)
            .setOkContent(App.context.getString(R.string.save))
            .setCancelContent(App.context.getString(R.string.cancel))

        mAddAlbumDialog = DialogHelper.createEditTextDialog(
            this,
            builder,
            object : DialogHelper.EditDialogCallback {
                override fun onOkClick(dialog: AlertDialog, etContent: String) {
                    if (TextUtils.isEmpty(etContent)) {
                        ToastUtils.show(App.context.getString(R.string.tips_please_input_album_name))
                        return
                    }

                    val imageFolder = ImageFolder()
                    imageFolder.name = (etContent)
                    imageFolder.dir = (PrivateHelper.PATH_ALBUM + File.separator + etContent)
                    imageFolder.data = ArrayList()
                    imageFolder.count = (0)

                    //创建相册
                    handleAddAlbum(etContent)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    ToastUtils.show("获取加密相册内容")
                    //获取加密相册内容
                    if (PermissionManager.hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        getPrivateAlbumData()
                    }
                }
                1 -> {
                    //创建相册
                    ToastUtils.show("创建相册")
                    if (PermissionManager.hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        handleAddAlbum()
                    }
                }
            }
        }
    }

    /***
     * 创建相册
     * @param album 相册名
     */
    private fun handleAddAlbum(album: String) {
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
                        DLog.d("album name = ${albumDir.name}")
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
                                thumbnailBean.isChecked = false
                            }

                        }

                        imageFolder.dir = albumDirPath
                        imageFolder.name = albumDir.name

                        val sortThumbnailBeans = ArrayList(thumbnailBeans)
                        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
                            java.lang.Long.compare(arg1.date, arg0.date)
                        })

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveDecodeEvent(decodeEvent: DecodeEvent) {
        val imageFolder = mImageFolderList[mClickAlbumPosition]
        decodeEvent.indexList.map {
            imageFolder.data?.removeAt(it)
            imageFolder.count = imageFolder.data?.size?:0
        }

        //刷新界面
        mPrivateAlbumAdapter.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveEncodeEvent(encodeEvent: EncodeEvent) {
        val imageFolder = mImageFolderList[mClickAlbumPosition]
        encodeEvent.thumbnailBeanList.map {
            imageFolder.data?.add(it)
        }

        imageFolder.count = imageFolder.data?.size?:0
        //排序
        val sortThumbnailBeans = ArrayList<ThumbnailBean>()
        sortThumbnailBeans.addAll(imageFolder.data!!)
        sortThumbnailBeans.sortWith(Comparator { arg0, arg1 ->
            java.lang.Long.compare(arg1.date, arg0.date)
        })

        imageFolder.data = sortThumbnailBeans

        //刷新界面
        mPrivateAlbumAdapter.notifyDataSetChanged()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AlbumActivity::class.java)
            context.startActivity(intent)
        }
    }
}

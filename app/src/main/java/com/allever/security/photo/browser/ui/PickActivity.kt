package com.allever.security.photo.browser.ui

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.allever.lib.common.util.DLog
import com.allever.lib.permission.PermissionListener
import com.allever.lib.permission.PermissionManager
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.app.GlobalData
import com.allever.security.photo.browser.bean.ImageFolder
import com.allever.security.photo.browser.bean.LocalThumbnailBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.EncodeEvent
import com.allever.security.photo.browser.function.endecode.EncodeListListener
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.ui.adapter.SelectAlbumAdapter
import com.allever.security.photo.browser.ui.mvp.presenter.PickPresenter
import com.allever.security.photo.browser.ui.mvp.view.PickView
import com.allever.security.photo.browser.ui.widget.tab.TabLayout
import com.allever.security.photo.browser.util.*
import com.android.absbase.ui.widget.RippleTextView
import com.android.absbase.utils.ResourcesUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class PickActivity : Base2Activity<PickView, PickPresenter>(),
    PickView,
    TabLayout.OnTabSelectedListener,
    PickFragment.PickCallback,
    View.OnClickListener,
    SelectAlbumAdapter.OnItemClickListener{

    private lateinit var mTabs: TabLayout
    private lateinit var mViewPager: androidx.viewpager.widget.ViewPager
    private lateinit var mFragmentPageAdapter: FragmentPageAdapter
    private lateinit var mLlAlbumTitleContainer: ViewGroup
    private lateinit var mFlSelectAlbumContainer: ViewGroup
    private lateinit var mIvSelectAlbum: ImageView
    private lateinit var mAlbumRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var mBtnImport: RippleTextView
    private lateinit var mTvTitle: TextView

    private lateinit var mAlbumAdapter: SelectAlbumAdapter

    //fragment数据的数据集
    private var mFragmentDataMap = mutableMapOf<AlbumTabModel.Tab, MutableList<ThumbnailBean>>()
    //底部选中的数据
    private var mSelectedData = mutableListOf<ThumbnailBean>()
    //All数据
    private var mAllData = mutableListOf<ThumbnailBean>()
    //Video数据
    private var mVideoData = mutableListOf<ThumbnailBean>()
    //Photo数据
    private var mPhotoData = mutableListOf<ThumbnailBean>()
    //相册列表数据
    private var mAlbumData = mutableListOf<ImageFolder>()

    private var mFragments = mutableListOf<androidx.fragment.app.Fragment>()

    private var mSelectAlbumContainerAnimShow: Animator? = null
    private var mSelectAlbumContainerAnimHide: Animator? = null
    private var mIvArrowRotateAnimUp: Animator? = null
    private var mIvArrowRotateAnimDown: Animator? = null

    private var mAlbumName: String? = null

    private lateinit var mImportLoadingDialog: AlertDialog

    override fun getContentView(): Int = R.layout.activity_pick
    override fun createPresenter(): PickPresenter = PickPresenter()
    override fun initView() {
        initTabs()
        initViewPager()
        initAnim()
        initAlbumRecyclerView()

        mBtnImport = findViewById(R.id.btn_import)
        mBtnImport.setOnClickListener(this)
        mTvTitle = findViewById(R.id.tv_title)
        findViewById<View>(R.id.iv_back).setOnClickListener(this)
        mImportLoadingDialog = DialogHelper.createLoadingDialog(this, getString(R.string.import_resource), false)
    }

    override fun initData() {
        mAlbumName = intent.getStringExtra(EXTRA_ALBUM_NAME)
        if (GlobalData.albumData.isNotEmpty()) {
            //默认显示第一个相册的数据
            val firstImageFolder = GlobalData.albumData[0]
            updateData(firstImageFolder)
            updateFragmentUI()
            mAlbumAdapter.mData = GlobalData.albumData
            mAlbumAdapter.notifyDataSetChanged()
            return
        }

        PermissionManager.request(object : PermissionListener {
            override fun onGranted(grantedList: MutableList<String>) {
                getFolderDataTask().executeOnExecutor(AsyncTask.DATABASE_THREAD_EXECUTOR)
            }
            override fun onDenied(deniedList: MutableList<String>) {}
            override fun alwaysDenied(deniedList: MutableList<String>) {}

        }, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSelectedData.map {
            it.isChecked = false
        }
    }

    override fun onBackPressed() {
        if (mFlSelectAlbumContainer.visibility == View.VISIBLE) {
            showSelectAlbumContainer(false)
            return
        }
        super.onBackPressed()
    }



    private fun initTabs() {
        //Tab
        val tabCount = AlbumTabModel.tabCount
        mTabs = findViewById(R.id.tab_bar)
        for (i in 0 until tabCount) {
            val tabModel = AlbumTabModel.getTab(i)
            val tabView = getTabView(tabModel, i)
            val tab = mTabs.newTab()
                .setCustomView(tabView)
            tab.tag = tabModel
            mTabs.addTab(tab)
        }
        mTabs.setOnTabSelectedListener(this)
        mTabs.setSelectedTabIndicatorWidth(ResourcesUtils.resources.getDimension(R.dimen.album_selected_tab_indicator_width).toInt())
        mTabs.setSelectedTabIndicatorHeight(ResourcesUtils.resources.getDimension(R.dimen.album_selected_tab_indicator_height).toInt())
        val indicatorColor = ResourcesUtils.resources.getColor(R.color.album_tab_indicator_color)
        mTabs.setSelectedTabIndicatorColor(indicatorColor)
        mTabs.needDrawSelectedIndicator(true)
    }

    private fun initViewPager() {
        // 先清除现有的fragment
        // 原因: 其他界面崩溃后,该界面会出现唤醒的情况, 而onCreate内部会重建所有的fragment,导致界面刷新异常的问题
        mViewPager = findViewById(R.id.view_pager)
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabs))
        val fragments = supportFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            fragments.map {
                beginTransaction.remove(it)
            }
            beginTransaction.commitAllowingStateLoss()
        }

        val tabCount = AlbumTabModel.tabCount
        for (i in 0 until tabCount) {
            val fragment = PickFragment()
            fragment.callback = this
            fragment.type = AlbumTabModel.getTab(i)
            mFragments.add(fragment)
        }
        mFragmentDataMap[AlbumTabModel.Tab.ALL] = mAllData
        mFragmentDataMap[AlbumTabModel.Tab.VIDEO] = mVideoData
        mFragmentDataMap[AlbumTabModel.Tab.PICTURE] = mPhotoData

        mFragmentPageAdapter = FragmentPageAdapter(supportFragmentManager, mFragments)
        mViewPager.adapter = mFragmentPageAdapter
        mViewPager.currentItem = 0
    }

    private fun initAnim() {
        mLlAlbumTitleContainer = findViewById(R.id.ll_album_title_container)
        mFlSelectAlbumContainer = findViewById(R.id.fl_select_album_container)
        mIvSelectAlbum = findViewById(R.id.iv_select_album)
        mLlAlbumTitleContainer.setOnClickListener(this)
        mFlSelectAlbumContainer.setOnClickListener(this)
        mIvSelectAlbum.setOnClickListener(this)

        mSelectAlbumContainerAnimShow = ObjectAnimator.ofFloat(mFlSelectAlbumContainer, "alpha", 0f, 1f)
        mSelectAlbumContainerAnimShow?.duration = ANIMATION_DURATION
        mSelectAlbumContainerAnimShow?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                mFlSelectAlbumContainer.visibility = View.VISIBLE
            }
        })


        mSelectAlbumContainerAnimHide = ObjectAnimator.ofFloat(mFlSelectAlbumContainer, "alpha", 1f, 0f)
        mSelectAlbumContainerAnimHide?.duration = ANIMATION_DURATION
        mSelectAlbumContainerAnimHide?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                mFlSelectAlbumContainer.visibility = View.GONE
            }
        })

        mIvArrowRotateAnimUp = ObjectAnimator.ofFloat(mIvSelectAlbum, "rotation", 0f, 180f)
        mIvArrowRotateAnimUp?.duration = ANIMATION_DURATION

        mIvArrowRotateAnimDown = ObjectAnimator.ofFloat(mIvSelectAlbum, "rotation", 180f, 360f)
        mIvArrowRotateAnimDown?.duration = ANIMATION_DURATION
    }

    private fun initAlbumRecyclerView() {
        mAlbumRecyclerView = findViewById(R.id.select_album_recycler_view)
        mAlbumRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mAlbumAdapter = SelectAlbumAdapter(this, R.layout.item_slect_album, mAlbumData)
        mAlbumAdapter?.setOnItemClickListener(this)
        mAlbumRecyclerView.adapter = mAlbumAdapter
    }



    override fun onChooseAlbumAdapterItemClick(imageFolder: ImageFolder, position: Int) {
        showSelectAlbumContainer(false)

        mTvTitle.text = imageFolder.name

        updateData(imageFolder)
        updateFragmentUI()
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab ?: return
        mViewPager.currentItem = tab.position
        AlbumTabModel.selectedTab = tab.tag as AlbumTabModel.Tab

        for (i in 0 until mTabs.tabCount) {
            val tabAt = mTabs.getTabAt(i)
            if (tabAt != null) {
                val textView = tabAt.customView?.findViewById<TextView>(R.id.tv_tab)
                if (tabAt == tab) {
                    textView?.setTextColor(resources.getColor(R.color.album_tab_selected_color))
                } else {
                    textView?.setTextColor(resources.getColor(R.color.album_tab_un_selected_color))
                }
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabReselected(tab: TabLayout.Tab?) {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                when (mFlSelectAlbumContainer.visibility) {
                    View.VISIBLE -> {
                        showSelectAlbumContainer(false)
                    }
                    View.GONE -> {
                        finish()
                    }
                }
            }
            R.id.ll_album_title_container, R.id.iv_select_album -> {
                when (mFlSelectAlbumContainer.visibility) {
                    View.VISIBLE -> {
                        showSelectAlbumContainer(false)
                    }
                    View.GONE -> {
                        showSelectAlbumContainer(true)
                    }
                }
            }

            R.id.fl_select_album_container -> {
                showSelectAlbumContainer(false)
            }

            R.id.btn_import -> {
                for (index in mSelectedData.indices) {
                    val bean = mSelectedData[index]
                    bean.sourceType = ThumbnailBean.DECODE
                    val nameMd5 = MD5.getMD5Str(bean.path)
                    if (nameMd5 != null) {
                        bean.tempPath = File(PrivateHelper.PATH_DECODE_TEMP, nameMd5).path
                    }
                }
                //加密
                if (mAlbumName == null) {
                    return
                }
                val albumPath = PrivateHelper.PATH_ALBUM + File.separator + mAlbumName
                encodeData(albumPath, mSelectedData)

            }

        }
    }

    /***
     * 加密
     * @param albumPath
     * @param beans
     */
    private var mEncodeList = mutableListOf<PrivateBean>()

    private fun encodeData(albumPath: String, beans: MutableList<ThumbnailBean>?) {
        mEncodeList.clear()
        if (beans != null && beans.size > 0) {
            val localThumbnailBeans = PrivateHelper.changeThumbnailList2LocalThumbnaiList(beans)
            for (bean in localThumbnailBeans) {
                if (SharePreferenceUtil.setObjectToShare(this, MD5.getMD5Str(bean.path), bean)) {
                    mEncodeList.add(
                        PrivateBean(
                            File(bean.path).length(),
                            bean.date,
                            bean.path,
                            albumPath
                        )
                    )
                }
            }
            PrivateHelper.encodeList(mEncodeList, object : EncodeListListener {
                override fun onStart() {
                    mBtnImport.isClickable = false
                    DLog.d("onStart encode")
                    showImportLoading()
                }

                override fun onSuccess(successList: List<PrivateBean>, errorList: List<PrivateBean>) {
                    hideImportLoading()
                    DLog.d("onSuccess encode")
                    mBtnImport.isClickable = true

                    val thumbnailBeans = mutableListOf<ThumbnailBean>()
                    successList.map {
                        val name = File(it.encodePath).name
                        DLog.d("file name = $name")
                        val obj = SharePreferenceUtil.getObjectFromShare(applicationContext, name)
                        if (obj is LocalThumbnailBean) {
                            val thumb = obj as LocalThumbnailBean
                            val thumbnailBean = PrivateHelper.changeLocalThumbnailBean2ThumbnailBean(thumb)
                            thumbnailBean.isChecked = false
                            if (!thumbnailBean.isInvalid) {
                                thumbnailBeans.add(thumbnailBean)
                            }
                        }
                    }

                    val encodeEvent =  EncodeEvent()
                    encodeEvent.albumName = mAlbumName
                    encodeEvent.thumbnailBeanList.addAll(thumbnailBeans)
                    EventBus.getDefault().post(encodeEvent)

                    finish()
                }

                override fun onFailed(successList: List<PrivateBean>, errorList: List<PrivateBean>) {
                    hideImportLoading()
                    DLog.d("onFailed encode")
                    mBtnImport.isClickable = true
                }
            })
        }
    }

    private fun showSelectAlbumContainer(show: Boolean) {
        if (show) {
            mSelectAlbumContainerAnimShow?.start()
            mIvArrowRotateAnimUp?.start()
        } else {
            mSelectAlbumContainerAnimHide?.start()
            mIvArrowRotateAnimDown?.start()
        }
    }

    private fun getTabView(tab: AlbumTabModel.Tab, position: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
        val tvLabel = view.findViewById<TextView>(R.id.tv_tab)
        tvLabel?.text = resources.getString(tab.labelResId)
        //修改第一个Tab为选中状态
        if (position == 0) {
            tvLabel?.setTextColor(resources.getColor(R.color.album_tab_selected_color))
        }
        return view
    }

    /**
     * 获取媒体数据的异步任务
     *
     * @return
     */
    private fun getFolderDataTask(): AsyncTask<Void, Void, ArrayList<ImageFolder>> {
//        mIsNeedRefresh = false
        return object : AsyncTask<Void, Void, ArrayList<ImageFolder>>() {

            override fun doInBackground(vararg params: Void): ArrayList<ImageFolder>? {

                //文件夹信息
                val datas = ImageHelper.getAllFolderData(this@PickActivity)

                //按目录名称排序
                datas.sortWith(Comparator { arg0, arg1 -> arg0.name!!.compareTo(arg1.name!!) })

                //插入第一个数据，all
                val firstImageFolder = ImageFolder()
                firstImageFolder.dir = null
                firstImageFolder.bucketId = null
//                firstImageFolder.name = getString(R.string.album_default_album_name)
                firstImageFolder.name = "全部"

                datas.add(0, firstImageFolder)

                for (i in 0 until datas.size) {
                    val imageFolder = datas[i]
                    var allThumbnailBean: ArrayList<ThumbnailBean>?
                    allThumbnailBean = if (imageFolder.bucketId == null) {
                        ImageHelper.getThumbnailBeanFromPath(this@PickActivity, imageFolder.dir)
                    } else {
                        //解决sdcard根目录数据重复问题，使用路径会搜索到根目录及子目录的内容，改成bucketId，
                        ImageHelper.getThumbnailBeanFromBucketId(this@PickActivity, imageFolder.bucketId)
                    }
                    imageFolder.data = allThumbnailBean

                    //分类
                    val photoThumbnailBean = mutableListOf<ThumbnailBean>()
                    val videoThumbnailBean = mutableListOf<ThumbnailBean>()
                    for (position in 0 until allThumbnailBean.size) {
                        val thumbnailBean = allThumbnailBean[position]
                        if (MediaTypeUtil.isImage(thumbnailBean.type)) {
                            photoThumbnailBean.add(thumbnailBean)
                        } else if (MediaTypeUtil.isVideo(thumbnailBean.type)) {
                            videoThumbnailBean.add(thumbnailBean)
                        }
                    }

                    //手动插入第一个folder数据，firstThumbnailBean字段为空，需要特殊处理
                    if (imageFolder.firstThumbnailBean == null && allThumbnailBean.size > 0) {
                        imageFolder.setFirstImageBean(allThumbnailBean[0])
                    }

                    imageFolder.photoThumbnailBeans = (photoThumbnailBean as ArrayList<ThumbnailBean>)
                    imageFolder.videoThumbnailBeans = (videoThumbnailBean as ArrayList<ThumbnailBean>)

                    imageFolder.photoCount = photoThumbnailBean.size
                    imageFolder.videoCount = videoThumbnailBean.size

                    datas[i] = imageFolder

                }

                //排除空目录
                val iterator = datas.iterator()
                while (iterator.hasNext()) {
                    val folder = iterator.next()
                    if (folder.data?.isEmpty() == true) {
                        iterator.remove()
                    }
                }

                GlobalData.cloneAlbumData(datas)

                return datas
            }

            override fun onPostExecute(result: ArrayList<ImageFolder>?) {
                if (result == null || result.size == 0) {
                    return
                }

                mAlbumData.clear()
                mAlbumData.addAll(result)
                mAlbumAdapter.notifyDataSetChanged()

                //默认显示第一个相册的数据
                val firstImageFolder = result[0]
                updateData(firstImageFolder)
                updateFragmentUI()

                //选中后，如果监听到系统相册变化，重新获取了数据，需要刷新选中的数据
                for (j in 0 until mSelectedData.size) {
                    val selectedData = mSelectedData[j]
                    for (i in 0 until mAllData.size) {
                        val thumbnailBean = mAllData[i]
                        if (selectedData.path == thumbnailBean.path) {
                            mSelectedData.removeAt(j)
                            mSelectedData.add(j, thumbnailBean)
                            break
                        }
                    }
                }
            }
        }
    }


    private fun updateData(imageFolder: ImageFolder) {
        mAllData.clear()
        mPhotoData.clear()
        mVideoData.clear()

        mAllData.addAll(imageFolder.data!!)
        mVideoData.addAll(imageFolder.videoThumbnailBeans!!)
        mPhotoData.addAll(imageFolder.photoThumbnailBeans!!)
    }

    private fun updateFragmentUI(thumbnailBean: ThumbnailBean? = null) {
        val fragmentCount = mFragments.size
        for (i in 0 until fragmentCount) {
            val fragment = mFragments[i] as? PickFragment
            if (thumbnailBean == null) {
                val tab = AlbumTabModel.getTab(i)
                fragment?.updateData(mFragmentDataMap[tab])
            } else {
                fragment?.updateData(thumbnailBean)
            }
        }
    }

    override fun onPickItemClick(thumbnailBean: ThumbnailBean) {

        if (thumbnailBean.isChecked) {
            //添加
            mSelectedData.add(thumbnailBean)
        } else {
            //移除
            if (mSelectedData.contains(thumbnailBean)) {
                mSelectedData.remove(thumbnailBean)
            }
        }

        updateFragmentUI(thumbnailBean)
    }

    override fun onPickItemLongClick(position: Int, thumbnailBean: ThumbnailBean) {
        val data = mFragmentDataMap[AlbumTabModel.selectedTab]
        val index = data?.indexOf(thumbnailBean) ?: 0
        if (data != null) {
            PreviewActivity.start(this, ArrayList(data), index, PreviewActivity.TYPE_SYSTEM)
        }
    }

    override fun showImportLoading() {
        mImportLoadingDialog.show()
    }

    override fun hideImportLoading() {
        mImportLoadingDialog.dismiss()
    }

    companion object {
        fun start(context: Context, albumName: String) {
            val intent = Intent(context, PickActivity::class.java)
            intent.putExtra(EXTRA_ALBUM_NAME, albumName)
            context.startActivity(intent)
        }

        private const val ANIMATION_DURATION = 200L

        private const val EXTRA_ALBUM_NAME = "EXTRA_ALBUM_NAME"
    }
}
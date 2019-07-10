package com.allever.security.photo.browser.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.allever.lib.common.app.BaseActivity
import com.allever.lib.common.ui.widget.recycler.MultiItemTypeSupport
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Activity
import com.allever.security.photo.browser.bean.SeparatorBean
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.ui.adapter.GalleryAdapter
import com.allever.security.photo.browser.ui.adapter.ItemClickListener
import java.util.ArrayList
import java.util.HashMap

class GalleryActivity : Base2Activity(), View.OnClickListener {


    private lateinit var mBtnPick: View
    private lateinit var mTvTitle: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mGalleryAdapter: GalleryAdapter
    private val mGalleryData = mutableListOf<Any>()
    private val mThumbnailBeanList = mutableListOf<ThumbnailBean>()

    private var mAlbumName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        mAlbumName = intent.getStringExtra(EXTRA_ALBUM_NAME)

        mGalleryData.addAll(intent.getParcelableArrayListExtra(EXTRA_DATA))
        mGalleryData.map {
            if (it is ThumbnailBean) {
                mThumbnailBeanList.add(it)
            }
        }

//        initTestData()

        initView()

        initData()
    }

    private fun initView() {
        mTvTitle = findViewById(R.id.gallery_tv_album_name)
        mTvTitle.text = mAlbumName
        mBtnPick = findViewById(R.id.gallery_btn_pick)
        mBtnPick.setOnClickListener(this)
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

            override fun onItemLongClick(position: Int) {

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

//    /**
//     * 格式化数据
//     *
//     * @param result
//     * @param map
//     * @param today
//     * @param adBeanList 广告列表
//     */
//    @JvmOverloads
//    fun uniformData(
//        result: ArrayList<ThumbnailBean>,
//        map: HashMap<String, Int>,
//        today: DateBean,
//        colNum: Int,
//        adBeanList: List<AdItemBean> = ArrayList()
//    ): ArrayList<Any> {
//
//        val datas = ArrayList<Any>()
//
//        val l = result.size
//        if (l == 0) {
//            return datas
//        }
//        var data: ArrayList<Any>? = null
//        val first = result[0]
//        var now = DateBean(first.date)
//
//        var nextAdPosition = 0
//
//        today.setDate(System.currentTimeMillis())
//
//        //今天减掉一天就是昨天
//        val yesterday = DateBean(today.dateTime - 24 * 60 * 60 * 1000)
//
//        val firstAdItemBean = getAdBeanSafeFromList(adBeanList, nextAdPosition)
//        if (firstAdItemBean != null && firstAdItemBean.isBanner) {
//            val adPosition = firstAdItemBean.adPosition
//            if (adPosition.row - 1 == 0) {
//                datas.add(firstAdItemBean)
//
//                map[SPONSORED + "_" + nextAdPosition] = 0
//
//                nextAdPosition++
//            }
//        }
//        /**
//         * 处理第一个
//         */
//        var hasToday = false
//        var hasYesTerday = false
//        var hasBlewSevenDays = false
//        var isAllBlewSevenDays = false
//        var isAllToday = false
//        var isAllYesterday = false
//        if (now.isSameDay(today)) {
//            datas.add(SeparatorBean(mToday, GalleryActivity.TODAY_KEY))
//            hasToday = true
//        } else if (now.isDurationDays(today, 7)) {//7天之内
//            datas.add(SeparatorBean(now.monthDayString))
//            hasBlewSevenDays = true
//        } else {
//            if (now.isSameMonth(today)) {
//                datas.add(SeparatorBean(mMonth, GalleryActivity.MONTH_KEY))
//            } else {
//                datas.add(SeparatorBean(now.yearMonthString))
//            }
//        }
//        data = ArrayList()
//        datas.add(data)
//        data.add(first)
//
//        var i = 1
//        var dayCount = 1 //第几天
//        if (hasToday) {
//            val res =
//                uniformDataByDayOrMonth(result, map, now, colNum, adBeanList, datas, dayCount, i, nextAdPosition, 1)
//            i = res[0]
//            nextAdPosition = res[2]
//            map[GalleryActivity.TODAY_KEY] = res[1]
//            map[GalleryActivity.TODAY_KEY + "_" + GalleryActivity.AD] = res[3]
//            if (i < l) {
//                now = DateBean(result[i].date)
//
//                if (now.isSameDay(yesterday)) {//有昨天
//                    hasYesTerday = true
//                    datas.add(SeparatorBean(mYesterday, GalleryActivity.YESTERDAY_KEY))
//                } else if (now.isDurationDays(today, 7)) {
//                    hasBlewSevenDays = true
//                    datas.add(SeparatorBean(now.monthDayString))
//                } else if (now.isSameMonth(today)) {
//                    datas.add(SeparatorBean(mMonth, GalleryActivity.MONTH_KEY))
//                } else {
//                    datas.add(SeparatorBean(now.yearMonthString))
//                }
//                data = ArrayList()
//                datas.add(data)
//                data.add(result[i])
//                i++
//                dayCount++
//            } else {
//                isAllToday = true
//            }
//
//            /**
//             * 这种是全是Today的情况
//             */
//            //            if(map.get(GalleryActivity.TODAY_KEY) == null){
//            //                isAllToday = true;
//            //                map.put(GalleryActivity.TODAY_KEY, res[1]);
//            //            }
//        }
//
//        if (hasYesTerday) {
//            val res =
//                uniformDataByDayOrMonth(result, map, now, colNum, adBeanList, datas, dayCount, i, nextAdPosition, 1)
//            i = res[0]
//            nextAdPosition = res[2]
//            map[GalleryActivity.YESTERDAY_KEY] = res[1]
//            map[GalleryActivity.YESTERDAY_KEY + "_" + GalleryActivity.AD] = res[3]
//            if (i < l) {
//                now = DateBean(result[i].date)
//
//                if (now.isDurationDays(today, 7)) {
//                    hasBlewSevenDays = true
//                    datas.add(SeparatorBean(now.monthDayString))
//                } else if (now.isSameMonth(today)) {
//                    datas.add(SeparatorBean(mMonth, GalleryActivity.MONTH_KEY))
//                } else {
//                    datas.add(SeparatorBean(now.yearMonthString))
//                }
//                data = ArrayList()
//                datas.add(data)
//                data.add(result[i])
//                i++
//                dayCount++
//            } else {
//                isAllYesterday = true
//            }
//
//            /**
//             * 这种是只有Today和Yesterday的情况
//             */
//            //            if(map.get(GalleryActivity.YESTERDAY_KEY) == null){
//            //                isAllYesterday = true;
//            //                map.put(GalleryActivity.YESTERDAY_KEY, res[1]);
//            //            }
//        }
//
//        if (!isAllYesterday && hasBlewSevenDays) {
//            isAllBlewSevenDays = true
//            while (i < l) {
//
//                val res =
//                    uniformDataByDayOrMonth(result, map, now, colNum, adBeanList, datas, dayCount, i, nextAdPosition, 1)
//                i = res[0]
//                nextAdPosition = res[2]
//                map[now.monthDayString] = res[1]//记录数量
//                map[now.monthDayString + "_" + GalleryActivity.AD] = res[3]
//
//                if (i < l) {
//                    now = DateBean(result[i].date)
//                    if (now.isDurationDays(today, 7)) {
//                        datas.add(SeparatorBean(now.monthDayString))
//                        data = ArrayList()
//                        datas.add(data)
//                        data.add(result[i])
//                        dayCount++
//                    } else if (now.isSameMonth(today)) {
//                        datas.add(SeparatorBean(mMonth, GalleryActivity.MONTH_KEY))
//                        data = ArrayList()
//                        datas.add(data)
//                        data.add(result[i])
//                        i++
//                        isAllBlewSevenDays = false
//                        dayCount++
//                        break
//                    } else {
//                        datas.add(SeparatorBean(now.yearMonthString))
//                        data = ArrayList()
//                        datas.add(data)
//                        data.add(result[i])
//                        i++
//                        isAllBlewSevenDays = false
//                        dayCount++
//                        break
//                    }
//                }
//                i++
//            }
//
//            //            if(isAllBlewSevenDays){
//            //                map.put(now.getMonthDayString(), sum);
//            //                sum = 0;
//            //                num = 0;
//            //            }
//        }
//
//        if (!isAllToday && !isAllYesterday && !isAllBlewSevenDays) {
//            while (i < l) {
//
//                val res =
//                    uniformDataByDayOrMonth(result, map, now, colNum, adBeanList, datas, dayCount, i, nextAdPosition, 2)
//                i = res[0]
//                nextAdPosition = res[2]
//                val mapKey: String
//                if (now.isSameMonth(today)) {
//                    mapKey = GalleryActivity.MONTH_KEY
//                } else {
//                    mapKey = now.yearMonthString
//                }
//                map[mapKey] = res[1]
//                map[mapKey + "_" + GalleryActivity.AD] = res[3]
//
//                if (i < l) {
//                    now = DateBean(result[i].date)
//                    datas.add(SeparatorBean(now.yearMonthString))
//                    data = ArrayList()
//                    datas.add(data)
//                    data.add(result[i])
//                    dayCount++
//                }
//                i++
//            }
//            /**
//             * 这里是最后一个月只有一张图片的情况
//             */
//            if (now.isSameMonth(today)) {
//                map[GalleryActivity.MONTH_KEY] = 1
//            } else {
//                map[now.yearMonthString] = 1
//            }
//        }
//
//        //		for(String key: map.keySet()){
//        //			System.out.println("key = " + key + "  value = " + map.get(key));
//        //		}
//
//        if (datas.size != 0 && nextAdPosition == 0) {
//            val adItemBean = getAdBeanSafeFromList(adBeanList, nextAdPosition)
//            if (adItemBean != null && adItemBean.isBanner) {
//                datas.add(adItemBean)
//                map[SPONSORED + "_" + nextAdPosition] = 0
//            }
//        }
//
//        return datas
//    }
//
//    /**
//     * @param result
//     * @param map
//     * @param now
//     * @param colNum
//     * @param adBeanList
//     * @param datas
//     * @param dayCount
//     * @param i
//     * @param nextAdPosition
//     * @param style
//     * @return int[] 需回传的数据，目前分别为：
//     * 1. 当前数据的位置
//     * 2. 当天或当月的图片数目
//     * 3. 当前广告位置
//     * 4. 当天或当月的广告数目
//     */
//    private fun uniformDataByDayOrMonth(
//        result: ArrayList<ThumbnailBean>,
//        map: HashMap<String, Int>,
//        now: DateBean,
//        colNum: Int,
//        adBeanList: List<AdItemBean>,
//        datas: ArrayList<Any>,
//        dayCount: Int,
//        i: Int,
//        nextAdPosition: Int,
//        style: Int
//    ): IntArray {
//        var i = i
//        var nextAdPosition = nextAdPosition
//
//        var nextAdItemBean = getAdBeanSafeFromList(adBeanList, nextAdPosition) //获取下一个广告
//        var data: ArrayList<Any>
//        val lastObject = datas[datas.size - 1]
//        if (lastObject is ArrayList<*>) {
//            data = lastObject as ArrayList<Any>
//        } else {
//            data = ArrayList()
//        }
//
//        val l = result.size
//        var sum = 1
//        var num = 1
//        var sumAd = 0
//        while (i < l) {
//            if (nextAdItemBean != null && nextAdItemBean.isIcon) {
//                val adPosition = nextAdItemBean.adPosition
//                if (adPosition.row == dayCount && adPosition.col <= sum + sumAd) {
//                    if (num < colNum) {
//                        data.add(nextAdItemBean)
//                    } else {
//                        data = ArrayList()
//                        num = 0
//                        datas.add(data)
//                        data.add(nextAdItemBean)
//                    }
//
//                    num++
//                    sumAd++
//                    i-- //数据复位
//                    nextAdItemBean = getAdBeanSafeFromList(adBeanList, ++nextAdPosition) //获取下一个广告
//
//                    i++
//                    continue
//                }
//            }
//            val next = DateBean(result[i].date)
//
//            if (style == 1 && next.isSameDay(now) || style == 2 && next.isSameMonth(now)) {
//                if (num < colNum) {
//                    data.add(result[i])
//                } else {
//                    data = ArrayList()
//                    num = 0
//                    datas.add(data)
//                    data.add(result[i])
//                }
//                num++
//                sum++
//            } else {
//                if (nextAdItemBean != null && nextAdItemBean.isIcon) {
//                    if (nextAdItemBean.adPosition.row == dayCount) {
//                        val o = data[num - 1]
//                        if (o !is AdItemBean) {
//                            if (num < colNum) {
//                                data.add(nextAdItemBean)
//                            } else {
//                                data = ArrayList()
//                                num = 0
//                                datas.add(data)
//                                data.add(nextAdItemBean)
//                            }
//                        }
//                        nextAdItemBean = getAdBeanSafeFromList(adBeanList, ++nextAdPosition) //获取下一个广告
//                    }
//                    while (nextAdItemBean != null && nextAdItemBean.adPosition.row < dayCount) {
//                        nextAdItemBean = getAdBeanSafeFromList(adBeanList, ++nextAdPosition) //获取下一个广告
//                    }
//                }
//                if (nextAdItemBean != null && nextAdItemBean.isBanner) {
//                    val adPosition = nextAdItemBean.adPosition
//                    if (adPosition.row - 1 == dayCount) {
//                        datas.add(nextAdItemBean)
//
//                        map[SPONSORED + "_" + nextAdPosition] = 0
//
//                        nextAdItemBean = getAdBeanSafeFromList(adBeanList, ++nextAdPosition) //获取下一个广告
//                    }
//                }
//                break
//            }
//            i++
//        }
//        return intArrayOf(i, sum, nextAdPosition, sumAd)
//    }
//
//    private fun getAdBeanSafeFromList(adBeanList: List<AdItemBean>?, position: Int): AdItemBean? {
//        return if (adBeanList != null && adBeanList.size > position) {
//            adBeanList[position]
//        } else null
//    }

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
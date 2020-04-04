package com.allever.security.photo.browser.ui

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.allever.lib.common.app.App
import com.allever.lib.permission.PermissionUtil
import com.allever.security.photo.browser.R
import com.allever.security.photo.browser.app.Base2Fragment
import com.allever.security.photo.browser.app.GlobalData
import com.allever.security.photo.browser.bean.ThumbnailBean
import com.allever.security.photo.browser.bean.event.DecodeAllEvent
import com.allever.security.photo.browser.function.endecode.PrivateBean
import com.allever.security.photo.browser.function.endecode.PrivateHelper
import com.allever.security.photo.browser.function.endecode.UnLockListListener
import com.allever.security.photo.browser.ui.mvp.presenter.SettingPresenter
import com.allever.security.photo.browser.ui.mvp.view.SettingView
import com.allever.security.photo.browser.util.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class SettingFragment: Base2Fragment<SettingView, SettingPresenter>(), SettingView, View.OnClickListener {
    override fun getContentView(): Int = R.layout.fragment_setting

    override fun initView(root: View) {
        root.findViewById<View>(R.id.setting_tv_share).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_permission).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_modify_password).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_feedback).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_about).setOnClickListener(this)
        root.findViewById<TextView>(R.id.setting_tv_restore_all).setOnClickListener(this)

    }

    override fun initData() { }

    override fun createPresenter(): SettingPresenter = SettingPresenter()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setting_tv_permission -> {
//                val d = Dialog
                PermissionUtil.GoToSetting(activity)
            }
            R.id.setting_tv_modify_password -> {
                ChangePasswordActivity.start(activity!!, true)
            }
            R.id.setting_tv_feedback -> {
                FeedbackHelper.feedback(activity)
            }
            R.id.setting_tv_about -> {
                AboutActivity.start(activity)
            }
            R.id.setting_tv_share -> {
                val msg = "【隐私相册浏览器】这个应用很不错，推荐一下。获取地址 \nhttps://shouji.baidu.com/software/26327319.html"
                val intent = SystemUtils.getShareIntent(App.context, msg)
                startActivity(intent)
            }
            R.id.setting_tv_restore_all -> {
                AlertDialog.Builder(activity!!)
                    .setMessage("是否恢复全部加密图片和视频")
                    .setPositiveButton("确定"
                    ) { dialog, which ->
                        dialog.dismiss()
                        restoreResourceList(GlobalData.privateAlbumData)
                    }
                    .setNegativeButton("取消"
                    ) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setCancelable(true)
                    .show()
            }
        }
    }
    private var mLoadingDialog: AlertDialog? = null

    /**
     * 移除加密资源
     */
    private fun restoreResourceList(beanList: MutableList<ThumbnailBean>) {
        if (mLoadingDialog == null) {
            mLoadingDialog = DialogHelper.createLoadingDialog(activity!!, getString(R.string.export_resource), false)
        }

        val privateThumbMap = mutableMapOf<PrivateBean, ThumbnailBean>()
        val privateBeanList = mutableListOf<PrivateBean>()
        beanList.map {
            val privateBean = PrivateBean()
            val name = MD5.getMD5Str(it.path)
            val file = File(PrivateHelper.PATH_ENCODE_ORIGINAL, name)
            if (privateBean.resolveHead(file.absolutePath)) {
                privateBeanList.add(privateBean)
                privateThumbMap[privateBean] = it
            }
        }

        PrivateHelper.unLockList(privateBeanList, object : UnLockListListener {
            override fun onStart() {
                showLoading()
            }

            override fun onSuccess(successList: List<PrivateBean>) {
                hideLoading()
                val successDecodeList = mutableListOf<ThumbnailBean>()
//                val exportIndexList = mutableListOf<Int>()
                successList.map {
                    val thumbnailBean = privateThumbMap[it]
                    if (thumbnailBean != null) {
                        thumbnailBean.isChecked = false
                        SharePreferenceUtil.setObjectToShare(App.context, MD5.getMD5Str(thumbnailBean.path), null)
//                        exportIndexList.add(mGalleryData.indexOf(thumbnailBean))
//                        mThumbnailBeanList.remove(thumbnailBean)
//                        mGalleryData.remove(thumbnailBean)
                        successDecodeList.add(thumbnailBean)
                        privateThumbMap.remove(it)
                    }
                }
//                mGalleryAdapter.notifyDataSetChanged()
//                mExportThumbnailBeanList.clear()
//                mBtnExport.visibility = View.GONE

                val decodeEvent = DecodeAllEvent()
                decodeEvent.thumbnailBeanList.addAll(successDecodeList)
                EventBus.getDefault().post(decodeEvent)
            }

            override fun onFailed(errors: List<PrivateBean>) {
                hideLoading()
                errors.map {
//                    val thumbnailBean = privateThumbMap[it]
//                    if (thumbnailBean != null) {
//                        thumbnailBean.isChecked = false
//                    }
                }
//                mGalleryAdapter.notifyDataSetChanged()
//                mExportThumbnailBeanList.clear()
//                mBtnExport.visibility = View.GONE
            }
        })
    }

    private fun showLoading() {
        if (mLoadingDialog?.isShowing == false) {
            mLoadingDialog?.show()
        }
    }

    private fun hideLoading() {
        mLoadingDialog?.dismiss()
    }
}
package com.allever.security.photo.browser.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.allever.security.photo.browser.R

import com.android.absbase.ui.widget.RippleTextView

/***
 * 输入分享内容界面
 */

class AlbumDialog : AppCompatDialogFragment {

    private var mView: View? = null
    private var mTvRename: RippleTextView? = null
    private var mTvDelete: RippleTextView? = null
    private var mCallback: Callback? = null


    constructor()
    @SuppressLint("ValidFragment")
    constructor(callback: Callback){
        mCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog.window
        if (window != null) {
            // 必须设置，否则无法全屏
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置dialog在屏幕底部
            window.setGravity(Gravity.BOTTOM)
            //设置dialog弹出时的动画效果，从屏幕底部向上弹出
            window.setWindowAnimations(R.style.ActivityStyle)
            //获得window窗口的属性
            val lp = window.attributes
            //设置窗口宽度为充满全屏
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            //设置窗口高度为充满全屏
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            //将设置好的属性set回去
            window.attributes = lp
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mView = LayoutInflater.from(activity).inflate(R.layout.fragment_dialog_album, null)
        val alertDialog = AlertDialog.Builder(activity!!)
                .setView(mView)
                .create()
        initView()

        return alertDialog
    }

    private fun initView() {
        mTvRename = mView?.findViewById(R.id.id_tv_rename)
        mTvDelete = mView?.findViewById(R.id.id_tv_delete)

        mTvRename?.setOnClickListener {
            mCallback?.onRenameClick(this)
        }

        mTvDelete?.setOnClickListener {
            mCallback?.onDeleteClick(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    public interface Callback{
        fun onDeleteClick(dialog: AppCompatDialogFragment)
        fun onRenameClick(dialog: AppCompatDialogFragment)
    }

}

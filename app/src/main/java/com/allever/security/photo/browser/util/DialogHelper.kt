package com.allever.security.photo.browser.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.allever.security.photo.browser.R
import com.allever.lib.common.app.App
import com.android.absbase.ui.widget.RippleButton
import java.lang.Exception

object DialogHelper {

    fun createMessageDialog(activity: Activity, msg: String, listener: DialogInterface.OnClickListener): AlertDialog {
        val builder = AlertDialog.Builder(activity)
            .setMessage(msg)
            .setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.confirm) { dialog, which ->
                listener.onClick(dialog, which)
            }
        return builder.create()
    }

    @SuppressLint("InflateParams")
    fun createLoadingDialog(activity: Activity, msg: String, cancelAble: Boolean = true): AlertDialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_load_progress, null)
        view.findViewById<TextView>(R.id.dialog_progress_tv_message).text = msg
        return AlertDialog.Builder(activity)
            .setView(view)
            .setCancelable(cancelAble)
            .create()
    }

    fun createEditTextDialog(
        activity: Activity,
        builder: DialogHelper.Builder?,
        callback: DialogHelper.EditDialogCallback?
    ): AlertDialog {
        var builder = builder
        val editAlertDialog = AlertDialog.Builder(activity).create()
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_alert_dialog, null)
        editAlertDialog.setView(view)
//        val window = editAlertDialog.window
//        window?.setContentView(R.layout.layout_alert_dialog)
        val titleView = view.findViewById(R.id.title) as TextView
        val detailView = view.findViewById(R.id.detail) as TextView
        val cancelView = view.findViewById(R.id.btn_cancel) as RippleButton
        val okView = view.findViewById(R.id.btn_ok) as RippleButton
        val editText = view.findViewById(R.id.id_edit_text) as EditText

        if (builder == null) {
            builder = DialogHelper.Builder()
        }

        titleView.text = builder.title
        detailView.text = builder.message
        cancelView.text = builder.cancelText
        okView.text = builder.okText

        if (builder.showEditText == true) {
            editText.visibility = View.VISIBLE
            val content = builder.etContent
            editText.setText(content)
            editText.setSelection(content?.length ?: 0)
        } else {
            editText.visibility = View.GONE
        }

        if (builder.showMessage == true) {
            detailView.visibility = View.VISIBLE
        } else {
            detailView.visibility = View.GONE
        }

        cancelView.setOnClickListener {
            callback?.onCancelClick(editAlertDialog)
            editAlertDialog.dismiss()
        }

        okView.setOnClickListener {
            callback?.onOkClick(editAlertDialog, editText.text.toString())
        }
        editAlertDialog.setCancelable(true)
        editAlertDialog.setCanceledOnTouchOutside(false)
        editAlertDialog.setOnShowListener {
            if (builder.showEditText == true) {
                try {
                    val imm = App.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                } catch (e: Exception) {

                }

            }
        }

        return editAlertDialog
    }

    public interface EditDialogCallback {
        fun onOkClick(dialog: AlertDialog, etContent: String) {}
        fun onCancelClick(dialog: AlertDialog) {}
    }


    class Builder {
        var title: String? = App.context.getString(R.string.dialog_default_title)
        var message: String? = App.context.getString(R.string.dialog_default_message)
        var okText: String? = App.context.getString(R.string.dialog_default_positive_text)
        var cancelText: String? = App.context.getString(R.string.dialog_default_negative_text)
        var showEditText: Boolean? = false
        var showMessage: Boolean? = true
        var etContent: String? = ""

        fun setTitleContent(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessageContent(message: String): Builder {
            this.message = message
            return this
        }

        fun setOkContent(okText: String): Builder {
            this.okText = okText
            return this
        }

        fun setCancelContent(cancelText: String): Builder {
            this.cancelText = cancelText
            return this
        }

        fun isShowEditText(show: Boolean): Builder {
            showEditText = show
            return this
        }

        fun isShowMessage(show: Boolean): Builder {
            showMessage = show
            return this
        }

        fun setEditTextContent(etContent: String): Builder {
            this.etContent = etContent
            return this
        }
    }


}
package com.gkzxhn.prison.customview

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

import com.gkzxhn.prison.R
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_title
as tvTitle
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_cancel
as tvCancel
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_confirm
as tvConfirm

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

class CustomDialog(context: Context, private val onClickListener: View.OnClickListener?) : Dialog(context, R.style.update_dialog_style) {
    private var title = ""
    private var leftText: String = ""
    private var rightText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null)
        setContentView(contentView)
        init()
        measureWindow()
    }
    private fun init() {
        tvTitle.text = title
        if (leftText.length > 0) tvCancel.text = leftText
        if (rightText.length > 0) tvConfirm.text = rightText
        tvCancel.setOnClickListener { view ->
            dismiss()
            onClickListener?.onClick(view)
        }
        tvConfirm.setOnClickListener { view ->
            dismiss()
            onClickListener?.onClick(view)
        }
    }
    fun measureWindow() {
        val dialogWindow = this.window
        val params = dialogWindow.attributes
        val m = dialogWindow.windowManager

        val d = m.defaultDisplay
        params.width = d.width
        //	        params.height=d.getHeight();
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.attributes = params
    }



    fun setTitle(title: String) {
        this.title = title
        if (tvTitle != null)
            tvTitle.text = title
    }

    fun setContent(title: String, leftText: String, rightText: String) {
        this.title = title
        this.leftText = leftText
        this.rightText = rightText
        if (tvTitle != null) {
            tvTitle.text = title
            tvCancel.text = leftText
            tvConfirm.text = rightText
        }
    }
}

package com.gkzxhn.prison.customview

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View

import com.gkzxhn.prison.R
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_title
as tvTitle
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_content
as tvContent
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_cancel
as tvCancel
import kotlinx.android.synthetic.main.custom_dialog_layout.custom_dialog_layout_tv_confirm
as tvConfirm

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

class CustomDialog(context: Context) : Dialog(context, R.style.update_dialog_style) {
    var onClickListener: View.OnClickListener?=null
    var title = ""
        set(value) {
            field=value
            if (tvTitle != null){
                //有标题，则自动显示
                if(!title.isEmpty())tvTitle.visibility=View.VISIBLE
                tvTitle.text = value
            }

        }
    var content = ""
        set(value) {
            field=value
            if (tvContent != null)
                tvContent.text = value
        }
    var cancelText: String = ""
        set(value) {
            field=value
            if (tvCancel != null)
                tvCancel.text = value
        }
    var confirmText: String = ""
        set(value) {
            field=value
            if (tvConfirm != null)
                tvConfirm.text = value
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null))
        init()
        measureWindow()
    }
    private fun init() {
        //标题为空，则自动不显示
        tvTitle.text = title
        if(title.isEmpty())tvTitle.visibility=View.GONE
        tvContent.text = content
        tvCancel.text = cancelText
        tvConfirm.text = confirmText
        tvCancel.setOnClickListener { view ->
            dismiss()
            onClickListener?.onClick(view)
        }
        tvConfirm.setOnClickListener { view ->
            dismiss()
            onClickListener?.onClick(view)
        }
    }

    override fun show() {
        super.show()
        if(tvConfirm!=null){
            tvConfirm.isFocusable=true
            tvConfirm.requestFocus()
            tvConfirm.isFocusableInTouchMode=true
            tvConfirm.requestFocusFromTouch()
            tvConfirm.isSelected=true
        }
    }
    fun measureWindow() {
        val dialogWindow = this.window
        val params = dialogWindow.attributes
        val m = dialogWindow.windowManager

        val d = m.defaultDisplay
        params.width = d.width/2
        //	        params.height=d.getHeight();
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.attributes = params
    }
}

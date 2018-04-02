package com.gkzxhn.prison.customview

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager

import com.gkzxhn.prison.R
import com.gkzxhn.prison.activity.ConfigActivity
import kotlinx.android.synthetic.main.show_terminal_dialog_layout.show_terminal_dialog_layout_tv_cancel
as tvCancel
import kotlinx.android.synthetic.main.show_terminal_dialog_layout.show_terminal_dialog_layout_tv_set
as tvSet

/**
 * Created by Raleigh.Luo on 17/3/27.
 */

class ShowTerminalDialog : Dialog {

    constructor(context: Context, theme: Int) : super(context, theme) {}
    constructor(context: Context) : super(context, R.style.update_dialog_style) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = LayoutInflater.from(getContext()).inflate(R.layout.show_terminal_dialog_layout, null)
        setContentView(contentView)
        init()
        measureWindow()
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

    private fun init() {
        setCanceledOnTouchOutside(false)
        tvCancel.setOnClickListener { dismiss() }
        tvSet.setOnClickListener {
            dismiss()
            context.startActivity(Intent(context, ConfigActivity::class.java))
        }
    }
}

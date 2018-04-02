package com.starlight.mobile.android.lib.view


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

import com.starlight.mobile.android.lib.R
//设置控件别名
import kotlinx.android.synthetic.main.cus_photo_from_dialog_layout.cus_photo_from_dialog_layout_btn_take_photo as btnTakePhoto
import kotlinx.android.synthetic.main.cus_photo_from_dialog_layout.cus_photo_from_dialog_layout_btn_album as btnAlbum
import kotlinx.android.synthetic.main.cus_photo_from_dialog_layout.cus_photo_from_dialog_layout_btn_cancel as btnCancel

/**选择图片来源对话框，相册/拍照
 * @author raleigh
 */
class CusPhotoFromDialog : Dialog {
    var photoFromClickListener: PhotoFromClickListener? = null
    private var takePhoto = ""
    private var album = ""
    private var cancel = ""
    private var  root:View

    constructor(context: Context) : super(context, R.style.CusPhotoFromDialog_style) {
        root = LayoutInflater.from(context).inflate(R.layout.cus_photo_from_dialog_layout, null)
        var i=0
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        root = LayoutInflater.from(context).inflate(R.layout.cus_photo_from_dialog_layout, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(root)
        btnTakePhoto.setOnClickListener(onClickListener)
        btnAlbum.setOnClickListener(onClickListener)
        btnCancel.setOnClickListener(onClickListener)
        btnTakePhoto.text = takePhoto
        btnAlbum.text = album
        btnCancel.text = cancel
        measureWindow()
    }
//
    fun measureWindow() {
        val dialogWindow = this.window
        dialogWindow.setGravity(Gravity.BOTTOM)

        val params = dialogWindow.attributes
        val m = dialogWindow.windowManager
        val d = m.defaultDisplay
        //填充页面
        root.layoutParams = FrameLayout.LayoutParams(
                d.width, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.width = d.width
        dialogWindow.attributes = params
    }

    private val onClickListener = View.OnClickListener { v ->
        photoFromClickListener?.back(v)
        dismiss()
    }

    /**设置标题，在show()方法之后使用
     * @param takePhoto 第一个按钮的text
     * @param album 第二个按钮的text
     * @param cancel 第三个按钮的text
     */
    fun setBtnTitle(takePhoto: String, album: String, cancel: String) {
        this.takePhoto = takePhoto
        this.album = album
        this.cancel = cancel
    }

    interface PhotoFromClickListener {
        fun back(v: View)
    }

}

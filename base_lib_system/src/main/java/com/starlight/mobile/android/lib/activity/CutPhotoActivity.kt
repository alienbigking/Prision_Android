package com.starlight.mobile.android.lib.activity


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View

import com.starlight.mobile.android.lib.R
import com.starlight.mobile.android.lib.album.AlbumActivity
import com.starlight.mobile.android.lib.util.ImageHelper
//设置控件别名
import kotlinx.android.synthetic.main.cut_photo_layout.cut_photo_layout_cp_cut as cpCutPhoto

import java.io.File

/**
 * Created by Raleigh on 15/8/19.
 * 剪切图片，并返回图片剪切后保存的路径
 * AlbumActivity.EXTRAS
 */
class CutPhotoActivity : Activity() {
    private var saveImageDir: String? = null
    private lateinit var mProgressDialog: ProgressDialog
    private var imagePath: String? = null
    private var originPath: String? = null
    private val LOADING_CODE = 1
    private val CUTTING_CODE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cut_photo_layout)
        init()
    }
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when(msg.what){
                CUTTING_CODE -> {//裁剪
                    mProgressDialog.dismiss()
                    if (imagePath != null) {
                        val intent = Intent()
                        intent.putExtra(AlbumActivity.EXTRAS, imagePath)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        val intent = Intent()
                        setResult(Activity.RESULT_CANCELED, intent)
                        finish()
                    }
                }
                LOADING_CODE -> {//加载
                    mProgressDialog.dismiss()
                    cpCutPhoto.setImagePath(originPath!!)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        cpCutPhoto.invalidate()
    }

    private fun init() {
        mProgressDialog = ProgressDialog.show(this, null, getString(R.string.album_cutting))
        if(mProgressDialog.isShowing) mProgressDialog.dismiss()

        Thread(Runnable {
            saveImageDir = Environment.getExternalStorageDirectory().path
            val data = intent
            val imageUri = data.data
            if (data.hasExtra(AlbumActivity.EXTRAS))
                saveImageDir = data.getStringExtra(AlbumActivity.EXTRAS)
            if (File(imageUri.path).exists()) {
                originPath = imageUri.path
            } else {
                originPath = getRealPathFromURI(imageUri)
            }
            mHandler.sendEmptyMessage(LOADING_CODE)
        }).start()
    }


    fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun onClickListener(v: View) {
        when(v.id){
            R.id.common_head_layout_iv_left -> {
                this.finish()
            }
            R.id.common_head_layout_tv_right -> {//确定
                if(!mProgressDialog.isShowing)mProgressDialog.show()
                Thread(Runnable {
                    try {
                        val bitmap = cpCutPhoto.clip()
                        bitmap?.let {
                            imagePath = ImageHelper.instance.compressImage(it, saveImageDir!!)
                            mHandler.sendEmptyMessage(CUTTING_CODE)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        imagePath = null
                        mHandler.sendEmptyMessage(CUTTING_CODE)
                    }
                }).start()
            }
        }
    }

    override fun onDestroy() {
        cpCutPhoto?.onDestory()
        if(mProgressDialog.isShowing)mProgressDialog.dismiss()
        super.onDestroy()
    }
}

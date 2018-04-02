package com.starlight.mobile.android.lib.album

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow

import com.starlight.mobile.android.lib.R

/**
 * Created by Raleigh on 15/7/10.
 */
class ImageDirPopupWindow
/**
 * ListView的数据集
 */
(width: Int, height: Int,
 datas: List<ImageFloder>, val allPhotoCount: Int,
 /**
  * 布局文件的最外层View
  */
 protected var mContentView: View) : PopupWindow(mContentView, width, height, true) {
    private val mRecyclerView: RecyclerView
    protected var context: Context
    private val adapter: FolderAdapter

    init {
        context = mContentView.context

        setBackgroundDrawable(BitmapDrawable())
        isTouchable = true
        isOutsideTouchable = true
        setTouchInterceptor(View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                dismiss()
                return@OnTouchListener true
            }
            false
        })
        mRecyclerView = mContentView.findViewById(R.id.album_dir_layout_rv_list) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        adapter = FolderAdapter(context, datas, allPhotoCount)
        adapter.mAlbumItemClickListener=object : FolderAdapter.AlbumItemClickListener {
            override fun onClick(v: View, floder: ImageFloder?) {
                mImageDirSelected?.let {
                    it.selected(floder)
                }
            }
        }
        mRecyclerView.adapter = adapter
    }


    interface OnImageDirSelected {
        fun selected(floder: ImageFloder?)
    }

    private var mImageDirSelected: OnImageDirSelected? = null

    fun setOnImageDirSelected(mImageDirSelected: OnImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected
    }
    fun onDestory() {
        adapter.onDestory()
    }


}

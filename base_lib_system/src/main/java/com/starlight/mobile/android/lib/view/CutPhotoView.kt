package com.starlight.mobile.android.lib.view


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.widget.RelativeLayout

import com.starlight.mobile.android.lib.album.AlbumImageLoader

/**
 * @author raleigh
 */
class CutPhotoView(private val mContext: Context, attrs: AttributeSet) : RelativeLayout(mContext, attrs) {

    private val mZoomImageView: CutPhotoZoom?
    private val mClipImageView: CutPhotoBorderView
    private val mAlbumImageLoader: AlbumImageLoader
    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private var mHorizontalPadding = 20

    init {
        mAlbumImageLoader = AlbumImageLoader(1, AlbumImageLoader.Type.LIFO)
        mZoomImageView = CutPhotoZoom(mContext)
        mClipImageView = CutPhotoBorderView(mContext)

        val lp = RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT)

        //		/**
        //		 * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
        //		 */
        //		mZoomImageView.setImageDrawable(getResources().getDrawable(
        //				R.drawable.a));

        this.addView(mZoomImageView, lp)
        this.addView(mClipImageView, lp)

        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val mScreenWidth = dm.widthPixels// 获取屏幕分辨率宽度
        val mScreenHeight = dm.heightPixels// 获取屏幕分辨率宽度
        if (mScreenWidth > mScreenHeight) {
            mHorizontalPadding = (mScreenWidth - (mScreenHeight - 10 * mHorizontalPadding)) / 2
        }
        // 计算padding的px
        mHorizontalPadding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding.toFloat(), resources
                .displayMetrics).toInt()
        mZoomImageView.setHorizontalPadding(mHorizontalPadding)
        mClipImageView.setHorizontalPadding(mHorizontalPadding)
    }

    fun setImageBitmap(bitmap: Bitmap) {
        mZoomImageView?.setImageBitmap(bitmap)
    }

    fun setImagePath(path: String) {
        mZoomImageView?.let{
            mAlbumImageLoader.loadImage(path, it)
        }
    }

    fun setImageUri(imageUri: Uri) {
        mZoomImageView?.setImageURI(imageUri)
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    fun setHorizontalPadding(mHorizontalPadding: Int) {
        this.mHorizontalPadding = mHorizontalPadding
    }

    /**
     * 裁切图片
     *
     * @return
     */
    fun clip(): Bitmap? {
        return mZoomImageView?.clip()
    }

    fun onDestory() {
        mAlbumImageLoader.onDestory()
    }

}

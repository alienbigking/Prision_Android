package com.starlight.mobile.android.lib.util


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect

class BubbleImageHelper private constructor(c: Context) {
    private var context: Context
    init {
        context = c
    }
    private fun getScaleImage(bitmap: Bitmap?, width: Float, height: Float): Bitmap? {
        if (null == bitmap || width < 0.0f || height < 0.0f) {
            return null
        }
        val matrix = Matrix()
        val scaleWidth = width / bitmap.width
        val scaleHeight = height / bitmap.height
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true)
    }

    fun getBubbleImageBitmap(srcBitmap: Bitmap?,
                             backgroundResourceID: Int): Bitmap? {
        if (null == srcBitmap) {
            return null
        }
        var background: Bitmap? = null
        background = BitmapFactory.decodeResource(context.resources,
                backgroundResourceID)
        if (null == background) {
            return null
        }

        var mask: Bitmap? = null
        var newBitmap: Bitmap? = null
        mask = srcBitmap

        var srcWidth = srcBitmap.width.toFloat()
        var srcHeight = srcBitmap.height.toFloat()
        if (srcWidth < CommonHelper.getImageMessageItemMinWidth(context).toFloat() && srcHeight < CommonHelper
                .getImageMessageItemMinHeight(context).toFloat()) {
            srcWidth = CommonHelper.getImageMessageItemMinWidth(context).toFloat()
            srcHeight = CommonHelper
                    .getImageMessageItemMinHeight(context).toFloat()
            var tmp = getScaleImage(background, srcWidth, srcHeight)
            if (null != tmp) {
                background = tmp
            } else {
                tmp = getScaleImage(srcBitmap,
                        CommonHelper.getImageMessageItemDefaultWidth(context).toFloat(),
                        CommonHelper.getImageMessageItemDefaultHeight(context).toFloat())
                if (null != tmp) {
                    mask = tmp
                }
            }
        }

        var config: Config? = background.config
        if (null == config) {
            config = Config.ARGB_8888
        }

        newBitmap = Bitmap.createBitmap(background.width,
                background.height, config)
        val newCanvas = Canvas(newBitmap)

        newCanvas.drawBitmap(background, 0f, 0f, null)

        val paint = Paint()

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

        var left = 0
        var top = 0
        var right = mask.width
        var bottom = mask.height
        if (mask.width > background.width) {
            left = (mask.width - background.width) / 2
            right = mask.width - left
        }

        if (mask.height > background.height) {
            top = (mask.height - background.height) / 2
            bottom = mask.height - top
        }

        newCanvas.drawBitmap(mask, Rect(left, top, right, bottom),
                Rect(0, 0, background.width, background.height),
                paint)

        return newBitmap
    }

    /**
     * 但参数的单例
     */
    companion object {
        //Volatile 易变的，不稳定的
        @Volatile
        private var instance: BubbleImageHelper? = null

        @Synchronized
        fun getInstance(c: Context): BubbleImageHelper? {
            if (null == instance) {
                instance = BubbleImageHelper(c)
            }
            return instance
        }
    }
}

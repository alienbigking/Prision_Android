package com.starlight.mobile.android.lib.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.DisplayMetrics

import java.io.File


/**图片处理
 * @author raleigh
 */
object ImageTool {
    private var MAX_NUM_PIXELS = 320 * 480
    private var MIN_SIDE_LENGTH = 320
    private val LARGER_IMAGE_MAX_SIDE_LENGTH = 720 * 1280//大图的比例
    private val LARGER_IMAGE_MIN_SIDE_LENGTH = 720

    /**生成图片的压缩图
     * @param context
     * @param filePath
     * @param scale 小于1的倍数，默认值为0.5，如scale=0.3f，屏幕的0.3倍
     * @return
     */
    fun createImageThumbnail(context: Context, filePath: String?, scale: Float): Bitmap? {
        var scale = scale
        if (null == filePath || !File(filePath).exists())
            return null
        if (scale > 1 || scale <= 0) scale = 0.5f
        var bitmap: Bitmap? = null
        val degree = ViewUtil.getExifOrientation(filePath)

        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        //绝对宽度与高度
        val maxWidth = (dm.widthPixels * scale).toInt()
        val maxHeight = (dm.heightPixels * scale).toInt()
        MIN_SIDE_LENGTH = if (maxWidth > maxHeight) maxHeight else maxWidth
        MAX_NUM_PIXELS = maxWidth * maxHeight
        try {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, opts)
            opts.inSampleSize = computeSampleSize(opts, -1, MAX_NUM_PIXELS, MIN_SIDE_LENGTH)
            opts.inJustDecodeBounds = false
            //            if (opts.inSampleSize == 1) {
            //                bitmap = BitmapFactory.decodeFile(filePath, opts);
            //
            //            } else {
            bitmap = BitmapFactory.decodeFile(filePath, opts)

            //            }
        } catch (e: Exception) {
            return null
        }

        return ViewUtil.rotaingImageView(degree, bitmap)
    }

    /**
     *
     * @Description 生成图片的大图
     * @param filePath
     * @return
     */
    fun createImageBmp(filePath: String?): Bitmap? {
        if (null == filePath || !File(filePath).exists())
            return null
        var bitmap: Bitmap? = null
        val degree = ViewUtil.getExifOrientation(filePath)
        try {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, opts)
            opts.inSampleSize = computeSampleSize(opts, -1, LARGER_IMAGE_MAX_SIDE_LENGTH, LARGER_IMAGE_MIN_SIDE_LENGTH)
            opts.inJustDecodeBounds = false
            //            if (opts.inSampleSize == 1) {
            //                bitmap = BitmapFactory.decodeFile(filePath, opts);
            //
            //            } else {
            bitmap = BitmapFactory.decodeFile(filePath, opts)
            //            }
        } catch (e: Exception) {
            return null
        }

        return ViewUtil.rotaingImageView(degree, bitmap)
    }

    private fun computeSampleSize(options: BitmapFactory.Options,
                                  minSideLength: Int, maxNumOfPixels: Int, SystemMinSideLength: Int): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels, SystemMinSideLength)
        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8
        }
        return roundedSize
    }

    private fun computeInitialSampleSize(options: BitmapFactory.Options,
                                         minSideLength: Int, maxNumOfPixels: Int, SystemMinSideLength: Int): Int {
        var SystemMinSideLength = SystemMinSideLength
        if (SystemMinSideLength < MIN_SIDE_LENGTH) SystemMinSideLength = MIN_SIDE_LENGTH
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()
        val lowerBound = if (maxNumOfPixels == -1)
            1
        else
            Math.ceil(Math.sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength == -1)
            SystemMinSideLength
        else
            Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength)).toInt()
        if (upperBound < lowerBound) {
            return lowerBound
        }
        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }

    fun getBigBitmapForDisplay(imagePath: String?,
                               context: Context): Bitmap? {
        if (null == imagePath || !File(imagePath).exists())
            return null
        try {
            val degeree = ViewUtil.getExifOrientation(imagePath)
            val bitmap = BitmapFactory.decodeFile(imagePath) ?: return null
            val dm = DisplayMetrics()
            (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
            val scaleW = bitmap.width / dm.widthPixels.toFloat()
            val scaleH = bitmap.height / dm.heightPixels.toFloat()
            val scale = if (scaleW > scaleH) scaleW else scaleH
            var newBitMap: Bitmap? = null
            if (scale > 1) {
                newBitMap = zoomBitmap(bitmap, (bitmap.width / scale).toInt(), (bitmap.height / scale).toInt())
                bitmap.recycle()
                return ViewUtil.rotaingImageView(degeree, newBitMap)
            }
            return ViewUtil.rotaingImageView(degeree, bitmap)
        } catch (e: Exception) {
            return null
        }

    }

    private fun zoomBitmap(bitmap: Bitmap?, width: Int, height: Int): Bitmap? {
        if (null == bitmap) {
            return null
        }
        try {
            val w = bitmap.width
            val h = bitmap.height
            val matrix = Matrix()
            val scaleWidth = width.toFloat() / w
            val scaleHeight = height.toFloat() / h
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
        } catch (e: Exception) {
            return null
        }

    }

}

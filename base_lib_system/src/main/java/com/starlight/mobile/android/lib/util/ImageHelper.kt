package com.starlight.mobile.android.lib.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix

import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**
 * Created by Raleigh on 15/9/9.
 */
class ImageHelper {
    //单例
    private object Holder { val INSTANCE = ImageHelper() }
    companion object {
        val instance:ImageHelper by lazy{
            Holder.INSTANCE
        }
    }
    internal val MAX_IMAGE_SIZE = 200//最大图片大小200KB
    internal val MAX_IMAGE_WIDTH = 720f//最大图片width
    internal val MAX_IMAGE_HEIGHT = 1280f//最大图片height
    private fun compress(image: Bitmap, photoDir: String): String? {
        var path: String? = null
        try {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100
            while (baos.toByteArray().size / 1024 > MAX_IMAGE_SIZE) {    //循环判断如果压缩后图片是否大于MAX_IMAGE_SIZE kb,大于继续压缩
                baos.reset()//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10//每次都减少10
                if (options < 0) {
                    options = 1
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos)
                    break
                }
            }
            val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
            val bitmap = BitmapFactory.decodeStream(isBm, null, null)//把ByteArrayInputStream数据生成图片
            path = saveBitmap(bitmap, photoDir, options)
            bitmap.recycle()
            image.recycle()
            System.gc()
        } catch (e: Exception) {
        }

        return path
    }

    private fun compress(image: Bitmap, photoDir: String, fileName: String): String? {
        var path: String? = null
        try {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            var options = 100
            while (baos.toByteArray().size / 1024 > MAX_IMAGE_SIZE) {    //循环判断如果压缩后图片是否大于MAX_IMAGE_SIZE kb,大于继续压缩
                baos.reset()//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos)//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10//每次都减少10
                if (options < 0) {
                    options = 1
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos)
                    break
                }
            }
            val isBm = ByteArrayInputStream(baos.toByteArray())//把压缩后的数据baos存放到ByteArrayInputStream中
            val bitmap = BitmapFactory.decodeStream(isBm, null, null)//把ByteArrayInputStream数据生成图片
            ViewUtil.createMkdir(photoDir)
            path = photoDir + fileName + ".jpg"
            try {
                val fout = FileOutputStream(path)
                val bos = BufferedOutputStream(fout)
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos)
                bos.flush()
                bos.close()
                // .i(tag, "saveJpeg：存储完毕！");
            } catch (e: IOException) {
                // Log.i(tag, "saveJpeg:存储失败！");
                e.printStackTrace()
            }

            bitmap.recycle()
            image.recycle()
            System.gc()
        } catch (e: Exception) {
        }

        return path
    }

    /**图片按比例大小压缩方法（根据路径获取图片并压缩）：
     * @param srcPath
     * @return
     */
    fun compressImage(srcPath: String, desPhotoDir: String): String? {
        var path: String? = null
        try {
            val degree = ViewUtil.getExifOrientation(srcPath)
            val newOpts = BitmapFactory.Options()
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)//此时返回bm为空
            try {
                if (degree == 90 || degree == 180 || degree == 270) {
                    // Roate preview icon according to exif
                    // orientation
                    val matrix = Matrix()
                    matrix.postRotate(degree.toFloat())
                    val tBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.width,
                            bitmap.height, matrix, true)
                    bitmap = tBitmap
                    tBitmap.recycle()
                }
            } catch (e: Exception) {
            }

            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val hh = MAX_IMAGE_HEIGHT
            val ww = MAX_IMAGE_WIDTH
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            //缩放倍数
            newOpts.inSampleSize = be//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            var isFinished = false
            while (!isFinished) {
                try {
                    bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
                    isFinished = true
                } catch (e: OutOfMemoryError) {
                    isFinished = false
                    newOpts.inSampleSize++
                }

            }
            if (bitmap.width > MAX_IMAGE_WIDTH || bitmap.height > MAX_IMAGE_HEIGHT) bitmap = resizeImage(bitmap)
            path = compress(bitmap, desPhotoDir)//压缩好比例大小后再进行质量压缩
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return path
    }

    /**图片按比例大小压缩方法（根据路径获取图片并压缩）：
     * @param srcPath
     * @return
     */
    fun compressImage(srcPath: String, desPhotoDir: String, fileName: String): String? {
        var path: String? = null
        try {
            val degree = ViewUtil.getExifOrientation(srcPath)
            val newOpts = BitmapFactory.Options()
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)//此时返回bm为空
            try {
                if (degree == 90 || degree == 180 || degree == 270) {
                    // Roate preview icon according to exif
                    // orientation
                    val matrix = Matrix()
                    matrix.postRotate(degree.toFloat())
                    val tBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.width,
                            bitmap.height, matrix, true)
                    bitmap = tBitmap
                    tBitmap.recycle()
                }
            } catch (e: Exception) {
            }

            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val hh = MAX_IMAGE_HEIGHT
            val ww = MAX_IMAGE_WIDTH
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            //缩放倍数
            newOpts.inSampleSize = be//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            var isFinished = false
            while (!isFinished) {
                try {
                    bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
                    isFinished = true
                } catch (e: OutOfMemoryError) {
                    isFinished = false
                    newOpts.inSampleSize++
                }

            }
            if (bitmap.width > MAX_IMAGE_WIDTH || bitmap.height > MAX_IMAGE_HEIGHT) bitmap = resizeImage(bitmap)
            path = compress(bitmap, desPhotoDir, fileName)//压缩好比例大小后再进行质量压缩
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return path
    }

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        var bitmap = bitmap
        val width = bitmap.width
        val height = bitmap.height
        val newWidth = MAX_IMAGE_WIDTH
        val newHeight = MAX_IMAGE_HEIGHT
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val scale = Math.min(scaleWidth, scaleHeight)
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // if you want to rotate the Bitmap
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true)
        } catch (e: Exception) {

        }

        return bitmap

    }

    /**图片按比例大小压缩方法（根据Bitmap图片压缩）：
     * @param image
     * @return
     */
    fun compressImage(image: Bitmap, desPhotoDir: String): String? {
        var path: String? = null
        try {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            //        if( baos.toByteArray().length / 1024>MAX_IMAGE_SIZE) {//判断如果图片大于MAX_IMAGE_SIZE KB,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            //            baos.reset();//重置baos即清空baos
            //            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
            //        }
            var isBm = ByteArrayInputStream(baos.toByteArray())
            val newOpts = BitmapFactory.Options()
            //开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true
            var bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val hh = MAX_IMAGE_HEIGHT
            val ww = MAX_IMAGE_WIDTH
            //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            var be = 1//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0)
                be = 1
            newOpts.inSampleSize = be//设置缩放比例
            //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            isBm = ByteArrayInputStream(baos.toByteArray())
            bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
            path = compress(bitmap, desPhotoDir)//压缩好比例大小后再进行质量压缩
        } catch (e: Exception) {
        }

        return path
    }


    /** 保存图片
     * @param bitmap
     * @param imageSaveDir 图片保存的路径，如/storage0/myDir/photo/
     * @param quality 图片压缩的质量，值的范围为0-100，100表示不压缩
     * @return 图片存储的完整路径
     */
    fun saveBitmap(bitmap: Bitmap, imageSaveDir: String, quality: Int): String {

        ViewUtil.createMkdir(imageSaveDir)
        val ramdom = UUID.randomUUID().toString().replace("-", "")

        //        String jpegPath = imageSaveDir + ramdom + ".jpg";
        val jpegPath = imageSaveDir + ramdom
        try {
            val fout = FileOutputStream(jpegPath)
            val bos = BufferedOutputStream(fout)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bos.flush()
            bos.close()
            // .i(tag, "saveJpeg：存储完毕！");
        } catch (e: IOException) {
            // Log.i(tag, "saveJpeg:存储失败！");
            e.printStackTrace()
        }

        return jpegPath
    }
}

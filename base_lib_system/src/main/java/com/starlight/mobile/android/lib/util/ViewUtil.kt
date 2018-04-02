package com.starlight.mobile.android.lib.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView

import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

/**控件资源工具类
 * @author raleigh
 */
object ViewUtil {

    /**缩放图片大小
     * @param path图片本地路径
     * @return
     */
    fun fitSizeImg(path: String): Bitmap? {
        val file = File(path)
        var resizeBmp: Bitmap? = null
        val opts = BitmapFactory.Options()
        // 数字越大读出的图片占用的heap越小 不然总是溢出
        if (file.length() < 20480) {       // 0-20k
            opts.inSampleSize = 1
        } else if (file.length() < 51200) { // 20-50k
            opts.inSampleSize = 2
        } else if (file.length() < 307200) { // 50-300k
            opts.inSampleSize = 4
        } else if (file.length() < 819200) { // 300-800k
            opts.inSampleSize = 6
        } else if (file.length() < 1048576) { // 800-1024k
            opts.inSampleSize = 8
        } else {
            opts.inSampleSize = 10
        }
        resizeBmp = BitmapFactory.decodeFile(file.path, opts)
        return resizeBmp
    }

    /**等比例缩放图片
     * @param path
     * @param width
     * @param height
     * @return
     */
    fun scalingBmp(path: String, width: Int, height: Int): Bitmap {
        val op = BitmapFactory.Options()
        op.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(path, op)

        // 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
        val widthRatio = Math.ceil((op.outWidth / width.toFloat()).toDouble()).toInt()
        val heightRatio = Math.ceil((op.outHeight / height.toFloat()).toDouble()).toInt()

        if (widthRatio > 1 && heightRatio > 1) {
            if (widthRatio > heightRatio) {
                // 压缩到原来的(1/widthRatios)
                op.inSampleSize = widthRatio
            } else {
                op.inSampleSize = heightRatio
            }
        }
        op.inJustDecodeBounds = false
        bmp = BitmapFactory.decodeFile(path, op)
        return bmp
    }

    /**等比例缩放图片
     * @param res  getResources()
     * @param id   图片的id,如:R.drawable.ic_lancher
     * @param width
     * @param height
     * @return
     */
    fun scalingBmp(res: Resources, id: Int, width: Int, height: Int): Bitmap {
        val op = BitmapFactory.Options()
        op.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeResource(res, id, op)

        // 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
        val widthRatio = Math.ceil((op.outWidth / width.toFloat()).toDouble()).toInt()
        val heightRatio = Math.ceil((op.outHeight / height.toFloat()).toDouble()).toInt()

        if (widthRatio > 1 && heightRatio > 1) {
            if (widthRatio > heightRatio) {
                // 压缩到原来的(1/widthRatios)
                op.inSampleSize = widthRatio
            } else {
                op.inSampleSize = heightRatio
            }
        }
        op.inJustDecodeBounds = false
        bmp = BitmapFactory.decodeResource(res, id, op)
        return bmp
    }

    /**等比例缩放图片,通过Matrix缩放
     * @param bm
     * @param width
     * @param height
     * @return
     */
    fun scalingBmp(bm: Bitmap, width: Int, height: Int): Bitmap {
        // 获得图片的宽高
        val mWidth = bm.width
        val mHeight = bm.height
        // 计算缩放比例
        val scaleWidth = width.toFloat() / mWidth
        val scaleHeight = height.toFloat() / mHeight
        val scale = if (scaleWidth > scaleHeight) scaleHeight else scaleWidth
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, mWidth, mHeight, matrix, true)
    }

    fun createMkdir(mkdirPath: String) {
        val folder = File(mkdirPath)
        if (!folder.exists())
        // 如果文件夹不存在则创建
        {
            folder.mkdirs()
        }
    }

    fun dipToPx(context: Context, dip: Int): Int {
        val r = context.resources
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(),
                r.displayMetrics)
        return px.toInt()
    }

    /**
     * @param context
     * @param view
     * @param colorId
     * getResouce.getColor()
     */
    fun setStateLeftColor(context: Context, view: TextView,
                          colorId: Int) {
        val r = dipToPx(context, 8)
        val outerR = floatArrayOf(r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat())
        val roundRectShape = RoundRectShape(outerR, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape) // 组合圆角矩形和ShapeDrawable
        shapeDrawable.paint.color = colorId // 设置形状的颜色
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        shapeDrawable.setBounds(0, 0, dipToPx(context, 10),
                dipToPx(context, 10))
        view.compoundDrawablePadding = dipToPx(context, 5)// 边距
        view.setCompoundDrawables(shapeDrawable, null, null, null) // 设置左图标
    }

    /**
     * --设置Textview的左图标
     *
     * @param context
     * @param view
     * @param colorId
     * getResouce.getColor()
     * @param size
     * 大小
     */
    fun setStateLeftColor(context: Context, view: TextView,
                          colorId: Int, size: Int) {
        val r = dipToPx(context, 8)
        val outerR = floatArrayOf(r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat())
        val roundRectShape = RoundRectShape(outerR, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape) // 组合圆角矩形和ShapeDrawable
        shapeDrawable.paint.color = colorId // 设置形状的颜色
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        shapeDrawable.setBounds(0, 0, dipToPx(context, size),
                dipToPx(context, size))
        view.compoundDrawablePadding = dipToPx(context, 5)// 边距
        view.setCompoundDrawables(shapeDrawable, null, null, null) // 设置左图标
    }

    /**
     * --设置Textview的右图标
     *
     * @param context
     * @param view
     * @param colorId
     * getResouce.getColor()
     */
    fun setStateRightColor(context: Context, view: TextView,
                           colorId: Int) {
        val r = dipToPx(context, 8)
        val outerR = floatArrayOf(r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat())
        val roundRectShape = RoundRectShape(outerR, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape) // 组合圆角矩形和ShapeDrawable
        shapeDrawable.paint.color = colorId // 设置形状的颜色
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        shapeDrawable.setBounds(0, 0, dipToPx(context, 10),
                dipToPx(context, 10))
        view.compoundDrawablePadding = dipToPx(context, 5)// 边距
        view.setCompoundDrawables(null, null, shapeDrawable, null) // 设置左图标
    }

    /**
     * 设置View圆形背景
     */
    fun setOvalBackGroundColor(context: Context, view: View,
                               colorId: Int) {
        val r = dipToPx(context, 8)
        val outerR = floatArrayOf(r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat(), r.toFloat())
        val roundRectShape = RoundRectShape(outerR, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape) // 组合圆角矩形和ShapeDrawable
        shapeDrawable.paint.color = colorId // 设置形状的颜色
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        shapeDrawable.setBounds(0, 0, dipToPx(context, 15),
                dipToPx(context, 15))
        view.setBackgroundDrawable(shapeDrawable) // 设置左图标
    }

    fun getAbsolutePathFromURI(contentUri: Uri,
                               activity: Activity): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    // /**处理图片为圆角图片,适用于长宽相差不大的图片
    // * @param bitmap 以最长的一边为边长
    // * @return
    // */
    // public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
    // Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
    // bitmap.getHeight(), Config.ARGB_8888);
    // Canvas canvas = new Canvas(output);
    //
    // final int color = 0xff424242;
    // final Paint paint = new Paint();
    // int side=
    // bitmap.getWidth()>bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight();
    //
    // final Rect rect = new Rect(0, 0, side, side);
    // final RectF rectF = new RectF(rect);
    // paint.setAntiAlias(true);
    // canvas.drawARGB(0, 0, 0, 0);
    // paint.setColor(color);
    // canvas.drawOval(rectF, paint);
    // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    // canvas.drawBitmap(bitmap, rect, rect, paint);
    //
    // return output;
    // }
    // /**处理图片为圆角图片,适用于长宽相差不大的图片
    // * @param bitmap 以最短的一边为边长
    // * @return
    // */
    // public static Bitmap getRoundedCornerBitmap2(Bitmap bitmap) {
    // Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
    // bitmap.getHeight(), Config.ARGB_8888);
    // Canvas canvas = new Canvas(output);
    //
    // final int color = 0xff424242;
    // final Paint paint = new Paint();
    // int side=
    // bitmap.getWidth()>bitmap.getHeight()?bitmap.getHeight():bitmap.getWidth();
    // final Rect rect = new Rect(0, 0, side, side);
    // final RectF rectF = new RectF(rect);
    // paint.setAntiAlias(true);
    // canvas.drawARGB(0, 0, 0, 0);
    // paint.setColor(color);
    // canvas.drawOval(rectF, paint);
    // paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    // canvas.drawBitmap(bitmap, rect, rect, paint);
    //
    // return output;
    // }
    /**
     * 处理图片为圆角图片,适用于长宽相差交大的图片
     *
     * @param bitmap
     * 以最短的一边为边长
     * @return
     */
    fun getRoundedCornerBitmap3(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = 0xff424242.toInt()
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    /**
     *
     */
    fun isImage(imagePath: String): Boolean {
        try {
            val imageFile = File(imagePath)
            if (imageFile.length() == 0L) {
                return false
            } else {
                val bitmap: Bitmap
                val drawable = Drawable.createFromPath(imagePath)
                val bd = drawable as BitmapDrawable
                bitmap = bd.bitmap
                // image = Image.From
                if (bitmap.height > 0 && bitmap.width > 0) {
                    return true
                }
            }
        } catch (e: Exception) {
            // TODO: handle exception
            return false
        }

        return false
    }

    fun getScreenDisplay(context: Context): Display {

        val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay
    }
    fun getScreenWidth(context: Context): Int {
        val size:Point= Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(size)
        return size.x
    }
    fun getScreenHeight(context: Context): Int {
        val size:Point= Point()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getSize(size)
        return size.y
    }
    fun imgToBase64(imgPath: String): String? {
        var bitmap: Bitmap? = null
        if ( imgPath.length > 0) {
            bitmap = readBitmap(imgPath)
        }
        if (bitmap == null) {
            // bitmap not found!!
            return null
        }
        //旋转角度
        val degree = getExifOrientation(imgPath)
        if (degree == 90 || degree == 180 || degree == 270) {
            //Roate preview icon according to exif orientation
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            bitmap = Bitmap.createBitmap(bitmap,
                    0, 0, bitmap.width, bitmap.height, matrix, true)

        }
        var out: ByteArrayOutputStream? = null
        try {
            out = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)

            out.flush()
            out.close()

            val imgBytes = out.toByteArray()
            return Base64.encodeToString(imgBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                out?.flush()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun readBitmap(imgPath: String): Bitmap? {
        try {
            return BitmapFactory.decodeFile(imgPath)
        } catch (e: Exception) {
            return null
        }

    }

    fun getExifOrientation(filepath: String): Int {
        var degree = 0
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)
        } catch (ex: IOException) {
        }

        exif?.let {
            val orientation = it.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90

                    ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180

                    ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                    else -> {
                    }
                }
            }
        }

        return degree
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    fun getSmallBitmap(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 600)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    fun getSmallBitmap(filePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, width)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 加载图片，防止内存溢出
     *
     * @param pathName
     * 图片路径
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun decodeSampledBitmapFromResource(pathName: String,
                                        reqWidth: Int, reqHeight: Int): Bitmap {
        // 获取尺寸
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(pathName, options)
    }

    fun zoomImage(bgimage: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        // 获取这个图片的宽和高
        val width = bgimage.width.toFloat()
        val height = bgimage.height.toFloat()
        // 创建操作图片用的matrix对象
        val matrix = Matrix()
        // 计算宽高缩放率
        val scaleWidth = newWidth / width
        val scaleHeight = newHeight / height
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bgimage, 0, 0, width.toInt(),
                height.toInt(), matrix, true)
    }

    /**
     * 计算图片变化后于原图的比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        // 图像原始高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    /**
     * 压缩图片质量
     *
     * @param image
     * 图片
     * @return 压缩后的图片文件
     */
    fun compressImage(image: Bitmap): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10// 每次都减少10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    /**
     * 获取图片
     *
     * @param srcPath
     * 图片路径
     * @return 返回图片
     */
    fun getimage(srcPath: String): Bitmap {
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)// 此时返回bm为空

        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        val hh = 800f// 这里设置高度为800f
        val ww = 480f// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        return compressImage(bitmap)// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩图片
     *
     * @param image
     * 图片文件
     * @return 返回压缩后的图片文件
     */
    fun comp(image: Bitmap): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        if (baos.toByteArray().size / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset()// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)// 这里压缩50%，把压缩后的数据存放到baos中
        }
        var isBm = ByteArrayInputStream(baos.toByteArray())
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true
        var bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        val hh = 800f// 这里设置高度为800f
        val ww = 480f// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = ByteArrayInputStream(baos.toByteArray())
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts)
        return compressImage(bitmap)// 压缩好比例大小后再进行质量压缩
    }

    /*
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
    fun rotaingImageView(angle: Int, bitmap: Bitmap?): Bitmap? {
        if (null == bitmap) {
            return null
        }
        // 旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true)
    }
}

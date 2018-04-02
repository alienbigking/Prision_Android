package com.starlight.mobile.android.lib.album

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache

import java.lang.reflect.Field
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

/**
 * Created by Raleigh on 15/7/10.
 */
class AlbumImageLoader @JvmOverloads constructor(threadCount: Int = 1, type: Type = Type.LIFO) {
    /**
     * 图片缓存的核心类
     */
    private var mLruCache: UsingFreqLimitedMemoryCache? = null
    /**
     * 线程池
     */
    private var mThreadPool: ExecutorService? = null
    /**
     * 线程池的线程数量，默认为1
     */
    private val mThreadCount = 1
    /**
     * 队列的调度方式
     */
    private var mType = Type.LIFO
    /**
     * 任务队列
     */
    private var mTasks: LinkedList<Runnable>? = null
    /**
     * 轮询的线程
     */
    private var mPoolThread: Thread? = null
    private var mPoolThreadHander: Handler? = null

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private var mHandler: Handler? = null



    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    @Volatile private var mPoolSemaphore: Semaphore? = null
    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    @Volatile private var mSemaphore: Semaphore? = Semaphore(0)

    /**
     * 队列的调度方式
     *
     * @author zhy
     */
    enum class Type {
        FIFO, LIFO
    }


    init {
        init(threadCount, type)
    }

    private fun init(threadCount: Int, type: Type?) {
        // loop thread
        mPoolThread = object : Thread() {
            override fun run() {
                Looper.prepare()

                mPoolThreadHander = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        task?.let {
                            mThreadPool?.execute(it)
                            try {
                                mPoolSemaphore?.acquire()
                            } catch (e: InterruptedException) {
                            }
                        }
                    }
                }
                // 释放一个信号量
                mSemaphore?.release()
                Looper.loop()
            }
        }
        mPoolThread?.start()

        // 获取应用程序最大可用内存
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 8
        mLruCache = UsingFreqLimitedMemoryCache(cacheSize)
        mThreadPool = Executors.newFixedThreadPool(threadCount)
        mPoolSemaphore = Semaphore(threadCount)
        mTasks = LinkedList()
        mType = type ?: Type.LIFO

    }

    /**
     * 加载图片
     *
     * @param path
     * @param imageView
     */
    fun loadImage(path: String, imageView: ImageView) {
        // set tag
        imageView.tag = path
        // UI线程
        if (mHandler == null) {
            mHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val holder = msg.obj as ImgBeanHolder
                    val imageView = holder.imageView
                    val bm = holder.bitmap
                    val path = holder.path
                    imageView?.let{
                        if (it.tag.toString() == path) {
                            it.setImageBitmap(bm)
                        }
                    }

                }
            }
        }

        val bm = getBitmapFromLruCache(path)
        if (bm != null) {
            val holder = ImgBeanHolder()
            holder.bitmap = bm
            holder.imageView = imageView
            holder.path = path
            val message = Message.obtain()
            message.obj = holder
            mHandler?.sendMessage(message)
        } else {
            addTask(Runnable {
                val imageSize = getImageViewWidth(imageView)

                val reqWidth = imageSize.width
                val reqHeight = imageSize.height

                val bm = decodeSampledBitmapFromResource(path, reqWidth,
                        reqHeight)
                addBitmapToLruCache(path, bm)
                val holder = ImgBeanHolder()
                holder.bitmap = getBitmapFromLruCache(path)
                holder.imageView = imageView
                holder.path = path
                val message = Message.obtain()
                message.obj = holder
                // Log.e("TAG", "mHandler.sendMessage(message);");
                mHandler?.sendMessage(message)
                mPoolSemaphore?.release()
            })
        }

    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    @Synchronized private fun addTask(runnable: Runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore?.acquire()
        } catch (e: InterruptedException) {
        }

        mTasks?.add(runnable)

        mPoolThreadHander?.sendEmptyMessage(0x110)
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private val task: Runnable?
        @Synchronized get() {
            when(mType){
                Type.FIFO ->
                    return mTasks?.removeFirst()
                Type.LIFO ->
                    return mTasks?.removeLast()
                else ->
                    return null
            }
        }

    /**
     * 根据ImageView获得适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private fun getImageViewWidth(imageView: ImageView): ImageSize {
        val imageSize = ImageSize()
        val displayMetrics = imageView.context
                .resources.displayMetrics
        val params = imageView.layoutParams

        var width = if (params.width == LayoutParams.WRAP_CONTENT) 0 else imageView.width // Get actual image width
        if (width <= 0)
            width = params.width // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth") // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels
        var height = if (params.height == LayoutParams.WRAP_CONTENT)
            0
        else
            imageView
                    .height // Get actual image height
        if (height <= 0)
            height = params.height // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight") // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels
        imageSize.width = width
        imageSize.height = height
        return imageSize

    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private fun getBitmapFromLruCache(key: String): Bitmap? {
        return mLruCache?.get(key)
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private fun addBitmapToLruCache(key: String, bitmap: Bitmap?) {
        if (getBitmapFromLruCache(key) == null) {
            bitmap?.let {
                mLruCache?.put(key, it)
            }
        }
    }

    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options,
                                      reqWidth: Int, reqHeight: Int): Int {
        // 源图片的宽度
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            val heightRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = Math.max(widthRatio, heightRatio)
        }
        return inSampleSize
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private fun decodeSampledBitmapFromResource(pathName: String,
                                                reqWidth: Int, reqHeight: Int): Bitmap? {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(pathName, options)
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight)
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false
        var bitmap: Bitmap? = null
        var isFinish = false
        while (!isFinish) {
            try {
                bitmap = BitmapFactory.decodeFile(pathName, options)
                isFinish = true
            } catch (e: OutOfMemoryError) {
                System.gc()
                options.inSampleSize++
                isFinish = false
            }

        }
        return bitmap
    }

    private inner class ImgBeanHolder {
        internal var bitmap: Bitmap? = null
        internal var imageView: ImageView? = null
        internal var path: String? = null
    }

    private inner class ImageSize {
        internal var width: Int = 0
        internal var height: Int = 0
    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private fun getImageViewFieldValue(`object`: Any, fieldName: String): Int {
        var value = 0
        try {
            val field = ImageView::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            val fieldValue = field.get(`object`) as Int
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue

                Log.e("TAG", value.toString() + "")
            }
        } catch (e: Exception) {
        }

        return value
    }

    fun onDestory() {
        mPoolThread?.let {
            if (!it.isInterrupted) mPoolThread?.interrupt()
        }
        mPoolThread = null
        mLruCache?.let {
            for (key in it.keys()) {
                var bitmap = getBitmapFromLruCache(key)
                bitmap?.let {
                    it.recycle()
                }
                bitmap = null
            }
        }
        mLruCache?.clear()
    }
}

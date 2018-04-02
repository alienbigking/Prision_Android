package com.gkzxhn.prison.utils

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.view.WindowManager

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.GKApplication
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.view.CusPhotoFromDialog

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Administrator on 2016/4/7.
 */
object Utils {
    /**网络是否连接
     * @return
     */
    fun getNetworkConnected(): Boolean{
        var result: Boolean = false
        try {
            val conn = GKApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = conn.activeNetworkInfo
            info?.let {
                result = info.isConnected
            }
        }catch (e:Exception){}
        return  result
    }
    /**
     * 获取屏幕宽高
     * @return
     */
    fun getScreenWidthHeight(): IntArray{
        val wm = GKApplication.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width = wm.defaultDisplay.width
        val height = wm.defaultDisplay.height
        return intArrayOf(width, height)
    }


    /**获取SD卡剩余空间
     * @return
     */
    //取得SD卡文件路径
    //获取单个数据块的大小(Byte)
    //空闲的数据块的数量
    //返回SD卡空闲大小
    //return freeBlocks * blockSize;  //单位Byte
    //return (freeBlocks * blockSize)/1024;   //单位KB
    //单位MB
    fun getSdFreeSize(): Long{
        val path = Environment.getExternalStorageDirectory()
        val sf = StatFs(path.path)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            val blockSize= sf.blockSizeLong
            val freeBlocks = sf.availableBlocksLong
            return freeBlocks * blockSize / 1024 / 1024
        }else{
            return 0
        }
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    /**
     * 显示图片来源对话框，相册/拍照
     *
     * @param context
     * @param photoFromClickListener
     */
    fun buildPhotoDialog(context: Context,
                         photoFromClickListener: CusPhotoFromDialog.PhotoFromClickListener): CusPhotoFromDialog {
        val dialog = CusPhotoFromDialog(context)
        dialog.photoFromClickListener=photoFromClickListener
        dialog.setBtnTitle(context.resources
                .getString(R.string.take_photo), context.resources
                .getString(R.string.album), context.resources
                .getString(R.string.cancel))
        return dialog
    }

    /**
     * 验证手机号码的合法性
     * @param mobiles 手机号码
     * @return 返回是否为手机号码
     */
    fun isPhoneNumber(mobiles: String): Boolean {
        //        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        //        Matcher m = p.matcher(mobiles);
        //        return m.matches();
        val telRegex = "[1][3456789]\\d{9}"//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return if (TextUtils.isEmpty(mobiles))
            false
        else
            mobiles.matches(telRegex.toRegex())
    }

    /**
     * 验证邮箱地址是否正确
     * @param email
     * @return
     */
    fun checkEmail(email: String): Boolean {
        var flag = false
        try {
            val check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
            val regex = Pattern.compile(check)
            val matcher = regex.matcher(email)
            flag = matcher.matches()
        } catch (e: Exception) {
            flag = false
        }

        return flag
    }

    /**
     * 处理时间
     *
     * @param timeInMillis
     */
    fun dealTime(timeInMillis: Long): String {
        val mContext = GKApplication.instance
        var result = ""
        if (isToday(timeInMillis)) {//今天
            result = ConvertUtil.getSystemShortTimeFormat(timeInMillis)
        } else {//非今天
            val distance = ConvertUtil.compareWithCurDate(timeInMillis)//不比较时分，只比较年月日
            result = getDateFromTimeInMillis(timeInMillis, SimpleDateFormat("yyyy-MM-dd HH:mm"))
        }
        return result
    }

    fun isToday(timeInMillis: Long): Boolean {
        val todayCalendar = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                todayCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getDateFromTimeInMillis(timeInMillis: Long): String {
        var result = ""
        if (timeInMillis > 0) {
            try {
                val date = Date(timeInMillis)
                //英文格式时间格式化
                val df = SimpleDateFormat("yyyy-MM-dd")
                result = df.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }

    fun getDateFromTimeInMillis(timeInMillis: Long, df: SimpleDateFormat): String {
        var result = ""
        if (timeInMillis > 0) {
            try {
                val date = Date(timeInMillis)
                //英文格式时间格式化
                result = df.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return result
    }

    fun getOptions(defualtImgRes: Int): DisplayImageOptions {
        return DisplayImageOptions.Builder()
                .showImageOnLoading(defualtImgRes)//默认加载的图片
                .showImageForEmptyUri(defualtImgRes)//下载地址不存在

                .showImageOnFail(defualtImgRes).cacheInMemory(false).cacheOnDisk(true)//加载失败的图
                //	.displayer(new RoundedBitmapDisplayer(0))  设置圆角，设置后不能使用loadimage方法，项目并不需要圆角
                .bitmapConfig(Bitmap.Config.RGB_565)    //设置图片的质量
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)    //设置图片的缩放类型，该方法可以有效减少内存的占用
                .build()
    }

    //当前应用是否处于前台
    fun isForeground(context: Context?): Boolean {
        if (context != null) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processes = activityManager.runningAppProcesses
            for (processInfo in processes) {
                if (processInfo.processName == context.packageName) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun hasSDFree(): Boolean {
        return getSdFreeSize() > 100//大于100M
    }

}

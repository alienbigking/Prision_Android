package com.gkzxhn.prison.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.android.volley.VolleyError

import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject

import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.Thread.UncaughtExceptionHandler
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author huangzhengneng
 * created on 2016/5/9
 */
class CrashHandler
/** 保证只有一个CrashHandler实例  */
private constructor() : UncaughtExceptionHandler {
    val TAG = CrashHandler::class.java.name
    //系统默认的UncaughtException处理类
    private var mDefaultHandler: UncaughtExceptionHandler? = null
    //程序的Context对象
    private lateinit var mContext: Context
    //用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    //用于格式化日期,作为日志文件名的一部分
    private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
    private val flag = false
    private val volleyUtils = VolleyUtils()

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        mContext = context
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : " + e.message)
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //        saveCrashInfo2File(ex);
        if (!flag) {
            val b = StringBuffer()
            b.append(ex.toString())
            val stackTraceElements = ex.stackTrace
            val lenth = if (stackTraceElements.size > 5) 5 else stackTraceElements.size
            if (stackTraceElements != null) {
                for (i in 0 until lenth) {
                    val s = stackTraceElements[i]
                    if (s != null && !s.toString().isEmpty()) b.append("\n" + s.toString())
                }
            }
            Log.e("raleigh_test crash",b.toString())
//TODO            uploadLog(b.toString())
        }
        if (Constants.IS_DEBUG_MODEL) {//打印日志
            ex.printStackTrace()
        }
        return true
    }

    /**上传奔溃日志
     * @param message
     */
    private fun uploadLog(message: String) {
        val params = JSONObject()
        try {
            val preferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
            params.put("phone", preferences.getString(Constants.USER_ACCOUNT, ""))
            params.put("contents", message)
            params.put("device_name", android.os.Build.MODEL)
            params.put("sys_version", "Android")
            params.put("device_type", Build.VERSION.SDK_INT.toString())
            val pm = mContext.packageManager
            var packageInfo: PackageInfo? = null
            packageInfo = pm.getPackageInfo(mContext.packageName,
                    PackageManager.GET_CONFIGURATIONS)
            params.put("app_version", packageInfo.versionCode)
            volleyUtils.post(Constants.REQUEST_CRASH_LOG_URL, JSONObject().put("logger", params), null,object :VolleyUtils.OnFinishedListener<JSONObject>{
                override fun onFailed(error: VolleyError) {
                }

                override fun onSuccess(response: JSONObject) {
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex: Throwable): String? {

        val sb = StringBuffer()
        for ((key, value) in infos) {
            sb.append(key + "=" + value + "\n")
        }

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)
        try {
            val timestamp = System.currentTimeMillis()
            val time = formatter.format(Date())
            val fileName = "crash-$time-$timestamp.log"
            if (true) {
                val path = Constants.SD_ROOT_PATH + "/crashLog"
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val fos = FileOutputStream(path + "/" + fileName)
                fos.write(sb.toString().toByteArray())
                fos.close()
            }
            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing file..." + e.message)
        }

        return null
    }
    //单例
    private object Holder { val INSTANCE = CrashHandler() }

    companion object {

        //CrashHandler实例
        /** 获取CrashHandler实例 ,单例模式  */
        val instance :CrashHandler by lazy {
            Holder.INSTANCE
        }
    }

}

package com.gkzxhn.prison.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.android.volley.VolleyError
import com.gkzxhn.prison.R

import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.model.iml.CrashModel
import com.gkzxhn.wisdom.async.VolleyUtils
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.JSONUtil

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

    private val flag = false
    private lateinit var mCrashModel:CrashModel
    //单例
    private object Holder { val INSTANCE = CrashHandler() }

    companion object {

        //CrashHandler实例
        /** 获取CrashHandler实例 ,单例模式  */
        val instance :CrashHandler by lazy {
            Holder.INSTANCE
        }
    }
    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        mCrashModel= CrashModel()
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
            deal(b.toString())
        }
        if (Constants.IS_DEBUG_MODEL) {//打印日志
            ex.printStackTrace()
        }
        return true
    }
    private fun deal(message:String){
        //上传奔溃日志
        val pm = mContext.packageManager
        var packageInfo: PackageInfo? = null
        packageInfo = pm.getPackageInfo(mContext.packageName,
                PackageManager.GET_CONFIGURATIONS)
        //上传奔溃日志
        mCrashModel.uploadLog(message,packageInfo.versionCode,null)
        // 关闭USB录屏
        if(mCrashModel.sharedPreferences.getBoolean(Constants.IS_OPEN_USB_RECORD,true)) {
            //查询USB是否正在录屏
            mCrashModel.queryUSBRecord(object :VolleyUtils.OnFinishedListener<JSONObject>{
                override fun onSuccess(response: JSONObject) {
                    val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                    if (code == 0) {
                        if(JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response,"v"),"")=="start"){
                            //停止录屏
                            mCrashModel.stopUSBRecord(null)
                        }
                    }
                }
                override fun onFailed(error: VolleyError) {
                }
            })
        }
    }
}

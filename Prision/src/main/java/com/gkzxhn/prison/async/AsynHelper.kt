package com.gkzxhn.prison.async

import android.os.AsyncTask
import android.widget.Toast

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.entity.MeetingEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.util.Log


/**
 * Created by Raleigh on 15/8/20.
 */
class AsynHelper(private val TAB: Int) : AsyncTask<Any, Int, Any>() {
    /*  * Params 启动任务执行的输入参数，比如HTTP请求的URL。
	 * Progress 后台任务执行的百分比。
	 * Result 后台执行任务最终返回的结果，比如String,Integer等。
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
								asynHelper.executeOnExecutor(Executors.newCachedThreadPool(),args..);
							}else{
								asynHelper.execute(args...);
							}
	 * */
    private var taskFinishedListener: TaskFinishedListener? = null

    fun setOnTaskFinishedListener(
            taskFinishedListener: TaskFinishedListener) {
        this.taskFinishedListener = taskFinishedListener
    }

    /** On load task finished listener  */
    interface TaskFinishedListener {
        fun back(`object`: Any?)
    }

    override fun doInBackground(vararg params: Any): Any? {
        var result: Any? = null
        try {
            when(TAB){
                Constants.MAIN_TAB ->{
                    val response = params[0] as String
                    val lastData = ArrayList<MeetingEntity>()
                    val datas = Gson().fromJson<List<MeetingEntity>>(response, object : TypeToken<List<MeetingEntity>>() {

                    }.type)
                    for (entity in datas) {
                        if (entity.status != "FINISHED") {
                            if (entity.status == "CANCELED") {
                                val reson = GKApplication.instance.resources.getStringArray(R.array.cancel_video_reason)
                                if (entity.remarks == reson[reson.size - 1] || entity.remarks == reson[reson.size - 2]) {
                                    lastData.add(entity)
                                }
                            } else {
                                lastData.add(entity)
                            }
                        }
                    }
                    result = lastData
                }
                Constants.CLOSE_GUI_TAB ->{
                    try {
                        val mActivityManager = GKApplication.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                        val appProcessList = mActivityManager
                                .getRunningAppProcesses()
                        for (appProcess in appProcessList) {
                            if (appProcess.processName.equals(Constants.C9_PACKAGE_NAME)) {
                                //关闭GUI
                                val p = Runtime.getRuntime().exec("adb shell am force-stop  cn.com.rocware.c9gui")
                                //status=0 关闭成功
                                val status = p.waitFor()
                                break
                            }
                        }
                    }catch (e: Exception){}
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
        }
        return result
    }


    override fun onPostExecute(result: Any?) {
        try {
            taskFinishedListener?.back(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}

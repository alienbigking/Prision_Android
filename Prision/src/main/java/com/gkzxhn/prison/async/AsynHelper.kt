package com.gkzxhn.prison.async

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.entity.MeetingEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.util.ArrayList
import java.io.DataOutputStream


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
                    result=operateGUI(false)
                }
                Constants.OPEN_GUI_TAB ->{
                    //关闭GUI
                    result=operateGUI(true)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
        }
        return result
    }

    /**
     * 停用应用
     * @param context 上下文信息
     * @param packageName 应用的包名
     * @return
     */
    fun operateGUI(isEnable:Boolean): Int {
        var result=-1
        val packageName="cn.com.rocware.c9gui"
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            val cmd = if(isEnable) "pm enable " + packageName
                       else "pm disable " + packageName
            process = Runtime.getRuntime().exec("su") //切换到root帐号
            os = DataOutputStream(process.outputStream)
            os.writeBytes(cmd + "\n")
            os.writeBytes("exit\n")
            os.flush()
            result=process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
//            Toast.makeText(GKApplication.instance, "冻结应用失败", Toast.LENGTH_LONG).show()
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: Exception) {
            }

        }
//        Toast.makeText(GKApplication.instance, "冻结应用成功", Toast.LENGTH_LONG).show()
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

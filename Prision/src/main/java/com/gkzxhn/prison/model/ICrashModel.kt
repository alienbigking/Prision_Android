package com.gkzxhn.prison.model

import com.gkzxhn.prison.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/4/6.
 */
interface ICrashModel :ICallZijingModel{
    /**
     * 上传日志
     */
    fun uploadLog(message: String,versionCode:Int,onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)
}
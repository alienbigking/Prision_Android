package com.gkzxhn.prison.model.iml

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICrashModel
import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/4/6.
 */
class CrashModel:CallZijingModel(),ICrashModel {
    /**
     * 上传奔溃日志
     */
    override fun uploadLog(message: String, versionCode:Int,onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            val params = JSONObject()
            params.put("phone", sharedPreferences.getString(Constants.USER_ACCOUNT, ""))
            params.put("contents", message)
            params.put("device_name", android.os.Build.MODEL)
            params.put("sys_version", "Android")
            params.put("device_type", Build.VERSION.SDK_INT.toString())
            params.put("app_version", versionCode)
            volleyUtils.post(Constants.REQUEST_CRASH_LOG_URL, JSONObject().put("logger", params),null,onFinishedListener)
        }catch (e:Exception){}
    }
}
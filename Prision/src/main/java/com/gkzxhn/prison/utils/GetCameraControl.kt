package com.gkzxhn.prison.utils

import android.util.Log

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.gkzxhn.wisdom.async.SingleRequestQueue

import org.json.JSONException
import org.json.JSONObject

/**
 * 方向控制
 * Created by lv on 2017/2/7.
 */

class GetCameraControl {
    private val TAG = GetCameraControl::class.java.name
    /**
     * 遥控器设置模式
     * @param v direct控制摄像机,  indirect控制UI
     */
    fun cameraControl(v: String) {
        var params: JSONObject? = null
        try {
            params = JSONObject()
            params.put("k","remote-control-role")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val request = JsonObjectRequest(Request.Method.POST, XtHttpUtil.CAMERA_CONTROL,
                params, Response.Listener { jsonObject -> Log.d(TAG, jsonObject.toString()) }, Response.ErrorListener { })
        SingleRequestQueue.instance.add(request, "")
    }
}

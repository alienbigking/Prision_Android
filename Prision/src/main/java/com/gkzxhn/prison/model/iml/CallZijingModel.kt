package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class CallZijingModel : BaseModel(), ICallZijingModel {

    /**
     * 减少免费会见次数
     */
    override fun updateFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val url = String.format("%s/%s/access", Constants.REQUEST_FREE_MEETING_TIME,
                    sharedPreferences.getString(Constants.USER_ACCOUNT, ""))
//            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun queryUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        volleyUtils.get(JSONObject::class.java,XtHttpUtil.GET_RECORD_NEAR_STATUS, REQUEST_TAG, onFinishedListener)

    }

    override fun startUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>) {
        volleyUtils.get(String::class.java,XtHttpUtil.START_NEAR_RECORD ,REQUEST_TAG,onFinishedListener)
    }

    override fun stopUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>) {
        try {
            volleyUtils.get(String::class.java,XtHttpUtil.STOP_NEAR_RECORD, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun cameraControl(v: String,onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            // 遥控器控制器
            val params = JSONObject()
            params.put("k","remote-control-role")
            volleyUtils.post(XtHttpUtil.CAMERA_CONTROL, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun hangUp(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val params = JSONObject()
            volleyUtils.post(XtHttpUtil.HANGUP, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override  fun setIsQuite(quiet: Boolean, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val params = JSONObject()
            params.put("k", "enable-line-out")
            params.put("v", !quiet)
            volleyUtils.post(XtHttpUtil.SET_AUDIOUT, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun sendPassWord(password: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val params = JSONObject("{\"key\":\"$password\"}")
            volleyUtils.post(XtHttpUtil.SENDDTMF, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun switchMuteStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            volleyUtils.post(XtHttpUtil.MUTE_AUDIIN, JSONObject(), REQUEST_TAG, onFinishedListener)
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}

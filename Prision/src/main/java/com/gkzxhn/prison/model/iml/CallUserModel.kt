package com.gkzxhn.prison.model.iml

import android.telecom.Call
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICallUserModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.prison.async.VolleyUtils
import com.gkzxhn.prison.utils.Utils
import com.starlight.mobile.android.lib.util.ConvertUtil
import org.json.JSONException

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

class CallUserModel : CallZijingModel(), ICallUserModel {


    /**
     * 获取免费呼叫次数
     * @param onFinishedListener
     */
    override fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val url = String.format("%s?terminalNumber=%s", Constants.REQUEST_FREE_MEETING_TIME,
                    sharedPreferences.getString(Constants.USER_ACCOUNT, ""))
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    /**
     * 获取家属信息
     */
    override fun request(familyId: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        val url = String.format("%s?id=%s", Constants.REQUEST_MEETING_DETAIL_URL, familyId)
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }

    /**
     * 查询会见家属
     */
    override fun requestByMettingId(meetingId: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        val url = String.format("%s?meetingId=%s", Constants.REQUEST_MEETING_MEMBERS_URL, meetingId)
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }
    override fun requestFamily(key: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        var searchKey=key
        if(!Utils.isPhoneNumber(key))searchKey=ConvertUtil.urlEncode(key)//中文转码
        val url = String.format("%s?key=%s&page=1&rows=1000&jailId=%s", Constants.REQUEST_FAMILY_BY_KEY, searchKey,
                sharedPreferences.getString(Constants.TERMINAL_JIAL_ID,""))
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }
}

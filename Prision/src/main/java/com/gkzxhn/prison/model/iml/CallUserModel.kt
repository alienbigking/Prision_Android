package com.gkzxhn.prison.model.iml

import android.telecom.Call
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICallUserModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils
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
    override fun requestFamily(key: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        val url = String.format("%s?key=%s&page=1&rows=1000", Constants.REQUEST_FAMILY_BY_KEY, key)
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }
}

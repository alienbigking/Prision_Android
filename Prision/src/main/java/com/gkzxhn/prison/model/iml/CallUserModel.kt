package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICallUserModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

class CallUserModel : BaseModel(), ICallUserModel {
    /**
     * 获取免费呼叫次数
     * @param onFinishedListener
     */
    override fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            val url = String.format("%s/%s/access_times", Constants.REQUEST_FREE_MEETING_TIME,
                    sharedPreferences.getString(Constants.USER_ACCOUNT, ""))
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun request(id: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        val url = String.format("%s/%s", Constants.REQUEST_MEETING_DETAIL_URL, id)
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun dial(account: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>) {
        try {
            val strings = account.split("##".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val protocol = sharedPreferences.getString(Constants.PROTOCOL, "h323")
            val rate = sharedPreferences.getInt(Constants.TERMINAL_RATE, 512)
            val params = JSONObject()
            params.put("url", String.format("%s:%s**%s", protocol, if (strings.size > 0) strings[0] else "", if (strings.size > 1) strings[1] else ""))
            params.put("rate", rate)
            volleyUtils.post(XtHttpUtil.DIAL, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

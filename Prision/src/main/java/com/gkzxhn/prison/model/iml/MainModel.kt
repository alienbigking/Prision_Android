package com.gkzxhn.prison.model.iml


import com.android.volley.AuthFailureError
import com.gkzxhn.prison.model.IMainModel
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

class MainModel : BaseModel(), IMainModel {


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

    override fun requestZijing(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            volleyUtils[JSONObject::class.java, XtHttpUtil.GET_DIAL_HISTORY, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            volleyUtils[JSONObject::class.java, Constants.REQUEST_VERSION_URL, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun requestCancel(id: String, reason: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>?) {
        val url = String.format("%s/%s", Constants.REQUEST_CANCEL_MEETING_URL, id)
        try {
            val params = HashMap<String, String>()
            params.put("remarks", reason)
            volleyUtils.patch(url, params, REQUEST_TAG, onFinishedListener)
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun request(date: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        val account = sharedPreferences.getString(Constants.USER_ACCOUNT, "")
        val url = String.format("%s/%s/meetings?application_date=%s", Constants.REQUEST_MEETING_LIST_URL, account, date)
        try {
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    override fun callFang(account: String, requestCode: Int, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        var account = account
        var strings: Array<String>? = null
        var password = ""
        if (account.contains("##")) {
            strings = account.split("##".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            account = strings[0]
            if (strings.size > 0) {
                password = strings[1]
            }
        }
        val protocol = sharedPreferences.getString(Constants.PROTOCOL, "h323")
        val rate = sharedPreferences.getInt(Constants.TERMINAL_RATE, 512)
        var params: JSONObject=JSONObject()
        try {
            params.put("url", "$protocol:$account**$password")
            params.put("rate",rate)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            volleyUtils.post(XtHttpUtil.DIAL, params!!, REQUEST_TAG, onFinishedListener)
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

}

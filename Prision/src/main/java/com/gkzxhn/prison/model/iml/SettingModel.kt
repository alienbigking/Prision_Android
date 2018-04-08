package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ISettingModel
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class SettingModel : BaseModel(), ISettingModel {
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

    /**
     * 请求版本信息
     */
    override fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            volleyUtils[JSONObject::class.java, Constants.REQUEST_VERSION_URL, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}

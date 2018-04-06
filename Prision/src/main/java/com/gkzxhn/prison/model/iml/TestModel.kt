package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/4/3.
 */
class TestModel :BaseModel() {
    /**
     * 获取免费呼叫次数
     * @param onFinishedListener
     */
    fun request(url:String,onFinishedListener: VolleyUtils.OnFinishedListener<String>) {
        try {
            volleyUtils[String::class.java, url , REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}
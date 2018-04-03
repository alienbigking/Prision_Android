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
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        try {
            volleyUtils[JSONObject::class.java, XtHttpUtil.ADD_ACCOUNT, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}
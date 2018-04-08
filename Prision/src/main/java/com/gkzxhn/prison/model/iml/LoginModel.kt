package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ILoginModel
import com.gkzxhn.wisdom.async.VolleyUtils

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

class LoginModel : CallZijingModel(), ILoginModel {
    /**
     * 获取终端信息
     */
    override fun getMeetingRoom(account: String, password: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>) {
        try {
            val url = Constants.REQUEST_MEETING_ROOM + "/" + account + "/detail"
            volleyUtils[String::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}

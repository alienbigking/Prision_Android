package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class CallZijingModel : BaseModel(), ICallZijingModel {
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

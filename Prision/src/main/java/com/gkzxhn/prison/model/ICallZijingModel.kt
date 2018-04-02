package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

interface ICallZijingModel : IBaseModel {
    fun hangUp(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun setIsQuite(quiet: Boolean, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun sendPassWord(password: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun switchMuteStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

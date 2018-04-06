package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

interface IMainModel : ICallZijingModel {
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun requestCancel(id: String, reason: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>?)
    fun request(date: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

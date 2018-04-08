package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

interface ICallUserModel : ICallZijingModel {
    /**
     *  请求免费会见次数
     */
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)

    /**
     *  请求数据
     */
    fun request(id: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

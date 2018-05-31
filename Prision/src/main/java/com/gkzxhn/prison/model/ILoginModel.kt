package com.gkzxhn.prison.model

import com.gkzxhn.prison.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ILoginModel : IBaseModel {
    /**
     * 请求网络信息
     */
    fun getNetworkStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 获取终端会议室号
     */
    fun getMeetingRoom(account: String, password: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

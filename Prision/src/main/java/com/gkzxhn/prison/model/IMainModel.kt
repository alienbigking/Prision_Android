package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

interface IMainModel : ICallZijingModel {
    /**
     * 获取免费会见次数
     */
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)

    /**
     * 请求版本信息
     */
    fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)

    /**
     *  取消会见
     */
    fun requestCancel(id: String, reason: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>?)

    /**
     *  获取指定日期会见列表
     */
    fun request(date: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

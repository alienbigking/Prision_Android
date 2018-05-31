package com.gkzxhn.prison.model

import com.gkzxhn.prison.async.VolleyUtils
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
     *  获取指定日期会见列表
     */
    fun request(date: String,currentPage:Int,pageNumber:Int, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

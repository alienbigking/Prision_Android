package com.gkzxhn.prison.model

import com.gkzxhn.prison.async.VolleyUtils
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
    fun request(familyId: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
   /**
     *  请求数据
     */
    fun requestByMettingId(meetingId: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    /**
     * 家属信息 通过手机号码和家属姓名
     */
    fun requestFamily(key: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

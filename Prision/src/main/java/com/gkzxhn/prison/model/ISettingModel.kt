package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

interface ISettingModel : IBaseModel {
    /**
     *  获取免费会见次数
     */
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)

    /**
     * 获取版本信息
     */
    fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

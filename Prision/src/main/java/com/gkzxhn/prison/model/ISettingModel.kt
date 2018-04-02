package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

interface ISettingModel : IBaseModel {
    fun requestFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    fun requestVersion(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ILoginModel : IBaseModel {
    /**
     * 获取终端会议室号
     */
    fun getMeetingRoom(account: String, password: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

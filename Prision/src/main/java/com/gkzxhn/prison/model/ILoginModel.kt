package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ILoginModel : ICallZijingModel {
    /**
     * 获取终端会议室号
     */
    fun getMeetingRoom(account: String, password: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>)
}

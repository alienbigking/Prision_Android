package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

interface ILoginModel : IBaseModel {
    fun getMeetingRoom(account: String, password: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>)
}

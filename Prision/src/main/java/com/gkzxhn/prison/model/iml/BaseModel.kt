package com.gkzxhn.prison.model.iml

import android.app.Activity
import android.content.SharedPreferences


import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.model.IBaseModel
import com.gkzxhn.wisdom.async.SingleRequestQueue
import com.gkzxhn.wisdom.async.VolleyUtils

import java.util.UUID

/**
 * Created by Administrator on 2016/6/12.
 */
open class BaseModel : IBaseModel {
    val REQUEST_TAG = UUID.randomUUID().toString().replace("-", "")
    var volleyUtils: VolleyUtils
    final override val sharedPreferences: SharedPreferences
    init {
        volleyUtils = VolleyUtils()
        sharedPreferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
    }

    override fun stopAllRequest() {
        SingleRequestQueue.instance.cancelAll(REQUEST_TAG)
    }
}

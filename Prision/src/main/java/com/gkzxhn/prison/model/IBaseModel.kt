package com.gkzxhn.prison.model

import android.content.SharedPreferences

/**
 * Created by Raleigh.Luo on 2016/6/12.
 */
interface IBaseModel {
    val sharedPreferences: SharedPreferences
    fun stopAllRequest()
}

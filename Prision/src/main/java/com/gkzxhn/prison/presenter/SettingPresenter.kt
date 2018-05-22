package com.gkzxhn.prison.presenter

import android.content.Context

import com.android.volley.VolleyError
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.model.ISettingModel
import com.gkzxhn.prison.model.iml.SettingModel
import com.gkzxhn.prison.view.ISettingView
import com.gkzxhn.wisdom.async.VolleyUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

class SettingPresenter(context: Context, view: ISettingView) : BasePresenter<ISettingModel, ISettingView>(context, SettingModel(), view) {
//    /**
//     * 请求免费会见次数
//     */
//    fun requestFreeTime() {
//        mModel.requestFreeTime(object : VolleyUtils.OnFinishedListener<JSONObject> {
//            override fun onSuccess(response: JSONObject) {
//                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
//                if (code == HttpStatus.SC_OK) {
//                    val time = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(
//                            JSONUtil.getJSONObject(response,"data"), "access_times"))
//                    mView?.updateFreeTime(time)
//                    //保存到本地
//                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME, time).apply()
//                }
//            }
//            override fun onFailed(error: VolleyError) {
//
//            }
//        })
//    }

    /**
     * 请求版本信息
     */
    fun requestVersion() {
        mModel.requestVersion(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val versionsJson=JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response,"data"),"versions")
                    val versions= Gson().fromJson<List<VersionEntity>>(versionsJson,
                            object : TypeToken<List<VersionEntity>>() {
                            }.type)
                    var v:VersionEntity?=null
                    for(version in versions){
                        if(version.id==2){
                            v=version
                            break
                        }
                    }
                    mView?.updateVersion(v)
                }
            }

            override fun onFailed(error: VolleyError) {
                mView?.updateVersion(null);
            }
        })
    }

    fun checkNetworkStatus(){
        mModel.getNetworkStatus(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = response.getInt("code")
                var isConnected=false
                if (code == 0) {
                    try {
                        val v = JSONUtil.getJSONObject(response, "v")
                        if (v.getBoolean("connected")) {
                            isConnected = true
                        }
                    }catch (e:Exception){}
                }
                mView?.networkStatus(isConnected)
            }

            override fun onFailed(error: VolleyError) {
                mView?.networkStatus(false)
            }

        })
    }

}

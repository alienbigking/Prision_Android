package com.gkzxhn.prison.presenter

import android.content.Context
import android.content.SharedPreferences

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.entity.FreeFamilyEntity
import com.gkzxhn.prison.entity.MeetingDetailEntity
import com.gkzxhn.prison.model.ICallUserModel
import com.gkzxhn.prison.model.iml.CallUserModel
import com.gkzxhn.prison.view.ICallFreeView
import com.gkzxhn.wisdom.async.VolleyUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil

import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/29.
 */

class CallFreePresenter(context: Context, view: ICallFreeView) : BasePresenter<ICallUserModel, ICallFreeView>(context, CallUserModel(), view) {
    var entity: MeetingDetailEntity? = null//搜索到的家属信息
    /**
     *  清空家属实体
     */
    fun clearEntity() {
        entity = null
    }

    /**
     * 获取免费会见次数
     */
    fun requestFreeTime() {
        //关闭GUI
        startAsynTask(Constants.CLOSE_GUI_TAB,null)
        mModel.requestFreeTime(object : VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val time = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(
                            JSONUtil.getJSONObject(response,"data"), "access_times"))
                    mView?.updateFreeTime(time)
                    //保存到sharepreferences
                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME,time).apply()
                }
            }
            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     *  通过手机号码查询家属信息
     */
    fun request(key: String) {
        mView?.startRefreshAnim()
        mModel.requestFamily(key, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                mView?.stopRefreshAnim()
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val familyJson=JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response,"data"), "infos")
                    val familys= Gson().fromJson<List<FreeFamilyEntity>>(familyJson,
                            object : TypeToken<List<FreeFamilyEntity>>() {}.type)
                    mView?.onSuccess(familys)
                } else {
                    mView?.showToast(R.string.query_phone_is_error)
                }
            }

            override fun onFailed(error: VolleyError) {
                showErrors(error)
            }
        })
    }

}

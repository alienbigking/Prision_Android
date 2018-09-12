package com.gkzxhn.prison.presenter

import android.content.Context
import android.util.Log
import android.widget.Toast

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.entity.MeetingDetailEntity
import com.gkzxhn.prison.model.ICallUserModel
import com.gkzxhn.prison.model.iml.CallUserModel
import com.gkzxhn.prison.view.ICallUserView
import com.gkzxhn.prison.async.VolleyUtils
import com.google.gson.Gson
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

class CallUserPresenter(context: Context, view: ICallUserView) : BasePresenter<ICallUserModel, ICallUserView>(context, CallUserModel(), view) {
    var entity: MeetingDetailEntity? = null
    private val TAG = CallUserPresenter::class.java.simpleName

    init {
        checkStatusCode()
    }

    /**
     * 请求用户信息
     */
    fun request(familyId: String) {
        //单元测试 延迟加载
        mView?.setIdleNow(true)
        mView?.startRefreshAnim()
        mModel.request(familyId, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                mView?.stopRefreshAnim()
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val data = JSONUtil.getJSONObject(response, "data")
                    entity = Gson().fromJson(JSONUtil.getJSONObjectStringValue(data, "family"), MeetingDetailEntity::class.java)
                    entity?.phone = familyId
                    val edit = getSharedPreferences().edit()
                    edit.putString(Constants.ACCID, entity?.accessToken)
                    edit.apply()
                    mView?.onSuccess()
                }
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }

            override fun onFailed(error: VolleyError) {
                showErrors(error)
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }
        })
    }

    /**
     *  检查呼叫信息
     */
    fun checkCallStatus(){
        mModel.getCallInfor(object :VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
                val code = response.getInt("code")
                if (code == 0 ) {//正在拨打电话
                    //挂断
                    mModel.hangUp(object :VolleyUtils.OnFinishedListener<JSONObject>{
                        override fun onSuccess(response: JSONObject) {
                        }

                        override fun onFailed(error: VolleyError) {
                        }
                    })
                }
            }

            override fun onFailed(error: VolleyError) {
            }
        })

    }
    /**
     * 判断当前云信id状态
     */
    fun checkStatusCode(): StatusCode {
        val code = NIMClient.getStatus()
        when(code) {
            StatusCode.KICKOUT -> {// 被其他端挤掉
                Toast.makeText(GKApplication.instance, R.string.kickout, Toast.LENGTH_SHORT).show()
                GKApplication.instance.loginOff()
            }
            StatusCode.CONNECTING,// 正在连接
            StatusCode.LOGINING,// 正在登录
            StatusCode.NET_BROKEN -> { // 网络连接已断开
//                mView?.showToast(R.string.network_error)
                //系统自动登录云信
                val username = getSharedPreferences().getString(Constants.USER_ACCOUNT, "")
                val password = getSharedPreferences().getString(Constants.USER_PASSWORD, "")
                if ((username != null) and (username!!.length > 0)) {
                    val info = LoginInfo(username, password) // config...
                    //登录云信
                    NIMClient.getService(AuthService::class.java).login(info)
                            .setCallback(null)
                }
            }
            StatusCode.UNLOGIN -> {// 未登录
                //系统自动登录云信
                val username = getSharedPreferences().getString(Constants.USER_ACCOUNT, "")
                val password = getSharedPreferences().getString(Constants.USER_PASSWORD, "")
                if ((username != null) and (username!!.length > 0)) {
                    val info = LoginInfo(username, password) // config...
                    //登录云信
                    NIMClient.getService(AuthService::class.java).login(info)
                            .setCallback(null)
                } else {//退出到登录界面
                    GKApplication.instance.loginOff()
                }
            }
        }
        return code
    }

    /**
     * 拨号 进入视频会议
     */
    fun dial() {
        //单元测试 延迟加载
        mView?.setIdleNow(true)
        val account=getMeettingAccount()?:""
        mModel.dial(account, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
//                val response=JSONUtil.getJSONObject(responseStr)
                Log.d(TAG, "DIAL" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0 ) {
                        mView?.dialSuccess(getSharedPreferences().getString(Constants.TERMINAL_HOST_PASSWORD,""))
                    } else {
                        mView?.dialFailed()
                        Log.i(TAG, "onResponse: 参数无效 code:  " + code)
                    }
                } catch (e: JSONException) {
                    mView?.dialFailed()

                }
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }
            override fun onFailed(error: VolleyError) {
                mView?.dialFailed()
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }
        })
    }
}

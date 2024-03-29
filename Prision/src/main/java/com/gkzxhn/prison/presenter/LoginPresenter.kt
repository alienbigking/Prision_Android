package com.gkzxhn.prison.presenter

import android.content.Context
import android.text.TextUtils

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.entity.LoginEntity
import com.gkzxhn.prison.model.ILoginModel
import com.gkzxhn.prison.model.iml.LoginModel
import com.gkzxhn.prison.utils.Utils
import com.gkzxhn.prison.view.ILoginView
import com.gkzxhn.prison.async.VolleyUtils
import com.gkzxhn.prison.entity.VersionEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

class LoginPresenter(context: Context, view: ILoginView) : BasePresenter<ILoginModel, ILoginView>(context, LoginModel(), view) {
    /**
     *  请求版本信息
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
                    for(version in versions){
                        if(version.id==2){
                            mView?.updateVersion(version)
                            break
                        }
                    }
                }
            }

            override fun onFailed(error: VolleyError) {
                }
        })
    }
    /**
     * 获取会见会议室号等
     * @param account
     * @param password
     */
    private fun getMeetingRoom(account: String, password: String) {
        mView?.setIdleNow(true)
        mModel.getMeetingRoom(account, password, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val loginEntity=Gson().fromJson(JSONUtil.getJSONObjectStringValue(response, "data"),LoginEntity::class.java)
                    val edit = getSharedPreferences().edit()
                    edit.putString(Constants.USER_ACCOUNT, account)
                    edit.putString(Constants.USER_PASSWORD, password)
                    //记住帐号密码
                    GKApplication.instance.getSharedPreferences(Constants.TEMP_TABLE,Context.MODE_PRIVATE).
                            edit().putString(Constants.USER_ACCOUNT, account).putString(Constants.USER_PASSWORD, password).apply()
                    edit.putString(Constants.TERMINAL_JIAL_ID, loginEntity.jailId)
                    edit.putString(Constants.TERMINAL_JIAL_NAME, loginEntity.title)
                    if(loginEntity.roomNumber!=null&&loginEntity.roomNumber?.length?:0>0){
                        edit.putString(Constants.TERMINAL_ROOM_NUMBER, loginEntity.roomNumber)
                        edit.putString(Constants.TERMINAL_HOST_PASSWORD, loginEntity.hostPassword)
                        edit.putString(Constants.TERMINAL_GUEST_PASSWORD, loginEntity.mettingPassword)
                    }else{
                        edit.putString(Constants.TERMINAL_ROOM_NUMBER, "")
                        edit.putString(Constants.TERMINAL_HOST_PASSWORD, "")
                        edit.putString(Constants.TERMINAL_GUEST_PASSWORD, "")
                    }
                    edit.apply()
                    //关闭加载条
                    mView?.stopRefreshAnim()
                    //单元测试 释放加载延迟
                    mView?.setIdleNow(false)
                    mView?.onSuccess()
                }else{
                    //关闭加载条
                    mView?.stopRefreshAnim()
                    //单元测试 释放加载延迟
                    mView?.setIdleNow(false)
                    mView?.showToast(R.string.account_pwd_error)
                }
            }

            override fun onFailed(error: VolleyError) {
                //单元测试 释放加载延迟
                mView?.setIdleNow(false)
                showErrors(error)
            }
        })
    }

    /**登录云信
     * @param account
     * @param password
     */
    fun login(account: String, password: String) {
        //单元测试 延迟加载
        mView?.setIdleNow(true)
        //刷新
        mView?.startRefreshAnim()
        val info = LoginInfo(account, password)
        NIMClient.getService(AuthService::class.java).login(info)
                .setCallback(object : RequestCallback<Any> {
                    override fun onSuccess(param: Any) {
                        getMeetingRoom(account, password)
                    }

                    override fun onFailed(code: Int) {
                        //单元测试 释放加载延迟
                        mView?.setIdleNow(false)
                        mView?.stopRefreshAnim()
                        when (code) {
                            302 -> mView?.showToast(R.string.account_pwd_error)
                            503 -> mView?.showToast(R.string.server_busy)
                            415 -> mView?.showToast(R.string.network_error)
                            408 -> mView?.showToast(R.string.time_out)
                            403 -> mView?.showToast(R.string.illegal_control)
                            422 -> mView?.showToast(R.string.account_disable)
                            500 -> mView?.showToast(R.string.service_not_available)
                            else -> mView?.showToast(R.string.login_failed)
                        }
                    }

                    override fun onException(exception: Throwable) {
                        //单元测试 释放加载延迟
                        mView?.setIdleNow(false)
                        mView?.stopRefreshAnim()
                        mView?.showToast(R.string.login_exception_retry)
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

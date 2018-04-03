package com.gkzxhn.prison.presenter

import android.content.Context
import android.text.TextUtils

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.entity.MeetingRoomInfo
import com.gkzxhn.prison.model.ILoginModel
import com.gkzxhn.prison.model.iml.LoginModel
import com.gkzxhn.prison.view.ILoginView
import com.gkzxhn.wisdom.async.VolleyUtils
import com.google.gson.GsonBuilder
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

class LoginPresenter(context: Context, view: ILoginView) : BasePresenter<ILoginModel, ILoginView>(context, LoginModel(), view) {
    /**
     * 获取会见会议室号等
     * @param account
     * @param password
     */
    private fun getMeetingRoom(account: String, password: String) {
        mModel.getMeetingRoom(account, password, object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
                val meetingRoomInfo = GsonBuilder().create().fromJson(response, MeetingRoomInfo::class.java)
                var content = meetingRoomInfo.data?.content
                val edit = getSharedPreferences().edit()
                edit.putString(Constants.USER_ACCOUNT, account)
                edit.putString(Constants.USER_PASSWORD, password)
                //记住帐号密码
                edit.putString(Constants.USER_ACCOUNT_CACHE, account)
                edit.putString(Constants.USER_PASSWORD_CACHE, password)
                content = "6851##7890##0987"//TODO
                if (!TextUtils.isEmpty(content)) {
                    edit.putString(Constants.TERMINAL_ACCOUNT, content)
                }
                edit.apply()
                //关闭加载条
                mView?.stopRefreshAnim()
                mView?.onSuccess()
            }

            override fun onFailed(error: VolleyError) {
                showErrors(error)
            }
        })
    }

    /**登录云信
     * @param account
     * @param password
     */
    fun login(account: String, password: String) {
        mView?.startRefreshAnim()
        val info = LoginInfo(account, password)
        NIMClient.getService(AuthService::class.java).login(info)
                .setCallback(object : RequestCallback<Any> {
                    override fun onSuccess(param: Any) {
                        getMeetingRoom(account, password)
                    }

                    override fun onFailed(code: Int) {
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
                        mView?.stopRefreshAnim()
                        mView?.showToast(R.string.login_exception_retry)
                    }
                })
    }
}

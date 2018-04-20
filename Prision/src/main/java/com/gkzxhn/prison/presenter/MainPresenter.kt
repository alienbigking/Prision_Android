package com.gkzxhn.prison.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.activity.VideoMettingActivity
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.entity.MeetingEntity
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.model.IMainModel
import com.gkzxhn.prison.model.iml.MainModel
import com.gkzxhn.prison.view.IMainView
import com.gkzxhn.wisdom.async.VolleyUtils
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
 * Created by Raleigh.Luo on 17/4/12.
 */

class MainPresenter(context: Context, view: IMainView) : BasePresenter<IMainModel, IMainView>(context, MainModel(), view) {
    private var requestZijingTime = 0

    private val TAG = MainPresenter::class.java.simpleName
    fun resetTime() {
        requestZijingTime = 0
    }
    fun turnOff(){
        mModel.turnOff(object :VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     * 发请求，检测设备视频会议是否已经准备好
     */
    fun requestZijing() {
        requestZijingTime++
        mModel.getCallHistory(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == 0) {
                    mView?.startZijingService()
                    checkCallStatus()
                } else {
                    mView?.zijingServiceFailed()
                }
            }

            override fun onFailed(error: VolleyError) {
                if (requestZijingTime < 5) {//最多请求5次
                    requestZijing()
                } else {
                    mView?.zijingServiceFailed()
                }
            }
        })
    }

    /**
     * 检查是否正在呼叫 是则挂断
     */
    fun checkCallStatus(){
        mModel.getCallInfor(object :VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
                val code = response.getInt("code")
                if (code == 0 ) {//正在拨打电话
                    //挂断
                    mModel.hangUp(null)
                }
            }

            override fun onFailed(error: VolleyError) {
            }

        })

    }

    /**
     * 请求免费会见次数
     */
    fun requestFreeTime() {
        mModel.requestFreeTime(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val time = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "access_times"))
                    //保存到本地
                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME, time).apply()
                }
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     * 请求会见列表
     */
    fun request(date: String) {
//        checkGUI()
        mView?.startRefreshAnim()
        mModel.request(date, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val view = if (mWeakView == null) null else mWeakView!!.get()
                view?.stopRefreshAnim()
                try {
                    val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                    if (code == HttpStatus.SC_OK) {
                        val resultJson = JSONUtil.getJSONObjectStringValue(response, "meetings")
                        startAsynTask(Constants.MAIN_TAB, object : AsynHelper.TaskFinishedListener {
                            override fun back(`object`: Any?) {
                                mView?.updateItems(`object` as List<MeetingEntity>)
                                mView?.stopRefreshAnim()
                            }
                        }, resultJson)

                    } else {
                        mView?.updateItems(null)
                    }
                } catch (e: Exception) {
                }

            }

            override fun onFailed(error: VolleyError) {
                showErrors(error)
            }
        })
    }

    /**
     *  取消会见
     */
    fun requestCancel(id: String, reason: String) {
        mView?.showProgress()
        mModel.requestCancel(id, reason, object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
                mView?.dismissProgress()
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response), "code"))
                if (code == HttpStatus.SC_OK) {
                    mView?.showToast(R.string.canceled_meeting)
                    mView?.onCanceled()
                } else {
                    mView?.showToast(R.string.operate_failed)
                }
            }

            override fun onFailed(error: VolleyError) {
                showErrors(error)
            }
        })

    }

    /**
     *  请求版本信息
     */
    fun requestVersion() {
        mModel.requestVersion(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    mView?.updateVersion(Gson().fromJson(response.toString(), VersionEntity::class.java))
                }
            }

            override fun onFailed(error: VolleyError) {}
        })
    }

    /**
     * 判断当前云信id状态
     */
    fun checkStatusCode(): StatusCode {
        val code = NIMClient.getStatus()
        when(code){
            StatusCode.KICKOUT-> {// 被其他端挤掉
                Toast.makeText(GKApplication.instance, R.string.kickout, Toast.LENGTH_SHORT).show()
                GKApplication.instance.loginOff()
            }
            StatusCode.CONNECTING ->{// 正在连接
                mView?.showToast(R.string.yunxin_offline)
            }
            StatusCode.LOGINING-> {// 正在登录
                mView?.showToast(R.string.yunxin_offline)
            }
//            StatusCode.NET_BROKEN -> { // 网络连接已断开
//                mView?.showToast(R.string.network_error)
//            }
            StatusCode.UNLOGIN-> {// 未登录
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

    override fun stopAnim() {
        super.stopAnim()
        mView?.dismissProgress()
    }


    /**
     * 拨号 进入视频会议
     */
    fun dial(account: String, requestCode: Int) {
        var account = account
        var strings: Array<String>? = null
        var password = ""
        if (account.contains("##")) {
            strings = account.split("##".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            account = strings[0]
            if (strings.size > 0) {
                password = strings[1]
            }
        }
        val finalStrings = strings
        mModel.dial(account, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.d(TAG, "DIAL" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        val intent = Intent(mView as Activity, VideoMettingActivity::class.java)
                        if (null != finalStrings && finalStrings.size > 1) {
                            intent.putExtra(Constants.ZIJING_PASSWORD, finalStrings[1])
                        }
                        (mView as Activity).startActivityForResult(intent, requestCode)
                    } else {
                        Log.i(TAG, "onResponse: 参数无效 code:  " + code)
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "onResponse: >>> " + e.message)
                    //                            e.printStackTrace();
                }

            }

            override fun onFailed(error: VolleyError) {
                Log.d(TAG, "ResetQuest..." + error.toString())
                mView?.showToast("ResetQuest...  " + error.toString())
            }
        })
    }

}

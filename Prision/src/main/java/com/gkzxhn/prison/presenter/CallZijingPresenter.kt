package com.gkzxhn.prison.presenter

import android.content.Context
import android.util.Log

import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.model.iml.CallZijingModel
import com.gkzxhn.prison.view.ICallZijingView
import com.gkzxhn.wisdom.async.VolleyUtils
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class CallZijingPresenter(context: Context, view: ICallZijingView) : BasePresenter<ICallZijingModel, ICallZijingView>(context, CallZijingModel(), view) {
    private val TAG = CallZijingPresenter::class.java.simpleName
    //// 遥控器控制器
    fun cameraControl(v:String){
        mModel.cameraControl(v,object :VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
            }

            override fun onFailed(error: VolleyError) {
            }

        })
    }
    fun hangUp() {
        //挂断
        mModel.hangUp(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.d(TAG, "HANGUP" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        //成功
                    } else {
                        Log.i(TAG, "onResponse: code :  " + code)
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "onResponse: >>> " + e.message)
                    //                            e.printStackTrace();
                }

            }

            override fun onFailed(error: VolleyError) {
                Log.d(TAG, "ResetQuest..." + error.toString())
            }
        })
    }

    fun setIsQuite(quiet: Boolean) {
        //设置静音状态  true表示设置成静音
        mModel.setIsQuite(quiet, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.d(TAG, "DIAL" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        //设置成功
                        mView?.setSpeakerUi(quiet)
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
            }
        })

    }

    fun sendPassWord(password: String) {
        //发送DTMF
        mModel.sendPassWord(password, object : VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {
                Log.d("CallZijingPresenter", "SENDDTMF" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        //成功
                    } else {
                        Log.i("CallZijingPresenter", "sendPassWord: code :  " + code)
                    }
                } catch (e: JSONException) {
                    Log.e("CallZijingPresenter", "sendPassWord: >>> " + e.message)
                    //                            e.printStackTrace();
                }

            }

            override fun onFailed(error: VolleyError) {
                Log.d("CallZijingPresenter", "ResetQuest..." + error.toString())
            }
        })
    }

    fun switchMuteStatus() {
        //修改哑音状态
        mModel.switchMuteStatus(object : VolleyUtils.OnFinishedListener<JSONObject>{
            override fun onSuccess(response: JSONObject) {

            }

            override fun onFailed(error: VolleyError) {

            }
        })
    }

    /**
     * 开始USB录屏
     */
    fun startUSBRecord(){
        mModel.startUSBRecord(object :VolleyUtils.OnFinishedListener<String>{
            override fun onSuccess(response: String) {
                mModel.queryUSBRecord(object :VolleyUtils.OnFinishedListener<JSONObject>{
                    override fun onSuccess(response: JSONObject) {
                        val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                        if (code == 0) {
                            if(JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response,"v"),"")!="start"){
                                mView?.showToast(R.string.check_has_not_usb)
                            }
                        }
                    }

                    override fun onFailed(error: VolleyError) {
                        mView?.showToast(R.string.check_has_not_usb)
                    }

                })
            }

            override fun onFailed(error: VolleyError) {
                mView?.showToast(R.string.check_has_not_usb)
            }

        })
    }

    /**
     * 停止USB录屏
     */
    fun stopUSBRecord(){
        mModel.stopUSBRecord(object :VolleyUtils.OnFinishedListener<String>{
            override fun onSuccess(response: String) {
            }

            override fun onFailed(error: VolleyError) {
            }

        })
    }
    fun updateFreeTime(){
        mModel.updateFreeTime(object :VolleyUtils.OnFinishedListener<JSONObject>{
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
}

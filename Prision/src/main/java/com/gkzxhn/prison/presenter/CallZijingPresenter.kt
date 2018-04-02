package com.gkzxhn.prison.presenter

import android.content.Context
import android.util.Log

import com.android.volley.VolleyError
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.model.iml.CallZijingModel
import com.gkzxhn.prison.view.ICallZijingView
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class CallZijingPresenter(context: Context, view: ICallZijingView) : BasePresenter<ICallZijingModel, ICallZijingView>(context, CallZijingModel(), view) {
    private val TAG = CallZijingPresenter::class.java.simpleName
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
}

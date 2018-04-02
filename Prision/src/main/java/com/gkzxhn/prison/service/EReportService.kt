package com.gkzxhn.prison.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.SingleRequestQueue
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONException
import org.json.JSONObject


/**
 * 负责接收异步事件并通过广播发送的服务
 * Created by lv on 2017/1/11.
 */

class EReportService : Service() {
    private val TAG = EReportService::class.java.name
    private var qid: String? = null
    private val volleyUtils = VolleyUtils()
    override fun onCreate() {
        super.onCreate()
        //取消所有请求
        SingleRequestQueue.instance.cancelAll(TAG)
        ResetQuest()
    }

    //重置事件队列id
    fun ResetQuest() {
        try {
            volleyUtils.get(JSONObject::class.java, XtHttpUtil.RESET, TAG, object : VolleyUtils.OnFinishedListener<JSONObject> {
                override fun onSuccess(response: JSONObject) {
                    Log.d(TAG, "Reset" + response.toString())
                    try {
                        val `object` = response.getJSONObject("v")
                        qid = `object`.getString("qid")
                        val next_seq = `object`.getInt("next_seq")//如果无效qid
                        GetClearQuest(next_seq)
                        GetQueryQuest(next_seq + 1)
                    } catch (e: JSONException) {
                        //                            e.printStackTrace();
                    }

                }

                override fun onFailed(error: VolleyError) {
                    ResetQuest()//如果访问拒绝，继续访问
                    Log.d(TAG, "ResetQuest..." + error.toString())
                }
            })
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }


    //清除队列
    private fun GetClearQuest(next_seq: Int) {
        val url = XtHttpUtil.CLEAR + qid + "&expect_seq=" + next_seq
        try {
            volleyUtils.get(JSONObject::class.java, url, TAG, object : VolleyUtils.OnFinishedListener<JSONObject> {
                override fun onSuccess(response: JSONObject) {

                    //Log.d(TAG, "Clear"+response.toString());

                    //                        init(response);
                }

                override fun onFailed(error: VolleyError) {
                    //                Log.d(TAG, error.toString() + "...");
                }
            })
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    //    private void init(JSONObject response) {
    //        try {
    //            JSONObject obj = response.getJSONObject("v");
    //            String e = response.getString("e");
    //            int next_seq = obj.getInt("next_seq");
    ////            GetQueryQuest(next_seq + 1);
    ////            if (e.equals("cleared")) {
    ////                GetQueryQuest(next_seq + 1);
    ////            }
    //            if (e.equals("event_lost")) {
    //                GetQueryQuest(next_seq);
    //            } else if (e.equals("cleared")) {
    //                GetQueryQuest(next_seq + 1);
    //            }
    //        } catch (JSONException e) {
    ////            e.printStackTrace();
    //        }
    //    }


    //查询事件
    fun GetQueryQuest(a: Int) {
        val url = XtHttpUtil.QUERY + qid + "&expect_seq=" + a
        try {
            volleyUtils.get(JSONObject::class.java, url, TAG, object : VolleyUtils.OnFinishedListener<JSONObject> {
                override fun onSuccess(response: JSONObject) {
                    initData(response, a)
                }

                override fun onFailed(error: VolleyError) {
                    GetQueryQuest(a)
                    Log.d(TAG, "GetQueryQuest: " + error.toString())
                }
            })
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }

    //{"service":"EventQueue","seq":0,"v":{"expect_seq":106,"next_seq":105},"e":"event_lost"}
    private fun initData(response: JSONObject, a: Int) {
        try {
            if (response.getString("e") == "event_lost") {
                GetQueryQuest(response.getJSONObject("v").getInt("next_seq"))
            } else {
                if (response.getString("e") == "qid_invalid") {
                    ResetQuest()
                } else {
                    sendLocalBroad(response, Constants.ZIJING_ACTION)
                }
                GetQueryQuest(a + 1)
            }
            Log.i(TAG, "initData: response >>> " + response.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    //发送本地广播
    private fun sendLocalBroad(response: JSONObject, action: String) {

        val intent = Intent()
        intent.action = action
        intent.putExtra(Constants.ZIJING_JSON, response.toString())
        sendBroadcast(intent)
        //        Log.d(TAG,response.toString());
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}



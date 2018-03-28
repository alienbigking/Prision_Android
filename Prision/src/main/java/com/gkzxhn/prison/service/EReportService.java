package com.gkzxhn.prison.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.utils.XtHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 负责接收异步事件并通过广播发送的服务
 * Created by lv on 2017/1/11.
 */

public class EReportService extends Service {

    private static final String TAG = "EReportService";

    private String qid;

    private VolleyUtils volleyUtils=new VolleyUtils();
    @Override
    public void onCreate() {
        super.onCreate();
        ResetQuest();
    }

    //重置事件队列id
    public void ResetQuest() {
        try {
            volleyUtils.get(JSONObject.class, XtHttpUtil.RESET, null, new VolleyUtils.OnFinishedListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d(TAG, "Reset" + response.toString());
                    try {
                        JSONObject object = response.getJSONObject("v");
                        qid = object.getString("qid");
                        int next_seq = object.getInt("next_seq");//如果无效qid
                        GetClearQuest(next_seq);
                        GetQueryQuest(next_seq + 1);
                    } catch (JSONException e) {
//                            e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(VolleyError error) {
                    ResetQuest();//如果访问拒绝，继续访问
                    Log.d(TAG, "ResetQuest..." + error.toString());
                }
            });
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }


    //清除队列
    private void GetClearQuest(final int next_seq) {
        String url=XtHttpUtil.CLEAR + qid + "&expect_seq=" + next_seq;
        try {
            volleyUtils.get(JSONObject.class, url, null, new VolleyUtils.OnFinishedListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {

                    //Log.d(TAG, "Clear"+response.toString());

//                        init(response);
                }

                @Override
                public void onFailed(VolleyError error) {
                    //                Log.d(TAG, error.toString() + "...");
                }
            });
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
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
    public void GetQueryQuest(final int a) {
        String url=XtHttpUtil.QUERY + qid + "&expect_seq=" + (a);
        try {
            volleyUtils.get(JSONObject.class, url, null, new VolleyUtils.OnFinishedListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    initData(response, a);
                }

                @Override
                public void onFailed(VolleyError error) {
                    GetQueryQuest(a);
                    Log.d(TAG, "GetQueryQuest: " + error.toString());
                }
            });
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    //{"service":"EventQueue","seq":0,"v":{"expect_seq":106,"next_seq":105},"e":"event_lost"}
    private void initData(JSONObject response, int a) {
        try {
            if (response.getString("e").equals("event_lost")) {
                GetQueryQuest(response.getJSONObject("v").getInt("next_seq"));
            } else {
                if (response.getString("e").equals("qid_invalid")) {
                    ResetQuest();
                } else {
                    sendLocalBroad(response, Constants.ZIJING_ACTION);
                }
                GetQueryQuest(a + 1);
            }
            Log.i(TAG, "initData: response >>> " + response.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    //发送本地广播
    private void sendLocalBroad(JSONObject response, String action) {

        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Constants.ZIJING_JSON, response.toString());
        sendBroadcast(intent);
//        Log.d(TAG,response.toString());
    }





    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

}

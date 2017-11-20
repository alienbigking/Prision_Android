package com.gkzxhn.prison.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.async.SingleRequestQueue;
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

    @Override
    public void onCreate() {
        super.onCreate();

        ResetQuest();
      
    }


    private String qid;

    //重置事件队列id
    public void ResetQuest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                XtHttpUtil.RESET, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ResetQuest();//如果访问拒绝，继续访问
                        Log.d(TAG, "ResetQuest..." + error.toString());
                    }
                }, 2000);

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.RETRY_TIME, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingleRequestQueue.getInstance().add(request, "");
    }


    //清除队列
    private void GetClearQuest(final int next_seq) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                XtHttpUtil.CLEAR + qid + "&expect_seq=" + next_seq, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d(TAG, "Clear"+response.toString());

//                        init(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, error.toString() + "...");
            }
        });
        SingleRequestQueue.getInstance().add(request, "");
//        Log.d(TAG, "url:" + XtHttpUtil.CLEAR + qid + "&expect_seq=" + next_seq);
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


    private List<JSONObject> jsonObject;


    //查询事件
    public void GetQueryQuest(final int a) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                XtHttpUtil.QUERY + qid + "&expect_seq=" + (a), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

//                        Log.d("Get", response.toString());
                        if (jsonObject != null) {
                            jsonObject = null;
                        }
                        initData(response, a);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                GetQueryQuest(a);
                Log.d(TAG, "GetQueryQuest: " + error.toString());
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.RETRY_TIME, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingleRequestQueue.getInstance().add(request, "");
    }

    //{"service":"EventQueue","seq":0,"v":{"expect_seq":106,"next_seq":105},"e":"event_lost"}
    private void initData(JSONObject response, int a) {
        try {
            if (response.getString("e").equals("event_lost")) {
                GetQueryQuest(response.getJSONObject("v").getInt("next_seq"));
            } else {
                jsonObject = new ArrayList<>();
                jsonObject.add(response);
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
        //销毁自启动
//        stopSelf();
        Intent intent = new Intent(this, EReportService.class);
        startService(intent);
    }

}

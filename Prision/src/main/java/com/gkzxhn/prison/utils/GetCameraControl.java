package com.gkzxhn.prison.utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.entity.CommonRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *方向控制
 * Created by lv on 2017/2/7.
 */

public class GetCameraControl {

    private static final String TAG = "GetCameraControl";

    public GetCameraControl() {
    }

    /**
     * 遥控器设置模式
     * @param v direct控制摄像机,  indirect控制UI
     */
    public void cameraControl(String v) {
        JSONObject object = null;
        String k = "remote-control-role";
        try {
            object = new JSONObject(new Gson().toJson(new CommonRequest(k, v)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, XtHttpUtil.CAMERA_CONTROL,
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG,jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        SingleRequestQueue.getInstance().add(request, "");
    }
}

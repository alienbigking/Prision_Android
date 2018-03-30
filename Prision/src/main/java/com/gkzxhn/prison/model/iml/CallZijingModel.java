package com.gkzxhn.prison.model.iml;

import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.entity.CommonRequest;
import com.gkzxhn.prison.model.ICallZijingModel;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public class CallZijingModel extends BaseModel implements ICallZijingModel{
    @Override
    public void hangUp(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            JSONObject params = new JSONObject();
            volleyUtils.post(XtHttpUtil.HANGUP,params,REQUEST_TAG,onFinishedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setIsQuite(boolean quiet, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            JSONObject params = new JSONObject();
            params.put("k","enable-line-out");
            params.put("v",!quiet);
            volleyUtils.post(XtHttpUtil.SET_AUDIOUT,params,REQUEST_TAG,onFinishedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPassWord(String password, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            JSONObject params = new JSONObject("{\"key\":\"" + password + "\"}");
            volleyUtils.post(XtHttpUtil.SENDDTMF,params,REQUEST_TAG,onFinishedListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchMuteStatus(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            volleyUtils.post(XtHttpUtil.MUTE_AUDIIN,new JSONObject(),REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}

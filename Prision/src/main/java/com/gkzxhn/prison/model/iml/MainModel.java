package com.gkzxhn.prison.model.iml;


import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.entity.ZijingCall;
import com.gkzxhn.prison.model.IMainModel;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainModel extends BaseModel implements IMainModel {
    @Override
    public void requestZijing(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            volleyUtils.get(JSONObject.class, XtHttpUtil.GET_DIAL_HISTORY,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void requestVersion(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            volleyUtils.get(JSONObject.class,Constants.REQUEST_VERSION_URL,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void requestCancel(String id, String reason,VolleyUtils.OnFinishedListener<String> onFinishedListener) {
        String url= String.format("%s/%s",Constants.REQUEST_CANCEL_MEETING_URL, id);
        try {
            Map<String,String> params=new HashMap<String,String>();
            params.put("remarks",reason);
            volleyUtils.patch(url,params,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void request(String date,VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        String account=preferences.getString(Constants.USER_ACCOUNT,"");
        String url= String.format("%s/%s/meetings?application_date=%s",Constants.REQUEST_MEETING_LIST_URL,account,date);
        try {
            volleyUtils.get(JSONObject.class,url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void callFang(String account, int requestCode, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        String[] strings = null;
        String password = "";
        if (account.contains("##")) {
            strings = account.split("##");
            account = strings[0];
            if (strings.length > 0) {
                password = strings[1];
            }
        }
        ZijingCall json = new ZijingCall();
        String protocol = getSharedPreferences().getString(Constants.PROTOCOL, "h323");
        int rate = getSharedPreferences().getInt(Constants.TERMINAL_RATE, 512);
        json.url = protocol + ":" + account + "**" + password;
        json.rate = rate;
        JSONObject params = null;
        try {
            params = new JSONObject(new Gson().toJson(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            volleyUtils.post(  XtHttpUtil.DIAL,params,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

}

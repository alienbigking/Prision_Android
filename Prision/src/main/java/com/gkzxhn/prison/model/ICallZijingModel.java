package com.gkzxhn.prison.model;

import com.gkzxhn.prison.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public interface ICallZijingModel extends IBaseModel {
    void hangUp(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void setIsQuite(boolean quiet,VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void sendPassWord(String password,VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void switchMuteStatus(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}

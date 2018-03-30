package com.gkzxhn.prison.model;

import com.gkzxhn.prison.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public interface ICallUserModel extends IBaseModel {
    void requestFreeTime(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void request(String id, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void dial(String account, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}

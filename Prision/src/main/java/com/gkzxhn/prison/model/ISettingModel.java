package com.gkzxhn.prison.model;

import com.gkzxhn.prison.async.VolleyUtils;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public interface ISettingModel extends IBaseModel{
    void requestFreeTime(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
    void requestVersion(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener);
}

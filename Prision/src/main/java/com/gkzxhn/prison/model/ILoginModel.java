package com.gkzxhn.prison.model;

import com.gkzxhn.prison.async.VolleyUtils;

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

public interface ILoginModel  extends IBaseModel {
    void getMeetingRoom(String account, String password, VolleyUtils.OnFinishedListener<String> onFinishedListener);
}

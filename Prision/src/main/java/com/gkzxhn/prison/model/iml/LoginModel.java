package com.gkzxhn.prison.model.iml;

import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.model.ILoginModel;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

public class LoginModel extends BaseModel implements ILoginModel {
    @Override
    public void getMeetingRoom(String account, String password, VolleyUtils.OnFinishedListener<String> onFinishedListener) {
        try {
            String url = Constants.REQUEST_MEETING_ROOM + "/" + account + "/detail";
            volleyUtils.get(String.class,url ,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}

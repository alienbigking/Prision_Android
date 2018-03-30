package com.gkzxhn.prison.model.iml;

import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.model.ISettingModel;
import com.gkzxhn.prison.utils.XtHttpUtil;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public class SettingModel extends BaseModel implements ISettingModel {
    /**
     * 获取免费呼叫次数
     * @param onFinishedListener
     */
    @Override
    public void requestFreeTime(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            String url=String.format("%s/%s/access_times", Constants.REQUEST_FREE_MEETING_TIME,
                    getSharedPreferences().getString(Constants.USER_ACCOUNT,""));
            volleyUtils.get(JSONObject.class, url,REQUEST_TAG,onFinishedListener);
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
}

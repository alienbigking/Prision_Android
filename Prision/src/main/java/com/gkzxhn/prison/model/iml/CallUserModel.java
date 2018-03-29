package com.gkzxhn.prison.model.iml;

import com.android.volley.AuthFailureError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.model.ICallUserModel;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

public class CallUserModel extends BaseModel implements ICallUserModel{
    /**
     * 获取免费呼叫次数
     * @param onFinishedListener
     */
    @Override
    public void requestFreeTime(VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        try {
            String url=Constants.REQUEST_FREE_MEETING_TIME+"/"+
                    getSharedPreferences().getString(Constants.USER_ACCOUNT,"");
            volleyUtils.get(JSONObject.class, url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
    @Override
    public void request(String id, VolleyUtils.OnFinishedListener<JSONObject> onFinishedListener) {
        String url=String.format("%s/%s",Constants.REQUEST_MEETING_DETAIL_URL,id);
        try {
            volleyUtils.get(JSONObject.class,url,REQUEST_TAG,onFinishedListener);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }
}

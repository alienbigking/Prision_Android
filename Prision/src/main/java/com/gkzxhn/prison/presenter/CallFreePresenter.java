package com.gkzxhn.prison.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.VolleyError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.entity.MeetingDetailEntity;
import com.gkzxhn.prison.model.ICallUserModel;
import com.gkzxhn.prison.model.iml.CallUserModel;
import com.gkzxhn.prison.view.ICallFreeView;
import com.gkzxhn.prison.view.ICallUserView;
import com.google.gson.Gson;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/29.
 */

public class CallFreePresenter extends  BasePresenter<ICallUserModel,ICallFreeView> {
    private MeetingDetailEntity entity;

    public MeetingDetailEntity getEntity() {
        return entity;
    }
    public CallFreePresenter(Context context,  ICallFreeView view) {
        super(context, new CallUserModel(), view);
    }
    public void requestFreeTime(){
        mModel.requestFreeTime(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                if(code== HttpStatus.SC_OK){
                    ICallFreeView view=mWeakView==null?null:mWeakView.get();
                    if(view!=null)view.updateFreeTime(ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"time")));
                }
            }

            @Override
            public void onFailed(VolleyError error) {

            }
        });

    }
    public void request(final String id){
        ICallFreeView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        mModel.request(id, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                ICallFreeView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.stopRefreshAnim();
                int code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"));
                if (code == HttpStatus.SC_OK) {
                    entity=new Gson().fromJson(JSONUtil.getJSONObjectStringValue(response, "family"), MeetingDetailEntity.class);
                    entity.setPhone(id);
                    SharedPreferences.Editor edit = getSharedPreferences().edit();
                    edit.putString(Constants.ACCID, entity.getAccid());
                    edit.apply();
                    view.onSuccess();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
    }
}

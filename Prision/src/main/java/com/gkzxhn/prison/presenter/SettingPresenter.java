package com.gkzxhn.prison.presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.entity.VersionEntity;
import com.gkzxhn.prison.model.IMainModel;
import com.gkzxhn.prison.model.ISettingModel;
import com.gkzxhn.prison.model.iml.MainModel;
import com.gkzxhn.prison.model.iml.SettingModel;
import com.gkzxhn.prison.view.IMainView;
import com.gkzxhn.prison.view.ISettingView;
import com.google.gson.Gson;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/28.
 */

public class SettingPresenter  extends BasePresenter<ISettingModel,ISettingView> {
    public SettingPresenter(Context context, ISettingView view) {
        super(context, new SettingModel(), view);
    }
    public void requestFreeTime(){
        mModel.requestFreeTime(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                if(code== HttpStatus.SC_OK){
                    ISettingView view=mWeakView==null?null:mWeakView.get();
                    int time=ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"access_times"));
                    if(view!=null)view.updateFreeTime(time);
                    //保存到本地
                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME,time).apply();
                }
            }

            @Override
            public void onFailed(VolleyError error) {

            }
        });
    }
    public void requestVersion(){
        mModel.requestVersion(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                if(code== HttpStatus.SC_OK){
                    ISettingView view=mWeakView==null?null:mWeakView.get();
                    if(view!=null)view.updateVersion(new Gson().fromJson(response.toString(), VersionEntity.class));
                }
            }

            @Override
            public void onFailed(VolleyError error) {}
        });
    }

}

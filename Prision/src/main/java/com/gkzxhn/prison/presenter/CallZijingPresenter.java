package com.gkzxhn.prison.presenter;

import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.model.ICallZijingModel;
import com.gkzxhn.prison.model.iml.CallZijingModel;
import com.gkzxhn.prison.view.ICallZijingView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

public class CallZijingPresenter extends  BasePresenter<ICallZijingModel,ICallZijingView> {
    private final String TAG = CallZijingPresenter.class.getSimpleName();
    public CallZijingPresenter(Context context,  ICallZijingView view) {
        super(context, new CallZijingModel(), view);
    }
    public void hangUp(){
        //挂断
        mModel.hangUp(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "HANGUP" + response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        //成功

                    } else {
                        Log.i(TAG, "onResponse: code :  " + code);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: >>> " + e.getMessage());
//                            e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.d(TAG, "ResetQuest..." + error.toString());
            }
        });
    }
    public void setIsQuite(final boolean quiet){
        //设置静音状态  true表示设置成静音
        mModel.setIsQuite(quiet, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "DIAL" + response.toString());
                try {
                    ICallZijingView view=mWeakView==null?null:mWeakView.get();
                    int code = response.getInt("code");
                    if (code == 0) {
                        //设置成功
                        view.setSpeakerUi(quiet);
                    } else {
                        Log.i(TAG, "onResponse: 参数无效 code:  " + code);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: >>> " + e.getMessage());
//                            e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.d(TAG, "ResetQuest..." + error.toString());
            }
        });

    }
    public void sendPassWord(String password){
        //发送DTMF
        mModel.sendPassWord(password, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("CallZijingPresenter", "SENDDTMF" + response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0) {
                        //成功
                    } else {
                        Log.i("CallZijingPresenter", "sendPassWord: code :  " + code);
                    }
                } catch (JSONException e) {
                    Log.e("CallZijingPresenter", "sendPassWord: >>> " + e.getMessage());
//                            e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.d("CallZijingPresenter", "ResetQuest..." + error.toString());
            }
        });
    }

    public void switchMuteStatus(){
        //修改哑音状态
        mModel.switchMuteStatus(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onFailed(VolleyError error) {

            }
        });
    }
}

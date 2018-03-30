package com.gkzxhn.prison.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.CallZiJingActivity;
import com.gkzxhn.prison.activity.MainActivity;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.entity.MeetingEntity;
import com.gkzxhn.prison.entity.VersionEntity;
import com.gkzxhn.prison.entity.ZijingCall;
import com.gkzxhn.prison.model.IMainModel;
import com.gkzxhn.prison.model.iml.MainModel;
import com.gkzxhn.prison.utils.AsynHelper;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.gkzxhn.prison.view.IMainView;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainPresenter extends BasePresenter<IMainModel,IMainView> {
    private AsynHelper asynHelper;
    public MainPresenter(Context context,IMainView view) {
        super(context, new MainModel(), view);
    }
    private int requestZijingTime=0;
    public void resetTime(){
        requestZijingTime=0;
    }
    public void requestZijing(){
        requestZijingTime++;
        mModel.requestZijing(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code =ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                IMainView view=mWeakView==null?null:mWeakView.get();
                if (code == 0) {
                    view.startZijingService();
                }else{
                    view.zijingServiceFailed();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                if(requestZijingTime<5) {//最多请求5次
                    requestZijing();
                }else{
                    IMainView view=mWeakView==null?null:mWeakView.get();
                    view.zijingServiceFailed();
                }
            }
        });
    }

    public void requestFreeTime(){
        mModel.requestFreeTime(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                if(code== HttpStatus.SC_OK){
                    int time=ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"access_times"));
                    //保存到本地
                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME,time).apply();
                }
            }

            @Override
            public void onFailed(VolleyError error) {

            }
        });
    }
    public void request(String date){
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        mModel.request(date, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                IMainView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.stopRefreshAnim();
                try{
                    int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                    if(code== HttpStatus.SC_OK){
                        String resultJson=JSONUtil.getJSONObjectStringValue(response,"meetings");
                        startAsynTask(AsynHelper.AsynHelperTag.DEFUALT_TAG, new AsynHelper.TaskFinishedListener() {
                            @Override
                            public void back(Object object) {
                                IMainView view=mWeakView==null?null:mWeakView.get();
                                if(view!=null){
                                    view.updateItems((List<MeetingEntity>) object);
                                    view.stopRefreshAnim();
                                }
                            }
                        },resultJson);

                    }else{
                        view.updateItems(null);
                    }
                }catch (Exception e){ }

            }
            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
    }
    public void requestCancel(String id,String reason){
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.showProgress();
        mModel.requestCancel(id, reason,new VolleyUtils.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String response) {
                IMainView view=mWeakView==null?null:mWeakView.get();
                if(view!=null)view.dismissProgress();
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response),"code"));
                if(view!=null){
                    if(code== HttpStatus.SC_OK) {
                        view.showToast(R.string.canceled_meeting);
                        view.onCanceled();
                    }else{
                        view.showToast(R.string.operate_failed);
                    }
                }


            }

            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });

    }
    public void requestVersion(){
        mModel.requestVersion(new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                int code= ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response,"code"));
                if(code== HttpStatus.SC_OK){
                    IMainView view=mWeakView==null?null:mWeakView.get();
                    if(view!=null)view.updateVersion(new Gson().fromJson(response.toString(), VersionEntity.class));
                }
            }

            @Override
            public void onFailed(VolleyError error) {}
        });
    }
    /**
     * 判断当前云信id状态
     */
    public StatusCode checkStatusCode() {
        IMainView view=mWeakView==null?null:mWeakView.get();
        StatusCode code = NIMClient.getStatus();
        if (code == StatusCode.KICKOUT) {// 被其他端挤掉
            Toast.makeText(GKApplication.getInstance(), R.string.kickout,Toast.LENGTH_SHORT).show();
            GKApplication.getInstance().loginOff();
            ((Activity)mWeakContext.get()).finish();
        } else if (code == StatusCode.CONNECTING) {// 正在连接
            if(view!=null)view.showToast(R.string.yunxin_offline);
        } else if (code == StatusCode.LOGINING) {// 正在登录
            if(view!=null)view.showToast(R.string.yunxin_offline);
        } else if (code == StatusCode.NET_BROKEN) { // 网络连接已断开
            if(view!=null)view.showToast(R.string.network_error);
        } else if (code == StatusCode.UNLOGIN) {// 未登录
            //系统自动登录云信
            String username=getSharedPreferences().getString(Constants.USER_ACCOUNT,"");
            String password= getSharedPreferences().getString(Constants.USER_PASSWORD,"");
            if(username!=null&username.length()>0) {
                LoginInfo info = new LoginInfo(username, password); // config...
                //登录云信
                NIMClient.getService(AuthService.class).login(info)
                        .setCallback(null);
            }else{//退出到登录界面
                GKApplication.getInstance().loginOff();
                ((Activity)mWeakContext.get()).finish();
            }
        }
        return code;
    }
    @Override
    protected void stopAnim() {
        super.stopAnim();
        IMainView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.dismissProgress();
    }

    /**
     * 启动异步任务
     *
     * @param tag
     * @param params
     */
    protected void startAsynTask(AsynHelper.AsynHelperTag tag, AsynHelper.TaskFinishedListener taskFinishedListener, Object... params) {
        try {
            if (asynHelper != null) {
                if (asynHelper.getStatus() == AsyncTask.Status.RUNNING) asynHelper.cancel(true);
                asynHelper = null;
            }
            asynHelper = new AsynHelper(tag);
            asynHelper.setOnTaskFinishedListener(taskFinishedListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                asynHelper.executeOnExecutor(Executors.newCachedThreadPool(), params);
            } else {
                asynHelper.execute(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String TAG = MainPresenter.class.getSimpleName();
    public void callFang(String account, final int requestCode){
        final IMainView view=mWeakView==null?null:mWeakView.get();
        String[] strings = null;
        String password = "";
        if (account.contains("##")) {
            strings = account.split("##");
            account = strings[0];
            if (strings.length > 0) {
                password = strings[1];
            }
        }
        final String[] finalStrings = strings;
        mModel.callFang(account, requestCode, new VolleyUtils.OnFinishedListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "DIAL" + response.toString());
                try {
                    int code = response.getInt("code");
                    if (code == 0){

                        Intent intent = new Intent((MainActivity) view, CallZiJingActivity.class);
                        if (null!=finalStrings && finalStrings.length > 1) {
                            intent.putExtra(Constants.ZIJING_PASSWORD, finalStrings[1]);
                        }
                        if (view != null) {
                            ((MainActivity)view).startActivityForResult(intent, requestCode);
                        }
                    }else {
                        Log.i(TAG, "onResponse: 参数无效 code:  " + code);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "onResponse: >>> "+ e.getMessage() );
//                            e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.d(TAG, "ResetQuest..." + error.toString());
                view.showToast("ResetQuest...  " + error.toString());
            }
        });
    }
}

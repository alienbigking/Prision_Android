package com.gkzxhn.prison.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.CallZiJingActivity;
import com.gkzxhn.prison.activity.LoginActivity;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.entity.MeetingRoomInfo;
import com.gkzxhn.prison.entity.ZijingCall;
import com.gkzxhn.prison.model.IBaseModel;
import com.gkzxhn.prison.model.iml.BaseModel;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.gkzxhn.prison.view.ILoginView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/10.
 */

public class LoginPresenter extends BasePresenter<IBaseModel,ILoginView>{
    public LoginPresenter(Context context, ILoginView view) {
        super(context, new BaseModel(), view);
    }

    /**登录云信
     * @param account
     * @param password
     */
    public void login(final String account, final String password){
        ILoginView view=mWeakView==null?null:mWeakView.get();
        if(view!=null)view.startRefreshAnim();
        LoginInfo info = new LoginInfo(account, password);
        //登录云信
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(new RequestCallback() {
                    @Override
                    public void onSuccess(Object param) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            //登录科达GK
//                            KDInitUtil.login();
                            //保存登录信息
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString(Constants.USER_ACCOUNT_CACHE,account);
                            editor.putString(Constants.USER_PASSWORD_CACHE,password);
//                            editor.putString(Constants.TERMINAL_ACCOUNT,account);
                            editor.apply();
                            //主要为了记住账号和密码
                            //关闭加载条
                            getMeetingRoom(account, password);

                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            view.stopRefreshAnim();
                            switch (code) {
                                case 302:
                                    view.showToast(R.string.account_pwd_error);
                                    break;
                                case 503:
                                    view.showToast(R.string.server_busy);
                                    break;
                                case 415:
                                    view.showToast(R.string.network_error);
                                    break;
                                case 408:
                                    view.showToast(R.string.time_out);
                                    break;
                                case 403:
                                    view.showToast(R.string.illegal_control);
                                    break;
                                case 422:
                                    view.showToast(R.string.account_disable);
                                    break;
                                case 500:
                                    view.showToast(R.string.service_not_available);
                                    break;
                                default:
                                    view.showToast(R.string.login_failed);
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onException(Throwable exception) {
                        ILoginView view=mWeakView==null?null:mWeakView.get();
                        if(view!=null) {
                            view.stopRefreshAnim();
                            view.showToast(R.string.login_exception_retry);
                        }
                    }
                });
    }


    private final String TAG = LoginPresenter.class.getSimpleName();
    public void callFang(){
        final ILoginView view=mWeakView==null?null:mWeakView.get();
        ZijingCall json = new ZijingCall();
        String zijingAcount = "fangyuxing@zijingcloud.com";
        json.url = "h323:" + zijingAcount;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                XtHttpUtil.DIAL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DIAL" + response.toString());
                        try {
                            int code = response.getInt("code");
                            if (code == 0){
                                if (view != null) {
                                    ((LoginActivity) view).startActivity(new Intent(((LoginActivity) view), CallZiJingActivity.class));
                                    ((LoginActivity) view).finish();
                                }
                            }else {
                                Log.i(TAG, "onResponse: 参数无效 code:  " + code);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: >>> "+ e.getMessage() );
//                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "ResetQuest..." + error.toString());
                        view.showToast("ResetQuest...  " + error.toString());
                    }
                }, 2000);

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(Constants.RETRY_TIME, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SingleRequestQueue.getInstance().add(request, "");
    }

    private void getMeetingRoom(final String account, final String password) {
        final ILoginView view=mWeakView==null?null:mWeakView.get();
        String url = Constants.REQUEST_MEETING_ROOM + "/" + account + "/get_number";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MeetingRoomInfo meetingRoomInfo = new GsonBuilder().create().fromJson(response, MeetingRoomInfo.class);
                String content = meetingRoomInfo.data.content;
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(Constants.USER_ACCOUNT, account);
                edit.putString(Constants.USER_PASSWORD, password);
                if (!TextUtils.isEmpty(content)) {
                    edit.putString(Constants.TERMINAL_ACCOUNT, content);
                }
                edit.apply();
                if (view != null) {
                    view.stopRefreshAnim();
                    view.onSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrors(error);
            }
        });
        SingleRequestQueue.getInstance().add(request,"");
    }
}

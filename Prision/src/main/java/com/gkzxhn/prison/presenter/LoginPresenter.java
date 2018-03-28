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
import com.gkzxhn.prison.async.VolleyUtils;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.entity.MeetingRoomInfo;
import com.gkzxhn.prison.entity.ZijingCall;
import com.gkzxhn.prison.model.IBaseModel;
import com.gkzxhn.prison.model.ILoginModel;
import com.gkzxhn.prison.model.iml.BaseModel;
import com.gkzxhn.prison.model.iml.LoginModel;
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

public class LoginPresenter extends BasePresenter<ILoginModel,ILoginView>{
    public LoginPresenter(Context context, ILoginView view) {
        super(context, new LoginModel(), view);
    }
    /**
     * 获取会见会议室号等
     * @param account
     * @param password
     */
    private void getMeetingRoom(final String account, final String password) {
        final ILoginView view=mWeakView==null?null:mWeakView.get();
        mModel.getMeetingRoom(account, password, new VolleyUtils.OnFinishedListener<String>() {
            @Override
            public void onSuccess(String response) {
                MeetingRoomInfo meetingRoomInfo = new GsonBuilder().create().fromJson(response, MeetingRoomInfo.class);
                String content = meetingRoomInfo.data.content;
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(Constants.USER_ACCOUNT, account);
                edit.putString(Constants.USER_PASSWORD, password);
                //记住帐号密码
                edit.putString(Constants.USER_ACCOUNT_CACHE, account);
                edit.putString(Constants.USER_PASSWORD_CACHE, password);
                if (!TextUtils.isEmpty(content)) {
                    content="6848##7890##0987";//TODO
                    edit.putString(Constants.TERMINAL_ACCOUNT, content);
                }
                edit.apply();
                //关闭加载条
                if (view != null) {
                    view.stopRefreshAnim();
                    view.onSuccess();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                showErrors(error);
            }
        });
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
                        getMeetingRoom(account, password);
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


}

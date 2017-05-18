package com.gkzxhn.prision.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gkzxhn.prision.R;
import com.gkzxhn.prision.async.VolleyUtils;
import com.gkzxhn.prision.common.Constants;
import com.gkzxhn.prision.common.GKApplication;
import com.gkzxhn.prision.entity.MeetingEntity;
import com.gkzxhn.prision.entity.VersionEntity;
import com.gkzxhn.prision.model.IMainModel;
import com.gkzxhn.prision.model.iml.MainModel;
import com.gkzxhn.prision.view.ICallUserView;
import com.gkzxhn.prision.view.IMainView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.starlight.mobile.android.lib.util.ConvertUtil;
import com.starlight.mobile.android.lib.util.HttpStatus;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class MainPresenter extends BasePresenter<IMainModel,IMainView> {
    public MainPresenter(Context context,IMainView view) {
        super(context, new MainModel(), view);
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
                        List<MeetingEntity> datas = new Gson().fromJson(JSONUtil.getJSONObjectStringValue(response,"meetings"),  new TypeToken<List<MeetingEntity>>() {}.getType());
                        if(view!=null){
                            view.updateItems(datas);
                        }
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
            if(view!=null)view.showToast(R.string.connecting_yunxin);
        } else if (code == StatusCode.LOGINING) {// 正在登录
            if(view!=null)view.showToast(R.string.login_yunxin);
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
}

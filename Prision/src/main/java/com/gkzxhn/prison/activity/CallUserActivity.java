package com.gkzxhn.prison.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.customview.CustomDialog;
import com.gkzxhn.prison.customview.ShowTerminalDialog;
import com.gkzxhn.prison.keda.utils.NetWorkUtils;
import com.gkzxhn.prison.presenter.CallUserPresenter;
import com.gkzxhn.prison.view.ICallUserView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class CallUserActivity extends SuperActivity implements ICallUserView{
    private CallUserPresenter mPresenter;
    private DotsTextView tvLoading;
    private ImageView ivCard01,ivCard02;
    private CustomDialog mCustomDialog;
    private ShowTerminalDialog mShowTerminalDialog;
    private ProgressDialog mProgress;
    private SharedPreferences preferences;
    private String phone=null;
    private String nickName=null,id=null;
    private boolean isClickCall=false;//是否点击了呼叫按钮
    private String mAccount;
    private static final float ID_RATIO = 856f / 540f ;
    private int mIDWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_user_layout);
        initControls();
        init();
        registerReceiver();
    }
    private void initControls(){
        tvLoading= (DotsTextView) findViewById(R.id.common_loading_layout_tv_load);
        ivCard01= (ImageView) findViewById(R.id.call_user_layout_iv_card_01);
        ivCard02= (ImageView) findViewById(R.id.call_user_layout_iv_card_02);

        ViewTreeObserver viewTreeObserver = ivCard01.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivCard01.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mIDWidth = ivCard01.getMeasuredWidth();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivCard01.getLayoutParams();
                layoutParams.height = (int) (mIDWidth / ID_RATIO);
                layoutParams.width = mIDWidth;
                ivCard01.setLayoutParams(layoutParams);
                ivCard02.setLayoutParams(layoutParams);
            }
        });
    }
    private void init(){
        mPresenter=new CallUserPresenter(this,this);
        id=getIntent().getStringExtra(Constants.EXTRA);
        phone=getIntent().getStringExtra(Constants.EXTRAS);
        nickName=getIntent().getStringExtra(Constants.EXTRA_TAB);
        mProgress = ProgressDialog.show(this, null, getString(R.string.check_other_status));
        mProgress.setCanceledOnTouchOutside(true);
        stopProgress();
        preferences=mPresenter.getSharedPreferences();
        mAccount = preferences.getString(Constants.TERMINAL_ACCOUNT,"");
        if (TextUtils.isEmpty(mAccount)) {
            if (mShowTerminalDialog == null) {
                mShowTerminalDialog = new ShowTerminalDialog(this);
            }
            if (!mShowTerminalDialog.isShowing()) mShowTerminalDialog.show();
        }
        mCustomDialog=new CustomDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.custom_dialog_layout_tv_confirm){
                    online(mAccount);
                }
            }
        });
        mPresenter.request(phone);//请求详情
    }
    public void openVConfVideoUI(){
        if(isClickCall) {
            stopProgress();
            mTimer.cancel();
            mPresenter.callFang(mAccount, 0);
        }
    }

    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.call_user_layout_bt_call:
//                if(Utils.getTFPath()==null) {//没有检测到TF卡
//                    if (mCustomDialog != null) {
//                        mCustomDialog.setContent(Utils.hasSDFree() ? getString(R.string.not_found_tf_card_but_sd_free) : getString(R.string.not_found_tf_card),
//                                getString(R.string.cancel),
//                                getString(R.string.continue_call));
//                        if (!mCustomDialog.isShowing()) mCustomDialog.show();
//                    }
//                }else if(Utils.getTFPath().length()==0){
//                    if (mCustomDialog != null) {
//                        mCustomDialog.setContent(Utils.hasSDFree() ? getString(R.string.tf_card_not_root_but_sd_free) : getString(R.string.tf_card_not_root),
//                                getString(R.string.cancel),
//                                getString(R.string.continue_call));
//                        if (!mCustomDialog.isShowing()) mCustomDialog.show();
//                    }
//                }else if(Utils.hasTFFree()){
//                    if (mCustomDialog != null) {
//                        mCustomDialog.setContent(getString(R.string.tf_card_is_fill),
//                                getString(R.string.cancel),
//                                getString(R.string.continue_call));
//                        if (!mCustomDialog.isShowing()) mCustomDialog.show();
//                    }
//                }else{
                String account=preferences.getString(Constants.TERMINAL_ACCOUNT,"");
                online(account);
//                }
                break;
        }
    }
    private void online(String account){
        isClickCall=true;
        if(account!=null&&account.length()>0) {
            if (NetWorkUtils.isAvailable(this)) {
                if(mPresenter.checkStatusCode()== StatusCode.LOGINED) {
                    startProgress();
                    //发送云信消息，检测家属端是否已经准备好可以呼叫
                    CustomNotification notification = new CustomNotification();
                    String accid = mPresenter.getEntity().getAccid();
                    notification.setSessionId(accid);
                    notification.setSessionType(SessionTypeEnum.P2P);
                    // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
                    // 这里以类型 “1” 作为“正在输入”的状态通知。
                    JSONObject json = new JSONObject();
                    try {
                        json.put("code", -1);
//                        json.put("msg", account);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notification.setContent(json.toString());
                    NIMClient.getService(MsgService.class).sendCustomNotification(notification);
                    mTimer.start();
                }else{
                    showToast(R.string.yunxin_offline);
                }
            } else {
                showToast(R.string.network_error);
            }
        }else{
            if(mShowTerminalDialog==null){
                mShowTerminalDialog=new ShowTerminalDialog(this);
            }
            if(!mShowTerminalDialog.isShowing())mShowTerminalDialog.show();
        }
    }
    @Override
    public void onSuccess() {
        String[] img_urls = mPresenter.getEntity().getImageUrl().split("\\|");
        ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[0],ivCard01);
        ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[1],ivCard02);
        findViewById(R.id.call_user_layout_bt_call).setEnabled(true);
        SharedPreferences.Editor editor=mPresenter.getSharedPreferences().edit();
        editor.putString(Constants.OTHER_CARD+1,img_urls[0]);
        editor.putString(Constants.OTHER_CARD+2,img_urls[1]);
        editor.putString(Constants.OTHER_CARD+3,img_urls[2]);
        editor.putString(Constants.EXTRA, mPresenter.getEntity().getAccid());
        editor.putString(Constants.EXTRAS,id);
        editor.commit();

    }
    @Override
    public void startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI);
    }

    @Override
    public void stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI);
    }



    /**
     * 加载动画
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== Constants.START_REFRESH_UI){//开始加载动画
                tvLoading.setVisibility(View.VISIBLE);
                if (!tvLoading.isPlaying()) {

                    tvLoading.showAndPlay();
                }
            }else if(msg.what==Constants.STOP_REFRESH_UI){//停止加载动画
                if (tvLoading.isPlaying() || tvLoading.isShown()) {
                    tvLoading.hideAndStop();
                    tvLoading.setVisibility(View.GONE);
                }
            }
        }
    };
    public void startProgress() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    public void stopProgress() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopRefreshAnim();
            if(intent.getAction().equals(Constants.TERMINAL_FAILED_ACTION)){//GK注册失败
                mCustomDialog.setContent(getString(R.string.GK_register_failed),
                        getString(R.string.cancel),getString(R.string.call_back));
                if(!mCustomDialog.isShowing())mCustomDialog.show();
            }else if(intent.getAction().equals(Constants.TERMINAL_SUCCESS_ACTION)){// GK 注册成功
                openVConfVideoUI();
            }else if(intent.getAction().equals(Constants.ONLINE_SUCCESS_ACTION)){
                openVConfVideoUI();
            }else if(intent.getAction().equals(Constants.ONLINE_FAILED_ACTION)){
            }else if(intent.getAction().equals(Constants.NIM_KIT_OUT)){
                finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(mShowTerminalDialog!=null&&mShowTerminalDialog.isShowing())mShowTerminalDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mShowTerminalDialog!=null&&mShowTerminalDialog.isShowing())mShowTerminalDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();

    }

    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.TERMINAL_FAILED_ACTION);
        intentFilter.addAction(Constants.TERMINAL_SUCCESS_ACTION);
        intentFilter.addAction(Constants.ONLINE_FAILED_ACTION);
        intentFilter.addAction(Constants.ONLINE_SUCCESS_ACTION);
        intentFilter.addAction(Constants.NIM_KIT_OUT);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isClickCall=false;
    }

    private final long DOWN_TIME=10000;//倒计时 10秒
    private CountDownTimer mTimer=new CountDownTimer(DOWN_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
//            long second = millisUntilFinished / 1000;
        }
        @Override
        public void onFinish() {
            stopVConfVideo();
        }
    };

    public void stopVConfVideo(){
        isClickCall=false;
        stopProgress();
        if(mCustomDialog!=null) {
            mCustomDialog.setContent(getString(R.string.other_offline),
                    getString(R.string.cancel),getString(R.string.call_back));
            if(!mCustomDialog.isShowing())mCustomDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 0) {
                if (data != null) {
                    boolean call_again = data.getBooleanExtra(Constants.CALL_AGAIN, false);
                    if (call_again) {
                        //换协议呼叫
                        String protocol = preferences.getString(Constants.PROTOCOL, "h323");
                        if ("h323".equals(protocol)) {
                            protocol = "sip";
                        }else {
                            protocol = "h323";
                        }
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString(Constants.PROTOCOL, protocol);
                        edit.apply();
                        Log.i(TAG, "protocol : " + protocol);
                        showToast("呼叫失败,切换成" + protocol + "协议重新进行呼叫...");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.callFang(mAccount, 1);
                            }
                        }, 1500);
                    }
                }
            }
        }
    }

    private final String TAG = CallUserActivity.class.getSimpleName();
}

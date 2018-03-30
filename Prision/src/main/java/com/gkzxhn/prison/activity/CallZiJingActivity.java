package com.gkzxhn.prison.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.R;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.customview.CancelVideoDialog;
import com.gkzxhn.prison.entity.CommonRequest;
import com.gkzxhn.prison.presenter.CallZijingPresenter;
import com.gkzxhn.prison.utils.GetCameraControl;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.gkzxhn.prison.view.ICallZijingView;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 方 on 2017/11/16.
 */

public class CallZiJingActivity extends SuperActivity implements View.OnClickListener,ICallZijingView {
    private final String TAG = CallZiJingActivity.class.getSimpleName();
    private CallZijingPresenter mPresenter;

    private Subscription mTimeSubscribe;
    private TextView tv_count_down;

    /**
     * 延迟time秒执行
     * @param time
     */
    private void startTime(final Long time) {
        mTimeSubscribe = Observable.interval(0 ,1, TimeUnit.SECONDS)
                .take(time + 1, TimeUnit.SECONDS)
                .map(new Func1<Long, String>() {
                    @Override
                    public String call(Long aLong) {
                        Long delay = time - aLong;
                        if (delay == 30){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_count_down.setTextColor(getResources().getColor(R.color.red_text));
                                }
                            });
                        }
                        long min = delay / 60;
                        long seconds = delay - min * 60;
                        return min + "分" + seconds + "秒";
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        showTimeUp();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "count_down : " + s);
                        tv_count_down.setText(s);
                    }
                });
    }

    /**
     * 提示通话时间已到
     */
    private void showTimeUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(R.string.reminder)
                .setMessage("通话时间已到,是否结束通话?")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendHangupMessage();
                        mPresenter.hangUp();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private TextView mText;
    private FrameLayout mContent;
    //挂断
    private ImageView mExit_img;

    private LinearLayout mLl_check_id;
    private ImageView mIv_avatar;
    private ImageView mIv_id_card_01;
    private ImageView mIv_id_card_02;
    private TextView mMute_txt;
    private TextView mQuite_txt;
    private GetCameraControl mGetCameraControl;  // 遥控器控制器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_zijing);
        mPresenter=new CallZijingPresenter(this,this);
        findViews();
        setIdCheckData();
        setClickListener();
        registerReceiver();
    }

    private void findViews() {
        mText = (TextView) findViewById(R.id.tv_call_zijing);
        mContent = (FrameLayout) findViewById(R.id.fl_call_zijing);
        mExit_img = (ImageView) findViewById(R.id.exit_Img);
        mMute_txt = (TextView) findViewById(R.id.mute_text);
        mQuite_txt = (TextView) findViewById(R.id.quiet_text);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);

        mLl_check_id = (LinearLayout) findViewById(R.id.ll_check_id); //审核布局
        mIv_avatar = (ImageView) findViewById(R.id.iv_avatar);   //审核头像
        mIv_id_card_01 = (ImageView) findViewById(R.id.iv_id_card_01);   //身份证1
        mIv_id_card_02 = (ImageView) findViewById(R.id.iv_id_card_02);   //身份证2

        mGetCameraControl = new GetCameraControl();
        mGetCameraControl.cameraControl("direct");
    }

    private void setClickListener() {
        mMute_txt.setOnClickListener(this);
        mQuite_txt.setOnClickListener(this);
        mExit_img.setOnClickListener(this);
        mLl_check_id.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (null != mCancelVideoDialog && mCancelVideoDialog.isShowing()) {
            mCancelVideoDialog.dismiss();
        }
        if (mTimeSubscribe != null) {
            mTimeSubscribe.unsubscribe();
        }
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        super.onDestroy();
    }

    /**
     * 注册广播监听器
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ZIJING_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private CancelVideoDialog mCancelVideoDialog;
    private boolean isQuite;    //是否静音

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mute_text:
                //哑音
                mPresenter.switchMuteStatus();
                break;
            case R.id.quiet_text:
                //修改线性输出状态
                mPresenter.setIsQuite(!isQuite);
                break;
            case R.id.exit_Img:
                //挂断
                showHangup();
                break;
            case R.id.ll_check_id:
                startScaleAnim(mLl_check_id);
                break;
        }
    }


    /**
     * 设置扬声器UI
     *
     * @param quiet
     */
    @Override
    public void setSpeakerUi(boolean quiet) {
        if (quiet) mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.vconf_mute_selector), null, null);
        else
            mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.vconf_speaker_selector), null, null);
    }


    //发送云信消息，挂断
    private void sendHangupMessage() {
        CustomNotification notification = new CustomNotification();
        String accid = GKApplication.getInstance().
                getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
                .getString(Constants.ACCID, "");
        notification.setSessionId(accid);
        notification.setSessionType(SessionTypeEnum.P2P);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        JSONObject json = new JSONObject();
        try {
            json.put("code", -2);//-2表示挂断
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notification.setContent(json.toString());
        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

    /**
     * 设置审核身份布局
     */
    private void setIdCheckData() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
        String avatarUri = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 3, "");
        String idCardUri1 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 1, "");
        String idCardUri2 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 2, "");
        ImageLoader.getInstance().displayImage(avatarUri, mIv_avatar);
        ImageLoader.getInstance().displayImage(idCardUri1, mIv_id_card_01);
        ImageLoader.getInstance().displayImage(idCardUri2, mIv_id_card_02);
    }

    private boolean isScaled = false;  //审核界面是否已缩放

    /**
     * 开始属性动画
     *
     * @param view
     */
    private void startScaleAnim(final View view) {
        ObjectAnimator anim = null;
        if (isScaled) {
            //放大动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tobig", 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f).setDuration(300);
            mLl_check_id.setPivotX(0);
            mLl_check_id.setPivotY(0);
            isScaled = !isScaled;
            anim.start();
        } else {
            //缩小动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tosmall", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f).setDuration(300);
            mLl_check_id.setPivotX(0);
            mLl_check_id.setPivotY(0);
            isScaled = !isScaled;
            anim.start();
        }
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float cVal = (float) valueAnimator.getAnimatedValue();
                view.setScaleX(cVal);
                view.setScaleY(cVal);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: getkeycode >>>>>> " + event.getKeyCode());
        switch (event.getKeyCode()) {
            case 221:
                //挂断按键
                showHangup();
                return true;
            case 225:
                //显示/缩放审核界面
                startScaleAnim(mLl_check_id);
                return true;
            case 218:
                //静音
                mPresenter.setIsQuite(!isQuite);
                return true;
        }
        return false;
    }

    private void showHangup() {
        if (mCancelVideoDialog == null) {
            mCancelVideoDialog = new CancelVideoDialog(this, true);
            mCancelVideoDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendHangupMessage();
                    mPresenter.hangUp();
                }
            });
        }
        if (!mCancelVideoDialog.isShowing()) mCancelVideoDialog.show();
    }

    /**
     * 通知远端进入房间
     */
    private void callAccount(){
        SharedPreferences sharedPreferences = GKApplication.getInstance().
                getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
        String account = sharedPreferences.getString(Constants.TERMINAL_ACCOUNT, "");
        Long time = sharedPreferences.getLong(Constants.TIME_LIMIT, 20);
        if(account!=null&&account.length()>0) {
                //发送云信消息，检测家属端是否已经准备好可以呼叫
            CustomNotification notification = new CustomNotification();
            String accid = sharedPreferences
                    .getString(Constants.ACCID, "");
            notification.setSessionId(accid);
            notification.setSessionType(SessionTypeEnum.P2P);
            // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
            // 这里以类型 “1” 作为“正在输入”的状态通知。
            JSONObject json = new JSONObject();
            try {
                json.put("code", -1);//-1表示接通
                json.put("msg", account);
                json.put("limit_time", time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notification.setContent(json.toString());
            NIMClient.getService(MsgService.class).sendCustomNotification(notification);
        }
        startTime(time * 60);
    }

    @Override
    public void startRefreshAnim() {

    }

    @Override
    public void stopRefreshAnim() {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constants.ZIJING_ACTION).equals(intent.getAction())) {
                boolean hangup = intent.getBooleanExtra(Constants.HANGUP, false);
                if (hangup) {
                    mPresenter.hangUp();
                }
                boolean time_connect = intent.getBooleanExtra(Constants.TIME_CONNECT, false);
                if (time_connect) {
                    if (mTimeSubscribe != null) {
                        mTimeSubscribe.unsubscribe();
                        SharedPreferences sharedPreferences = GKApplication.getInstance().
                                getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
                        Long time = sharedPreferences.getLong(Constants.TIME_LIMIT, 20);
                        startTime(time * 60);
                    }
                }
                String jsonStr = intent.getStringExtra(Constants.ZIJING_JSON);
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                Log.w(TAG, "jsonStr received : " + jsonStr);
                String e = null;
                try {
                    e = JSONUtil.getJSONObjectStringValue(new JSONObject(jsonStr), "e");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                if (e == null) {
                    return;
                }
                switch (e) {
                    case "setup_call_calling":
                        mText.setText("正在连接...");
                        break;
                    case "ring_call":
                        mText.setText("等待接听...");
                        break;
                    case "established_call":
                        //呼叫建立
                        mText.setVisibility(View.GONE);
                        mContent.setBackgroundColor(getResources().getColor(R.color.zijing_video_bg));
                        callAccount();
                        break;
                    case "cleared_call":
                        JSONObject jsonObject = JSONUtil.getJSONObject(jsonStr);
                        JSONObject objv = null;
                        try {
                            objv = jsonObject.getJSONObject("v");
                            String reason = objv.getString("reason");
                            if (!"Ended by local user".equals(reason)) {
//                            if ("Remote host offline".equals(reason) || "No common capabilities".equals(reason)) {
                                Intent data = new Intent();
                                data.putExtra(Constants.CALL_AGAIN, true);
                                data.putExtra(Constants.END_REASON, reason);
                                CallZiJingActivity.this.setResult(RESULT_CANCELED, data);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        showToast("已挂断");
                        CallZiJingActivity.this.finish();
                        break;
                    case "missed_call":
                        mText.setText("对方未接听...");
                        showToast("对方未接听");
                        CallZiJingActivity.this.finish();
                        break;
                    case "error":
                        mText.setText("呼叫错误");
                        CallZiJingActivity.this.finish();
                        break;
                    case "MuteOn":
                        //麦克风静音
                        mMute_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                                getResources().getDrawable(R.drawable.vconf_microphone_off_selector),
                                null,
                                null);
                        break;
                    case "MuteOff":
                        //麦克风静音
                        mMute_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                                getResources().getDrawable(R.drawable.vconf_microphone_on_selector),
                                null,
                                null);
                        break;

                }
            }
        }
    };
}


package com.gkzxhn.prison.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.gkzxhn.prison.customview.CancelVideoDialog;
import com.gkzxhn.prison.entity.CommonRequest;
import com.gkzxhn.prison.utils.GetCameraControl;
import com.gkzxhn.prison.utils.XtHttpUtil;
import com.google.gson.Gson;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.starlight.mobile.android.lib.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 方 on 2017/11/16.
 */

public class CallZiJingActivity extends SuperActivity implements View.OnClickListener {

    private final String TAG = CallZiJingActivity.class.getSimpleName();

    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ((Constants.ZIJING_ACTION).equals(intent.getAction())) {
                String jsonStr = intent.getStringExtra(Constants.ZIJING_JSON);
                Log.w(TAG, "jsonStr received : " +jsonStr );
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
                        mText.setVisibility(View.GONE);
                        mContent.setBackgroundColor(getResources().getColor(R.color.zijing_video_bg));
                    break;
                    case "cleared_call":
                        showToast("已挂断");
                        finish();
                        break;
                    case "missed_call":
                        mText.setText("对方未接听...");
                        showToast("对方未接听");
                        finish();
                        break;
                    case "error":
                        mText.setText("呼叫错误");
                        finish();
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

        mLl_check_id = (LinearLayout)findViewById(R.id.ll_check_id); //审核布局
        mIv_avatar = (ImageView)findViewById(R.id.iv_avatar);   //审核头像
        mIv_id_card_01 = (ImageView)findViewById(R.id.iv_id_card_01);   //身份证1
        mIv_id_card_02 = (ImageView)findViewById(R.id.iv_id_card_02);   //身份证2

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
        super.onDestroy();
        if (mCancelVideoDialog.isShowing()) {
            mCancelVideoDialog.dismiss();
        }
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
    }

    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.ZIJING_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    private CancelVideoDialog mCancelVideoDialog;
    private boolean isQuite;    //是否静音
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mute_text :
                //哑音
                switchMuteStatus();
                break;
            case R.id.quiet_text :
                //修改线性输出状态
                setIsQuite(!isQuite);
                break;
            case R.id.exit_Img :
                //挂断
                if(mCancelVideoDialog==null){
                    mCancelVideoDialog=new CancelVideoDialog(this,true);
                    mCancelVideoDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hangUp();
                        }
                    });
                }
                if(!mCancelVideoDialog.isShowing())mCancelVideoDialog.show();
                break;
            case R.id.ll_check_id :
                startScaleAnim(mLl_check_id);
                break;
        }
    }

    /**设置静音状态
     * @param quiet true表示设置成静音
     */
    private void setIsQuite(final boolean quiet) {
        CommonRequest json = new CommonRequest();
        json.k = "enable-line-out";
        json.v = !quiet;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(json));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                XtHttpUtil.SET_AUDIOUT, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DIAL" + response.toString());
                        try {
                            int code = response.getInt("code");
                            if (code == 0){
                                //设置成功
                                isQuite = quiet;
                                setSpeakerUi(isQuite);
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
                    }
                }, 2000);

            }
        });
        SingleRequestQueue.getInstance().add(request, "");
    }

    /**
     * 设置扬声器UI
     * @param quiet
     */
    private void setSpeakerUi(boolean quiet) {
        if (quiet) mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.vconf_mute_selector), null, null);
        else
        mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.vconf_speaker_selector), null, null);
    }

    //修改哑音状态
    private void switchMuteStatus() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                XtHttpUtil.MUTE_AUDIIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "HANGUP" + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "ResetQuest..." + error.toString());
                    }
                }, 2000);

            }
        });
        SingleRequestQueue.getInstance().add(request, "");
    }

    /**
     * 挂断
     */
    private void hangUp() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                XtHttpUtil.HANGUP, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "HANGUP" + response.toString());
                        try {
                            int code = response.getInt("code");
                            if (code == 0){
                                //成功
                                finish();
                            }else {
                                Log.i(TAG, "onResponse: code :  " + code);
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
                    }
                }, 2000);

            }
        });
        SingleRequestQueue.getInstance().add(request, "");
    }

    /**
     * 设置审核身份布局
     */
    private void setIdCheckData() {
        SharedPreferences sharedPreferences=getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
        String avatarUri =Constants.DOMAIN_NAME_XLS+"/"+sharedPreferences.getString (Constants.OTHER_CARD+3,"");
        String idCardUri1 = Constants.DOMAIN_NAME_XLS+"/"+sharedPreferences.getString (Constants.OTHER_CARD+1,"");
        String idCardUri2 =Constants.DOMAIN_NAME_XLS+"/"+sharedPreferences.getString (Constants.OTHER_CARD+2,"");
        ImageLoader.getInstance().displayImage(avatarUri,mIv_avatar);
        ImageLoader.getInstance().displayImage(idCardUri1,mIv_id_card_01);
        ImageLoader.getInstance().displayImage(idCardUri2,mIv_id_card_02);
    }

    private boolean isScaled = false;  //审核界面是否已缩放
    /**
     * 开始属性动画
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
        }else {
            //缩小动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tosmall", 1f,0.9f, 0.8f,0.7f,0.6f,0.5f,0.4f,0.3f, 0.2f).setDuration(300);
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
                if(mCancelVideoDialog==null){
                    mCancelVideoDialog=new CancelVideoDialog(this,true);
                    mCancelVideoDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hangUp();
                        }
                    });
                }
                if(!mCancelVideoDialog.isShowing())mCancelVideoDialog.show();
                return true;
            case 225:
                //显示/缩放审核界面
                startScaleAnim(mLl_check_id);
                return true;
            case 218:
                //静音
                setIsQuite(!isQuite);
                return true;
        }
        return false;
    }
}


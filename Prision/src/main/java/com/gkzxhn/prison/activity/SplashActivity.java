package com.gkzxhn.prison.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gkzxhn.prison.R;
import com.gkzxhn.prison.async.SingleRequestQueue;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.utils.XtHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raleigh.Luo on 17/4/5.
 */

public class SplashActivity extends Activity {
    private final long SPLASH_DELAY_MILLIS = 1000;
    private FrameLayout mFl_content;
    private LinearLayout mLl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        init();
    }

    private void init() {
        TextView tvVersionName = (TextView) findViewById(R.id.splash_layout_tv_version);
        mFl_content = (FrameLayout) findViewById(R.id.fl_content);
        mLl_content = (LinearLayout) findViewById(R.id.ll_content);
        mFl_content.setBackgroundColor(getResources().getColor(R.color.zijing_video_bg));
        mLl_content.setVisibility(View.GONE);

//        query();
        mHandler.sendEmptyMessageDelayed(1, SPLASH_DELAY_MILLIS);

        String versionName = "";
        // 包管理器
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersionName.setText(getString(R.string.app_v) + versionName);

        /*SharedPreferences preferences = getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
//        if(preferences.getBoolean(Constants.IS_FIRST_IN,true)) {
//            mHandler.sendEmptyMessageDelayed(0, SPLASH_DELAY_MILLIS);
//        }else
        if (preferences.getString(Constants.USER_ACCOUNT, "").length() == 0) {//未登录 未认证
            mHandler.sendEmptyMessageDelayed(1, SPLASH_DELAY_MILLIS);
        } else {//已登录
            mHandler.sendEmptyMessageDelayed(2, SPLASH_DELAY_MILLIS);
        }*/
    }

    private void query() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                XtHttpUtil.GET_DIAL_HISTORY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");
                            if (code == 0) {
                                mLl_content.setVisibility(View.VISIBLE);
                                mFl_content.setBackgroundDrawable(getResources().getDrawable(R.mipmap.splash_common_tablet));
                                SharedPreferences preferences = getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE);
//        if(preferences.getBoolean(Constants.IS_FIRST_IN,true)) {
//            mHandler.sendEmptyMessageDelayed(0, SPLASH_DELAY_MILLIS);
//        }else
                                if (preferences.getString(Constants.USER_ACCOUNT, "").length() == 0) {//未登录 未认证
                                    mHandler.sendEmptyMessageDelayed(1, SPLASH_DELAY_MILLIS);
                                } else {//已登录
                                    mHandler.sendEmptyMessageDelayed(2, SPLASH_DELAY_MILLIS);
                                }
                            }
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                query();
                            }
                        }, 2000);

            }
        });
        SingleRequestQueue.getInstance().add(request,"");
    }

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://启动动画
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1://跳转登录界面
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2://跳转主页
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }

            super.handleMessage(msg);

        }
    };

}
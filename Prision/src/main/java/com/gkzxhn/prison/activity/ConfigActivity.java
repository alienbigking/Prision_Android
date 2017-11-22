package com.gkzxhn.prison.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.starlight.mobile.android.lib.util.CommonHelper;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class ConfigActivity extends SuperActivity {
    private EditText etAccount;
    private Spinner mSpinner;
    private String[] mRateArray;
    private String mRate=null;
    private ProgressDialog mProgress;
    private SharedPreferences preferences;
    private final long DOWN_TIME=20000;//倒计时 20秒
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
        initControls();
        init();
        registerReceiver();
    }
    private void initControls(){
        etAccount= (EditText) findViewById(R.id.config_layout_et_account);
        mSpinner= (Spinner) findViewById(R.id.config_layout_sp_rate);
    }
    private void init(){
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting));
        stopRefreshAnim();
        mRateArray = getResources().getStringArray(R.array.rate_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mRateArray);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mRate = mRateArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        int index=1;
        preferences=getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE);
        String account=preferences.getString(Constants.TERMINAL_ACCOUNT,"");
        if(account!=null&&account.length()>0) {
            etAccount.setText(account);
            for(int i = 0; i< mRateArray.length; i++){
                String mRate= mRateArray[i];
                if(mRate.equals(String.valueOf(GKApplication.getInstance().getTerminalRate()))){
                    index=i;
                    break;
                }
            }
        }
        mRate = mRateArray[index];
        mSpinner.setSelection(index);
    }
    public void onClickListener(View view){
        CommonHelper.clapseSoftInputMethod(this);
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.config_layout_btn_save:
                String account = etAccount.getText().toString().trim();
                if (TextUtils.isEmpty(account) ) {
                    showToast(R.string.please_input_terminal_account);
                }else {
                    //退修改账号
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString(Constants.TERMINAL_ACCOUNT,account);
                    editor.putInt(Constants.TERMINAL_RATE,Integer.valueOf(mRate));
                    editor.apply();
                    showToast("修改成功");
                }
                break;
        }
    }
    public void startRefreshAnim() {
        if(mProgress!=null&&!mProgress.isShowing())mProgress.show();
    }

    public void stopRefreshAnim() {
        if(mProgress!=null&&mProgress.isShowing())mProgress.dismiss();
    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopRefreshAnim();
            if(intent.getAction().equals(Constants.NIM_KIT_OUT)){
                finish();
            }
        }
    };



    /**
     * 注册广播监听器
     */
    private void registerReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Constants.TERMINAL_FAILED_ACTION);
        intentFilter.addAction(Constants.TERMINAL_SUCCESS_ACTION);
        intentFilter.addAction(Constants.NIM_KIT_OUT);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        super.onDestroy();
    }

}

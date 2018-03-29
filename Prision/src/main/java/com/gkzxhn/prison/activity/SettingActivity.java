package com.gkzxhn.prison.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.common.GKApplication;
import com.gkzxhn.prison.customview.CustomDialog;
import com.gkzxhn.prison.customview.UpdateDialog;
import com.gkzxhn.prison.entity.MeetingEntity;
import com.gkzxhn.prison.entity.VersionEntity;
import com.gkzxhn.prison.presenter.MainPresenter;
import com.gkzxhn.prison.presenter.SettingPresenter;
import com.gkzxhn.prison.view.IMainView;
import com.gkzxhn.prison.view.ISettingView;

import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

public class SettingActivity extends SuperActivity implements ISettingView{
    private int mResultCode=RESULT_CANCELED;
    private TextView tvUpdateHint,tvCallFreeTime;
    private SettingPresenter mPresenter;
    private UpdateDialog updateDialog;
    private CustomDialog mCustomDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        tvUpdateHint= (TextView) findViewById(R.id.setting_layout_tv_update_hint);
        tvCallFreeTime=(TextView) findViewById(R.id.setting_layout_tv_call_free_hint);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            tvUpdateHint.setText(getString(R.string.current_version)+"v"+packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mPresenter=new SettingPresenter(this,this);
        tvCallFreeTime.setText(getString(R.string.leave)+
                mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME,0)+getString(R.string.time));
        mPresenter.requestFreeTime();
        mCustomDialog = new CustomDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.custom_dialog_layout_tv_confirm) {
                    GKApplication.getInstance().loginOff();
                    finish();
                }
            }
        });
        mCustomDialog.setContent(getString(R.string.exit_account_hint),
                getString(R.string.cancel),getString(R.string.ok));
        registerReceiver();
    }
    public void onClickListener(View view){
        switch (view.getId()) {
            case R.id.setting_layout_tv_end_setting:
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivityForResult(intent, Constants.EXTRA_CODE);
                break;
            case R.id.setting_layout_tv_update:
                tvUpdateHint.setText(R.string.check_updating);
                mPresenter.requestVersion();
                break;
            case R.id.setting_layout_tv_logout:
                if (mCustomDialog != null&&!mCustomDialog.isShowing())
                    mCustomDialog.show();
                break;
            case R.id.common_head_layout_iv_left:
                setResult(mResultCode);
                finish();
                break;
            case R.id.setting_layout_tv_call_free:
                startActivityForResult(new Intent(this,CallFreeActivity.class),Constants.EXTRAS_CODE);
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.EXTRA_CODE&&resultCode==RESULT_OK){//修改终端信息成功
            mResultCode=RESULT_OK;
            showToast(R.string.alter_terminal_account_success);
        }else if(requestCode==Constants.EXTRAS_CODE&&resultCode==RESULT_OK){//免费呼叫
            mPresenter.requestFreeTime();

        }
    }


    @Override
    public void updateVersion(VersionEntity version) {
        //新版本
        int newVersion = version.getVersionCode();
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            int currentVersion=packageInfo.versionCode;//当前App版本
            if (newVersion > currentVersion) {//新版本大于当前版本
                //版本名
                String versionName =  version.getVersionName();
                // 下载地址
                String downloadUrl =  version.getDownloadUrl();
                //是否强制更新
                if(updateDialog==null)updateDialog=new UpdateDialog(this);
                updateDialog.setForceUpdate(version.isForce());
                updateDialog.setDownloadInfor(versionName,newVersion,downloadUrl);
                updateDialog.show();//显示对话框
                tvUpdateHint.setText(getString(R.string.new_version_colon)+versionName);
            }else{
                tvUpdateHint.setText(R.string.has_last_version);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFreeTime(int time) {
        tvCallFreeTime.setText(time+getString(R.string.time));
    }

    @Override
    public void startRefreshAnim() {

    }

    @Override
    public void stopRefreshAnim() {

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);//注销广播监听器
        if (mCustomDialog != null&&mCustomDialog.isShowing())   mCustomDialog.dismiss();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.dismiss();
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//点击返回键，返回到主页
            setResult(mResultCode);
            finish();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(updateDialog!=null&&updateDialog.isShowing())updateDialog.measureWindow();
        if(mCustomDialog!=null&&mCustomDialog.isShowing())mCustomDialog.measureWindow();

    }
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
        intentFilter.addAction(Constants.NIM_KIT_OUT);
        registerReceiver(mBroadcastReceiver,intentFilter);
    }
}

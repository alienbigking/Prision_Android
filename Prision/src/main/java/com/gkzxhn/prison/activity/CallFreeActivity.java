package com.gkzxhn.prison.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.common.Constants;
import com.gkzxhn.prison.presenter.CallFreePresenter;
import com.gkzxhn.prison.view.ICallFreeView;
import com.starlight.mobile.android.lib.view.CusHeadView;
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView;

/**
 * Created by Raleigh.Luo on 18/3/29.
 */

public class CallFreeActivity extends SuperActivity implements ICallFreeView {
    private TextView tvFamilyName,tvPhone,tvPrisionName,tvPrisionNumber,tvSearch,tvFreeTime,tvClickCall;
    private DotsTextView tvLoading;
    private CallFreePresenter mPresenter;
    private EditText etPhone;
    private ImageView ivSearchClear;
    private int mCallFreeTime=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_free_layout);
        tvClickCall= (TextView) findViewById(R.id.call_free_layout_tv_click_call);
        tvFreeTime= (TextView) findViewById(R.id.call_free_layout_tv_leave_time);
        tvFamilyName= (TextView) findViewById(R.id.call_free_layout_tv_family_name);
        tvPhone= (TextView) findViewById(R.id.call_free_layout_tv_phone);
        tvPrisionName= (TextView) findViewById(R.id.call_free_layout_tv_prision_name);
        tvPrisionNumber= (TextView) findViewById(R.id.call_free_layout_tv_prision_number);
        tvLoading= (DotsTextView) findViewById(R.id.call_free_layout_i_loading);
        etPhone=(EditText)findViewById(R.id.call_free_layout_et_phone);
        ivSearchClear=(ImageView)findViewById(R.id.call_free_layout_iv_clear);
        tvSearch= (TextView) findViewById(R.id.call_free_layout_tv_search);
        etPhone.addTextChangedListener(mTextWatcher);
        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if( actionId== EditorInfo.IME_ACTION_SEARCH){
                    if(mCallFreeTime>0) {
                        String phone = etPhone.getText().toString().trim();
                        if (!phone.isEmpty()) {
                            recovery();//恢复页面
                            mPresenter.request(phone);//请求
                        }
                    }
                    return true;
                }else{
                    return false;
                }

            }
        });
        stopRefreshAnim();
        mPresenter=new CallFreePresenter(this,this);
        mCallFreeTime=  mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME,0);
        tvFreeTime.setText(String.valueOf(mCallFreeTime));
        initSearchBtn();
        mPresenter.requestFreeTime();

    }
    private void initSearchBtn(){
        if(mCallFreeTime>0){
            int padingBottom=tvSearch.getPaddingBottom();
            int padingLeft=tvSearch.getPaddingLeft();
            int padingRight=tvSearch.getPaddingRight();
            int padingTop=tvSearch.getPaddingTop();
            tvSearch.setEnabled(true);
            tvSearch.setBackgroundResource(R.drawable.search_btn_selector);
            tvSearch.setPadding(padingLeft,padingTop,padingRight,padingBottom);
        }else{
            int padingBottom=tvSearch.getPaddingBottom();
            int padingLeft=tvSearch.getPaddingLeft();
            int padingRight=tvSearch.getPaddingRight();
            int padingTop=tvSearch.getPaddingTop();
            tvSearch.setBackgroundResource(R.drawable.search_btn_uneable_selector);
            tvSearch.setEnabled(false);
            tvSearch.setPadding(padingLeft,padingTop,padingRight,padingBottom);
        }
    }
    public void onClickListener(View view){
        switch (view.getId()){
            case R.id.common_head_layout_iv_left:
                finish();
                break;
            case R.id.call_free_layout_tv_search://搜索
                String phone=etPhone.getText().toString().trim();
                if(!phone.isEmpty()){
                    recovery();//恢复页面
                    mPresenter.request(phone);//请求
                }
                break;
            case R.id.call_free_layout_rl_item://点击项
                if(mPresenter.getEntity()!=null) {
                    if (mCallFreeTime > 0) {
                        Intent intent = new Intent(this, CallUserActivity.class);
                        intent.putExtra(Constants.EXTRA, "");
                        intent.putExtra(Constants.EXTRAS, mPresenter.getEntity().getPhone());
                        intent.putExtra(Constants.EXTRA_TAB, mPresenter.getEntity().getPrisonerName());
                        startActivityForResult(intent, Constants.EXTRA_CODE);
                    } else {
                        showToast(R.string.no_call_free_time);
                    }
                }
                break;
            case R.id.call_free_layout_iv_clear:
                etPhone.setText("");
                recovery();
                break;
        }
    }
    private void recovery(){
        mPresenter.clearEntity();
        tvFamilyName.setText(R.string.family_name_default);
        tvPhone.setText(R.string.family_phone_default);
        tvPrisionName.setText(R.string.prision_name_default);
        tvPrisionNumber.setText(R.string.prision_number_default);
        tvClickCall.setVisibility(View.GONE);
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

    @Override
    public void startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI);
    }

    @Override
    public void stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI);
    }


    @Override
    public void onSuccess() {
        tvFamilyName.setText( mPresenter.getEntity().getName());
        tvPhone.setText(mPresenter.getEntity().getPhone());
        tvPrisionName.setText(mPresenter.getEntity().getPrisonerName());
        tvPrisionNumber.setText(mPresenter.getEntity().getPrisonerNumber()+" "+mPresenter.getEntity().getRelationship());
        tvClickCall.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateFreeTime(int time) {
        mCallFreeTime=time;
        tvFreeTime.setText(String.valueOf(mCallFreeTime));
        initSearchBtn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.EXTRA_CODE&&resultCode==RESULT_OK){
            mPresenter.requestFreeTime();
            mCallFreeTime--;
            tvFreeTime.setText(String.valueOf(mCallFreeTime));
            initSearchBtn();
        }
    }

    private TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            ivSearchClear.setVisibility(etPhone.getText().length()>0?View.VISIBLE : View.GONE);
        }
    };

}

package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.presenter.LoginPresenter
import com.gkzxhn.prison.service.EReportService
import com.gkzxhn.prison.view.ILoginView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthService
import com.starlight.mobile.android.lib.util.CommonHelper
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_username
as etAccount
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_password
as etPassword
/**
 * Created by Raleigh.Luo on 17/3/29.
 */

class LoginActivity : SuperActivity(), ILoginView {
    private lateinit var mPresenter: LoginPresenter
    private lateinit var mProgress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        init()
        //清除下信息
        val sharedPreferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.remove(Constants.USER_ACCOUNT)
        edit.remove(Constants.USER_PASSWORD)
        edit.remove(Constants.TERMINAL_ACCOUNT)
        edit.apply()
    }


    private fun init() {
        //显示记住的密码
        val preferences = getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
        //        etAccount.setText(preferences.getString(Constants.USER_ACCOUNT_CACHE,""));
        //        etPassword.setText(preferences.getString(Constants.USER_PASSWORD_CACHE,""));
        //TODO
        etAccount.setText("9999")
        etPassword.setText("9999")
        mPresenter = LoginPresenter(this, this)
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting))
        stopRefreshAnim()
    }

    fun onClickListener(view: View) {
        if (view.id == R.id.loign_layout_btn_login) {
            val account = etAccount.text.toString().trim { it <= ' ' }
            val password = etPassword.text.toString().trim { it <= ' ' }
            if (account.length == 0) {
                showToast(getString(R.string.please_input) + getString(R.string.account))
            } else if (password.length == 0) {
                showToast(getString(R.string.please_input) + getString(R.string.password))
            } else {
                CommonHelper.clapseSoftInputMethod(this)
                mPresenter.login(account, password)
            }
        }
    }

    override fun onSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun startRefreshAnim() {
        if (  !mProgress.isShowing) mProgress.show()
    }

    override fun stopRefreshAnim() {
        if (  mProgress.isShowing) mProgress.dismiss()
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onDestroy() {
        stopRefreshAnim()
        mPresenter.onDestory()
        super.onDestroy()
    }
}

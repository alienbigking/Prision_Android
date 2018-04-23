package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View

import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.presenter.LoginPresenter
import com.gkzxhn.prison.view.ILoginView
import com.starlight.mobile.android.lib.util.CommonHelper
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_username
as etAccount
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_password
as etPassword

import kotlinx.android.synthetic.main.login_layout.login_layout_tv_check_network_hint
as tvNetworkHint
import kotlinx.android.synthetic.main.login_layout.login_layout_tv_check_network
as tvNetwork
import kotlinx.android.synthetic.main.login_layout.login_layout_tv_start_gui_hint
as tvStartGuiHint
import kotlinx.android.synthetic.main.login_layout.login_layout_tv_stop_gui_hint
as tvStopGuiHint
/**登录
 * Created by Raleigh.Luo on 17/3/29.
 */

class LoginActivity : SuperActivity(), ILoginView {
    //请求Presenter
    private lateinit var mPresenter: LoginPresenter
    //进度条
    private lateinit var mProgress: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        //初始化
        init()
        //清除下信息
        GKApplication.instance.clearSharedPreferences()
    }
    /**
     * 初始化
     */
    private fun init() {
        //显示记住的密码
        val preferences = getSharedPreferences(Constants.TEMP_TABLE, Activity.MODE_PRIVATE)
        etAccount.setText(preferences.getString(Constants.USER_ACCOUNT,""));
        etPassword.setText(preferences.getString(Constants.USER_PASSWORD,""));
        //初始化Presenter
        mPresenter = LoginPresenter(this, this)
        //初始化进度条
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting))
        mProgress.setCanceledOnTouchOutside(true)
        mProgress.setCancelable(true)
        stopRefreshAnim()
        mProgress.setOnDismissListener {
            //关闭请求
            mPresenter.onDestory()
        }
    }

    /**
     * 响应点击事件
     */
    fun onClickListener(view: View) {
        when(view.id){
            R.id.loign_layout_btn_login ->{
                val account = etAccount.text.toString().trim()
                val password = etPassword.text.toString().trim()
                if (account.length == 0) {//账号为空
                    showToast(getString(R.string.please_input) + getString(R.string.account))
                } else if (password.length == 0) {//密码为空
                    showToast(getString(R.string.please_input) + getString(R.string.password))
                } else { //登录
                    //关闭虚拟键盘
                    CommonHelper.clapseSoftInputMethod(this)
                    //登录
                    mPresenter.login(account, password)

                }
            }
            R.id.login_layout_tv_check_network ->{

                tvNetworkHint.setTextColor(resources.getColor(R.color.common_gray_title_color))
                tvNetworkHint.setText(R.string.check_network_ing)
                //按钮不可点击
                tvNetwork.isEnabled=false
                //关闭GUI
                mPresenter.checkNetworkStatus()
            }
            R.id.login_layout_tv_start_gui->{//启用gui
                tvStartGuiHint.setText(R.string.start_gui_ing)
                tvStartGuiHint.isEnabled=false
                // adb shell pm enable cn.com.rocware.c9gui
                mPresenter.startAsynTask(Constants.OPEN_GUI_TAB,object : AsynHelper.TaskFinishedListener{
                    override fun back(`object`: Any?) {
                        tvStartGuiHint.isEnabled=true
                        val i=`object` as Int
                        if(i==0){//启用成功
                            tvStartGuiHint.setText(R.string.start_gui_success)
                            tvStopGuiHint.setText(R.string.stop_gui_hint)
                        }else{
                            tvStartGuiHint.setText(R.string.start_gui_failed)
                        }
                    }

                })
            }
            R.id.login_layout_tv_stop_gui ->{//禁用gui
                tvStopGuiHint.isEnabled=false
                tvStopGuiHint.setText(R.string.stop_gui_ing)
                //adb shell pm disable cn.com.rocware.c9gui
                mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,object : AsynHelper.TaskFinishedListener{
                    override fun back(`object`: Any?) {
                        tvStopGuiHint.isEnabled=true
                        val i=`object` as Int
                        if(i==0){//禁用用成功
                            tvStopGuiHint.setText(R.string.stop_gui_success)
                            tvStartGuiHint.setText(R.string.start_gui_hint)
                        }else{
                            tvStopGuiHint.setText(R.string.stop_gui_failed)
                        }
                    }
                })
            }
        }
    }

    /**
     * 登录成功 跳转到主页
     */
    override fun onSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * 开始进度条
     */
    override fun startRefreshAnim() {
        if (  !mProgress.isShowing) mProgress.show()
    }

    /**
     * 结束进度条
     */
    override fun stopRefreshAnim() {
        if (  mProgress.isShowing) mProgress.dismiss()
    }

    override fun onDestroy() {
        //关闭窗口，避免窗口溢出
        stopRefreshAnim()
        //释放资源 停止所有请求
        mPresenter.onDestory()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //关闭GUI
        mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,null)
    }

    override fun networkStatus(isConnected: Boolean) {
        //按钮可点击
        tvNetwork.isEnabled=true
        if(isConnected){
            tvNetworkHint.setTextColor(resources.getColor(R.color.connect_success))
            tvNetworkHint.setText(R.string.check_network_normal)

        }else{
            tvNetworkHint.setTextColor(resources.getColor(R.color.red_text))
            tvNetworkHint.setText(R.string.check_network_innormal)
        }
    }
}

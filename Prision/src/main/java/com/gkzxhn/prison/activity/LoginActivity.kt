package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.UpdateDialog
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.LoginPresenter
import com.gkzxhn.prison.view.ILoginView
import com.starlight.mobile.android.lib.util.CommonHelper
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_password as etPassword
import kotlinx.android.synthetic.main.login_layout.loign_layout_et_username as etAccount

/**登录
 * Created by Raleigh.Luo on 17/3/29.
 */

class LoginActivity : SuperActivity(), ILoginView {
    override fun updateVersion(version: VersionEntity) {
        //新版本
        val newVersion = version.versionCode
        val pm = packageManager
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS)
            val currentVersion = packageInfo.versionCode//当前App版本
            val lastIgnoreVersion = mPresenter.getSharedPreferences().getInt(Constants.LAST_IGNORE_VERSION, 0)
            var isIgoreVersion = lastIgnoreVersion == newVersion//若是已忽略的版本，则不弹出升级对话框
            if (version.isForce == 1) isIgoreVersion = false
            if (newVersion > currentVersion && !isIgoreVersion) {//新版本大于当前版本，则弹出更新下载到对话框
                //版本名
                val versionName = version.versionName
                // 下载地址
                val downloadUrl = version.downloadUrl
                //是否强制更新
                val isForceUpdate = version.isForce
                if (updateDialog == null) updateDialog = UpdateDialog(this)
                updateDialog?.setForceUpdate(isForceUpdate == 1)
                updateDialog?.setDownloadInfor(versionName ?: "", newVersion, downloadUrl
                        ?: "", version.description ?: "")
                updateDialog?.show()//显示对话框
            }

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private var updateDialog: UpdateDialog? = null
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

    override fun onResume() {
        super.onResume()
        //请求版本信息
        mPresenter.requestVersion()
    }

    /**
     * 初始化
     */
    private fun init() {
        //显示记住的密码
        val preferences = getSharedPreferences(Constants.TEMP_TABLE, Activity.MODE_PRIVATE)
        etAccount.setText(preferences.getString(Constants.USER_ACCOUNT, ""))
        etPassword.setText(preferences.getString(Constants.USER_PASSWORD, ""))
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
        when (view.id) {
            R.id.loign_layout_btn_login -> {
                val account = etAccount.text.toString().trim()
                val password = etPassword.text.toString().trim()
                when {
                    account.isEmpty() -> //账号为空
                        showToast(getString(R.string.please_input) + getString(R.string.account))
                    password.isEmpty() -> //密码为空
                        showToast(getString(R.string.please_input) + getString(R.string.password))
                    else -> { //登录
                        //关闭虚拟键盘
                        CommonHelper.clapseSoftInputMethod(this)
                        //登录
                        mPresenter.login(account, password)

                    }
                }
            }
            R.id.login_layout_tv_check_network_title -> {//跳转到检测网络界面
                startActivity(Intent(this, NetworkActivity::class.java))
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
        if (!mProgress.isShowing) mProgress.show()
    }

    /**
     * 结束进度条
     */
    override fun stopRefreshAnim() {
        if (mProgress.isShowing) mProgress.dismiss()
    }

    override fun onDestroy() {
        //关闭窗口，避免窗口溢出
        stopRefreshAnim()
        //释放资源 停止所有请求
        mPresenter.onDestory()
        if (updateDialog?.isShowing == true) updateDialog?.dismiss()
        super.onDestroy()
    }


    override fun networkStatus(isConnected: Boolean) {

    }
}

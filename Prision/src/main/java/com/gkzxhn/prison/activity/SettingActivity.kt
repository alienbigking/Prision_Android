package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup

import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.AsynHelper
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.customview.UpdateDialog
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.SettingPresenter
import com.gkzxhn.prison.view.ISettingView
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_check_update_hint
as tvCheckUpdateHint
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_version
as tvCurrentVersion


/**系统设置
 * Created by Raleigh.Luo on 17/4/12.
 */

class SettingActivity : SuperActivity(),ISettingView {
        //请求presenter
    private lateinit var mPresenter: SettingPresenter
    //app更新对话框
    private lateinit var updateDialog: UpdateDialog
    //退出提示对话框
    private lateinit var mExitDialog: CustomDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        //版本更新 显示当前版本
        val pm = packageManager
        try {
            val packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS)
            tvCurrentVersion.text = getString(R.string.current_version) + "V" + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        //初始化Presenter
        mPresenter = SettingPresenter(this, this)
        //请求免费会见次数
        // 初始化退出对话框
        mExitDialog = CustomDialog(this)
        //设置显示内容
        mExitDialog.content=getString(R.string.exit_account_hint)
        //设置取消文字
        mExitDialog.cancelText= getString(R.string.cancel)
        //设置确定文字
        mExitDialog.confirmText= getString(R.string.ok)
        //设置监听器
        mExitDialog.onClickListener=View.OnClickListener { view ->
            if (view.id == R.id.custom_dialog_layout_tv_confirm) {
                //退出
                GKApplication.instance.loginOff()
                finish()
            }
        }
        //初始化更新对话框
        updateDialog = UpdateDialog(this)
        //注册接收器
        registerReceiver()
    }

    /**
     * 点击监听
     */
    fun onClickListener(view: View) {
        when (view.id) {
            R.id.setting_layout_v_video_setting -> {
                //终端设置 跳转界面
                val intent = Intent(this, ConfigActivity::class.java)
                startActivityForResult(intent, Constants.EXTRA_CODE)
            }
            R.id.setting_layout_v_check_update -> {
//                //版本更新
                tvCheckUpdateHint.setText(R.string.check_updating)
                //请求更新
                mPresenter.requestVersion()
            }
            R.id.setting_layout_v_exit ->{ //退出账号
                if (!mExitDialog.isShowing)
                    mExitDialog.show()
            }
            R.id.common_head_layout_iv_left -> { //返回
                finish()
            }
            R.id.setting_layout_v_free_video ->{//免费会见
                startActivityForResult(Intent(this, CallFreeActivity::class.java), Constants.EXTRAS_CODE)
            }
            R.id.setting_layout_v_check_network ->{//检查网络
                startActivity(Intent(this,NetworkActivity::class.java))
            }
        }
    }

    /**
     * 广播接收器
     */
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.NIM_KIT_OUT) {
                GKApplication.instance.loginOff()
                //云信挤出
                finish()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if(resultCode == Activity.RESULT_OK ){
                if (requestCode == Constants.EXTRA_CODE ) {//修改终端信息成功
                    showToast(R.string.alter_terminal_account_success)
                }
            }

        }catch (e:Exception){}
    }

    /**
     * 成功获取版本信息
     */
    override fun updateVersion(version: VersionEntity?) {
        if(version!=null) {
            //新版本
            val newVersion = version.versionCode
            val pm = packageManager
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = pm.getPackageInfo(packageName,
                        PackageManager.GET_CONFIGURATIONS)
                val currentVersion = packageInfo.versionCode//当前App版本
                //新版本大于当前版本
                if (newVersion > currentVersion) {
                    //版本名
                    val versionName = version.versionName
                    // 下载地址
                    val downloadUrl = version.downloadUrl
                    //是否强制更新
                    updateDialog.setForceUpdate(version.isForce==1)
                    updateDialog.setDownloadInfor(versionName ?: "", newVersion, downloadUrl ?: "",version.description?:"")
                    updateDialog.show()//显示对话框
                    tvCheckUpdateHint.text = getString(R.string.new_version_colon) + versionName
                } else {//无版本更新
                    tvCheckUpdateHint.setText(R.string.has_last_version)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }else{//无版本更新
            tvCheckUpdateHint.setText(R.string.has_last_version)
        }

    }


    override fun startRefreshAnim() {

    }

    override fun stopRefreshAnim() {

    }

    override fun onDestroy() {
        mPresenter.onDestory()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        if ( mExitDialog.isShowing) mExitDialog.dismiss()
        if (updateDialog.isShowing) updateDialog.dismiss()
        super.onDestroy()
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }
    override fun networkStatus(isConnected: Boolean) {
    }

}

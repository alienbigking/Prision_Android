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
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.customview.UpdateDialog
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.SettingPresenter
import com.gkzxhn.prison.view.ISettingView
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_update_hint
as tvUpdateHint
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_call_free_hint
as tvCallFreeTime
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_check_network_hint
as tvNetworkHint
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_check_network
as tvNetwork
import kotlinx.android.synthetic.main.setting_layout.setting_layout_rg_usb
as mRadioGroup



/**系统设置
 * Created by Raleigh.Luo on 17/4/12.
 */

class SettingActivity : SuperActivity(), ISettingView {


    //请求presenter
    private lateinit var mPresenter: SettingPresenter
    //app更新对话框
    private lateinit var updateDialog: UpdateDialog
    //退出提示对话框
    private lateinit var mExitDialog: CustomDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)
        //设置USB录播选择监听器
        mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener)
        //版本更新 显示当前版本
        val pm = packageManager
        try {
            val packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS)
            tvUpdateHint.text = getString(R.string.current_version) + "v" + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        //初始化Presenter
        mPresenter = SettingPresenter(this, this)
        //显示已设置的USB录播
        val isOpenUsb = mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD, true)
        mRadioGroup.check(if (isOpenUsb) R.id.setting_layout_rb_usb_open else R.id.setting_layout_rb_usb_close)
        //请求免费会见次数
        mPresenter.requestFreeTime()
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
            R.id.setting_layout_tv_end_setting -> {
                //终端设置 跳转界面
                val intent = Intent(this, ConfigActivity::class.java)
                startActivityForResult(intent, Constants.EXTRA_CODE)
            }
            R.id.setting_layout_tv_update -> {
//                updateDialog.show()
//                //版本更新
                tvUpdateHint.setText(R.string.check_updating)
                //请求更新
                mPresenter.requestVersion()
            }
            R.id.setting_layout_tv_logout ->{ //退出账号

                if (!mExitDialog.isShowing)
                    mExitDialog.show()
            }
            R.id.common_head_layout_iv_left -> { //返回
                finish()
            }
            R.id.setting_layout_tv_call_free ->{//免费会见
                startActivityForResult(Intent(this, CallFreeActivity::class.java), Constants.EXTRAS_CODE)
            }
            R.id.setting_layout_tv_check_network ->{
                tvNetworkHint.setTextColor(resources.getColor(R.color.common_gray_title_color))
                tvNetworkHint.setText(R.string.check_network_ing)
                //按钮不可点击
                tvNetwork.isEnabled=false
                //关闭GUI
                mPresenter.checkNetworkStatus()
            }
        }

    }

    /**
     * 加速完成
     */
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
    //开启／关闭Usb录屏监听
    private val mOnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.setting_layout_rb_usb_close//关闭USB录播 保存
            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, false).apply()
            R.id.setting_layout_rb_usb_open//开启录播 保存
            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, true).apply()
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
                } else if (requestCode == Constants.EXTRAS_CODE) {//免费呼叫
                    mPresenter.requestFreeTime()
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
                    updateDialog.setDownloadInfor(versionName ?: "", newVersion, downloadUrl ?: "")
                    updateDialog.show()//显示对话框
                    tvUpdateHint.text = getString(R.string.new_version_colon) + versionName
                } else {//无版本更新
                    tvUpdateHint.setText(R.string.has_last_version)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }else{//无版本更新
            tvUpdateHint.setText(R.string.has_last_version)
        }

    }

    /**
     *  成功获取到免费会见次数
     */
    override fun updateFreeTime(time: Int) {
        tvCallFreeTime.text = getString(R.string.leave) + time + getString(R.string.time)
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

    override fun onResume() {
        super.onResume()
        tvCallFreeTime.text = getString(R.string.leave) +
                mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME, 0) + getString(R.string.time)
        //关闭GUI
        mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,null)
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }
}

package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.customview.UpdateDialog
import com.gkzxhn.prison.entity.MeetingEntity
import com.gkzxhn.prison.entity.VersionEntity
import com.gkzxhn.prison.presenter.MainPresenter
import com.gkzxhn.prison.presenter.SettingPresenter
import com.gkzxhn.prison.view.IMainView
import com.gkzxhn.prison.view.ISettingView
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_update_hint
as tvUpdateHint
import kotlinx.android.synthetic.main.setting_layout.setting_layout_tv_call_free_hint
as tvCallFreeTime
import kotlinx.android.synthetic.main.setting_layout.setting_layout_rg_usb
as mRadioGroup

/**
 * Created by Raleigh.Luo on 17/4/12.
 */

class SettingActivity : SuperActivity(), ISettingView {
    private var mResultCode = Activity.RESULT_CANCELED
    private lateinit var mPresenter: SettingPresenter
    private lateinit var updateDialog: UpdateDialog
    private lateinit var mCustomDialog: CustomDialog
    private val mOnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.setting_layout_rb_usb_close//关闭
            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, false).apply()
            R.id.setting_layout_rb_usb_open//开启
            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, true).apply()
        }
    }
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.NIM_KIT_OUT) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_layout)

        mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener)
        val pm = packageManager
        try {
            val packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_CONFIGURATIONS)
            tvUpdateHint.text = getString(R.string.current_version) + "v" + packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        mPresenter = SettingPresenter(this, this)
        val isOpenUseb = mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD, true)
        mRadioGroup.check(if (isOpenUseb) R.id.setting_layout_rb_usb_open else R.id.setting_layout_rb_usb_close)
        tvCallFreeTime.text = getString(R.string.leave) +
                mPresenter.getSharedPreferences().getInt(Constants.CALL_FREE_TIME, 0) + getString(R.string.time)
        mPresenter.requestFreeTime()
        mCustomDialog = CustomDialog(this, View.OnClickListener { view ->
            if (view.id == R.id.custom_dialog_layout_tv_confirm) {
                GKApplication.instance.loginOff()
                finish()
            }
        })
        mCustomDialog.setContent(getString(R.string.exit_account_hint),
                getString(R.string.cancel), getString(R.string.ok))
        updateDialog = UpdateDialog(this)
        registerReceiver()
    }

    fun onClickListener(view: View) {
        when (view.id) {
            R.id.setting_layout_tv_end_setting -> {
                val intent = Intent(this, ConfigActivity::class.java)
                startActivityForResult(intent, Constants.EXTRA_CODE)
            }
            R.id.setting_layout_tv_update -> {
                tvUpdateHint.setText(R.string.check_updating)
                mPresenter.requestVersion()
            }
            R.id.setting_layout_tv_logout -> if (mCustomDialog != null && !mCustomDialog.isShowing)
                mCustomDialog.show()
            R.id.common_head_layout_iv_left -> {
                setResult(mResultCode)
                finish()
            }
            R.id.setting_layout_tv_call_free -> startActivityForResult(Intent(this, CallFreeActivity::class.java), Constants.EXTRAS_CODE)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (requestCode == Constants.EXTRA_CODE && resultCode == Activity.RESULT_OK) {//修改终端信息成功
                mResultCode = Activity.RESULT_OK
                showToast(R.string.alter_terminal_account_success)
            } else if (requestCode == Constants.EXTRAS_CODE && resultCode == Activity.RESULT_OK) {//免费呼叫
                mPresenter.requestFreeTime()
            }
        }catch (e:Exception){}
    }

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
                if (newVersion > currentVersion) {//新版本大于当前版本
                    //版本名
                    val versionName = version.versionName
                    // 下载地址
                    val downloadUrl = version.downloadUrl
                    //是否强制更新
                    updateDialog.setForceUpdate(version.isForce)
                    updateDialog.setDownloadInfor(versionName ?: "", newVersion, downloadUrl ?: "")
                    updateDialog.show()//显示对话框
                    tvUpdateHint.text = getString(R.string.new_version_colon) + versionName
                } else {
                    tvUpdateHint.setText(R.string.has_last_version)
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }else{
            tvUpdateHint.setText(R.string.has_last_version)
        }

    }

    override fun updateFreeTime(time: Int) {
        tvCallFreeTime.text = getString(R.string.leave) + time + getString(R.string.time)
    }

    override fun startRefreshAnim() {

    }

    override fun stopRefreshAnim() {

    }

    override fun onDestroy() {
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        if (mCustomDialog != null && mCustomDialog.isShowing) mCustomDialog.dismiss()
        if (updateDialog != null && updateDialog.isShowing) updateDialog.dismiss()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//点击返回键，返回到主页
            setResult(mResultCode)
            finish()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (updateDialog != null && updateDialog.isShowing) updateDialog.measureWindow()
        if (mCustomDialog != null && mCustomDialog.isShowing) mCustomDialog.measureWindow()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (updateDialog != null && updateDialog.isShowing) updateDialog.measureWindow()
        if (mCustomDialog != null && mCustomDialog.isShowing) mCustomDialog.measureWindow()

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

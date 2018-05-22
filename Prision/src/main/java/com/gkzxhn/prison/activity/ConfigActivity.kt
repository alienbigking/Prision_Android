package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.starlight.mobile.android.lib.util.CommonHelper
import kotlinx.android.synthetic.main.config_layout.config_layout_et_account
as etAccount
import kotlinx.android.synthetic.main.config_layout.config_layout_sp_rate
as mSpinner
import kotlinx.android.synthetic.main.config_layout.config_layout_sp_protocol
as mSpinnerProtocol
import kotlinx.android.synthetic.main.config_layout.config_layout_et_config_time
as etTime
import kotlinx.android.synthetic.main.config_layout.config_layout_ch_head
as chHead


/**终端配置
 * Created by Raleigh.Luo on 17/4/12.
 */

class ConfigActivity : SuperActivity() {
    //码率数组值
    private lateinit var mRateArray: Array<String>
    // 码率
    private var mRate: String? = null
    //协议 h323／sip
    private var protocol: String? = null
    private lateinit var preferences: SharedPreferences
    private var mAccount: String=""
    //限制的时间
    private var mTimeLimit: Long = 0
    private val DEFAULTACC = "*******************"
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.NIM_KIT_OUT) {
                //云信被挤出
                finish()
            }
        }
    }
    //设置USB录播选择监听器
//    mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener)
//    //显示已设置的USB录播
//    val isOpenUsb = mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD, true)
//    mRadioGroup.check(if (isOpenUsb) R.id.setting_layout_rb_usb_open else R.id.setting_layout_rb_usb_close)

    //    //开启／关闭Usb录屏监听
//    private val mOnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
//        when (checkedId) {
//            R.id.setting_layout_rb_usb_close//关闭USB录播 保存
//            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, false).apply()
//            R.id.setting_layout_rb_usb_open//开启录播 保存
//            -> mPresenter.getSharedPreferences().edit().putBoolean(Constants.IS_OPEN_USB_RECORD, true).apply()
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config_layout)
        //初始化
        init()
        //注册广播接收器
        registerReceiver()
    }

    private fun init() {

        //协议初始化
        val protocolArray = resources.getStringArray(R.array.protocol)
        val protocolAdapter = ArrayAdapter(this,
                R.layout.spinner_item, protocolArray)
        mSpinnerProtocol.adapter = protocolAdapter
        mSpinnerProtocol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                protocol = protocolArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        //码率初始化
        mRateArray = resources.getStringArray(R.array.rate_array)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, mRateArray)
        mSpinner.adapter = adapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                mRate = mRateArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        //dropdown显示位置调整
        mSpinner.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if( mSpinner.dropDownVerticalOffset==0) {
                    //设置位移为mSpinner显示的高度
                    mSpinner.dropDownVerticalOffset = mSpinner.measuredHeight
                    mSpinnerProtocol.dropDownVerticalOffset = mSpinnerProtocol.measuredHeight
                }
                return true
            }
        })
        var index = 1
        preferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        //获取终端号
        mAccount = String.format("%s##%s##%s",preferences.getString(Constants.TERMINAL_ROOM_NUMBER, ""),
                preferences.getString(Constants.TERMINAL_HOST_PASSWORD, ""), preferences.getString(Constants.TERMINAL_GUEST_PASSWORD, ""))
        //初始化标题
        chHead.getTvTitle().setText(String.format("%s(%s)",getString(R.string.end_setting),preferences.getString(Constants.USER_ACCOUNT, "")))
        //获取
        mTimeLimit = preferences.getLong(Constants.TIME_LIMIT, 20L)
        etTime.setText(mTimeLimit.toString())
        if (!mAccount.isEmpty()) {//终端号不为空
            etAccount.setText(DEFAULTACC)
            for (i in mRateArray.indices) {
                val mRate = mRateArray[i]
                if (mRate ==GKApplication.instance.terminalRate.toString()) {
                    index = i
                    break
                }
            }
            for (i in protocolArray.indices) {
                val protocol = preferences.getString(Constants.PROTOCOL, "h323")
                if (protocolArray[i] == protocol) {
                    mSpinnerProtocol.setSelection(i)
                    break
                }
            }
        }
        mRate = mRateArray[index]
        mSpinner.setSelection(index)
    }

    /**
     * 响应点击事件
     */
    fun onClickListener(view: View) {
        //关闭虚拟键盘
        CommonHelper.clapseSoftInputMethod(this)
        when (view.id) {
            R.id.common_head_layout_iv_left -> {//左上角返回
                finish()
            }
            R.id.config_layout_btn_save -> {//保存
                val account = etAccount.text.toString().trim()
                val timeStr = etTime.text.toString().trim()
                if (account != DEFAULTACC) {
                    mAccount = account
                }
                if (TextUtils.isEmpty(mAccount)) {
                    showToast(R.string.please_input_terminal_account)
                } else {
                    //修改账号  保存到sharepreference中
                    val editor = preferences.edit()
                    val array=mAccount.split("##")
                    editor.putString(Constants.TERMINAL_ROOM_NUMBER, if(array.size>0)array[0] else "")
                    editor.putString(Constants.TERMINAL_HOST_PASSWORD, if(array.size>1)array[1] else "")
                    editor.putString(Constants.TERMINAL_GUEST_PASSWORD, if(array.size>2)array[2] else "")
                    editor.putInt(Constants.TERMINAL_RATE, Integer.valueOf(mRate))
                    editor.putString(Constants.PROTOCOL, protocol)
                    editor.putLong(Constants.TIME_LIMIT,timeStr.toLong())
                    editor.apply()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            R.id.config_layout_iv_terminal_account_clear ->{
                etAccount.setText("")//置空
            }
        }
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        super.onDestroy()
    }
}

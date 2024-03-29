package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.text.InputType
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
import kotlinx.android.synthetic.main.config_layout.config_layout_et_guest_password as etGuestPassword
import kotlinx.android.synthetic.main.config_layout.config_layout_et_host_password as etHostPassword
import kotlinx.android.synthetic.main.config_layout.config_layout_et_meeting_number as etMeettingNumber
import kotlinx.android.synthetic.main.config_layout.config_layout_rg_usb as rgUSB
import kotlinx.android.synthetic.main.config_layout.config_layout_sp_protocol as mSpinnerProtocol
import kotlinx.android.synthetic.main.config_layout.config_layout_sp_rate as mSpinner
import kotlinx.android.synthetic.main.config_layout.config_layout_tv_account as tvAccount


/**
 * 终端配置 OK
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
    //是否开启USB录制
    private var isOpenUsb = false
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.NIM_KIT_OUT) {
                //云信被挤出
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config_layout)
        InputType.TYPE_CLASS_NUMBER
        preferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        //初始化
        init()
        initSpinner()
        //注册广播接收器
        registerReceiver()
    }


    private fun init() {
        //设置USB录播选择监听器
        rgUSB.setOnCheckedChangeListener(mOnCheckedChangeListener)
        //显示已设置的USB录播
        isOpenUsb = preferences.getBoolean(Constants.IS_OPEN_USB_RECORD, false)
        rgUSB.check(if (isOpenUsb) R.id.config_layout_rb_usb_open else R.id.config_layout_rb_usb_close)
        //显示当前终端号
        tvAccount.text = preferences.getString(Constants.USER_ACCOUNT, "")
        //默认都显示*
        etGuestPassword.setText(preferences.getString(Constants.TERMINAL_GUEST_PASSWORD, ""))
        etHostPassword.setText(preferences.getString(Constants.TERMINAL_HOST_PASSWORD, ""))
        etMeettingNumber.setText(preferences.getString(Constants.TERMINAL_ROOM_NUMBER, ""))
    }

    /**
     * 初始化下拉选择框
     */
    private fun initSpinner() {
        //协议初始化
        val protocolArray = resources.getStringArray(R.array.protocol)
        val protocolAdapter = ArrayAdapter(this,
                R.layout.spinner_item, protocolArray)
        mSpinnerProtocol.adapter = protocolAdapter
        mSpinnerProtocol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                CommonHelper.clapseSoftInputMethod(this@ConfigActivity)
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
                CommonHelper.clapseSoftInputMethod(this@ConfigActivity)
                mRate = mRateArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        //dropdown显示位置调整
        mSpinner.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (mSpinner.dropDownVerticalOffset == 0) {
                    //设置位移为mSpinner显示的高度
                    mSpinner.dropDownVerticalOffset = mSpinner.measuredHeight
                    mSpinnerProtocol.dropDownVerticalOffset = mSpinnerProtocol.measuredHeight
                }
                return true
            }
        })

        var index = 1
        for (i in mRateArray.indices) {
            val mRate = mRateArray[i]
            if (mRate == GKApplication.instance.terminalRate.toString()) {
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

        mRate = mRateArray[index]
        mSpinner.setSelection(index)
    }

    //开启／关闭Usb录屏监听
    private val mOnCheckedChangeListener = RadioGroup.OnCheckedChangeListener { group, checkedId ->
        when (checkedId) {
            R.id.config_layout_rb_usb_open//开启录播 保存
            -> isOpenUsb = true
            R.id.config_layout_rb_usb_close//关闭USB录播 保存
            -> isOpenUsb = false
        }
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
                val meettingNumber = etMeettingNumber.text.toString().trim()
                val hostPassword = etHostPassword.text.toString().trim()
                val guestPassword = etGuestPassword.text.toString().trim()

                if (TextUtils.isEmpty(meettingNumber)) {
                    showToast(R.string.please_input_meeting_numbers)
                } else if (TextUtils.isEmpty(hostPassword)) {
                    showToast(R.string.please_input_host_password)
                } else if (TextUtils.isEmpty(guestPassword)) {
                    showToast(R.string.please_input_guest_password)
                } else {
                    //单元测试 延迟加载
                    setIdleNow(true)
                    //修改账号  保存到sharepreference中
                    val editor = preferences.edit()
                    editor.putString(Constants.TERMINAL_ROOM_NUMBER, meettingNumber)
                    editor.putString(Constants.TERMINAL_HOST_PASSWORD, hostPassword)
                    editor.putString(Constants.TERMINAL_GUEST_PASSWORD, guestPassword)
                    editor.putInt(Constants.TERMINAL_RATE, Integer.valueOf(mRate))
                    editor.putString(Constants.PROTOCOL, protocol)
                    editor.putBoolean(Constants.IS_OPEN_USB_RECORD, isOpenUsb)
                    editor.apply()
                    //单元测试 释放延迟加载
                    setIdleNow(false)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
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

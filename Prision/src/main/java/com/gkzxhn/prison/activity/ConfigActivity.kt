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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.starlight.mobile.android.lib.util.CommonHelper
import kotlinx.android.synthetic.main.config_layout.config_layout_et_account
as etAccount
import kotlinx.android.synthetic.main.config_layout.config_layout_sp_rate
as mSpinner
import kotlinx.android.synthetic.main.config_layout.sp_protocol
as mSp_protocol
import kotlinx.android.synthetic.main.config_layout.et_config_time
as etTime
/**
 * Created by Raleigh.Luo on 17/4/12.
 */

class ConfigActivity : SuperActivity() {
    private lateinit var mRateArray: Array<String>
    private var mRate: String? = null
    private var protocol: String? = null
    private lateinit var mProgress: ProgressDialog
    private lateinit var preferences: SharedPreferences
    private val DOWN_TIME: Long = 20000//倒计时 20秒
    private var mAccount: String=""
    private var mTimeLimit: Long = 0
    private val DEFAULTACC = "*******************"
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopRefreshAnim()
            if (intent.action == Constants.NIM_KIT_OUT) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config_layout)
        init()
        registerReceiver()
    }

    private fun init() {
        mProgress = ProgressDialog.show(this, null, getString(R.string.please_waiting))
        stopRefreshAnim()
        mRateArray = resources.getStringArray(R.array.rate_array)
        val adapter = ArrayAdapter(this,
                R.layout.spinner_item, mRateArray)
        val protocolArray = resources.getStringArray(R.array.protocol)
        val protocolAdapter = ArrayAdapter(this,
                R.layout.spinner_item, protocolArray)
        mSp_protocol.adapter = protocolAdapter
        mSp_protocol.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                protocol = protocolArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        mSpinner.adapter = adapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                mRate = mRateArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        var index = 1
        preferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        mAccount = preferences.getString(Constants.TERMINAL_ACCOUNT, "")
        mTimeLimit = preferences.getLong(Constants.TIME_LIMIT, 20L)
        etTime.setText(mTimeLimit.toString())
        if (mAccount != null && mAccount.length > 0) {
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
                    mSp_protocol.setSelection(i)
                    break
                }
            }
        }
        mRate = mRateArray[index]
        mSpinner.setSelection(index)
    }

    fun onClickListener(view: View) {
        CommonHelper.clapseSoftInputMethod(this)
        when (view.id) {
            R.id.common_head_layout_iv_left -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
            R.id.config_layout_btn_save -> {
                val account = etAccount.text.toString().trim { it <= ' ' }
                val timeStr = etTime.text.toString().trim { it <= ' ' }
                if (account != DEFAULTACC) {
                    mAccount = account
                }
                if (TextUtils.isEmpty(mAccount)) {
                    showToast(R.string.please_input_terminal_account)
                } else {
                    //退修改账号
                    val editor = preferences.edit()
                    editor.putString(Constants.TERMINAL_ACCOUNT, mAccount)
                    editor.putInt(Constants.TERMINAL_RATE, Integer.valueOf(mRate))
                    editor.putString(Constants.PROTOCOL, protocol)
                    editor.putLong(Constants.TIME_LIMIT,timeStr.toLong())
                    editor.apply()
                    showToast("修改成功")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    fun startRefreshAnim() {
        if ( !mProgress.isShowing) mProgress.show()
    }

    fun stopRefreshAnim() {
        if ( mProgress.isShowing) mProgress.dismiss()
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
        if (mProgress.isShowing) mProgress.dismiss()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        super.onDestroy()
    }

}

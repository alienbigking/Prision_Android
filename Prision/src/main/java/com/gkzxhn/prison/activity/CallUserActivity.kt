package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.customview.ShowTerminalDialog
import com.gkzxhn.prison.presenter.CallUserPresenter
import com.gkzxhn.prison.view.ICallUserView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

import org.json.JSONException
import org.json.JSONObject

import kotlinx.android.synthetic.main.i_common_loading_layout.common_loading_layout_tv_load
as tvLoading

import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_iv_card_01
as ivCard01
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_iv_card_02
as ivCard02

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

class CallUserActivity : SuperActivity(), ICallUserView {
    private lateinit var mPresenter: CallUserPresenter
    private lateinit var mCustomDialog: CustomDialog
    private lateinit var mShowTerminalDialog: ShowTerminalDialog
    private lateinit var mProgress: ProgressDialog
    private var preferences: SharedPreferences? = null
    private lateinit var phone: String
    private lateinit var nickName: String
    private lateinit var id: String
    private var isClickCall = false//是否点击了呼叫按钮
    private var mAccount: String? = null
    private var mIDWidth: Int = 0
    private var mCallRequestCode = Constants.EXTRA_CODE


    /**
     * 加载动画
     */
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == Constants.START_REFRESH_UI) {//开始加载动画
                tvLoading.visibility = View.VISIBLE
                if (!tvLoading.isPlaying) {

                    tvLoading.showAndPlay()
                }
            } else if (msg.what == Constants.STOP_REFRESH_UI) {//停止加载动画
                if (tvLoading.isPlaying || tvLoading.isShown) {
                    tvLoading.hideAndStop()
                    tvLoading.visibility = View.GONE
                }
            }
        }
    }
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopRefreshAnim()
            if (intent.action == Constants.ONLINE_SUCCESS_ACTION) {
                //对方在线，进行呼叫
                if (isClickCall) {
                    mCallRequestCode = Constants.EXTRA_CODE
                    stopProgress()
                    mTimer.cancel()
                    mPresenter.dial(mAccount?:"")
                }
            } else if (intent.action == Constants.ONLINE_FAILED_ACTION) {
            } else if (intent.action == Constants.NIM_KIT_OUT) {
                finish()
            }
        }
    }

    private val DOWN_TIME: Long = 10000//倒计时 10秒
    private val mTimer = object : CountDownTimer(DOWN_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            //            long second = millisUntilFinished / 1000;
        }

        override fun onFinish() {
            stopVConfVideo()
        }
    }

    private val TAG = CallUserActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_user_layout)
        init()
        registerReceiver()
    }

    private fun init() {
        mPresenter = CallUserPresenter(this, this)
        id = intent.getStringExtra(Constants.EXTRA)
        phone = intent.getStringExtra(Constants.EXTRAS)
        nickName = intent.getStringExtra(Constants.EXTRA_TAB)
        mProgress = ProgressDialog.show(this, null, getString(R.string.check_other_status))
        mProgress.setCanceledOnTouchOutside(true)
        stopProgress()
        preferences = mPresenter.getSharedPreferences()
        mAccount = preferences?.getString(Constants.TERMINAL_ACCOUNT, "")
        mShowTerminalDialog = ShowTerminalDialog(this)
        if (TextUtils.isEmpty(mAccount)) {
            if (!mShowTerminalDialog.isShowing) mShowTerminalDialog.show()
        }
        mCustomDialog = CustomDialog(this, View.OnClickListener { view ->
            if (view.id == R.id.custom_dialog_layout_tv_confirm) {
                online(mAccount)
            }
        })
        val viewTreeObserver = ivCard01.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ivCard01.viewTreeObserver.removeGlobalOnLayoutListener(this)
                mIDWidth = ivCard01.measuredWidth
                val layoutParams = ivCard01.layoutParams as LinearLayout.LayoutParams
                layoutParams.height = (mIDWidth / ID_RATIO).toInt()
                layoutParams.width = mIDWidth
                ivCard01.layoutParams = layoutParams
                ivCard02.layoutParams = layoutParams
            }
        })
        mPresenter.request(phone)//请求详情
    }


    fun onClickListener(view: View) {
        when (view.id) {
            R.id.common_head_layout_iv_left -> finish()
            R.id.call_user_layout_bt_call -> {
                val account = preferences?.getString(Constants.TERMINAL_ACCOUNT, "")
                online(account)
            }
        }
    }

    private fun online(account: String?) {
        isClickCall = true
        if (account != null && account.length > 0) {
            if (mPresenter.checkStatusCode() == StatusCode.LOGINED) {
                startProgress()
                //发送云信消息，检测家属端是否已经准备好可以呼叫
                val notification = CustomNotification()
                val accid = mPresenter.entity?.accid
                notification.sessionId = accid
                notification.sessionType = SessionTypeEnum.P2P
                // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
                // 这里以类型 “1” 作为“正在输入”的状态通知。
                val json = JSONObject()
                try {
                    json.put("code", -1)
                    //                        json.put("msg", account);
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                notification.content = json.toString()
                NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
                mTimer.start()
            } else {
                showToast(R.string.yunxin_offline)
            }
        } else {
            if (mShowTerminalDialog == null) {
                mShowTerminalDialog = ShowTerminalDialog(this)
            }
            if (!mShowTerminalDialog.isShowing) mShowTerminalDialog.show()
        }
    }

    override fun onSuccess() {
        val img_urls = mPresenter.entity?.imageUrl?.split("|")
        val options = DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build()
        img_urls?.let {
            ivCard01.post { ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[0], ivCard01, options) }
            ivCard02.post { ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[1], ivCard02, options) }
            findViewById(R.id.call_user_layout_bt_call).isEnabled = true
            val editor = mPresenter.getSharedPreferences().edit()
            editor.putString(Constants.OTHER_CARD + 1, img_urls[0])
            editor.putString(Constants.OTHER_CARD + 2, img_urls[1])
            editor.putString(Constants.OTHER_CARD + 3, img_urls[2])
            editor.putString(Constants.EXTRA, mPresenter.entity?.accid)
            editor.putString(Constants.EXTRAS, id)
            editor.commit()
        }
    }

    /**
     * 拨号成功 直接进入呼叫界面
     * @param password
     */
    override fun dialSuccess(password: String) {
        stopProgress()
        val intent = Intent(this, CallZiJingActivity::class.java)
        intent.action=getIntent().action
        if (password != null) {
            intent.putExtra(Constants.ZIJING_PASSWORD, password)
        }
        startActivityForResult(intent, mCallRequestCode)
    }

    override fun startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI)
    }

    override fun stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI)
    }

    fun startProgress() {
        if (!mProgress.isShowing) mProgress.show()
    }

    fun stopProgress() {
        if (mProgress.isShowing) mProgress.dismiss()
    }

    override fun onResume() {
        super.onResume()
        if (mShowTerminalDialog.isShowing) mShowTerminalDialog.measureWindow()
        if (  mCustomDialog.isShowing) mCustomDialog.measureWindow()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (  mShowTerminalDialog.isShowing) mShowTerminalDialog.measureWindow()
        if (  mCustomDialog.isShowing) mCustomDialog.measureWindow()

    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ONLINE_FAILED_ACTION)
        intentFilter.addAction(Constants.ONLINE_SUCCESS_ACTION)
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        isClickCall = false
    }

    fun stopVConfVideo() {
        isClickCall = false
        stopProgress()
        if (mCustomDialog != null) {
            mCustomDialog.setContent(getString(R.string.other_offline),
                    getString(R.string.cancel), getString(R.string.call_back))
            if (!mCustomDialog.isShowing) mCustomDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.EXTRA_CODE && resultCode == Activity.RESULT_CANCELED) {
            if (data != null) {
                val call_again = data.getBooleanExtra(Constants.CALL_AGAIN, false)
                val reason = data.getStringExtra(Constants.END_REASON)
                if (call_again) {
                    //换协议呼叫
                    var protocol = preferences?.getString(Constants.PROTOCOL, "h323")
                    if ("h323" == protocol) {
                        protocol = "sip"
                    } else {
                        protocol = "h323"
                    }
                    val edit = preferences?.edit()
                    edit?.putString(Constants.PROTOCOL, protocol)
                    edit?.apply()
                    Log.i(TAG, "protocol : " + protocol)
                    showToast("呼叫失败,原因:" + reason + "/n切换成" + protocol + "协议重新进行呼叫...")
                    mCallRequestCode = Constants.EXTRAS_CODE
                    mPresenter.dial(mAccount?:"")
                }
            }
        }
    }

    companion object {
        private val ID_RATIO = 856f / 540f
    }
}

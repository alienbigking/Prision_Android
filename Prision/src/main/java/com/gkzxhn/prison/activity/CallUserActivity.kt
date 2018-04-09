package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.presenter.CallUserPresenter
import com.gkzxhn.prison.view.ICallUserView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.nostra13.universalimageloader.core.ImageLoader

import org.json.JSONObject
import javax.security.auth.callback.Callback

import kotlinx.android.synthetic.main.i_common_loading_layout.common_loading_layout_tv_load
as tvLoading

import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_iv_card_01
as ivCard01
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_iv_card_02
as ivCard02

import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_bt_call
as btnCall

/**呼叫用户
 * Created by Raleigh.Luo on 17/4/11.
 */

class CallUserActivity : SuperActivity(), ICallUserView {


    //请求对象
    private lateinit var mPresenter: CallUserPresenter
    private lateinit var mCustomDialog: CustomDialog
    private var mShowTerminalDialog: CustomDialog?=null
    private lateinit var mProgress: ProgressDialog
    private var preferences: SharedPreferences? = null
    //手机号码
    private lateinit var phone: String
    //昵称
    private lateinit var nickName: String
    private lateinit var id: String
    //是否正在连线
    private var isConnecting = false
    //是否正在视频会见
    private var isVideoMetting= false
    //用户账号
    private var mAccount: String? = null
    private var mIDWidth: Int = 0
    private var mCallRequestCode = Constants.EXTRA_CODE
    private val DOWN_TIME: Long = 10000//倒计时 10秒
    private val TAG = CallUserActivity::class.java.simpleName
    //发送云信消息成功
    private var mSendOnlineSuccess=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_user_layout)
        //初始化
        init()
        //注册接收器
        registerReceiver()
    }

    override fun dialFailed() {
        showToast(R.string.error_to_meetting)
        isConnecting=false
        isVideoMetting=false
        stopProgress()
        mTimer.cancel()
    }
    /**
     * 初始化
     */
    private fun init() {
        btnCall.isEnabled=true
        mPresenter = CallUserPresenter(this, this)
        //获取传递过来时的数据
        id = intent.getStringExtra(Constants.EXTRA)
        phone = intent.getStringExtra(Constants.EXTRAS)
        nickName = intent.getStringExtra(Constants.EXTRA_TAB)
        mProgress = ProgressDialog.show(this, null, getString(R.string.check_other_status))
        mProgress.setCanceledOnTouchOutside(true)
        mProgress.setCancelable(true)
        mProgress.setOnDismissListener {
            isConnecting =false
        }
        stopProgress()
        preferences = mPresenter.getSharedPreferences()
        mAccount = preferences?.getString(Constants.TERMINAL_ACCOUNT, "")
        if (TextUtils.isEmpty(mAccount)) {
            initTerminalDialog()
            if (!(mShowTerminalDialog?.isShowing?:false)) mShowTerminalDialog?.show()
        }
        //初始化对话框
        mCustomDialog = CustomDialog(this)
        mCustomDialog.onClickListener=View.OnClickListener { view ->
            if (view.id == R.id.custom_dialog_layout_tv_confirm) {
                online(mAccount)
            }
        }
        val viewTreeObserver = ivCard01.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val ID_RATIO= 856f / 540f
                ivCard01.viewTreeObserver.removeGlobalOnLayoutListener(this)
                mIDWidth = ivCard01.measuredWidth
                val layoutParams = ivCard01.layoutParams as LinearLayout.LayoutParams
                layoutParams.height = (mIDWidth / ID_RATIO).toInt()
                layoutParams.width = mIDWidth
                ivCard01.layoutParams = layoutParams
                ivCard02.layoutParams = layoutParams
            }
        })
        //请求详情
        mPresenter.request(phone)
    }

    /**
     * 配置好终端提示对话框
     */
    private fun initTerminalDialog(){
        if(mShowTerminalDialog==null) {
            mShowTerminalDialog = CustomDialog(this)
            with(mShowTerminalDialog!!){
                this.title = getString(R.string.hint)
                this.content = getString(R.string.please_set_terminal_infor)
                this.confirmText = getString(R.string.to_setting)
                this.cancelText = getString(R.string.cancel)
                this.onClickListener= View.OnClickListener { v->
                    if(v.id==R.id.custom_dialog_layout_tv_confirm)//去设置
                        startActivity(Intent(context, ConfigActivity::class.java))
                }
            }
        }
    }
    /**
     *响应点击事件
     */
    fun onClickListener(view: View) {
        when (view.id) {
        //左上角返回
            R.id.common_head_layout_iv_left -> finish()
            R.id.call_user_layout_bt_call -> {
                //呼叫
                val account = preferences?.getString(Constants.TERMINAL_ACCOUNT, "")
                online(account)
            }
        }
    }

    /**
     * 连线对方账号
     */
    private fun online(account: String?) {
        mSendOnlineSuccess=false
        isConnecting = true
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
                json.put("code", -1)
                //                        json.put("msg", account);
                notification.content = json.toString()
                NIMClient.getService(MsgService::class.java).sendCustomNotification(notification).
                        setCallback(object :RequestCallback<Void>{
                            override fun onException(exception: Throwable?) {
                                mSendOnlineSuccess=false
                            }

                            override fun onSuccess(param: Void?) {
                                mSendOnlineSuccess=true
                            }

                            override fun onFailed(code: Int) {
                                mSendOnlineSuccess=false
                            }

                        })
                //开始倒计时
                mTimer.start()
            } else {
                //云信已掉线
                showToast(R.string.yunxin_offline)
            }
        } else {//未设置终端
            initTerminalDialog()
            //显示设置终端对话框
            if (!(mShowTerminalDialog?.isShowing?:false)) mShowTerminalDialog?.show()
        }
    }


    /**
     * 获取信息成功
     */
    override fun onSuccess() {
        val img_urls = mPresenter.entity?.imageUrl?.split("|")
        img_urls?.let {
            ivCard01.post { ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[0], ivCard01) }
            ivCard02.post { ImageLoader.getInstance().displayImage(Constants.DOMAIN_NAME_XLS + img_urls[1], ivCard02) }
            findViewById(R.id.call_user_layout_bt_call).isEnabled = true
            val editor = mPresenter.getSharedPreferences().edit()
            editor.putString(Constants.OTHER_CARD + 1, img_urls[0])
            editor.putString(Constants.OTHER_CARD + 2, img_urls[1])
            editor.putString(Constants.OTHER_CARD + 3, img_urls[2])
            editor.putString(Constants.EXTRA, mPresenter.entity?.accid)
            editor.putString(Constants.EXTRAS, id)
            editor.apply()
        }
    }

    /**
     * 拨号成功 直接进入呼叫界面
     * @param password
     */
    override fun dialSuccess(password: String) {
        btnCall.setBackgroundResource(R.drawable.common_grey_btn_shape)
        btnCall.isEnabled=false
        //跳转到视频界面
        val intent = Intent(this, VideoMettingActivity::class.java)
        intent.action=getIntent().action
        intent.putExtra(Constants.ZIJING_PASSWORD, password)
        startActivityForResult(intent, mCallRequestCode)
        //关闭显示进度条
        stopProgress()
    }

    /**
     * 关闭数据加载动画
     */
    override fun startRefreshAnim() {
        handler.sendEmptyMessage(Constants.START_REFRESH_UI)
    }

    /**
     * 开始数据加载动画
     */
    override fun stopRefreshAnim() {
        handler.sendEmptyMessage(Constants.STOP_REFRESH_UI)
    }

    /**
     * 开始进度条
     */
    fun startProgress() {
        if (!mProgress.isShowing) mProgress.show()
    }

    /**
     * 关闭进度条
     */
    fun stopProgress() {
        if (mProgress.isShowing) mProgress.dismiss()
    }

    override fun onResume() {
        super.onResume()
        //关闭GUI
        mPresenter.startAsynTask(Constants.CLOSE_GUI_TAB,null)
        isVideoMetting=false
        //检查是否正在通话，有就挂断
        mPresenter.checkCallStatus()
        if(!btnCall.isEnabled){
            mTimer.start()
        }
    }


    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ONLINE_SUCCESS_ACTION)
        intentFilter.addAction(Constants.NIM_KIT_OUT)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onDestroy() {
        mPresenter.onDestory()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        //关闭窗口，避免窗口溢出
        if(mCustomDialog.isShowing)mCustomDialog.dismiss()
        if(mShowTerminalDialog?.isShowing?:false)mShowTerminalDialog?.dismiss()
        if(mProgress.isShowing)mProgress.dismiss()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        isConnecting = false
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.EXTRA_CODE){
            data?.let {
                if (resultCode == Activity.RESULT_FIRST_USER) {
                    //连线失败，重新呼叫
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
                }else if(resultCode == Activity.RESULT_CANCELED){
                    //对方挂断
                    val hint=data.getStringExtra(Constants.EXTRA)
                    if(!hint.isEmpty()){
                        showToast(hint)
                    }
                }

            }
        }
    }

    /**
     * 倒计时
     */
    private val mTimer = object : CountDownTimer(DOWN_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            if(!btnCall.isEnabled) {//呼叫等待10秒的倒计时
                val second = millisUntilFinished / 1000
                btnCall.setText(String.format("%s(%sS)",getString(R.string.call),second))
            }
        }

        override fun onFinish() {
            if(!btnCall.isEnabled){//呼叫等待10秒的倒计时
                btnCall.isEnabled=true
                btnCall.setBackgroundResource(R.drawable.common_blue_btn_selector)
                btnCall.setText(getString(R.string.call))
            }else{//是否在线倒计时
                stopVConfVideo()
            }

        }
    }
    /**
     * 对方不在线
     */
    fun stopVConfVideo() {
        if(isConnecting) {
            isConnecting = false
            stopProgress()
            mCustomDialog.content = getString(if(mSendOnlineSuccess)R.string.other_offline else R.string.own_offline)
            mCustomDialog.cancelText = getString(R.string.cancel)
            mCustomDialog.confirmText = getString(R.string.call_back)
            if (!mCustomDialog.isShowing) mCustomDialog.show()
        }
    }

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
    /**
     * 广播接收器
     */
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopRefreshAnim()
            if (intent.action == Constants.ONLINE_SUCCESS_ACTION) {
                //对方在线，进行呼叫
                if (isConnecting&&!isVideoMetting) {
                    isConnecting =false
                    isVideoMetting=true
                    mCallRequestCode = Constants.EXTRA_CODE
                    mTimer.cancel()
                    mPresenter.dial(mAccount?:"")
                }
            } else if (intent.action == Constants.NIM_KIT_OUT) {
                //云信被踢下线
                finish()
            }
        }
    }
}

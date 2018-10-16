package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.gkzxhn.prison.R
import com.gkzxhn.prison.adapter.CallUserViewPagerAdapter
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.presenter.CallUserPresenter
import com.gkzxhn.prison.utils.Utils
import com.gkzxhn.prison.utils.Utils.dip2px
import com.gkzxhn.prison.view.ICallUserView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.starlight.mobile.android.lib.view.dotsloading.DotsTextView
import kotlinx.android.synthetic.main.call_user_layout.*
import org.json.JSONObject
import java.util.*
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_i_loading as tvLoading
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_iv_call as btnCall
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_tv_next_call_hint as tvNextCallHint
import kotlinx.android.synthetic.main.call_user_layout.call_user_layout_vp as vpCallUser


/**呼叫用户
 * Created by Raleigh.Luo on 17/4/11.
 */

class CallUserActivity : SuperActivity(), ICallUserView {
    //请求对象
    private lateinit var mPresenter: CallUserPresenter
    private lateinit var mCustomDialog: CustomDialog
    private var mShowTerminalDialog: CustomDialog? = null
    private lateinit var mProgress: ProgressDialog
    private var preferences: SharedPreferences? = null
    //家属id
    private lateinit var mFamilyId: String
    //会见id
    private lateinit var id: String
    //是否正在连线
    private var isConnecting = false
    //是否正在视频会见
    private var isVideoMetting = false
    //用户账号
    private var mIDWidth: Int = 0
    private var mCallRequestCode = Constants.EXTRA_CODE
    //总会见时长
    private var mTotalCallDuration=0L
    //呼叫云信等待时间
    private val DOWN_TIME: Long = 15000//倒计时 15秒
    // 下一次呼叫间隔时间
    private val NEXT_CALL_TIME: Long = 5000//倒计时 5秒
    private val TAG = CallUserActivity::class.java.simpleName
    //发送云信消息成功
    private var mSendOnlineSuccess = false
    //viewpager小点点的容器
    private var dotsList: ArrayList<ImageView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_user_layout)
//        //初始化
        init()
        //注册接收器
        registerReceiver()
    }

    override fun dialFailed() {
        showToast(R.string.error_to_meetting)
        isConnecting = false
        isVideoMetting = false
        stopProgress()
        mTimer.cancel()
    }

    /**
     * 初始化
     */
    private fun init() {
        mPresenter = CallUserPresenter(this, this)
        //获取传递过来时的数据
        id = intent.getStringExtra(Constants.EXTRA) ?: ""
        mFamilyId = intent.getStringExtra(Constants.EXTRAS) ?: ""
        mTotalCallDuration=intent.getLongExtra(Constants.TOTAL_CALL_DURATION,0L)
        mProgress = ProgressDialog.show(this, null, getString(R.string.check_other_status))
        mProgress.setCanceledOnTouchOutside(true)
        mProgress.setCancelable(true)
        mProgress.setOnDismissListener {
            isConnecting = false
        }
        stopProgress()
        preferences = mPresenter.getSharedPreferences()
        if (TextUtils.isEmpty(preferences?.getString(Constants.TERMINAL_ROOM_NUMBER, ""))) {
            initTerminalDialog()
            if (mShowTerminalDialog?.isShowing != true) mShowTerminalDialog?.show()
        }
        //初始化对话框
        mCustomDialog = CustomDialog(this)
        mCustomDialog.onClickListener = View.OnClickListener { view ->
            if (view.id == R.id.custom_dialog_layout_tv_confirm) {
                online()
            }
        }
        if (isFreeMetting()) {
//            没有会见ID 根据家属id请求获取家属身份证信息
            mPresenter.request(mFamilyId)
        }else{
            //远程会见 根据会见ID请求获取相关家属身份证信息
            mPresenter.requestByMettingID(id,mFamilyId)
        }
    }
    fun isFreeMetting():Boolean{
        return id.isEmpty()
    }
    override fun onResume() {
        super.onResume()
        isVideoMetting = false
        //检查是否正在通话，有就挂断
        mPresenter.checkCallStatus()
        if (!btnCall.isEnabled) {
            tvNextCallHint.visibility = View.VISIBLE
            mNextCallTimer.start()
        }
    }

    /**
     * 配置好终端提示对话框
     */
    private fun initTerminalDialog() {
        if (mShowTerminalDialog == null) {
            mShowTerminalDialog = CustomDialog(this)
            with(mShowTerminalDialog!!) {
                this.title = getString(R.string.hint)
                this.content = getString(R.string.please_set_terminal_infor)
                this.confirmText = getString(R.string.to_setting)
                this.cancelText = getString(R.string.cancel)
                this.onClickListener = View.OnClickListener { v ->
                    if (v.id == R.id.custom_dialog_layout_tv_confirm)//去设置
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
            R.id.call_user_layout_iv_call -> {
                //单元测试 延迟加载
                setIdleNow(true)
                online()
            }
//            向左的箭头
            R.id.call_user_layout_iv_left -> {
                vpCallUser.currentItem = vpCallUser.currentItem - 1
                changeViewPagerLeftAndRight()
            }
//            向右的箭头
            R.id.call_user_layout_iv_right -> {
                vpCallUser.currentItem = vpCallUser.currentItem + 1
                changeViewPagerLeftAndRight()
            }
        }
    }

    /**
     * Explanation: 根据当前的页面动态改变左右方向按扭的底色
     * @author LSX
     *    -----2018/9/14
     */
    private fun changeViewPagerLeftAndRight() {
        when {
            vpCallUser.currentItem == 0 -> {
                call_user_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_gary_select)
                call_user_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
            }
            vpCallUser.currentItem == vpCallUser.adapter.count - 1 -> {
                call_user_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
                call_user_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_gary_select)
            }
            else -> {
                call_user_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
                call_user_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
            }
        }
    }

    /**
     * 连线对方账号
     */
    private fun online() {
        if (mPresenter.meetingMemberEntity != null) {
            val mettingRoomNumber = preferences?.getString(Constants.TERMINAL_ROOM_NUMBER, "")
            mSendOnlineSuccess = false
            isConnecting = true
            if (mettingRoomNumber != null && mettingRoomNumber.isNotEmpty()) {
                if (mPresenter.checkStatusCode() == StatusCode.LOGINED || mPresenter.checkStatusCode() == StatusCode.NET_BROKEN) {
                    startProgress()
                    //发送云信消息，检测家属端是否已经准备好可以呼叫
                    val notification = CustomNotification()
                    val accid = mPresenter.accessToken
                    notification.sessionId = accid
                    notification.sessionType = SessionTypeEnum.P2P
                    // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
                    // 这里以类型 “1” 作为“正在输入”的状态通知。
                    val json = JSONObject()
                    json.put("code", -1)
                    //会见记录id
                    json.put("meetingId", id)
                    //本次会见时长，callDuration大于等于0,免费会见callDuration＝－1
                    var callDuration=mTotalCallDuration-mPresenter.lastCallDuration
                    callDuration=if(callDuration>0)callDuration else 0
                    json.put("callDuration",if(id.isEmpty())-1L else callDuration)
                    //  json.put("msg", account);
                    notification.content = json.toString()
                    NIMClient.getService(MsgService::class.java).sendCustomNotification(notification).setCallback(object : RequestCallback<Void> {
                        override fun onException(exception: Throwable?) {
                            mSendOnlineSuccess = false
                        }

                        override fun onSuccess(param: Void?) {
                            mSendOnlineSuccess = true
                        }

                        override fun onFailed(code: Int) {
                            mSendOnlineSuccess = false
                        }

                    })
                    //开始倒计时
                    mTimer.start()
                } else {
                    setIdleNow(false)
                    //云信已掉线
                    showToast(R.string.yunxin_offline)
                }
            } else {//未设置终端
                setIdleNow(false)
                initTerminalDialog()
                //显示设置终端对话框
                if (mShowTerminalDialog?.isShowing != true) {
                    mShowTerminalDialog?.show()
                }
            }
        }
    }

    /**
     * 获取信息成功
     */
    override fun onSuccess() {
        btnCall.isEnabled = true
        val editor = mPresenter.getSharedPreferences().edit()
        editor.putString(Constants.EXTRAS, id)
        editor.apply()
        mPresenter.meetingMemberEntity?.let {
            vpCallUser.adapter = CallUserViewPagerAdapter(it)
        }
        if (mPresenter.meetingMemberEntity?.size?:0 > 1) {
            initDots()
            // 设置ViewPager的监听
            vpCallUser.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    changeViewPagerLeftAndRight()
                    //遍历存放图片的数组
                    for (i in 0 until mPresenter.meetingMemberEntity?.size!!) {
                        //判断小点点与当前的图片是否对应，对应设置为亮色 ，否则设置为暗色
                        if (i == position % mPresenter.meetingMemberEntity?.size!!) {
                            dotsList?.get(i)?.setImageDrawable(
                                    resources.getDrawable(
                                            R.drawable.shape_call_user_pint_blue))
                        } else {
                            dotsList?.get(i)?.setImageDrawable(
                                    resources.getDrawable(
                                            R.drawable.shape_call_user_pint_gary))
                        }

                    }
                }
            })

        } else {
            //
            call_user_layout_iv_left.visibility = View.INVISIBLE
            call_user_layout_iv_right.visibility = View.INVISIBLE
        }
    }

    /**
     * 初始化viewpager下面的小点
     */
    private fun initDots() {
        //创建存放小点点的集合
        dotsList = ArrayList<ImageView>()
        //每次初始化之前清空集合
        dotsList?.clear()
        // 每次初始化之前  移除  布局中的所有小点
        ll_dots.removeAllViews()
        for (i in 0 until mPresenter.meetingMemberEntity?.size!!) {
            //创建小点点图片
            val imageView = ImageView(this)
            var drawable: Drawable? = null
            drawable = if (i == 0) {
                // 亮色图片
                resources.getDrawable(R.drawable.shape_call_user_pint_blue)
            } else {
                resources.getDrawable(R.drawable.shape_call_user_pint_gary)
            }
            imageView.setImageDrawable(drawable)
            // 考虑屏幕适配
            val params = LinearLayout.LayoutParams(Utils.dip2px(this, 18f), dip2px(this, 18f))
            //设置小点点之间的间距
            params.setMargins(dip2px(this, 5f), 0, dip2px(this, 5f), 0)
            //将小点点添加大线性布局中
            ll_dots.addView(imageView, params)
            // 将小点的控件添加到集合中
            dotsList?.add(imageView)
        }
    }


    /**
     * 拨号成功 直接进入呼叫界面
     * @param password
     */
    override fun dialSuccess(hostPassword: String) {
        btnCall.setImageResource(R.mipmap.ic_call_disable)
        btnCall.isEnabled = false
        //跳转到视频界面
        val intent = Intent(this, VideoMettingActivity::class.java)
        intent.action = getIntent().action
        intent.putExtra(Constants.ZIJING_PASSWORD, hostPassword)
        intent.putExtra(Constants.EXTRAS, mFamilyId)//家属id
        intent.putExtra(Constants.EXTRA, id)//会见id
        intent.putExtra(Constants.EXTRA_ENTITY, mPresenter.meetingMemberEntity)
        //总会见时长
        intent.putExtra(Constants.TOTAL_CALL_DURATION,mTotalCallDuration)
        //上次通话时长
        intent.putExtra(Constants.LAST_CALL_DURATION,mPresenter.lastCallDuration)
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
        if (mCustomDialog.isShowing) mCustomDialog.dismiss()
        if (mShowTerminalDialog?.isShowing == true) mShowTerminalDialog?.dismiss()
        if (mProgress.isShowing) mProgress.dismiss()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        isConnecting = false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.EXTRA_CODE) {
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
                        mPresenter.dial()
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //对方挂断或终端挂断
                    //更新上次会见时长
                    mPresenter.lastCallDuration=data.getLongExtra(Constants.LAST_CALL_DURATION,0L)
                    //显示提示
                    val hint = data.getStringExtra(Constants.EXTRA)
                    if (!hint.isEmpty()) {
                        showToast(hint)
                    }
                }else if(resultCode==Activity.RESULT_OK){
                    //会见正常结束，关闭此界面
                    finish()
                }

            }
        }
    }

    /**
     * 倒计时
     */
    private val mTimer = object : CountDownTimer(DOWN_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            setIdleNow(false)
            stopVConfVideo()
        }
    }
    /**
     * 倒计时
     */
    private val mNextCallTimer = object : CountDownTimer(NEXT_CALL_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            if (!btnCall.isEnabled) {//呼叫等待10秒的倒计时
                val second = millisUntilFinished / 1000
                tvNextCallHint.text = String.format("%s%sS", getString(R.string.next_call_hint), second)
            }
        }

        override fun onFinish() {
            if (!btnCall.isEnabled) {//呼叫等待10秒的倒计时
                btnCall.isEnabled = true
                btnCall.setImageResource(R.drawable.call_btn_selector)
                tvNextCallHint.text = getString(R.string.next_call_hint)
                tvNextCallHint.visibility = View.GONE
            }
        }
    }

    /**
     * 对方不在线
     */
    fun stopVConfVideo() {
        if (isConnecting) {
            isConnecting = false
            stopProgress()
            mCustomDialog.content = getString(if (mSendOnlineSuccess) R.string.other_offline else R.string.own_offline)
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
            val view = (tvLoading as DotsTextView)
            if (msg.what == Constants.START_REFRESH_UI) {//开始加载动画
                view.visibility = View.VISIBLE
                if (!view.isPlaying) {
                    view.showAndPlay()
                }
            } else if (msg.what == Constants.STOP_REFRESH_UI) {//停止加载动画
                if (view.isPlaying || view.isShown) {
                    view.hideAndStop()
                    view.visibility = View.GONE
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
                if (isConnecting && !isVideoMetting) {
                    isConnecting = false
                    isVideoMetting = true
                    mCallRequestCode = Constants.EXTRA_CODE
                    mTimer.cancel()
                    mPresenter.dial()
                }
            } else if (intent.action == Constants.NIM_KIT_OUT) {
                //云信被踢下线
                finish()
            }
        }
    }
}

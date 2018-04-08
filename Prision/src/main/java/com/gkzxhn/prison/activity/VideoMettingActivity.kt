package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CancelVideoDialog
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.presenter.CallZijingPresenter
import com.gkzxhn.prison.view.ICallZijingView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.nineoldandroids.animation.ObjectAnimator
import com.nostra13.universalimageloader.core.ImageLoader
import com.starlight.mobile.android.lib.util.JSONUtil


import org.json.JSONException
import org.json.JSONObject

import kotlinx.android.synthetic.main.activity_call_zijing.tv_call_zijing
as mText
import kotlinx.android.synthetic.main.activity_call_zijing.fl_call_zijing
as mContent
import kotlinx.android.synthetic.main.activity_call_zijing.exit_Img
as mExit_img
import kotlinx.android.synthetic.main.activity_call_zijing.mute_text
as mMute_txt
import kotlinx.android.synthetic.main.activity_call_zijing.quiet_text
as mQuite_txt
import kotlinx.android.synthetic.main.activity_call_zijing.ll_check_id
as mLl_check_id
import kotlinx.android.synthetic.main.activity_call_zijing.iv_avatar
as mIv_avatar
import kotlinx.android.synthetic.main.activity_call_zijing.iv_id_card_01
as mIv_id_card_01
import kotlinx.android.synthetic.main.activity_call_zijing.iv_id_card_02
as mIv_id_card_02
import kotlinx.android.synthetic.main.activity_call_zijing.tv_count_down
as tv_count_down
import kotlinx.android.synthetic.main.activity_call_zijing.rl_bottom
as rlBottomPanel
import kotlinx.android.synthetic.main.activity_call_zijing.tv_header_count_down
as tvHeaderCountDown



/**视频会见界面
 * Created by 方 on 2017/11/16.
 */

class VideoMettingActivity : SuperActivity(), ICallZijingView {

    private val TAG = VideoMettingActivity::class.java.simpleName
    //请求Presenter
    private lateinit var mPresenter: CallZijingPresenter
    //关闭视频会见对话框
    private lateinit var mCloseVideoDialog: CancelVideoDialog
    //是否静音
    private val isQuite: Boolean = false
    //审核界面是否已缩放
    private var isScaled = false
    //提示通话时间已到 对话框
    private lateinit var mHintDialog:CustomDialog
    private lateinit var mTimer:CountDownTimer
    private var FIMALY_IS_JOIN=false;//家属是否已经加入会议室
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_zijing)

        //初始化Present
        mPresenter = CallZijingPresenter(this, this)
        //初始化倒计时时间 必须presenter初始化后
        initCountDownTimer()
        //遥控器控制器
        mPresenter.cameraControl("direct")

        //初始化挂断对话框
        initHangUpDialog()
        setIdCheckData()
        registerReceiver()
    }

    /**
     * 初始化倒计时时间
     */
    private fun initCountDownTimer(){
        val time = mPresenter.getSharedPreferences().getLong(Constants.TIME_LIMIT, 20)
        //倒计时time分钟
        var DOWN_TIME=time*60*1000//倒计时
        /**
         * 延迟time秒执行 倒计时
         */
        mTimer = object : CountDownTimer(DOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000//秒
                if (second == 30L) {//30秒时
                    runOnUiThread {
                        tv_count_down.setTextColor(resources.getColor(R.color.red_text))
                        tvHeaderCountDown.setTextColor(resources.getColor(R.color.red_text))
                    }
                }
                //分钟
                val min = second / 60
                val seconds = second - min * 60
                val time= min.toString() + getString(R.string.minute) + seconds.toString() +  getString(R.string.second)
                tv_count_down.text = getString(R.string.the_leave_time)+time
                tvHeaderCountDown.text=time
            }

            override fun onFinish() {
                tv_count_down.text=getString(R.string.the_leave_time)+getString(R.string.metting_has_time_out)
                tvHeaderCountDown.text=getString(R.string.metting_has_time_out)
                //倒计时完成
                if(!mHintDialog.isShowing)mHintDialog.show()
            }
        }
    }

    /**
     * 初始化挂断对话框
     */
    private fun initHangUpDialog(){
        //取消会见 提示对话框
        mCloseVideoDialog = CancelVideoDialog(this, true)
        mCloseVideoDialog.onClickListener=View.OnClickListener {
            //发送挂断消息给对方
            sendHangupMessage()
            //挂断
            mPresenter.hangUp("")
        }
        //提示通话时间已到
        mHintDialog= CustomDialog(this)
        mHintDialog.title=getString(R.string.hint)
        mHintDialog.content=getString(R.string.call_time_has_arrived)
        mHintDialog.cancelText=getString(R.string.cancel)
        mHintDialog.confirmText=getString(R.string.ok)
        mHintDialog.onClickListener= View.OnClickListener { v->
            if(v.id==R.id.custom_dialog_layout_tv_confirm) {
                //发送挂断消息给对方
                sendHangupMessage()
                //挂断
                mPresenter.hangUp("")
            }
        }
    }
    override fun onDestroy() {
        mPresenter.onDestory()
        //关闭窗口，避免窗口溢出
        if (mCloseVideoDialog.isShowing) mCloseVideoDialog.dismiss()
        if (mHintDialog.isShowing) mHintDialog.dismiss()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        //停止倒计时
        mTimer.cancel()
        super.onDestroy()
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.PRISION_JOIN_METTING)
        intentFilter.addAction(Constants.FAMILY_FAILED_JOIN_METTING)
        intentFilter.addAction(Constants.FAMILY_JOIN_METTING)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    fun onClickListener(v: View) {
        when (v.id) {
            R.id.mute_text ->//哑音
                mPresenter.switchMuteStatus()
            R.id.quiet_text ->  //修改线性输出状态
                mPresenter.setIsQuite(!isQuite)
            R.id.exit_Img ->  //挂断
                showHangup()
            R.id.ll_check_id ->//身份证缩放
                startScaleAnim(mLl_check_id)
            R.id.fl_call_zijing ->{//点击示或隐藏底部
                showOrHideBottomPanel(rlBottomPanel.visibility==View.VISIBLE)
            }
        }
    }

    /**
     * 显示或隐藏底部条
     */
    private fun showOrHideBottomPanel(isShown: Boolean) {
        if (isShown) {// 显示动画
            val bottomHideAnim = AnimationUtils.loadAnimation(this,
                    com.starlight.mobile.android.lib.R.anim.slide_out_to_bottom)
            //设置动画时间
            bottomHideAnim.duration = 400
            rlBottomPanel.startAnimation(bottomHideAnim)
            rlBottomPanel.visibility = View.GONE
            //头部显示
            tvHeaderCountDown.visibility=View.VISIBLE
        } else {// 隐藏动画
            val bottomShowAnim = AnimationUtils.loadAnimation(this,
                    com.starlight.mobile.android.lib.R.anim.slide_in_from_bottom)
            //设置动画时间
            bottomShowAnim.duration = 400
            rlBottomPanel.startAnimation(bottomShowAnim)
            rlBottomPanel.visibility = View.VISIBLE
            //头部不现实
            tvHeaderCountDown.visibility=View.GONE
        }
    }
    /**
     * 设置扬声器UI
     *
     * @param quiet
     */
    override fun setSpeakerUi(quiet: Boolean) {
        if (quiet)
            mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                    resources.getDrawable(R.drawable.vconf_mute_selector), null, null)
        else
            mQuite_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                    resources.getDrawable(R.drawable.vconf_speaker_selector), null, null)
    }


    //发送云信消息，挂断
    private fun sendHangupMessage() {
        val notification = CustomNotification()
        val accid = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
                .getString(Constants.ACCID, "")
        notification.sessionId = accid
        notification.sessionType = SessionTypeEnum.P2P
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
        // 这里以类型 “1” 作为“正在输入”的状态通知。
        val json = JSONObject()
        json.put("code", -2)//-2表示挂断
        notification.content = json.toString()
        //发送云信消息 给对方
        NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
    }

    /**
     * 设置审核身份布局
     */
    private fun setIdCheckData() {
        //获取图片信息
        val sharedPreferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        val avatarUri = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 3, "")
        val idCardUri1 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 1, "")
        val idCardUri2 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 2, "")
        //加载图片信息
        ImageLoader.getInstance().displayImage(avatarUri, mIv_avatar)
        ImageLoader.getInstance().displayImage(idCardUri1, mIv_id_card_01)
        ImageLoader.getInstance().displayImage(idCardUri2, mIv_id_card_02)
    }

    /**
     * 开始属性动画
     *
     * @param view
     */
    private fun startScaleAnim(view: View?) {
        var anim: ObjectAnimator? = null
        if (isScaled) {
            //放大动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tobig", 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f).setDuration(300)
            mLl_check_id.pivotX = 0f
            mLl_check_id.pivotY = 0f
            isScaled = !isScaled
            anim.start()
        } else {
            //缩小动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tosmall", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f).setDuration(300)
            mLl_check_id.pivotX = 0f
            mLl_check_id.pivotY = 0f
            isScaled = !isScaled
            anim.start()
        }
        anim.addUpdateListener { valueAnimator ->
            val cVal = valueAnimator.animatedValue as Float
            view?.scaleX = cVal
            view?.scaleY = cVal
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.i(TAG, "onKeyDown: getkeycode >>>>>> " + event.keyCode)
        when (event.keyCode) {
            221 -> { //挂断按键
                showHangup()
                return true
            }
            225 -> { //显示/缩放审核界面
                startScaleAnim(mLl_check_id)
                return true
            }
            218 -> {//静音
                mPresenter.setIsQuite(!isQuite)
                return true
            }
        }
        return false
    }

    /**
     * 显示关闭会见对话框
     */
    private fun showHangup() {
        if(FIMALY_IS_JOIN){//家属已加入会议 则提示挂断原因
            if (!mCloseVideoDialog.isShowing) mCloseVideoDialog.show()
        }else{//直接挂断
            mPresenter.hangUp("")
        }
    }

    /**
     * 通知远端进入房间
     */
    private fun callAccount() {
        val sharedPreferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
        val account = sharedPreferences.getString(Constants.TERMINAL_ACCOUNT, "")
        val time = sharedPreferences.getLong(Constants.TIME_LIMIT, 20)
        if (account != null && account.length > 0) {
            //发送云信消息，检测家属端是否已经准备好可以呼叫
            val notification = CustomNotification()
            val accid = sharedPreferences
                    .getString(Constants.ACCID, "")
            notification.sessionId = accid
            notification.sessionType = SessionTypeEnum.P2P
            // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
            // 这里以类型 “1” 作为“正在输入”的状态通知。
            val json = JSONObject()
            try {
                json.put("code", -1)//-1表示接通
                json.put("msg", account)
                json.put("limit_time", time)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            notification.content = json.toString()
            //发送云信消息
            NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
        }
    }

    override fun hangUpSuccess(hint :String) {
        val intent=Intent()
        intent.putExtra(Constants.EXTRA,hint)
        setResult(Activity.RESULT_CANCELED,intent)
        finish()
    }

    /**
     * 广播接收器
     */
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                Constants.FAMILY_FAILED_JOIN_METTING ->{//家属加入会议失败
                    //家属已加入 －家属挂断   ／ 家属未加入 人脸失败失败
                    mPresenter.hangUp(getString(if(FIMALY_IS_JOIN)R.string.video_metting_hangup else R.string.video_metting_failed))
                }
                Constants.FAMILY_JOIN_METTING ->{//家属加入会议成功
                    if(mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD,true)){
                        //开始录屏
                        mPresenter.startUSBRecord()
                    }
                    FIMALY_IS_JOIN=true;

                    mTimer.start()
                    //免费呼叫次数更新
                    if(getIntent().action==Constants.CALL_FREE_ACTION) {
                        mPresenter.updateFreeTime()
                    }
                }
                Constants.PRISION_JOIN_METTING ->{//监狱端进入会见房间
                    val jsonStr = intent.getStringExtra(Constants.EXTRA)

                    if (TextUtils.isEmpty(jsonStr)) {
                        return
                    }
                    var  e = JSONUtil.getJSONObjectStringValue(JSONObject(jsonStr), "e")
                    if (e.isEmpty()) {
                        return
                    }
                    when (e) {
                        "setup_call_calling" -> mText.text = getString(R.string.connecting)
                        "ring_call" -> mText.text =  getString(R.string.wait_answer)
                        "established_call" -> {
                            //呼叫建立
                            mText.visibility = View.GONE
                            mContent.setBackgroundColor(resources.getColor(R.color.zijing_video_bg))
                            callAccount()
                        }
                        "cleared_call" -> {
                            try {
                                val jsonObject = JSONUtil.getJSONObject(jsonStr)
                                var objv = jsonObject.getJSONObject("v")
                                val reason = objv!!.getString("reason")
                                if ("Ended by local user" != reason) {
                                    //连接失败 重新连接 切换协议
                                    //                            if ("Remote host offline".equals(reason) || "No common capabilities".equals(reason)) {
                                    val data = Intent()
                                    data.putExtra(Constants.CALL_AGAIN, true)
                                    data.putExtra(Constants.END_REASON, reason)
                                    this@VideoMettingActivity.setResult(Activity.RESULT_FIRST_USER, data)
                                    this@VideoMettingActivity.finish()
                                }
                            } catch (e1: JSONException) {
                                e1.printStackTrace()
                            }
                        }
                        "missed_call" -> {//对方未接听
                            hangUpSuccess(getString(R.string.family_no_answer))
                        }
                        "error" -> {//呼叫错误
                            hangUpSuccess(getString(R.string.call_error))
                        }
                        "MuteOn" ->
                            //麦克风静音
                            mMute_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                                    resources.getDrawable(R.drawable.vconf_microphone_off_selector), null, null)
                        "MuteOff" ->
                            //麦克风静音
                            mMute_txt.setCompoundDrawablesWithIntrinsicBounds(null,
                                    resources.getDrawable(R.drawable.vconf_microphone_on_selector), null, null)
                    }
                }
            }
        }
    }



    override fun startRefreshAnim() {

    }

    override fun stopRefreshAnim() {

    }

    override fun finish() {
        if(mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD,true)){
            //停止录屏
            mPresenter.stopUSBRecord()
        }
        super.finish()
    }
}


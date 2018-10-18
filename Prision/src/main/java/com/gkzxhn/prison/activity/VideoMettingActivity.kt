package com.gkzxhn.prison.activity


import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.gkzxhn.prison.R
import com.gkzxhn.prison.adapter.VideoMettingViewPagerAdapter
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CancelVideoDialog
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.entity.MeetingMemberEntity
import com.gkzxhn.prison.presenter.CallZijingPresenter
import com.gkzxhn.prison.view.ICallZijingView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.nineoldandroids.animation.ObjectAnimator
import com.starlight.mobile.android.lib.util.JSONUtil
import kotlinx.android.synthetic.main.video_metting_layout.*
import org.json.JSONException
import org.json.JSONObject
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_cb_micro as cbMicro
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_cb_speaker as cbSpeaker
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_iv_hang_up as mExit_img
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_ll_check_id as mLl_check_id
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_root as mContent
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_tv_call_zijing as mText
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_tv_header_count_down as tvHeaderCountDown
import kotlinx.android.synthetic.main.video_metting_layout.video_metting_layout_vp as vp_metting


/**视频会见界面
 * Created by 方 on 2017/11/16.
 */

class VideoMettingActivity : SuperActivity(), ICallZijingView {

    private val TAG = VideoMettingActivity::class.java.simpleName
    //请求Presenter
    private lateinit var mPresenter: CallZijingPresenter
    //关闭视频会见对话框
    private lateinit var mCloseVideoDialog: CancelVideoDialog
    //是否已经开启扬声器
    private var isOpenedYSQ: Boolean = true
    //审核界面是否已缩放
    private var isScaled = false
    //提示通话时间已到 对话框
    private lateinit var mHintDialog: CustomDialog
    //会见id
    private lateinit var id: String
    //倒计时
    private var mTimer: CountDownTimer?=null
    private var FIMALY_IS_JOIN = false//家属是否已经加入会议室
    private var ESTABLISHED_CALL = false//监狱端已经建立连接
    private var init = true
    private var mTotalCallDuration = 0L
    private var mLastCallDuration = 0L
    //结束页面返回resultCode
    private var mFinishResult=Activity.RESULT_CANCELED
    //正计时秒数
    private var mSecond=0L
    //正计时使用线程
    private var mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_metting_layout)
        //初始化Present
        mPresenter = CallZijingPresenter(this, this)

        //遥控器控制器
        mPresenter.cameraControl("direct")
        //打开扬声器
        mPresenter.setIsQuite(isOpenedYSQ)
        //打开麦克风
        mPresenter.switchMuteStatus()
        //获取传递过来时的数据
        id = intent.getStringExtra(Constants.EXTRA) ?: ""
        mTotalCallDuration=intent.getLongExtra(Constants.TOTAL_CALL_DURATION,0L)
        mLastCallDuration=intent.getLongExtra(Constants.LAST_CALL_DURATION,0L)
        mPresenter.setLastCallDuration(mLastCallDuration)
        //初始化倒计时时间 必须presenter初始化后
        initCountDownTimer()
        //初始化挂断对话框
        initHangUpDialog()
        setIdCheckData()
        registerReceiver()
    }
    /**
     * 开始倒计时-远程会见
     */
    private fun startCountDownTimer(){
        //本次会见时长
        var duration = mTotalCallDuration-mLastCallDuration
        if(duration>0){
            //还有通话时间，开始倒计时
            mTimer?.start()
        }else{
            //没有通话时间，开始正计时
            startTimer()
        }
    }
    /**
     * 停止倒计时-远程会见
     */
    private fun stopCountDownTimer(){
        mTimer?.cancel()
    }
    /**
     * 开始正计时
     */
    private fun startTimer(){
        mSecond=0
        mHandler.postDelayed(mRunnable,1000)
    }

    /**
     * 停止正计时
     */
    private fun stopTimer(){
        mHandler.removeCallbacks(mRunnable)
    }

    /**
     * 是否为免费会见
     */
    private fun isFreeMetting():Boolean{
        return id.isEmpty()
    }

    private val mRunnable=object :Runnable {
        override fun run() {
            mSecond++
            //分钟
            if(isFreeMetting()){//免费会见，显示"已通话：xxxx秒"
                tvHeaderCountDown.text = getString(R.string.has_called_colon) + getShowTime(mSecond)
            }else{//远程会见，显示"已超时：xxxx秒"
                tvHeaderCountDown.text = getString(R.string.has_time_out_colon) + getShowTime(mSecond)
            }
            mHandler.postDelayed(this,1000)
        }
    }
    private fun getShowTime(totalSecond: Long):String{
        var minute = totalSecond / 60
        var time=""
        if(minute<60){
            val seconds = totalSecond % 60
            if(minute==0L){
                time = seconds.toString() + getString(R.string.second)
            }else{
                time = minute.toString() + getString(R.string.minute) + seconds.toString() + getString(R.string.second)
            }
        }else{
            val hour = minute / 60
            minute = minute % 60
            val seconds = totalSecond - hour * 3600 - minute * 60
            time = minute.toString() + getString(R.string.minute) + seconds.toString() + getString(R.string.second)
        }
        return time
    }


    /**
     * 初始化倒计时时间
     */
    private fun initCountDownTimer() {
        var DOWN_TIME = mTotalCallDuration-mLastCallDuration
        DOWN_TIME=if(DOWN_TIME>0)DOWN_TIME*1000 else 0
        /**
         * 延迟time秒执行 倒计时
         */
        mTimer = object : CountDownTimer(DOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000//秒
                if (second == 30L) {//30秒时
                    runOnUiThread {
                        tvHeaderCountDown.setTextColor(resources.getColor(R.color.red_text))
                    }
                }
                tvHeaderCountDown.text = getString(R.string.the_leave_time) + getShowTime(second)
            }

            override fun onFinish() {
                tvHeaderCountDown.text = getString(R.string.the_leave_time) + getString(R.string.metting_has_time_out)
                //开始正计时
                startTimer()
                //倒计时完成
                if (!mHintDialog.isShowing) mHintDialog.show()
            }
        }
    }

    /**
     * 初始化挂断对话框
     */
    private fun initHangUpDialog() {
        //取消会见 提示对话框
        mCloseVideoDialog = CancelVideoDialog(this, true)
        mCloseVideoDialog.onClickListener = View.OnClickListener {
            //发送挂断消息给对方
            sendHangupMessage()
            //挂断
            mPresenter.hangUp("",id)
        }
        mCloseVideoDialog.setFinishListener(object :CancelVideoDialog.FinishListener{
            override fun checkFinishStatus(reason: String) {
                this@VideoMettingActivity.checkFinishStatus(reason)
            }
        })
        //提示通话时间已到
        mHintDialog = CustomDialog(this)
        mHintDialog.title = getString(R.string.hint)
        mHintDialog.content = getString(R.string.call_time_has_arrived)
        mHintDialog.cancelText = getString(R.string.cancel)
        mHintDialog.confirmText = getString(R.string.ok)
        mHintDialog.onClickListener = View.OnClickListener { v ->
            if (v.id == R.id.custom_dialog_layout_tv_confirm) {
                //发送挂断消息给对方
                sendHangupMessage()
                //挂断
                mPresenter.hangUp("",id)
                //时间到了，挂断
                timeOutHangUp()
            }
        }
    }

    /**
     * 超时挂断 发送云信，意义未知
     */
    private fun timeOutHangUp() {
        val array = resources.getStringArray(R.array.cancel_video_reason)
        val mContent = if (FIMALY_IS_JOIN) array[0] else array[4]
        val sharedPreferences = mPresenter.getSharedPreferences()
        val otherAccount = sharedPreferences.getString(Constants.EXTRA, "")//对方云信帐号
        val meetingId = sharedPreferences.getString(Constants.EXTRAS, "")//记录ID
        if (otherAccount != null && otherAccount.isNotEmpty()) {
            //// 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
            //            NIMClient.getService(MsgService.class).sendMessage(message, false);
            // 构造自定义通知，指定接收者
            val notification = CustomNotification()
            notification.sessionId = otherAccount
            notification.sessionType = SessionTypeEnum.P2P

            // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
            // 这里以类型 “1” 作为“正在输入”的状态通知。
            val json = JSONObject()
            try {
                json.put("msg", mContent)
                json.put("code", 0)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            notification.content = json.toString()
            // 发送自定义通知
            NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
        }
        if (!meetingId.isEmpty()) {//meetingid不能为空
            mPresenter.requestCancel(meetingId, mContent)
        }
    }

    /**
     *  检查会见为是否正常结束（包括免费会见）
     */
    override fun checkFinishStatus(reason:String){
        val finishResons=GKApplication.instance.resources.getStringArray(R.array.cancel_video_reason)
        if(finishResons[0]==reason||finishResons[1]==reason||finishResons[2]==reason){
            //会见正常结束finsh,提醒上一个页面自动关闭
            mFinishResult=Activity.RESULT_OK
        }
    }
    override fun onDestroy() {
        mPresenter.onDestory()
        //关闭窗口，避免窗口溢出
        if (mCloseVideoDialog.isShowing) mCloseVideoDialog.dismiss()
        if (mHintDialog.isShowing) mHintDialog.dismiss()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        //停止倒计时
        stopCountDownTimer()
        //停止正计时
        stopTimer()
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

    /**
     * 所有页面点击事件
     */
    fun onClickListener(v: View) {
        when (v.id) {
            R.id.video_metting_layout_cb_micro ->//麦克风
            {
                //单元测试 延迟加载
                setIdleNow(true)
                mPresenter.switchMuteStatus()
            }
            R.id.video_metting_layout_cb_speaker ->  //扬声器
            {
                mPresenter.setIsQuite(!isOpenedYSQ)
            }
            R.id.video_metting_layout_iv_hang_up ->  //挂断
                showHangup()
            R.id.video_metting_layout_ll_check_id ->//身份证缩放
                startScaleAnim(mLl_check_id)
            R.id.video_metting_layout_iv_left -> {
                vp_metting.currentItem = vp_metting.currentItem - 1
                changeViewPagerLeftAndRight()
            }
            R.id.video_metting_layout_iv_right -> {
                vp_metting.currentItem = vp_metting.currentItem + 1
                changeViewPagerLeftAndRight()
            }
        }
    }

    /**
     * 设置扬声器UI
     *
     * @param quiet
     */
    override fun setSpeakerUi(mIsOpenYSQ: Boolean) {
        isOpenedYSQ = mIsOpenYSQ
        cbSpeaker.isChecked = isOpenedYSQ
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
        val handler = Handler()
        //延迟执行Runnable中的run方法
        handler.postDelayed({
            //获取上个界面传过来的信息
            val meetingMemberEntitys = intent.getSerializableExtra(Constants.EXTRA_ENTITY) as ArrayList<MeetingMemberEntity>
            vp_metting.adapter = VideoMettingViewPagerAdapter(meetingMemberEntitys)

            if (meetingMemberEntitys.size > 1) {
                vp_metting.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    }

                    override fun onPageSelected(position: Int) {
                        changeViewPagerLeftAndRight()
                    }

                })
            } else {
//            隐藏左右划动的按扭
                video_metting_layout_iv_left.visibility = View.INVISIBLE
                video_metting_layout_iv_right.visibility = View.INVISIBLE
            }
        }, 500)
    }

    /**
     * Explanation: 根据当前的页面动态改变左右方向按扭的底色
     * @author LSX
     *    -----2018/9/14
     */
    private fun changeViewPagerLeftAndRight() {
        when {
            vp_metting.currentItem == 0 -> {
                video_metting_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_gary_select)
                video_metting_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
            }
            vp_metting.currentItem == vp_metting.adapter.count - 1 -> {
                video_metting_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
                video_metting_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_gary_select)
            }
            else -> {
                video_metting_layout_iv_left.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
                video_metting_layout_iv_right.setBackgroundResource(R.drawable.shape_call_user_point_blue_select)
            }
        }
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
            mLl_check_id.pivotX = mLl_check_id.measuredWidth.toFloat()
            mLl_check_id.pivotY = 0f
            isScaled = !isScaled
            anim.start()
        } else {
            //缩小动画
            anim = ObjectAnimator.ofFloat(mLl_check_id, "tosmall", 1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f).setDuration(300)
            mLl_check_id.pivotX = mLl_check_id.measuredWidth.toFloat()
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

    /**
     * 响应设备遥控器
     */
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
                mPresenter.setIsQuite(!isOpenedYSQ)
                return true
            }
        }
        return false
    }

    /**
     * 显示关闭会见对话框
     */
    private fun showHangup() {
        if (FIMALY_IS_JOIN) {//家属已加入会议 则提示挂断原因
            if (!mCloseVideoDialog.isShowing) mCloseVideoDialog.show()
        } else {//直接挂断
            //发送挂断消息给对方
            sendHangupMessage()
            //挂断
            mPresenter.hangUp("",id)
            //时间到了，挂断
            timeOutHangUp()
        }
    }

    /**
     * 通知远端进入房间
     */
    private fun callAccount() {
        val sharedPreferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
        val account = mPresenter.getMeettingAccount()
        val time = sharedPreferences.getLong(Constants.TIME_LIMIT, 20)
        if (account != null && account.isNotEmpty()) {
            //发送云信消息，检测家属端是否已经准备好可以呼叫
            val notification = CustomNotification()
            val accid = sharedPreferences.getString(Constants.ACCID, "")
            notification.sessionId = accid
            notification.sessionType = SessionTypeEnum.P2P
            // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。
            // 这里以类型 “1” 作为“正在输入”的状态通知。
            val jailId = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE).getString(Constants.TERMINAL_JIAL_ID, "")
            val json = JSONObject()
            try {
                json.put("code", -1)//-1表示接通
                json.put("msg", account)
                json.put("limit_time", time)
                json.put("jail", jailId)
                json.put("meetingId", id)//会见id
                //本次会见时长，callDuration大于等于0,免费会见callDuration＝－1
                var callDuration=mTotalCallDuration-mLastCallDuration
                callDuration=if(callDuration>0)callDuration else 0
                json.put("callDuration",if(isFreeMetting())-1L else callDuration)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            notification.content = json.toString()
            //发送云信消息
            NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
        }
    }

    /**
     * 挂断成功，返回到上一个页面，异常挂断原因返回给上一个页面
     */
    override fun hangUpSuccess(hint: String) {
        val intent = Intent()
        intent.putExtra(Constants.EXTRA, hint)
        intent.putExtra(Constants.LAST_CALL_DURATION, mPresenter.getCallDuration())
        setResult(mFinishResult, intent)
        finish()
    }

    /**
     * 广播接收器
     */
    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.FAMILY_FAILED_JOIN_METTING -> {//家属加入会议失败
                    //家属已加入 －家属挂断   ／ 家属未加入 人脸失败失败
                    mPresenter.hangUp(getString(if (FIMALY_IS_JOIN) R.string.video_metting_hangup else R.string.video_metting_failed),id)
                }
                Constants.FAMILY_JOIN_METTING -> {//家属加入会议成功
                    mPresenter.initStartMeetingTime()
                    if (mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD, true)) {
                        //开始录屏
                        mPresenter.startUSBRecord()
                    }
                    FIMALY_IS_JOIN = true
                    if(isFreeMetting()){
                        //免费会见，开始正计时
                        startTimer()
                    }else{
                        //远程开始倒计时
                        startCountDownTimer()
                    }
                    if (getIntent().action == Constants.CALL_FREE_ACTION) {
                        val activityIntent = this@VideoMettingActivity.intent
                        //免费呼叫次数更新
                        mPresenter.updateFreeTime()
                        //添加免费会见信息  参数是家属id
                        if (activityIntent.hasExtra(Constants.EXTRA))
                            mPresenter.addFreeMeetting(activityIntent.getStringExtra(Constants.EXTRA))
                    }
                }
                Constants.PRISION_JOIN_METTING -> {//监狱端进入会见房间
                    val jsonStr = intent.getStringExtra(Constants.EXTRA)

                    if (TextUtils.isEmpty(jsonStr)) {
                        return
                    }
                    var e = JSONUtil.getJSONObjectStringValue(JSONObject(jsonStr), "e")
                    if (e.isEmpty()) {
                        return
                    }
                    when (e) {
                        "setup_call_calling" -> mText.text = getString(R.string.connecting)
                        "ring_call" -> mText.text = getString(R.string.wait_answer)
                        "established_call" -> {
                            if (!ESTABLISHED_CALL) {
                                ESTABLISHED_CALL = true
                                //呼叫建立
                                mText.visibility = View.INVISIBLE
                                mContent.setBackgroundColor(resources.getColor(R.color.zijing_video_bg))
                                callAccount()
                            }
                        }
                        "cleared_call" -> {
                            try {
                                val jsonObject = JSONUtil.getJSONObject(jsonStr)
                                var objv = jsonObject.getJSONObject("v")
                                val reason = objv!!.getString("reason")
                                if ("Ended by local user" != reason && !ESTABLISHED_CALL) {
                                    //连接失败 重新连接 切换协议
                                    //   if ("Remote host offline".equals(reason) || "No common capabilities".equals(reason)) {
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
                        "MuteOn" -> {  //关闭了 麦克风
                            micro(false)
                        }
                        "MuteOff" -> {//打开了麦克风
                            micro(true)
                        }
                    }
                }
            }
        }
    }

    /**
     * 打开或关闭麦克风
     */
    private fun micro(isOpenMicro: Boolean) {
        if (isOpenMicro) {
            cbMicro.isChecked = true
        } else {
            if (init) {//第一次打开
                init = false
                mPresenter.switchMuteStatus()
            }
            cbMicro.isChecked = false
            setIdleNow(false)
        }
        //释放延迟加载
        setIdleNow(false)
    }

    override fun startRefreshAnim() {

    }

    override fun stopRefreshAnim() {

    }

    override fun finish() {
        if (mPresenter.getSharedPreferences().getBoolean(Constants.IS_OPEN_USB_RECORD, true)) {
            //停止录屏
            mPresenter.stopUSBRecord()
        }
        super.finish()
    }
}


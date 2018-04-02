package com.gkzxhn.prison.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CancelVideoDialog
import com.gkzxhn.prison.presenter.CallZijingPresenter
import com.gkzxhn.prison.utils.GetCameraControl
import com.gkzxhn.prison.view.ICallZijingView
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.nineoldandroids.animation.ObjectAnimator
import com.nineoldandroids.animation.ValueAnimator
import com.nostra13.universalimageloader.core.ImageLoader
import com.starlight.mobile.android.lib.util.JSONUtil


import org.json.JSONException
import org.json.JSONObject

import java.util.concurrent.TimeUnit

import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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

/**
 * Created by 方 on 2017/11/16.
 */

class CallZiJingActivity : SuperActivity(), View.OnClickListener, ICallZijingView {
    private val TAG = CallZiJingActivity::class.java.simpleName
    private lateinit var mPresenter: CallZijingPresenter

    private var mTimeSubscribe: Subscription? = null

    private lateinit var mGetCameraControl: GetCameraControl   // 遥控器控制器

    private lateinit var mCancelVideoDialog: CancelVideoDialog
    private val isQuite: Boolean = false    //是否静音

    private var isScaled = false  //审核界面是否已缩放

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Constants.ZIJING_ACTION.equals(intent.action)) {
                val hangup = intent.getBooleanExtra(Constants.HANGUP, false)
                if (hangup) {
                    mPresenter.hangUp()
                }
                val time_connect = intent.getBooleanExtra(Constants.TIME_CONNECT, false)
                if (time_connect) {
                    if (mTimeSubscribe !=
                            null) {
                        mTimeSubscribe?.unsubscribe()
                        val sharedPreferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
                        val time = sharedPreferences.getLong(Constants.TIME_LIMIT, 20)
                        startTime(time * 60)
                    }
                }
                val jsonStr = intent.getStringExtra(Constants.ZIJING_JSON)
                if (TextUtils.isEmpty(jsonStr)) {
                    return
                }
                Log.w(TAG, "jsonStr received : " + jsonStr)
                var e: String? = null
                try {
                    e = JSONUtil.getJSONObjectStringValue(JSONObject(jsonStr), "e")
                } catch (e1: JSONException) {
                    e1.printStackTrace()
                }

                if (e == null) {
                    return
                }
                when (e) {
                    "setup_call_calling" -> mText.text = "正在连接..."
                    "ring_call" -> mText.text = "等待接听..."
                    "established_call" -> {
                        //呼叫建立
                        mText.visibility = View.GONE
                        mContent.setBackgroundColor(resources.getColor(R.color.zijing_video_bg))
                        callAccount()
                    }
                    "cleared_call" -> {
                        val jsonObject = JSONUtil.getJSONObject(jsonStr)
                        var objv: JSONObject? = null
                        try {
                            objv = jsonObject.getJSONObject("v")
                            val reason = objv.getString("reason")
                            if ("Ended by local user" != reason) {
                                //                            if ("Remote host offline".equals(reason) || "No common capabilities".equals(reason)) {
                                val data = Intent()
                                data.putExtra(Constants.CALL_AGAIN, true)
                                data.putExtra(Constants.END_REASON, reason)
                                this@CallZiJingActivity.setResult(Activity.RESULT_CANCELED, data)
                            }
                        } catch (e1: JSONException) {
                            e1.printStackTrace()
                        }

                        showToast("已挂断")
                        this@CallZiJingActivity.finish()
                    }
                    "missed_call" -> {
                        mText.text = "对方未接听..."
                        showToast("对方未接听")
                        this@CallZiJingActivity.finish()
                    }
                    "error" -> {
                        mText.text = "呼叫错误"
                        this@CallZiJingActivity.finish()
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

    /**
     * 延迟time秒执行
     * @param time
     */
    private fun startTime(time: Long) {
        mTimeSubscribe = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(time+ 1, TimeUnit.SECONDS)
                .map { aLong ->
                    val delay = time - aLong
                    if (delay == 30L) {
                        runOnUiThread { tv_count_down.setTextColor(resources.getColor(R.color.red_text)) }
                    }
                    val min = delay / 60
                    val seconds = delay - min * 60
                    min.toString() + "分" + seconds + "秒"
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onCompleted() {
                        showTimeUp()
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message)
                    }

                    override fun onNext(s: String) {
                        Log.i(TAG, "count_down : " + s)
                        tv_count_down.text = s
                    }
                })
    }

    /**
     * 提示通话时间已到
     */
    private fun showTimeUp() {
        val builder = AlertDialog.Builder(this)
        builder
                .setTitle(R.string.reminder)
                .setMessage("通话时间已到,是否结束通话?")
                .setPositiveButton(R.string.ok) { dialog, which ->
                    sendHangupMessage()
                    mPresenter.hangUp()
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                .setCancelable(true)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_zijing)
        mPresenter = CallZijingPresenter(this, this)
        mGetCameraControl = GetCameraControl()
        mGetCameraControl.cameraControl("direct")
        mCancelVideoDialog = CancelVideoDialog(this, true)
        mCancelVideoDialog.setOnClickListener(View.OnClickListener {
            sendHangupMessage()
            mPresenter.hangUp()

        })
        setIdCheckData()
        setClickListener()
        registerReceiver()
    }


    private fun setClickListener() {
        mMute_txt.setOnClickListener(this)
        mQuite_txt.setOnClickListener(this)
        mExit_img.setOnClickListener(this)
        mLl_check_id.setOnClickListener(this)
    }

    override fun onDestroy() {
        if (null != mCancelVideoDialog && mCancelVideoDialog.isShowing) {
            mCancelVideoDialog.dismiss()
        }
        mTimeSubscribe?.unsubscribe()
        unregisterReceiver(mBroadcastReceiver)//注销广播监听器
        super.onDestroy()
    }

    /**
     * 注册广播监听器
     */
    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ZIJING_ACTION)
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mute_text ->
                //哑音
                mPresenter.switchMuteStatus()
            R.id.quiet_text ->
                //修改线性输出状态
                mPresenter.setIsQuite(!isQuite)
            R.id.exit_Img ->
                //挂断
                showHangup()
            R.id.ll_check_id -> startScaleAnim(mLl_check_id)
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
        try {
            json.put("code", -2)//-2表示挂断
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        notification.content = json.toString()
        NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
    }

    /**
     * 设置审核身份布局
     */
    private fun setIdCheckData() {
        val sharedPreferences = getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
        val avatarUri = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 3, "")
        val idCardUri1 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 1, "")
        val idCardUri2 = Constants.DOMAIN_NAME_XLS + "/" + sharedPreferences.getString(Constants.OTHER_CARD + 2, "")
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
            221 -> {
                //挂断按键
                showHangup()
                return true
            }
            225 -> {
                //显示/缩放审核界面
                startScaleAnim(mLl_check_id)
                return true
            }
            218 -> {
                //静音
                mPresenter.setIsQuite(!isQuite)
                return true
            }
        }
        return false
    }

    private fun showHangup() {

        if (!mCancelVideoDialog.isShowing) mCancelVideoDialog.show()
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
            NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
        }
        startTime(time * 60)
    }

    override fun startRefreshAnim() {

    }

    override fun stopRefreshAnim() {

    }
}


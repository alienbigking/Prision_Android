package com.gkzxhn.prison.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.test.espresso.IdlingResource
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.VolleyUtils
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.AlarmClockPopWindow
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.idlingregistry.SimpleIdlingResource
import com.gkzxhn.prison.model.iml.CallZijingModel
import org.json.JSONObject


/**
 * Created by Raleigh.Luo on 17/3/9.
 */

open class SuperActivity : AppCompatActivity() {

    private lateinit var mToast: Toast
    private lateinit var tvToastText: TextView
    //自动化测试使用
    private var mIdlingResource: SimpleIdlingResource? = null
    //关机
    private lateinit var mTurnOffProgress: ProgressDialog
    private lateinit var mTurnOffDialog: CustomDialog
    private lateinit var mCallZijingModel: CallZijingModel
    lateinit var mAlarmClockPopWindow: AlarmClockPopWindow
    //是否在前台
    private var isFont=true
    //自动化测试使用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GKApplication.instance.pushActivity(this)
        mCallZijingModel = CallZijingModel()
        mToast = Toast(this)
        val view = View.inflate(this, R.layout.toast_layout, null)
        mToast.view = view
        tvToastText = view.findViewById(R.id.toast_layout_tv_title) as TextView
        //初始化进度条
        mTurnOffProgress = ProgressDialog.show(this, null, getString(R.string.turn_off_ing))
        mTurnOffProgress.setCanceledOnTouchOutside(true)
        mTurnOffProgress.setCancelable(true)
        mTurnOffProgress.dismiss()
        mTurnOffDialog = CustomDialog(this)
        with(mTurnOffDialog!!) {
            this.title = getString(R.string.hint)
            this.content = getString(R.string.turn_off_hint)
            this.confirmText = getString(R.string.ok)
            this.cancelText = getString(R.string.back)
            this.onClickListener = View.OnClickListener { v ->
                if (v.id == R.id.custom_dialog_layout_tv_confirm) {
                    if (!mTurnOffProgress.isShowing) mTurnOffProgress.show()
//                    //关机按键
                    mCallZijingModel.turnOff(object : VolleyUtils.OnFinishedListener<JSONObject> {
                        override fun onSuccess(response: JSONObject) {
                        }

                        override fun onFailed(error: VolleyError) {
                        }
                    })
                }
            }
        }
        //注册闹钟广播
        registerMettingAlarmReceiver()
        //初始化闹钟铃声界面
        mAlarmClockPopWindow= AlarmClockPopWindow(this)
//        mAlarmClockPopWindow.setOnDismissListener {
//            //窗口关闭时，取消闹钟
//            cancelAlarmClock()
//        }

    }

    /**
     * 取消闹钟
     */
    fun cancelAlarmClock(){
//        //闹钟取消
        val intent = Intent(Constants.ALARM_CLOCK)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        // And cancel the alarm.
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(sender)

    }

    /**
     * 会见开始前提醒一次性闹钟
     */
    fun createAlarmClock(timeInMilli:Long){
        //一次性闹钟,自定义action
        val intent = Intent(Constants.ALARM_CLOCK)
        //PendingIntent.FLAG_UPDATE_CURRENT
        val sender = PendingIntent.getBroadcast(this, 0, intent,0)
        //定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // Schedule the alarm!
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //set 设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
        am.set(AlarmManager.RTC_WAKEUP, timeInMilli, sender)
    }

    /**
     * Espresso 自动化测试延迟操作
     * @param isIdleNow 是否为空闲，false则阻塞测试线程
     */
    fun setIdleNow(isIdleNow: Boolean) {
        if (mIdlingResource?.isIdleNow != isIdleNow) {
            if (isIdleNow) {
                //耗时操作结束，设置空闲状态为true，放开测试线程
                mIdlingResource?.setIdleState(true)
            } else {
                //耗时操作开始，设置空闲状态为false，阻塞测试线程
                mIdlingResource?.setIdleState(false)
            }
        }
    }

    /**
     * Only called from test, creates and returns a new [SimpleIdlingResource].
     */
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        if (mIdlingResource == null) {
            mIdlingResource = SimpleIdlingResource()
        }
        return mIdlingResource!!
    }

    fun showToast(testResId: Int) {
        tvToastText.setText(testResId)
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }

    fun showToast(showText: String) {
        tvToastText.text = showText
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }

    fun cancelToast() {
        mToast.cancel()
    }

    override fun onDestroy() {
        cancelToast()
        mCallZijingModel.stopAllRequest()
        if (mTurnOffDialog.isShowing) mTurnOffDialog.dismiss()
        if (mTurnOffProgress.isShowing) mTurnOffProgress.dismiss()

        //注销广播
        unregisterReceiver(mMettingAlarmReceiver)
        super.onDestroy()
    }


    override fun finish() {
        cancelToast()
        super.finish()
        GKApplication.instance.popActivity(this)
    }


    /**
     * 响应设备遥控器
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (event.keyCode) {
            222 -> {//关机键
                if (!mTurnOffDialog.isShowing) mTurnOffDialog.show()
                return true
            }
        }
        return false
    }

    override fun onPause() {
        isFont=false
        if(mAlarmClockPopWindow.isShowing)mAlarmClockPopWindow.dismiss()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        isFont=true
    }

    /**
     * 注册会见闹钟广播
     */
    private fun registerMettingAlarmReceiver(){
        val intentFilter = IntentFilter()
        // 2. 设置接收广播的类型
        intentFilter.addAction(Constants.ALARM_CLOCK)
        // 3. 动态注册：调用Context的registerReceiver（）方法
        registerReceiver(mMettingAlarmReceiver, intentFilter)

    }
    private var mMettingAlarmReceiver=object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
//            if(isFont)//Activity在前台显示
//                mAlarmClockPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER,0,0)
        }
    }
}

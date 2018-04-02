package com.gkzxhn.prison.customview

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.model.iml.MainModel
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.starlight.mobile.android.lib.util.CommonHelper

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

class CancelVideoDialog( context: Context, private val isCancelVideo: Boolean) : Dialog(context) {
    private lateinit var mSpinner: Spinner
    private lateinit var rateArray: Array<String>
    var content: String = ""
    private var onClickListener: View.OnClickListener? = null

    private val mModel: MainModel
    fun setOnClickListener(onClickListener: View.OnClickListener) {
        this.onClickListener = onClickListener
    }

    init {

        mModel = MainModel()
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val contentView = LayoutInflater.from(getContext()).inflate(
                if (isCancelVideo) R.layout.cancel_video_dialog else R.layout.cancel_meeting_dialog_layout, null)
        setContentView(contentView)
        mSpinner = contentView.findViewById(R.id.spinner) as Spinner
        init()
        measureWindow()
    }

    private fun init() {

        rateArray = context.resources.getStringArray(if (isCancelVideo) R.array.cancel_video_reason else R.array.cancel_meeting_reason)
        content = rateArray[0]
        val adapter = ArrayAdapter(getContext(),

                R.layout.spinner_item, rateArray)

        mSpinner.adapter = adapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                content = rateArray[position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        findViewById(R.id.cancel_video_dialog_tv_cancel).setOnClickListener {
            dismiss()
            CommonHelper.clapseSoftInputMethod(context as Activity)
        }
        findViewById(R.id.cancel_video_dialog_tv_set).setOnClickListener { view ->
            dismiss()
            CommonHelper.clapseSoftInputMethod(context as Activity)
             onClickListener?.onClick(view)
            if (isCancelVideo) sendMessage()
        }
    }

    private fun sendMessage() {
        val sharedPreferences = mModel.sharedPreferences
        val otherAccount = sharedPreferences.getString(Constants.EXTRA, "")//对方云信帐号
        val meetingId = sharedPreferences.getString(Constants.EXTRAS, "")//记录ID
        if (otherAccount != null && otherAccount.length > 0) {
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
                json.put("msg", content)
                json.put("code", 0)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            notification.content = json.toString()

            // 发送自定义通知
            NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
        }
        mModel.requestCancel(meetingId, content, null)
    }

    fun measureWindow() {
        val dialogWindow = this.window
        val params = dialogWindow.attributes
        val m = dialogWindow.windowManager

        val d = m.defaultDisplay
        params.width = d.width
        //	        params.height=d.getHeight();
        dialogWindow.setGravity(Gravity.CENTER)
        dialogWindow.attributes = params
    }

}

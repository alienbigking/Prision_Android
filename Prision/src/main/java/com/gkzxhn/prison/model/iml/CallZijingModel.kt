package com.gkzxhn.prison.model.iml

import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.utils.XtHttpUtil
import com.gkzxhn.wisdom.async.VolleyUtils

import org.json.JSONObject
import java.util.HashMap

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

open class CallZijingModel : BaseModel(), ICallZijingModel {
    override fun resetAudioOut(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        volleyUtils[JSONObject::class.java, XtHttpUtil.RESET_AUDIOUT, REQUEST_TAG, onFinishedListener]
    }

    override fun resetAudioIn(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>) {
        volleyUtils[JSONObject::class.java, XtHttpUtil.RESET_AUDIIN, REQUEST_TAG, onFinishedListener]
    }

    /**
     *  取消会见
     */
    override fun requestCancel(id: String, reason: String, onFinishedListener: VolleyUtils.OnFinishedListener<String>?) {
        try {
            var status=Constants.MEETTING_PASSED
            val finishResons=GKApplication.instance.resources.getStringArray(R.array.cancel_video_reason)
            val cancelResons=GKApplication.instance.resources.getStringArray(R.array.cancel_meeting_reason)
            if(cancelResons.contains(reason)){
                status=Constants.MEETTING_CANCELED
            }else if(finishResons[0]==reason||finishResons[1]==reason||finishResons[2]==reason){
                status=Constants.MEETTING_FINISHED
            }
            val params = HashMap<String, String>()
            params.put("remark", reason)
            params.put("id", id)
            params.put("status", status)
            volleyUtils.post( Constants.REQUEST_CANCEL_MEETING_URL, params, REQUEST_TAG, onFinishedListener)
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }

    override fun turnOff(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        volleyUtils[JSONObject::class.java, XtHttpUtil.POWEROFF, REQUEST_TAG, onFinishedListener]
    }

    /**
     * 获取网络请求
     */
    override fun getNetworkStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        volleyUtils[JSONObject::class.java, XtHttpUtil.GET_NETWORK_STATUS, REQUEST_TAG, onFinishedListener]
    }

    /**
     * 获取呼叫列表
     */
    override fun getCallHistory(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            volleyUtils[JSONObject::class.java, XtHttpUtil.GET_DIAL_HISTORY, REQUEST_TAG, onFinishedListener]
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }
    }

    /**
     * 拨号 进入视频会议
     */
    override fun dial(account: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            val strings = account.split("##".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val protocol = sharedPreferences.getString(Constants.PROTOCOL, "h323")
            val rate = sharedPreferences.getInt(Constants.TERMINAL_RATE, 512)
            val params =JSONObject()
            params.put("url", String.format("%s:%s**%s", protocol, if (strings.size > 0) strings[0] else "", if (strings.size > 1) strings[1] else ""))
            params.put("rate", rate.toString())
            volleyUtils.post(XtHttpUtil.DIAL, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     *  获取视频会见信息
     */
    override fun getCallInfor(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            volleyUtils[JSONObject::class.java, XtHttpUtil.GET_CALLINFO, REQUEST_TAG, onFinishedListener]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 减少免费会见次数
     */
    override fun updateFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            val url = String.format("%s?terminalNumber=%s", Constants.UPDATE_FREE_MEETING_TIME,
                    sharedPreferences.getString(Constants.USER_ACCOUNT, ""))
            volleyUtils[JSONObject::class.java, url, REQUEST_TAG, onFinishedListener]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 查询USB录屏是否开启
     */
    override fun queryUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        volleyUtils.get(JSONObject::class.java,XtHttpUtil.GET_RECORD_NEAR_STATUS, REQUEST_TAG, onFinishedListener)
    }

    /**
     * 开启USB录播
     */
    override fun startUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>?) {
        volleyUtils.get(String::class.java,XtHttpUtil.START_NEAR_RECORD ,REQUEST_TAG,onFinishedListener)
    }

    /**
     * 关闭USB录播
     */
    override fun stopUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>?) {
        try {
            volleyUtils.get(String::class.java,XtHttpUtil.STOP_NEAR_RECORD, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 遥控器控制
     */
    override fun cameraControl(v: String,onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            // 遥控器控制器
            val params = JSONObject()
            params.put("k","remote-control-role")
            volleyUtils.post(XtHttpUtil.CAMERA_CONTROL, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 挂断
     */
    override fun hangUp(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            val params = JSONObject()
            volleyUtils.post(XtHttpUtil.HANGUP, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 设置是否静音
     */
    override  fun setIsQuite(quiet: Boolean, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            val params = JSONObject()
            params.put("k", "enable-line-out")
            params.put("v", !quiet)
            volleyUtils.post(XtHttpUtil.SET_AUDIOUT, params, REQUEST_TAG, onFinishedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun switchMuteStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?) {
        try {
            volleyUtils.post(XtHttpUtil.MUTE_AUDIIN, JSONObject(), REQUEST_TAG, onFinishedListener)
        } catch (authFailureError: AuthFailureError) {
            authFailureError.printStackTrace()
        }

    }
}

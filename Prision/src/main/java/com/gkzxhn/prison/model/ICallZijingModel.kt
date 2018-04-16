package com.gkzxhn.prison.model

import com.gkzxhn.wisdom.async.VolleyUtils
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

interface ICallZijingModel : IBaseModel {
    fun  requestCancel(id: String, reason: String,onFinishedListener: VolleyUtils.OnFinishedListener<String>?)
    /**
     * 关机
     */
    fun turnOff(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)
    /**
     * 请求网络信息
     */
    fun getNetworkStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)


    /**
     * 获取呼叫记录－测试
     */
    fun getCallHistory(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 拨打电话
     */
    fun dial(account: String, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 获取呼叫信息
     */
    fun getCallInfor(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 更新免费次数
     */
    fun updateFreeTime(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 查询USB录播状态
     */
    fun queryUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 开始USB录播
     */
    fun startUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>?)

    /**
     * 停止USB录播
     */
    fun stopUSBRecord(onFinishedListener: VolleyUtils.OnFinishedListener<String>?)

    /**
     * 遥控器
     */
    fun cameraControl(v: String,onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 挂断
     */
    fun hangUp(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)

    /**
     * 设置静音
     */
    fun setIsQuite(quiet: Boolean, onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)
    /**
     * 修改哑音状态
     */
    fun switchMuteStatus(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>?)


    /**
     * 恢复音频输出
     */
    fun resetAudioOut(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
    /**
     * 恢复音频输入
     */
    fun resetAudioIn(onFinishedListener: VolleyUtils.OnFinishedListener<JSONObject>)
}

package com.gkzxhn.prison.presenter

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.async.VolleyUtils
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.model.ICallZijingModel
import com.gkzxhn.prison.model.iml.CallZijingModel
import com.gkzxhn.prison.view.ICallZijingView
import com.starlight.mobile.android.lib.util.ConvertUtil
import com.starlight.mobile.android.lib.util.HttpStatus
import com.starlight.mobile.android.lib.util.JSONUtil
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Raleigh.Luo on 18/3/30.
 */

class CallZijingPresenter(context: Context, view: ICallZijingView) : BasePresenter<ICallZijingModel, ICallZijingView>(context, CallZijingModel(), view) {
    private val TAG = CallZijingPresenter::class.java.simpleName
    //免费会见id
    private var mFreeMeetingId: String? = null
    //开始会见的时间戳,单位毫秒
    private var mStartMeetingTime: Long = 0L
    //结束会见的时间戳,单位毫秒
    private var mEndMeetingTime: Long = 0L
    //已经会见时长，单位秒
    private var mLastCallDuration: Long = 0L

    fun setLastCallDuration(duration: Long) {
        this.mLastCallDuration = duration
    }

    /**
     * 初始化会见开始时间，家属进入会议室算起
     */
    fun initStartMeetingTime() {
        mStartMeetingTime = System.currentTimeMillis()
    }

    /**
     *  取消会见
     */
    fun requestCancel(id: String, reason: String) {
        mView?.checkFinishStatus(reason)
        mModel.requestCancel(id, reason, object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {

            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     * 获取总会见时长
     */
    fun getCallDuration(): Long {
        return getCallDuration(mLastCallDuration)
    }

    /**
     * 获取总会见时长
     */
    private fun getCallDuration(lastcallDuration: Long): Long {
        if (mEndMeetingTime == 0L) mEndMeetingTime = System.currentTimeMillis()
        if (mStartMeetingTime == 0L) mStartMeetingTime = mEndMeetingTime
        //通话时长转成秒＋上次通话时长
        val meettingSecond = (mEndMeetingTime - mStartMeetingTime) / 1000 + lastcallDuration
        return meettingSecond
    }

    /**
     *  更新免费会见时长
     */
    fun updateFreeMeetting() {
        mFreeMeetingId?.let {
            //免费会见不需累计上次通话时间
            mModel.updateFreeMeetting(it, getCallDuration(0), object : VolleyUtils.OnFinishedListener<String> {
                override fun onSuccess(response: String) {
                }

                override fun onFailed(error: VolleyError) {
                }
            })
        }
    }

    /**
     *  更新远程会见时长(该接口已关闭)
     */
    fun updateMeetting(mettingId: String) {

        mModel.updateMeetting(mettingId, getCallDuration(mLastCallDuration), object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     *  添加免费会见
     */
    fun addFreeMeetting(familyId: String) {
        mModel.addFreeMeetting(familyId, object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
                val json = JSONUtil.getJSONObject(response)
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(json, "code"))
                if (code == HttpStatus.SC_OK) {
                    val meettingJson = JSONUtil.getJSONObject(JSONUtil.getJSONObject(json, "data"), "freeMeeting")
                    mFreeMeetingId = JSONUtil.getJSONObjectStringValue(meettingJson, "id")
                }
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    //// 遥控器控制器
    fun cameraControl(v: String) {
        mModel.cameraControl(v, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = response.getInt("code")
                if (code == 0) {
                }
            }

            override fun onFailed(error: VolleyError) {
            }

        })
    }


    /**
     *  挂断
     */
    fun hangUp(reason: String, mettingId: String) {
        //单元测试 延迟加载
        mView?.setIdleNow(true)
        if (mFreeMeetingId != null) {
            //更新免费会见时长
            updateFreeMeetting()
        } else {//更新远程会见时长
            //该接口已关闭
//            updateMeetting(mettingId)
        }
        //挂断
        mModel.hangUp(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.d(TAG, "HANGUP" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        //成功
                        mView?.hangUpSuccess(reason)
                    } else {
                        mView?.hangUpSuccess(GKApplication.instance.getString(R.string.network_innormal_hang_up))
                        Log.i(TAG, "onResponse: code :  " + code)
                    }
                } catch (e: JSONException) {
                    mView?.hangUpSuccess(GKApplication.instance.getString(R.string.network_innormal_hang_up))
                    Log.e(TAG, "onResponse: >>> " + e.message)
                    //                            e.printStackTrace();
                }
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }

            override fun onFailed(error: VolleyError) {
                mView?.hangUpSuccess(GKApplication.instance.getString(R.string.network_innormal_hang_up))
                Log.d(TAG, "ResetQuest..." + error.toString())
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }
        })
    }

    /**
     * 设置是否静音
     */
    fun setIsQuite(quiet: Boolean) {
        //单元测试 延迟加载
        mView?.setIdleNow(true)
        //设置静音状态  true表示设置成静音
        mModel.setIsQuite(quiet, object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.d(TAG, "DIAL" + response.toString())
                try {
                    val code = response.getInt("code")
                    if (code == 0) {
                        //设置成功
                        mView?.setSpeakerUi(quiet)
                    } else {
                        Log.i(TAG, "onResponse: 参数无效 code:  " + code)
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "onResponse: >>> " + e.message)
                    //                            e.printStackTrace();
                }
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }

            override fun onFailed(error: VolleyError) {
                Log.d(TAG, "ResetQuest..." + error.toString())
                //单元测试 释放延迟加载
                mView?.setIdleNow(false)
            }
        })

    }


    /**
     * 切换哑音
     */
    fun switchMuteStatus() {
        //修改哑音状态
        mModel.switchMuteStatus(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                Log.e("raleigh_test" + response.toString(), "raleigh_test")

            }

            override fun onFailed(error: VolleyError) {

            }
        })
    }

    /**
     * 开始USB录屏
     */
    fun startUSBRecord() {
        mModel.startUSBRecord(object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
                mModel.queryUSBRecord(object : VolleyUtils.OnFinishedListener<JSONObject> {
                    override fun onSuccess(response: JSONObject) {
                        val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                        if (code == 0) {
                            if (JSONUtil.getJSONObjectStringValue(JSONUtil.getJSONObject(response, "v"), "") != "start") {
                                mView?.showToast(R.string.check_has_not_usb)
                            }
                        }
                    }

                    override fun onFailed(error: VolleyError) {
                        mView?.showToast(R.string.check_has_not_usb)
                    }

                })
            }

            override fun onFailed(error: VolleyError) {
                mView?.showToast(R.string.check_has_not_usb)
            }

        })
    }

    /**
     * 停止USB录屏
     */
    fun stopUSBRecord() {
        mModel.stopUSBRecord(object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
            }

            override fun onFailed(error: VolleyError) {
            }

        })
    }

    /**
     * 获取免费会见次数
     */
    fun updateFreeTime() {
        mModel.updateFreeTime(object : VolleyUtils.OnFinishedListener<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(response, "code"))
                if (code == HttpStatus.SC_OK) {
                    val time = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(
                            JSONUtil.getJSONObject(response, "data"), "access_times"))
                    //保存到本地
                    getSharedPreferences().edit().putInt(Constants.CALL_FREE_TIME, time).apply()
                }
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    var sequence: String? = null
    fun addCommunicateRecords(meetingId: String) {
        mModel.addCommunicateRecords(meetingId, object : VolleyUtils.OnFinishedListener<String> {
            override fun onSuccess(response: String) {
                val json = JSONUtil.getJSONObject(response)
                val code = ConvertUtil.strToInt(JSONUtil.getJSONObjectStringValue(json, "code"))
                if (code == HttpStatus.SC_OK) {
                    sequence = JSONUtil.getJSONObjectStringValue(json,
                            "data")
                    Log.w(TAG, "$meetingId sequence send success:  " + sequence)
                }
            }

            override fun onFailed(error: VolleyError) {
            }
        })
    }

    /**
     * 更新(结束)通话记录
     */
    fun updateCommunicateRecords(remarks: String) {
        sequence?.let {
            mModel.updateCommunicateRecords(it, remarks, object : VolleyUtils.OnFinishedListener<String> {
                override fun onSuccess(response: String) {
                    Log.w(TAG, "sequence ${sequence}send success:  " + remarks)
                }

                override fun onFailed(error: VolleyError) {
                }
            })
        }
    }
}

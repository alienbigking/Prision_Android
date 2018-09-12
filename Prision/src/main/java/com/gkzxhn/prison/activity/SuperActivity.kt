package com.gkzxhn.prison.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.test.espresso.IdlingResource
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.VolleyError
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.GKApplication
import com.gkzxhn.prison.customview.CustomDialog
import com.gkzxhn.prison.model.iml.CallZijingModel
import com.gkzxhn.prison.async.VolleyUtils
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.idlingregistry.SimpleIdlingResource
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
    private lateinit var mCallZijingModel:CallZijingModel

    //自动化测试使用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GKApplication.instance.pushActivity(this)
        mCallZijingModel=CallZijingModel()
        mToast = Toast(this)
        val view=View.inflate(this, R.layout.toast_layout,null)
        mToast.view=view
        tvToastText= view.findViewById(R.id.toast_layout_tv_title) as TextView
        //初始化进度条
        mTurnOffProgress = ProgressDialog.show(this, null, getString(R.string.turn_off_ing))
        mTurnOffProgress.setCanceledOnTouchOutside(true)
        mTurnOffProgress.setCancelable(true)
        mTurnOffProgress.dismiss()
        mTurnOffDialog = CustomDialog(this)
        with(mTurnOffDialog!!){
            this.title = getString(R.string.hint)
            this.content = getString(R.string.turn_off_hint)
            this.confirmText = getString(R.string.ok)
            this.cancelText = getString(R.string.back)
            this.onClickListener= View.OnClickListener { v ->
                if(v.id==R.id.custom_dialog_layout_tv_confirm){
                    if(!mTurnOffProgress.isShowing)mTurnOffProgress.show()
//                    //关机按键
                    mCallZijingModel.turnOff(object :VolleyUtils.OnFinishedListener<JSONObject>{
                        override fun onSuccess(response: JSONObject) {
                        }

                        override fun onFailed(error: VolleyError) {
                        }
                    })
                }
            }
        }

    }
    /**
     * Espresso 自动化测试延迟操作
     * @param isIdleNow 是否为空闲，false则阻塞测试线程
     */
    fun setIdleNow(isIdleNow: Boolean){
        if(Constants.IS_TEST_MODEL) {//自动化测试模式才调用
            //状态不相等时
            if(mIdlingResource?.isIdleNow!=isIdleNow) {
                if (isIdleNow) {
                    //耗时操作结束，设置空闲状态为true，放开测试线程
                    mIdlingResource?.setIdleState(true);
                } else {
                    //耗时操作开始，设置空闲状态为false，阻塞测试线程
                    mIdlingResource?.setIdleState(false);
                }
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
        tvToastText.setText(showText)
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }

    fun cancelToast() {
        mToast.cancel()
    }

    override fun onDestroy() {
        cancelToast()
        mCallZijingModel.stopAllRequest()
        if(mTurnOffDialog.isShowing) mTurnOffDialog.dismiss()
        if(mTurnOffProgress.isShowing)mTurnOffProgress.dismiss()
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
                if(!mTurnOffDialog.isShowing) mTurnOffDialog.show()
                return true
            }
        }
        return false
    }
}

package com.gkzxhn.prison.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.GKApplication

/**
 * Created by Raleigh.Luo on 17/3/9.
 */

open class SuperActivity : AppCompatActivity() {
    private lateinit var mToast: Toast
    private lateinit var tvToastText: TextView
    //自动化测试使用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GKApplication.instance.pushActivity(this)
        mToast = Toast(this)
        val view=View.inflate(this, R.layout.toast_layout,null)
        mToast.view=view
        tvToastText= view.findViewById(R.id.toast_layout_tv_title) as TextView
    }
    /**
     * Espresso 自动化测试延迟操作
     * @param isIdleNow 是否为空闲，false则阻塞测试线程
     */
    fun setIdleNow(isIdleNow: Boolean){

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
        super.onDestroy()
    }

    override fun finish() {
        cancelToast()
        super.finish()
        GKApplication.instance.popActivity(this)
    }

    override fun onResume() {
        super.onResume()

    }
}

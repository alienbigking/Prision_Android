package com.gkzxhn.prison.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 * Created by Raleigh.Luo on 17/3/9.
 */

open class SuperActivity : AppCompatActivity() {
    private lateinit var mToast: Toast
    //自动化测试使用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
    }
    /**
     * Espresso 自动化测试延迟操作
     * @param isIdleNow 是否为空闲，false则阻塞测试线程
     */
    fun setIdleNow(isIdleNow: Boolean){

    }

    fun showToast(testResId: Int) {
        mToast.setText(testResId)
        mToast.duration = Toast.LENGTH_LONG
        mToast.show()
    }

    fun showToast(showText: String) {
        mToast.setText(showText)
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
    }
}

package com.gkzxhn.prison.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView

import com.gkzxhn.prison.R
import com.gkzxhn.prison.common.Constants

/**
 * Created by Raleigh.Luo on 17/4/5.
 */

class SplashActivity : Activity() {

    /**
     * Handler:跳转到不同界面
     */
    private val mHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0//启动动画
                -> {
                    var intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                1//跳转登录界面
                -> {
                    intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                2//跳转主页
                -> {
                    intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            super.handleMessage(msg)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)
        init()
    }

    private fun init() {
        val tvVersionName = findViewById(R.id.splash_layout_tv_version) as TextView
        var versionName = ""
        // 包管理器
        val pm = packageManager
        try {
            val packInfo = pm.getPackageInfo(packageName, 0)
            versionName = packInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        tvVersionName.text = getString(R.string.app_v) + versionName

        val preferences = getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
        //        if(preferences.getBoolean(Constants.IS_FIRST_IN,true)) {
        //            mHandler.sendEmptyMessageDelayed(0, SPLASH_DELAY_MILLIS);
        //        }else
        if (preferences.getString(Constants.USER_ACCOUNT, "").length == 0) {//未登录 未认证
            mHandler.sendEmptyMessage(1)
        } else {//已登录
            mHandler.sendEmptyMessage(2)
        }
    }

}
package com.gkzxhn.prison.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import com.gkzxhn.prison.activity.SplashActivity

object PackageUtils {

    /**
     * 强杀应用
     */
    fun killPackage(context: Context, packageName: String) {
        val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        manager.killBackgroundProcesses(packageName)
    }

    /**
     * 强杀应用
     */
    fun killMyself(context: Context) {
        val manager = context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager
        manager.killBackgroundProcesses(context.packageName)
    }

    fun restartApp(context: Context) {
        //启动页
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
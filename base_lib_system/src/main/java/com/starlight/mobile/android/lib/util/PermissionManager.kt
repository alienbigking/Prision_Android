package com.starlight.mobile.android.lib.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import java.util.ArrayList

/**
 * Created by Raleigh.Luo on 17/4/7.
 * android 6.0 Permission权限兼容的封装
 */

class PermissionManager private constructor() {
    /**
     * 执行请求多个权限
     */
    fun execute(activity: Activity, requestCode: Int, vararg permissions: String): Boolean {
        var result = true
        val lists = ArrayList<String>()
        for (permission in permissions) {
            if (!isGranted(activity,permission)) {
                lists.add(permission)
            }
        }
        if (lists.size == 0) {
            result = false
        } else {
            requestPerissins(activity, requestCode, *lists.toTypedArray())
        }
        return result
    }

    /**
     * 判断是不是授权
     */
    private fun isGranted(context: Context,permission: String): Boolean {
        return  ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求的方法
     */
    private fun requestPerissins(activity: Activity, requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }
    private object  Holder{val INSTANCE=PermissionManager()}
    companion object {
        val instance: PermissionManager by lazy {
            Holder.INSTANCE
        }
    }

}

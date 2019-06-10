package com.gkzxhn.prison.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.Toast
import com.gkzxhn.prison.R
import com.gkzxhn.prison.activity.MainActivity
import com.gkzxhn.prison.common.Constants
import com.gkzxhn.prison.common.GKApplication
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.uinfo.UserInfoProvider
import org.json.JSONObject

/**
 * Author: Huang ZN
 * Date: 2016/12/20
 * Email:943852572@qq.com
 * Description:云信sdk相关
 * sdk初始化、
 * UI初始化、
 * 监听云信系统通知及后续操作
 */

class NimInitUtil {
    //单例
    private object Holder {
        val INSTANCE = NimInitUtil()
    }

    companion object {
        val NOTIFICATION_LOCATION = -3
        val NOTIFICATION_FREE_LOCATION = -4

        //CrashHandler实例
        /** 获取CrashHandler实例 ,单例模式  */
        val instance: NimInitUtil by lazy {
            Holder.INSTANCE
        }
    }

    private val TAG = NimInitUtil::class.java.name

    /**
     * 初始化云信sdk相关
     */
    fun initNim() {
        NIMClient.init(GKApplication.instance, loginInfo(), options()) // 初始化
        if (inMainProcess()) {
            observeCustomNotification()
            NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(
                    observer, true)
        }
    }

    /**
     * 观察者
     * @return
     */
    private val observer: Observer<StatusCode>//                        被踢下线进入主页
        get() = Observer { status ->
            when (status) {
                StatusCode.KICKOUT -> if (Utils.isForeground(GKApplication.instance)) {
                    Toast.makeText(GKApplication.instance, R.string.kickout, Toast.LENGTH_SHORT).show()
                    GKApplication.instance.loginOff()
                }
                StatusCode.NET_BROKEN -> {
                    val preferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Activity.MODE_PRIVATE)
                    if (!preferences.getString(Constants.USER_ACCOUNT, "").isEmpty()) {
                        //已登录才提示
                        Toast.makeText(GKApplication.instance, R.string.network_error, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }


    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private fun loginInfo(): LoginInfo? {
        return loginInfo
    }

    /**
     * // 从本地读取上次登录成功时保存的用户登录信息
     * @return
     */
    private val loginInfo: LoginInfo?
        get() {
            val preferences = GKApplication.instance.getSharedPreferences(Constants.USER_TABLE, Context.MODE_PRIVATE)
            val token = preferences.getString(Constants.USER_ACCOUNT, "")
            val password = preferences.getString(Constants.USER_PASSWORD, "")
            return if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(token)) {
                LoginInfo(token, password)
            } else {
                null
            }
        }

    /**
     * 主进程
     * @return
     */
    private fun inMainProcess(): Boolean {
        val packageName = GKApplication.instance.getPackageName()
        val processName = getProcessName()
        return packageName == processName
    }


    /**
     * 获取当前进程名
     * @return 进程名
     */
    // ActivityManager
    // go home
    // take a rest and again
    fun getProcessName(): String? {
        var processName: String? = null
        val am = GKApplication.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        while (true) {
            for (info in am.runningAppProcesses) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName
                    break
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName
            }
            try {
                Thread.sleep(100L)
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }

        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    private fun options(): SDKOptions {
        val options = SDKOptions()

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        val config = StatusBarNotificationConfig()
        config.notificationEntrance = MainActivity::class.java // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher
        options.statusBarNotificationConfig = config

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = GKApplication.instance.cacheDir.absolutePath + "/nim"

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true

        // 配置附件缩略图的尺寸大小，该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = Utils.getScreenWidthHeight()[0] / 2

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = object : UserInfoProvider {
            override fun getUserInfo(account: String): UserInfoProvider.UserInfo? {
                return null
            }

            override fun getDefaultIconResId(): Int {
                return R.mipmap.avatar_def
            }

            override fun getTeamIcon(tid: String): Bitmap? {
                return null
            }

            override fun getAvatarForMessageNotifier(account: String): Bitmap? {
                return null
            }

            override fun getDisplayNameForMessageNotifier(account: String, sessionId: String,
                                                          sessionType: SessionTypeEnum): String? {
                return null
            }
        }
        return options
    }


    /**
     * 监听系统通知
     */
    private fun observeCustomNotification() {
        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification({ customNotification ->
            val content = customNotification.content
            try {
                val json = JSONObject(content)
                if (json.has("code")) {
                    val code = Integer.valueOf(json.getString("code"))
                    if (code == -1) {//呼叫 连线成功
                        GKApplication.instance.sendBroadcast(Intent(Constants.ONLINE_SUCCESS_ACTION))
                    } else if (code == -2) {//挂断 联系失败
                        val intent = Intent(Constants.FAMILY_FAILED_JOIN_METTING)
                        GKApplication.instance.sendBroadcast(intent)
                    } else if (code == 0) {
                        //表示远端进入通话 更新计时器
                        val intent = Intent(Constants.FAMILY_JOIN_METTING)
                        GKApplication.instance.sendBroadcast(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, true)
    }

}

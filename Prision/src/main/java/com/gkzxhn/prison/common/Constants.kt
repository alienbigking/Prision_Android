package com.gkzxhn.prison.common

import android.os.Environment

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

object Constants {
    /*-------------------------------Config-------------------------------------------------*/
    val CACHE_FILE = GKApplication.instance.filesDir.absolutePath

    val APK_NAME="/app.apk"
    val IS_DEBUG_MODEL = false//debug模式打印日志到控制台,发布版本不打印
    val REQUEST_TIMEOUT = 30000//超时时间半分钟
    /*-------------------------------User Tab-------------------------------------------------*/
    val USER_TABLE = "user_table"
    val USER_IS_UNAUTHORIZED = "isUnauthorized"
    val USER_ACCOUNT = "user_account"//云信帐号
    val USER_PASSWORD = "user_password"//云信密码
    val TERMINAL_ACCOUNT = "terminal_account"//终端帐号
    val TERMINAL_RATE = "terminal_rate"//终端码率
    val TERMINAL_PASSWORD = "terminal_password"//终端密码，空
    val LAST_IGNORE_VERSION = "last_ignore_version"//上一个忽略的版本
    val OTHER_CARD = "other_card"//身份证信息

    val CALL_FREE_TIME = "call_free_time"//免费呼叫次数
    val IS_OPEN_USB_RECORD = "is_open_usb_record"//是否开启usb录屏状态 true/false 开启／关闭


    /*-------------------------------request url-------------------------------------------------*/
    val RELEASE_DOMAIN = "https://www.yuwugongkai.com"//新发布正式环境
    val DEMO_DOMAIN = "https://www.fushuile.com"//开发环境
    val DOMAIN_NAME_XLS = RELEASE_DOMAIN

    val REQUEST_MEETING_LIST_URL = DOMAIN_NAME_XLS + "/api/v1/terminals"//会见列表
    val REQUEST_CANCEL_MEETING_URL = DOMAIN_NAME_XLS + "/api/v1/meetings"// 取消会见
    val REQUEST_MEETING_DETAIL_URL = DOMAIN_NAME_XLS + "/api/v1/families"// 会见详情
    val REQUEST_VERSION_URL = DOMAIN_NAME_XLS + "/api/v1/versions/2"//版本更新
    val REQUEST_CRASH_LOG_URL = DOMAIN_NAME_XLS + "/api/v1/loggers"//奔溃日志
    val REQUEST_MEETING_ROOM = DOMAIN_NAME_XLS + "/api/v1/terminals"//会议室信息

    val REQUEST_FREE_MEETING_TIME = DOMAIN_NAME_XLS + "/api/v1/jails"//免费呼叫次数

    /*-------------------------------msg what-------------------------------------------------*/
    val START_REFRESH_UI = 1
    val STOP_REFRESH_UI = 2//msg what
    /*-------------------------------Request Code-------------------------------------------------*/
    val EXTRA = "extra"
    val EXTRA_TAB = "extra_tab"
    val EXTRAS = "extras"
    val EXTRA_ENTITY = "extra_entity"
    val EXTRA_POSITION = "extra_position"
    val EXTRA_CODE = 0x001
    val PREVIEW_PHOTO_CODE = 0x102
    val SELECT_PHOTO_CODE = 0x103
    val TAKE_PHOTO_CODE = 0x104
    val RESIZE_REQUEST_CODE = 0x105
    val EXTRAS_CODE = 0x106
    /*-------------------------------Request Tab-------------------------------------------------*/
    val MAIN_TAB = 0x206
    val CLOSE_GUI_TAB = 0x207
    /*-------------------------------action-------------------------------------------------*/

    val ONLINE_SUCCESS_ACTION = "com.gkzxhn.prison.ONLINE_SUCCESS_ACTION"//连线成功
    //云信被踢下线
    val NIM_KIT_OUT = "com.gkzxhn.prison.NIM_KIT_OUT"


    //C9包名
    val C9_PACKAGE_NAME="cn.com.rocware.c9gui"
    //免费会见
    val CALL_FREE_ACTION = "com.gkzxhn.prison.CALL_FREE_ACTION"
    val CALL_DEFUALT_ACTION = "com.gkzxhn.prison.CALL_DEFUALT_ACTION"

    //家属加入会议失败
    val FAMILY_FAILED_JOIN_METTING = "com.gkzxhn.prison.CALL_HANG_UP"
    //家属加入会议
    val FAMILY_JOIN_METTING = "com.gkzxhn.prison.FAMILY_JOIN_METTING"
    //监狱加入会议
    val PRISION_JOIN_METTING = "com.gkzxhn.prison.PRISION_JOIN_METTING"
    //紫荆会议主之人密码密码
    val ZIJING_PASSWORD = "zijing_password"
    val ACCID = "accid"
    val PROTOCOL = "protocol"
    val TIME_LIMIT = "time_limit"
    val CALL_AGAIN = "call_again"
    val USER_ACCOUNT_CACHE = "user_account_cache"
    val USER_PASSWORD_CACHE = "user_password_cache"
    val END_REASON = "end_reason"
}

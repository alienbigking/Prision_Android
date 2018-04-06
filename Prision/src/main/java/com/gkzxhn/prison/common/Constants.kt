package com.gkzxhn.prison.common

import android.os.Environment

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

object Constants {
    /*-------------------------------Config-------------------------------------------------*/

    val SD_ROOT_PATH = Environment.getExternalStorageDirectory().path + "/YWTPrision"
    val SD_FILE_CACHE_PATH = SD_ROOT_PATH + "/cache/"
    val CACHE_FILE = GKApplication.instance.filesDir.absolutePath
    val APK_C9_DIR = "data/app"

    val SD_IMAGE_CACHE_PATH = SD_ROOT_PATH + "/imageCache/"//图片下载的缓存
    val SD_ROOT_PHOTO_PATH = SD_ROOT_PATH + "/photo/"//图片，不自动删除
    val SD_PHOTO_PATH = SD_ROOT_PHOTO_PATH + "cutPhoto/"//拍照存储或压缩图片的图片路径,启动时自动删除
    val SD_VIDEO_PATH = SD_ROOT_PATH + "/video/"
    val IS_DEBUG_MODEL = false//debug模式打印日志到控制台,发布版本不打印
    val REQUEST_TIMEOUT = 30000//超时时间半分钟
    /*-------------------------------User Tab-------------------------------------------------*/
    val USER_TABLE = "user_table"
    val USER_ACCOUNT_TABLE = "user_account_table"//记住账号密码
    val USER_IS_UNAUTHORIZED = "isUnauthorized"
    val USER_ID = "user_id"
    val IS_FIRST_IN = "is_first_in"
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
    /*-------------------------------action-------------------------------------------------*/

    val ONLINE_FAILED_ACTION = "com.gkzxhn.prison.ONLINE_FAILED_ACTION"//连线失败
    val ONLINE_SUCCESS_ACTION = "com.gkzxhn.prison.ONLINE_SUCCESS_ACTION"//连线成功
    //云信被踢下线
    val NIM_KIT_OUT = "com.gkzxhn.prison.NIM_KIT_OUT"

    //退出视频会议
    val MEETING_FORCE_CLOSE_ACTION = "com.gkzxhn.prison.MEETING_FORCE_CLOSE_ACTION"
    //接收双流
    val MEETING_ASSSENDSREAMSTATUSNTF_ACTION = "com.gkzxhn.prison.MEETING_ASSSENDSREAMSTATUSNTF_ACTION"
    //根据呼叫状态选择是否切换界面
    val MEETING_SWITCHVCONFVIEW_ACTION = "com.gkzxhn.prison.MEETING_SWITCHVCONFVIEW_ACTION"
    //设置哑音图标
    val MEETING_MUTEIMAGE_ACTION = "com.gkzxhn.prison.MEETING_MUTEIMAGE_ACTION"
    //不设置哑音图标
    val MEETING_NOT_MUTEIMAGE_ACTION = "com.gkzxhn.prison.MEETING_NOT_MUTEIMAGE_ACTION"
    // 设置静音
    val MEETING_QUIETIMAGE_ACTION = "com.gkzxhn.prison.MEETING_QUIETIMAGE_ACTION"
    // 不设置静音
    val MEETING_NOT_QUIETIMAGE_ACTION = "com.gkzxhn.prison.MEETING_NOT_QUIETIMAGE_ACTION"
    // 刷新音视频下面的工具栏
    val MEETING_REMOVEREQCHAIRMANHANDLER_ACTION = "com.gkzxhn.prison.MEETING_REMOVEREQCHAIRMANHANDLER_ACTION"
    // 刷新音视频下面的工具栏
    val MEETING_REMOVEREQSPEAKERHANDLER_ACTION = "com.gkzxhn.prison.MEETING_REMOVEREQSPEAKERHANDLER_ACTION"

    //开始录屏
    val START_RECORDSCREEN_ACTION = "com.gkzxhn.prison.START_RECORDSCREEN_ACTION"
    //结束录屏
    val STOP_RECORDSCREEN_ACTION = "com.gkzxhn.prison.STOP_RECORDSCREEN_ACTION"

    //免费会见
    val CALL_FREE_ACTION = "com.gkzxhn.prison.CALL_FREE_ACTION"
    val CALL_DEFUALT_ACTION = "com.gkzxhn.prison.CALL_DEFUALT_ACTION"

    /**
     * C9上行消息发送广播
     */
    val ZIJING_ACTION = "zijing_action"
    //紫荆上报消息
    val ZIJING_JSON = "jsonObject"
    //紫荆会议主之人密码密码
    val ZIJING_PASSWORD = "zijing_password"
    val HANGUP = "hangup"
    val ACCID = "accid"
    val PROTOCOL = "protocol"
    val TIME_LIMIT = "time_limit"
    val CALL_AGAIN = "call_again"
    val USER_ACCOUNT_CACHE = "user_account_cache"
    val USER_PASSWORD_CACHE = "user_password_cache"
    val END_REASON = "end_reason"
    //远端连接
    val TIME_CONNECT = "time_connect"
}

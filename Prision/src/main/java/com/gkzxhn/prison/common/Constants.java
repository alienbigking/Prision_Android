package com.gkzxhn.prison.common;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 17/3/29.
 */

public interface Constants {
    /*-------------------------------Config-------------------------------------------------*/

    String SD_ROOT_PATH= Environment.getExternalStorageDirectory().getPath()+"/YWTPrision";
    String SD_FILE_CACHE_PATH = SD_ROOT_PATH+"/cache/";
    String CACHE_FILE = GKApplication.getInstance().getFilesDir().getAbsolutePath();
    String APK_C9_DIR = "data/app";

    String SD_IMAGE_CACHE_PATH = SD_ROOT_PATH+"/imageCache/";//图片下载的缓存
    String SD_ROOT_PHOTO_PATH = SD_ROOT_PATH+"/photo/";//图片，不自动删除
    String SD_PHOTO_PATH = SD_ROOT_PHOTO_PATH+"cutPhoto/";//拍照存储或压缩图片的图片路径,启动时自动删除
    String SD_VIDEO_PATH = SD_ROOT_PATH+"/video/";
    boolean IS_DEBUG_MODEL=false;//debug模式打印日志到控制台,发布版本不打印
    int REQUEST_TIMEOUT=60000;//超时时间1分钟
    /*-------------------------------User Tab-------------------------------------------------*/
    String USER_TABLE="user_table";
    String USER_ACCOUNT_TABLE="user_account_table";//记住账号密码
    String USER_IS_UNAUTHORIZED="isUnauthorized";
    String USER_ID="user_id";
    String IS_FIRST_IN="is_first_in";
    String USER_ACCOUNT="user_account";//云信帐号
    String USER_PASSWORD="user_password";//云信密码
    String TERMINAL_ACCOUNT="terminal_account";//终端帐号
    String TERMINAL_RATE="terminal_rate";//终端码率
    String TERMINAL_PASSWORD="terminal_password";//终端密码，空
    String LAST_IGNORE_VERSION="last_ignore_version";//上一个忽略的版本
    String OTHER_CARD="other_card";//身份证信息

    String CALL_FREE_TIME="call_free_time";//免费呼叫次数
    String IS_OPEN_USB_RECORD="is_open_usb_record";//是否开启usb录屏状态 true/false 开启／关闭


    /*-------------------------------request url-------------------------------------------------*/
    String RELEASE_DOMAIN="https://www.yuwugongkai.com";//新发布正式环境
    String DEMO_DOMAIN="https://www.fushuile.com";//开发环境
    String DOMAIN_NAME_XLS = RELEASE_DOMAIN;

    String REQUEST_MEETING_LIST_URL=DOMAIN_NAME_XLS+"/api/v1/terminals";//会见列表
    String REQUEST_CANCEL_MEETING_URL=DOMAIN_NAME_XLS+"/api/v1/meetings";// 取消会见
    String REQUEST_MEETING_DETAIL_URL=DOMAIN_NAME_XLS+"/api/v1/families";// 会见详情
    String REQUEST_VERSION_URL=DOMAIN_NAME_XLS+"/api/v1/versions/2";//版本更新
    String REQUEST_CRASH_LOG_URL=DOMAIN_NAME_XLS+"/api/v1/loggers";//版本更新
    String REQUEST_MEETING_ROOM=DOMAIN_NAME_XLS+"/api/v1/terminals";//会议室信息

    String REQUEST_FREE_MEETING_TIME=DOMAIN_NAME_XLS+"/api/v1/jails";//免费呼叫次数

    /*-------------------------------msg what-------------------------------------------------*/
    int START_REFRESH_UI=1,STOP_REFRESH_UI=2;//msg what
    /*-------------------------------Request Code-------------------------------------------------*/
    String EXTRA="extra";
    String EXTRA_TAB="extra_tab";
    String EXTRAS="extras";
    String EXTRA_ENTITY="extra_entity";
    String EXTRA_POSITION="extra_position";
    public  int EXTRA_CODE=0x001;
    int PREVIEW_PHOTO_CODE=0x102;
    int SELECT_PHOTO_CODE=0x103;
    int TAKE_PHOTO_CODE=0x104;
    int RESIZE_REQUEST_CODE=0x105;
    int EXTRAS_CODE=0x106;
    /*-------------------------------action-------------------------------------------------*/
    String _ACTION="com.gkzxhn.prison.TERMINAL_SUCCESS";//注册终端成功

    String ONLINE_FAILED_ACTION="com.gkzxhn.prison.ONLINE_FAILED_ACTION";//连线失败
    String ONLINE_SUCCESS_ACTION="com.gkzxhn.prison.ONLINE_SUCCESS_ACTION";//连线成功
    //云信被踢下线
    String NIM_KIT_OUT="com.gkzxhn.prison.NIM_KIT_OUT";

    //退出视频会议
    String MEETING_FORCE_CLOSE_ACTION="com.gkzxhn.prison.MEETING_FORCE_CLOSE_ACTION";
    //接收双流
    String MEETING_ASSSENDSREAMSTATUSNTF_ACTION="com.gkzxhn.prison.MEETING_ASSSENDSREAMSTATUSNTF_ACTION";
    //根据呼叫状态选择是否切换界面
    String MEETING_SWITCHVCONFVIEW_ACTION="com.gkzxhn.prison.MEETING_SWITCHVCONFVIEW_ACTION";
    //设置哑音图标
    String MEETING_MUTEIMAGE_ACTION="com.gkzxhn.prison.MEETING_MUTEIMAGE_ACTION";
    //不设置哑音图标
    String MEETING_NOT_MUTEIMAGE_ACTION="com.gkzxhn.prison.MEETING_NOT_MUTEIMAGE_ACTION";
    // 设置静音
    String MEETING_QUIETIMAGE_ACTION="com.gkzxhn.prison.MEETING_QUIETIMAGE_ACTION";
    // 不设置静音
    String MEETING_NOT_QUIETIMAGE_ACTION="com.gkzxhn.prison.MEETING_NOT_QUIETIMAGE_ACTION";
    // 刷新音视频下面的工具栏
    String MEETING_REMOVEREQCHAIRMANHANDLER_ACTION="com.gkzxhn.prison.MEETING_REMOVEREQCHAIRMANHANDLER_ACTION";
    // 刷新音视频下面的工具栏
    String MEETING_REMOVEREQSPEAKERHANDLER_ACTION="com.gkzxhn.prison.MEETING_REMOVEREQSPEAKERHANDLER_ACTION";

    //开始录屏
    String START_RECORDSCREEN_ACTION="com.gkzxhn.prison.START_RECORDSCREEN_ACTION";
    //结束录屏
    String STOP_RECORDSCREEN_ACTION="com.gkzxhn.prison.STOP_RECORDSCREEN_ACTION";


    int RETRY_TIME = 10000;
    /**
     * C9上行消息发送广播
     */
    String ZIJING_ACTION = "zijing_action";
    //紫荆上报消息
    String ZIJING_JSON = "jsonObject";
    //紫荆会议主之人密码密码
    String ZIJING_PASSWORD = "zijing_password";
    String HANGUP = "hangup";
    String ACCID = "accid";
    String PROTOCOL = "protocol";
    String TIME_LIMIT = "time_limit";
    String CALL_AGAIN = "call_again";
    String USER_ACCOUNT_CACHE = "user_account_cache";
    String USER_PASSWORD_CACHE = "user_password_cache";
    String END_REASON = "end_reason";
    //远端连接
    String TIME_CONNECT = "time_connect";
}

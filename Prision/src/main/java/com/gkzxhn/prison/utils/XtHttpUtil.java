package com.gkzxhn.prison.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * http地址
 * Created by lv on 2016/9/30.
 */

public class XtHttpUtil {


    private final static String TAG = "XTHttpUtil";


    public static final String mAdmin="admin";//表示登陆设置用户名

   
//   public final static String credential = Credentials.basic("", "");

//           public static String URL="http://"+GetIpUtil.getIPAddress(true);//获取ipv4地址
    private static String URL = "http://127.0.0.1:8080";
    // public static String URL="http://192.168.1.45";//获取ipv4地址


    public final static Map<String, String> map = new HashMap<>();

    //logo地址
    public final static String LOGO = "http://127.0.0.1:88" + "/custom/logo.png";
    // http請求地址

 /*  - - - - - - 初始化- - - - - - - - - */

    //重置
    public final static String RESET = URL + "/api/v1/event/resetqid/?role=gui";
    //清除
    public final static String CLEAR = URL + "/api/v1/event/clear/?role=gui&qid=";
    //查询
    public final static String QUERY = URL + "/api/v1/event/get/?role=gui&qid=";


    /*  - - - - - - 2.1- - - - - - - - - */
    //关机
    public final static String POWEROFF = URL + "/api/v1/poweroff/";
    //重启
    public final static String REBOOT = URL + "/api/v1/reboot/";
    //待机
    public final static String STANDBY = URL + "/api/v1/standby/";
    //唤醒
    public final static String RESUME = URL + "/api/v1/resume/";
    //保活
    public final static String KEEPALIVE = URL + "/api/v1/keepalive/";


    /*  - - - - - - 2.2- - - - - - - - - */
    //发起呼叫
    public final static String DIAL = URL + "/api/v1/call/dial/";
    //接受呼叫
    public final static String ANSWER = URL + "/api/v1/call/answer/";
    //拒绝呼叫
    public final static String REJECT = URL + "/api/v1/call/reject/";
    //挂断呼叫
    public final static String HANGUP = URL + "/api/v1/call/hangup/";

    //发送DTMF
    public final static String SENDDTMF = URL + "/api/v1/call/senddtmf/";
    //查看双流状态
    public final static String FLOW_STATUS = URL + "/api/v1/call/presentation/status/";
    //获取当前呼叫的统计信息
    public final static String DIAL_INFO = URL + "/api/v1/cal/stat/get/0/";
    //获取呼叫选项
    public final static String GET_DIAL_CHOOSE = URL + "/api/v1/preferences/call/get/0/";
    //设置呼叫选项
    public final static String SET_DIAL_CHOOSE = URL + "/api/v1/preferences/call/set/";
    //恢复呼叫默认选项设置
    public final static String RESET_DISL_CHOOSE = URL + "/api/v1/preferences/call/reset/";


    /*  - - - - - - 2.3- - - - - - - - - */
    //查询呼叫记录
    public final static String GET_DIAL_HISTORY = URL + "/api/v1/history/contacts/get/";
    //删除呼叫记录
    public final static String DEL_DIAL_HISTORY = URL + "/api/v1/history/contact/remove/";
    //清除全部呼叫记录
    public final static String CLEAR_DIAL_HISTIRY = URL + "/api/v1/history/clear/";


    /*  - - - - - - 2.4- - - - - - - - - */
    //查询联系人
    public final static String LOOK_CONTACTS = URL + "/api/v1/simplebook/contacts/";
    //增加联系人
    public final static String ADD_CONTACTS = URL + "/api/v1/simplebook/contact/add/";
    //删除联系人
    public final static String DEL_CONTACTS = URL + "/api/v1/simplebook/contact/remove/";
    //修改联系人
    public final static String UPDATA_CONTACTS = URL + "/api/v1/simplebook/contact/update/";
    //清除联系人
    public final static String CLEAR_CONTACTS = URL + "/api/v1/simplebook/contact/clear/";


    /*  - - - - - - 2.5- - - - - - - - - */
    //保持摄像机参数
    public final static String SAVE_CAMERA = URL + "/api/v1/camera/setting/save/";
    //获取白平衡参数
    public final static String GET_WB = URL + "/api/v1/camera/wb/get/0/";
    //设置白平衡参数
    public final static String SET_WB = URL + "/api/v1/camera/wb/set/";
    //获取背光补偿参数
    public final static String GET_BLC = URL + "/api/v1/camera/backlight/get/0/";
    //设置背光补偿参数
    public final static String SET_BLC = URL + "/api/v1/camera/backlight/set/";
    //获取抗闪烁参数
    public final static String GET_AFS = URL + "/api/v1/camera/flicker/get/0/";
    //设置抗闪烁参数
    public final static String SET_AFS = URL + "/api/v1/camera/flicker/set/";
    //获取倒装参数inversion
    public final static String GET_IS = URL + "/api/v1/camera/flip/get/0/";
    //设置倒装参数
    public final static String SET_IS = URL + "/api/v1/camera/flip/set/";
    //获取对比度、亮度、色素度contrast
    public final static String GET_CONTRAST = URL + "/api/v1/camera/image/get/0/";
    //设置对比度、亮度、色素度
    public final static String SET_CONTRAST = URL + "/api/v1/camera/image/set/";

    //查询预位置
    public final static String GET_PRESET = URL + "/api/v1/camera/preset/get/";
    //设置、清除、调用预位置
    public final static String SET_PRESET = URL + "/api/v1/camera/preset/set/";
    //本端转动控制
    public final static String SET_PTZ = URL + "/api/v1/camera/ptz/set/";
    //远端转动和变焦控制
    public final static String SET_FAR = URL + "/api/v1/call/farend/camera/control/";


    //变焦控制
    public final static String SET_ZOOM = URL + "/api/v1/camera/zoom/set/";
    //远端变焦

    public final static String CAMERA_CONTROL = URL + "/api/v1/camera/controlmode/set/";

    /*  - - - - - - 2.6- - - - - - - - - */


    //获取网络设置
    public final static String GET_NETWORK = URL + "/api/v1/network/config-get/";
    //修改网络设置
    public final static String SET_NETWORK = URL + "/api/v1/network/config-set/";
    //重置网络设置
    public final static String RESET_NETWORK = URL + "/api/v1/network/config-reset/";

    //获取防火墙设置
    public final static String GET_FIREWALL = URL + "/api/v1/preferences/firewall/get/0/";
    //修改防火墙设置
    public final static String SET_FIREWALL = URL + "/api/v1/preferences/firewall/set/";
    //恢复防火墙默认设置
    public final static String RESET_FIREWALL = URL + "/api/v1/preferences/firewall/reset/";
    //获取服务质量
    public final static String GET_QOS = URL + "/api/v1/preferences/qos/get/0/";
    //修改服务质量
    public final static String SET_QOS = URL + "/api/v1/preferences/qos/set/";
    //恢复服务默认质量
    public final static String RESET_QOS = URL + "/api/v1/preferences/qos/reset/";
    //获取网络状态
    public final static String GET_NETWORK_STATUS = URL + "/api/v1/network/status/";

    //获取VPN设置
    public final static String GET_VPN = URL + "/api/v1/preferences/vpn/get/0/";
    //修改VPN设置
    public final static String SET_VPN = URL + "/api/v1/preferences/vpn/set/";
    //重置VPN设置
    public final static String RESET_VPN = URL + "/api/v1/preferences/vpn/reset/";

    /*  - - - - - - 2.7- - - - - - - - - */
    //获取终端名称及其编码方式
    public final static String GET_GENGRAL = URL + "/api/v1/preferences/general/get/0/";
    //修改终端名称及其编码方式
    public final static String SET_GENGRAL = URL + "/api/v1/preferences/general/set/";
    //恢复终端名称及其编码方式的默认设置
    public final static String RESET_GENGRAL = URL + "/api/v1/preferences/general/reset/";

    //查询紫荆云账号信息
    public final static String GET_AUTHENTICATION = URL + "/api/v1/account/authentication/info/get/";
    //设置紫荆云账号信息
    public final static String SET_AUTHENTICATION = URL + "/api/v1/account/authentication/info/set/";

    //查询账号
    public final static String GET_ACCOUNT = URL + "/api/v1/account/get/";
    //添加账号
    public final static String ADD_ACCOUNT = URL + "/api/v1/account/add/";
    //修改账号
    public final static String EDIT_ACCOUNT = URL + "/api/v1/account/edit/";

    //查询SIP注册方式
    public final static String GET_SIP_REGISTER_MODE = URL + "/api/v1/account/sip/register/mode/get/0/";
    //设置SIP注册方式
    public final static String SET_SIP_REGISTER_MODE = URL + "/api/v1/account/sip/register/mode/set/";
    //查询SIP注册状态
    public final static String GET_SIP_REGISTER_STATUS = URL + "/api/v1/account/sip/status/get/";
    //查询GK注册方式
    public final static String GET_GK_REGISTER_MODE = URL + "/api/v1/account/gk/register/mode/get/0/";
    //设置GK注册方式
    public final static String SET_GK_REGISTER_MODE = URL + "/api/v1/account/gk/register/mode/set/";
    //查询GK注册状态
    public final static String GET_GK_REGISTER_STATUS = URL + "/api/v1/account/gk/status/get/";


    /*  - - - - - - 2.8- - - - - - - - - */
    //增大输出音量
    public final static String INCREASE_AUDIOUT = URL + "/api/v1/preferences/audioout/volume/increase/";
    //减少输出音量
    public final static String DECREASE_AUDIOUT = URL + "/api/v1/preferences/audioout/volume/decrease/";
    //增大输入音量
    public final static String INCREASE_AUDIIN = URL + "/api/v1/preferences/audioin/volume/increase/";
    //减少输入音量
    public final static String DECREASE_AUDIIN = URL + "/api/v1/preferences/audioin/volume/decrease/";
    //输入静音控制
    public final static String MUTE_AUDIIN = URL + "/api/v1/preferences/audioin/mute/";
    //获取音频输入设置
    public final static String GET_AUDIIN = URL + "/api/v1/preferences/audioin/get/0/";
    //修改音频输入设置
    public final static String SET_AUDIIN = URL + "/api/v1/preferences/audioin/set/";
    //恢复音频默认输入设置
    public final static String RESET_AUDIIN = URL + "/api/v1/preferences/audioin/reset/";
    //获取音频输出设置
    public final static String GET_AUDIOUT = URL + "/api/v1/preferences/audioout/get/0/";
    //修改音频输出设置
    public final static String SET_AUDIOUT = URL + "/api/v1/preferences/audioout/set/";
    //恢复音频默认输出设置
    public final static String RESET_AUDIOUT = URL + "/api/v1/preferences/audioout/reset/";

    //获取回声消除设置
    public final static String GET_ACE = URL + "/api/v1/preferences/aec/get/0/";
    //修改回声消除设置
    public final static String SET_ACE = URL + "/api/v1/preferences/aec/set/";
    //恢复回声消除默认设置
    public final static String RESET_ACE = URL + "/api/v1/preferences/aec/reset/";

    /*  - - - - - - 2.9- - - - - - - - - */
    //显示模式切换
    public final static String SET_DISPLAYMODE = URL + "/api/v1/displaymode/set/";

    //查詢双流
    public final static String GET_PRESENTATION = URL + "/api/v1/call/presentation/status/";
    //发送双流
    public final static String OPEN_PRESENTATION = URL + "/api/v1/presentation/open/";
    //停止发送双流
    public final static String CLOSE_PRESENTATION = URL + "/api/v1/presentation/close/";

    //查询双流预览
    public final static String GET_PREVIEW = URL + "/api/v1/layout/get-presentation-preview-status/";
    //打开双流预览
    public final static String OPEN_PREVIEW = URL + "/api/v1/layout/start-presentation-preview/";
    //关闭双流预览
    public final static String CLOSE_PREVIEW = URL + "/api/v1/layout/stop-presentation-preview/";


    //获取视频输入参数
    public final static String GET_INPUTFORMAT = URL + "/api/v1/camera/visca/net/get/0/";
    //设置视频输入参数
    public final static String SET_INPUTFORMAT = URL + "/api/v1/camera/visca/net/set/";
    //恢复视频输入默认参数
    public final static String RESET_INPUTFORMAT = URL + "/api/v1/camera/visca/net/reset/";

    //获取视频输出参数
    public final static String GET_OUTPUTFORMAT = URL + "/api/v1/outputFormat/get/0/";
    //设置视频输出参数
    public final static String SET_OUTPUTFORMAT = URL + "/api/v1/outputFormat/set/";
    //恢复视频输出默认参数
    public final static String RESET_OUTPUTFORMAT = URL + "/api/v1/outputFormat/reset/";
    //获取视频源信息
    public final static String GET_VIDEOSOURCE = URL + "/api/v1/video/source/in/info/";

    /*  - - - - - - 2.10  - - - - - - - - - */
    //获取系统安全设置
    public final static String GET_SYSTEM_SEC = URL + "/api/v1/preferences/security/system/get/0/";
    //修改系统安全设置
    public final static String SET_SYSTEM_SEC = URL + "/api/v1/preferences/security/system/set/";
    //恢复系统安全默认设置
    public final static String RESET_SYSTEM_SEC = URL + "/api/v1/preferences/security/system/reset/";
    //获取H323加密能力设置
    public final static String GET_H323_SEC = URL + "/api/v1/preferences/security/cryptos/h323/get/0/";
    //修改H323加密能力设置
    public final static String SET_H323_SEC = URL + "/api/v1/preferences/security/cryptos/h323/set/";
    //恢复H323加密能力设置
    public final static String RESET_H323_SEC = URL + "/api/v1/preferences/security/cryptos/h323/reset/";
    //获取SIP加密能力设置
    public final static String GET_SIP_SEC = URL + "/api/v1/preferences/security/cryptos/sip/get/0/";
    //修改SIP加密能力设置
    public final static String SET_SIP_SEC = URL + "/api/v1/preferences/security/cryptos/sip/set/";
    //恢复SIP加密能力设置
    public final static String RESET_SIP_SEC = URL + "/api/v1/preferences/security/cryptos/sip/reset/";

    /*  - - - - - - 2.11  - - - - - - - - - */
    //获取H323协议设置
    public final static String GET_H323_PREF = URL + "/api/v1/preferences/h323/get/0/";
    //修改H323协议设置
    public final static String SET_H323_PREF = URL + "/api/v1/preferences/h323/set/";
    //恢复H323协议默认设置
    public final static String RESET_H323_PREF = URL + "/api/v1/preferences/h323/reset/";
    //获取SIP协议设置
    public final static String GET_SIP_PREF = URL + "/api/v1/preferences/sip/get/0/";
    //修改SIP协议设置
    public final static String SET_SIP_PREF = URL + "/api/v1/preferences/sip/set/";
    //恢复SIP协议默认设置
    public final static String RESET_SIP_PREF = URL + "/api/v1/preferences/sip/reset/";

    /*  - - - - - - 2.12.1   - - - - - - - - - */
    //获取升级设置
    public final static String GET_UPGRADE = URL + "/api/v1/preferences/upgrade/get/0/";
    //修改升级设置
    public final static String SET_UPGRADE = URL + "/api/v1/preferences/upgrade/set/";
    //恢复升级默认设置
    public final static String RESET_UPGRADE = URL + "/api/v1/preferences/upgrade/reset/";
    //查询版本系统信息
    public final static String VERSIONS_INFO = URL + "/api/v1/sysinfo/get/0/";

    /*  - - - - -  - -  补充-  - -  -  - - - */
    //获取内置MCU查询
    public final static String GET_MCU = URL + "/api/v1/preferences/emcu/get/0/";
    public final static String SET_MCU = URL + "/api/v1/preferences/emcu/set/";
    public final static String RESET_MCU = URL + "/api/v1/preferences/emcu/reset/";


    //获取字幕色设置
    public final static String GET_ZIMU = URL + "/api/v1/camera/string/dispaly/get/0/";
    //修改字幕设置
    public final static String SET_ZIMU = URL + "/api/v1/camera/string/dispaly/set/";
    //重置字幕设置
    public final static String RESET_ZIMU = URL + "/api/v1/camera/string/dispaly/reset/";

    //获取电源设置
    public final static String GET_POWER = URL + "/api/v1/preferences/power/get/0/";
    //修改电源设置
    public final static String SET_POWER = URL + "/api/v1/preferences/power/set/";
    //重置电源设置
    public final static String RESET_POWER = URL + "/api/v1/preferences/power/reset/";


    //获取显示名称设置
    public final static String GET_SHOWNAME = URL + "/api/v1/preferences/general/get/0/";
    //修改显示名称设置
    public final static String SET_SHOWNAME = URL + "/api/v1/preferences/general/set/";
    //重置显示名称设置
    public final static String RESET_SHOWNAME = URL + "/api/v1/preferences/general/reset/";


    /**
     * 这里要参数
     * acc_type: "h323"
     */
    //获取H323账号设置
    public final static String GET_H323ACCOUNT = URL + "/api/v1/account/get/0/";
    //修改H323账号设置
    public final static String SET_H323ACCOUNT_EDIT = URL + "/api/v1/account/edit/";
    public final static String SET_H323ACCOUNT_ADD = URL + "/api/v1/account/add/";
    //重置H323账号设置
    public final static String GET_ZJY_GK = URL + "/api/v1/account/gk/register/mode/get/0/";
    public final static String SET_ZJY_GK = URL + "/api/v1/account/gk/register/mode/set/";
    public final static String GETS_ZJY_GK = URL + "/api/v1/account/gk/authentication/info/get/";
    public final static String SETS_ZJY_GK = URL + "/api/v1/account/gk/authentication/info/set/";

    //国科定制OEM值
    public final static String GET_ZJY_OEM = URL + "/api/v1/oem/get/0/";

    //    public final static String GET_ZJY_ACCOUNT = "http://"+ GetIpUtil.getIPAddress(true)+ "/api/provisioning";
    //注册紫荆云视的账号
    public final static String GET_ZJY_ACCOUNT = "http://127.0.0.1:88" + "/api/provisioning";

    /**
     * 这里要参数
     * acc_type: "sip"
     */
    //获取SIP账号设置
    public final static String GET_SIPACCOUNT = URL + "/api/v1/account/get/0/";
    //修改SIP账号设置
    public final static String SET_SIPACCOUNT_EEDIT = URL + "/api/v1/account/edit/";
    public final static String SET_SIPACCOUNT_ADD = URL + "/api/v1/account/add/";
    //重置SIP账号设置
    //获取sip中的紫荆云账号
    public final static String GET_ZJY_SIP = URL + "/api/v1/account/sip/register/mode/get/0/";
    public final static String SET_ZJY_SIP = URL + "/api/v1/account/sip/register/mode/set/";
    public final static String GETS_ZJY_SIP = URL + "/api/v1/account/sip/authentication/info/get/";
    public final static String SETS_ZJY_SIP = URL + "/api/v1/account/sip/authentication/info/set/";
    //获取与真账号
    public final static String GET_YZ_ACCOUNT = URL + "/api/v1/account/sip/meetime/authentication/info/get/";
    //保存与真账号
    public final static String SET_YZ_ACCOUNT = URL + "/api/v1/account/sip/meetime/authentication/info/set/";

    //获取呼叫选项设置
    public final static String GET_CALLCHOOSE = URL + "/api/v1/preferences/call/get/0/";
    //修改呼叫选项设置
    public final static String SET_CALLCHOOSE = URL + "/api/v1/preferences/call/set/";
    //重置呼叫选项设置
    public final static String RESET_CALLCHOOSE = URL + "/api/v1/preferences/call/reset/";

    //获取音频能力设置
    public final static String GET_AUDIOABL = URL + "/api/v1/preferences/codecs/audio/get/0/";
    //修改音频能力设置
    public final static String SET_AUDIOABL = URL + "/api/v1/preferences/codecs/audio/set/";
    //重置音频能力设置
    public final static String RESET_AUDIOABL = URL + "/api/v1/preferences/codecs/audio/reset/";


    //获取USB能力设置
    public final static String GET_USBABL = URL + "/api/v1/preferences/usbaudio/get/0/";
    //修改USB能力设置
    public final static String SET_USBABL = URL + "/api/v1/preferences/usbaudio/set/";
    //重置USB能力设置
    public final static String RESET_USBABL = URL + "/api/v1/preferences/usbaudio/reset/";


    //获取HDMI-1布局设置
    public final static String GET_HDMI1LAY = URL + "/api/v1/preferences/layout/1/get/0/";
    //修改HDMI-1布局设置
    public final static String SET_HDMI1LAY = URL + "/api/v1/preferences/layout/1/set/";
    //重置HDMI-1布局设置
    public final static String RESET_HDMI1LAY = URL + "/api/v1/preferences/layout/1/reset/";

    //获取HDMI-2布局设置
    public final static String GET_HDMI2LAY = URL + "/api/v1/preferences/layout/2/get/0/";
    //修改HDMI-2布局设置
    public final static String SET_HDMI2LAY = URL + "/api/v1/preferences/layout/2/set/";
    //重置HDMI-2布局设置
    public final static String RESET_HDMI2LAY = URL + "/api/v1/preferences/layout/2/reset/";

    //获取视频能力设置
    public final static String GET_VEDIOABL = URL + "/api/v1/preferences/codecs/video/get/0/";
    //修改视频能力设置
    public final static String SET_VEDIOABL = URL + "/api/v1/preferences/codecs/video/set/";
    //重置视频能力设置
    public final static String RESET_VEDIOABL = URL + "/api/v1/preferences/codecs/video/reset/";

    //获取系统安全设置
    public final static String GET_SYSSOFT = URL + "/api/v1/preferences/security/system/get/0/";
    //修改系统安全设置
    public final static String SET_SYSSOFT = URL + "/api/v1/preferences/security/system/set/";
    //重置系统安全设置
    public final static String RESET_SYSSOFT = URL + "/api/v1/preferences/security/system/reset/";

    //获取H323媒体安全设置
    public final static String GET_H323SOFT = URL + "/api/v1/preferences/security/cryptos/h323/get/0/";
    //修改H323媒体安全设置
    public final static String SET_H323SOFT = URL + "/api/v1/preferences/security/cryptos/h323/set/";
    //重置H323媒体安全设置
    public final static String RESET_H323SOFT = URL + "/api/v1/preferences/security/cryptos/h323/reset/";

    //获取SIP媒体安全设置
    public final static String GET_SIPSOFT = URL + "/api/v1/preferences/security/cryptos/sip/get/0/";
    //修改SIP媒体安全设置
    public final static String SET_SIPSOFT = URL + "/api/v1/preferences/security/cryptos/sip/set/";
    //重置SIP媒体安全设置
    public final static String RESET_SIPSOFT = URL + "/api/v1/preferences/security/cryptos/sip/reset/";

    //音频环回查询
    public final static String GET_LOOP_AUDIO = URL + "/api/v1/loop/audio/io/get/0/";
    //音频环回设置
    public final static String SET_LOOP_AUDIO = URL + "/api/v1/loop/audio/io/set/";

    //视频布局1查询
    public final static String GET_LAYOUT1 = URL + "/api/v1/layout/1/get/0/";
    //视频布局1设置
    public final static String SET_LAYOUT1 = URL + "/api/v1/layout/1/set/";
    //视频布局2查询
    public final static String GET_LAYOUT2 = URL + "/api/v1/layout/2/get/0/";
    //视频布局2设置
    public final static String SET_LAYOUT2 = URL + "/api/v1/layout/2/set/";
    //呼叫信息
    public final static String GET_CALLINFO = URL + "/api/v1/call/stat/get/0/";
    //获取呼叫列表
    public final static String GET_CALLLIST = URL + "/api/v1/call/list/get/0";


    //获取本地录播配置
    public final static String GET_RECORDED_LOCAL = URL + "/api/v1/record/parameters/get/0/";
    //设置本地录播配置
    public final static String SET_RECORDED_LOCAL = URL + "/api/v1/record/parameters/set/";
    //恢复本地默认设置
    public final static String RESET_RECORDED_LOCAL = URL + "/api/v1/record/parameters/reset/";

    //获取远程录播配置
    public final static String GET_RECORD_FAR = URL + "/api/v1/preferences/record/get/0/";
    //设置远程录播配置
    public final static String SET_RECORDED_FAR = URL + "/api/v1/preferences/record/set/";
    //恢复远程默认设置
    public final static String RESET_RECORDED_FAR = URL + "/api/v1/preferences/record/reset/";

    //本地录播状态查询
    //  {"code":0,"v":{"status":"start"}}  /  {"code":0,"v":{"status":"stop"}}
    public final static String GET_RECORD_NEAR_STATUS = URL + "/api/v1/record/statusnear/get/";
    //开启本地录播
    public final static String START_NEAR_RECORD = URL + "/api/v1/record/startnear/";
    //关闭本地录播
    public final static String STOP_NEAR_RECORD = URL + "/api/v1/record/stopnear/";
    //远端录播状态查询
    public final static String GET_RECORD_REMOTE_STATUS = URL + "/api/v1/record/statusremote/get/";
    //开启远端录播
    public final static String START_REMOTE_RECORD = URL + "/api/v1/record/startremote/";
    //关闭远端录播
    public final static String STOP_REMOTE_RECORD = URL + "/api/v1/record/stopremote/";


    //主视频源查询
    public final static String GET_MAIN_SOURCE = URL + "/api/v1/video/in/channel/main/source/get/";
    //主视频源修改
    public final static String SET_MAIN_SOURCE = URL + "/api/v1/video/in/channel/main/source/switch/";

    //音频位置查询
    public final static String GET_AUDIO_POS = URL + "/api/v1/preferences/audiopos/get/0/";
    //音频位置设置
    public final static String SET_AUDIO_POS = URL + "/api/v1/preferences/audiopos/set/";
    //音频位置恢复
    public final static String RESET_AUDIO_POS = URL + "/api/v1/preferences/audiopos/reset/";

    //地址簿中分组查询
    public final static String GET_ADDRESS_GROUP = URL + "/api/v1/simplebook/tags/";

    //地址簿中分组添加
    public final static String ADD_ADDRESS_GROUP = URL + "/api/v1/simplebook/tag/add/";

    //地址簿中分组删除
    public final static String DEL_ADDRESS_GROUP = URL + "/api/v1/simplebook/tag/remove/";

    //地址簿中分组修改
    public final static String SET_ADDRESS_GROUP = URL + "/api/v1/simplebook/tag/update/";


    //日志级别查询
    public final static String GET_LOG = URL + "/api/v1/preferences/log/get/0/";
    //日志级别设置
    public final static String SET_LOG = URL + "/api/v1/preferences/log/set/";
    //日志级别恢复
    public final static String RESET_LOG = URL + "/api/v1/preferences/log/reset/";


    //安全选项获取
    public final static String GET_SOFT_CHOOSE = URL + "/api/v1/preferences/security/cryptos/icon/get/0/";

    //安全选项设置
    public final static String SET_SOFT_CHOOSE = URL + "/api/v1/preferences/security/cryptos/icon/set/";


    //与真空间配置查看
    public final static String GET_MEETIME_CFG = "https://config.meetime.com/sip";

    public final static String SET_MEETIME_CFG = "https://config.meetime.com/sipconfig";

    //系统升级
    public final static String UP_MEETIME = "http://update.meetime.com/vhdupdate";


    //翻译文件
    public final static String GET_JSON = "http://127.0.0.1:88/resources/";


    //进入内置菜单
    public final static String GET_BUILT_MENU = URL + "/api/v1/camera/menu/calling/";

    //恢复出厂设置
    public final static String GET_RESET_SETTING = URL + "/api/v1/camera/settings/reset/";


    //ota
    public final static String SEND_OTA_MSG = URL + "/api/v1/ota/event/";

    //api/ v1/isfirstrun/
    //开机向导
    public final static String FIRST_RUN = URL + "/api/v1/isfirstrun/";

    //开机向导
    public final static String CLEAR_FIRST_RUN = URL + "/api/v1/clearfirstrun/";


    //曲线校验
    public final static String Len_Test = URL + "/api/v1/camera/len/test/";

    //云台测试
    public final static String Burn_Test = URL + "/api/v1/camera/BurnIn/test/";

    //坏点测试
    public final static String Hotpixel_Test = URL + "/api/v1/camera/hotpixel/test/";

    //获取风扇配置
    public final static String GET_FAN = URL + "/api/v1/camera/fan/control/get/0/";

    //设置风扇配置
    public final static String SET_FAN = URL + "/api/v1/camera/fan/control/set/";

    //设置风扇控制模式
    public final static String FAN_CONTROL_MODE = URL + "/api/v1/camera/fan/control/set/";

    //获取呼叫环回
    public final static String GET_CALL_LOOKBACK = URL + "/api/v1/call/loopback/get/0/";
    //开始呼叫环回
    public final static String START_CALL_LOOKBACK = URL + "/api/v1/call/loopback/start/";

    //停止呼叫环回
    public final static String STOP_CALL_LOOKBACK = URL + "/api/v1/call/loopback/stop/";

    //设置序列号
    public final static String SET_SERIAL = URL + "/api/v1/sysinfo/set/";


    //获取rtmp
    public final static String GET_RTMP = URL + "/api/v1/rtmp/getrtmpstatus/";
    //开启rtmp
    public final static String START_RTMP = URL + "/api/v1/rtmp/startrtmp/";
    //关闭rtmp
    public final static String STOP_RTMP = URL + "/api/v1/rtmp/stoprtmp/";


    //登录
    public final static String LOGIN = URL + "/api/v1/userweb/parameters/login/";

    //获取账号信息
    public final static String GET_ACCOUNT_INFO = URL + "/api/v1/userweb/all/parameters/get/";

    //设置账号信息
    public final static String SET_ACCOUNT_INFO = URL + "/api/v1/userweb/parameters/set/";

    //重置账号信息
    public final static String RESET_ACCOUNT_INFO = URL + "/api/v1/userweb/parameters/reset/";

    //网络设置信息获取
    public final static String SET_NETWORK_INFO = URL + "/api/v1/network/config-report/";

    //会控请求
    public final static String MEETING_CONTROL = URL + "/api/v1/call/conf/control/send/";
    //会控信息获取
    public final static String GET_MEETING_CONTROL = URL + "/api/v1/call/conf/control/get/";

    //取消自动关机
    public final static String CANCEL_AUTO_POWEROFF = URL + "/api/v1/power/cancel-auto-power-off/";

    //E1链路状态显示
    public final static String GET_LINK_STATUS = URL + "/api/v1/ipovere1/e1-link-status/";


    //查询会议锁定状态
    public final static String GET_LOCK_STATUS = URL + "/api/v1/call/conferenceLockStatus/";
    //打开会议锁定状态
    public final static String OPEN_LOCK_STATUS = URL + "/api/v1/call/openConferenceLock/";
    //关闭会议锁定状态
    public final static String CLOSE_LOCK_STATUS = URL + "/api/v1/call/closeConferenceLock/";

}

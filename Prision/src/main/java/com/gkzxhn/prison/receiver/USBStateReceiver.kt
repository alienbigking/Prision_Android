package com.gkzxhn.prison.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Raleigh.Luo on 17/5/18.
 * 检测USB和TF检测挂载和异常
 */

class USBStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //        String action = intent.getAction();
        //        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
        //            //USB设备移除，更新UI
        //            String rootPath = Utils.getTFPath();
        //            if (rootPath == null) {//停止录像
        //                Intent service = new Intent(context, ScreenRecordService.class);
        //                context.stopService(service);
        //            }
        //        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
        //            //USB设备挂载，更新UI
        //        }
    }
}

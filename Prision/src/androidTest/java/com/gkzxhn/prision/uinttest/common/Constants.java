package com.gkzxhn.prision.uinttest.common;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 17/5/3.
 */

public interface Constants {
    final String SD_ROOT_PATH= Environment.getExternalStorageDirectory().getPath()+"/automator";
    final String SD_SCREENSHOT_PATH = SD_ROOT_PATH+"/takeScreenshot/";
    final String ATTACH_TYPE_IMAGE_POSTFIX_JPEG = ".jpg";
    final String CURRENT_PACKAGENAME="com.gkzxhn.prision";
    final int VERSION_CODE=3;//测试的版本号
    final int WAIT_TIMEOUT=30000;//超时时间30秒
    final int WAIT_DELAY=1000;
    final int RETRY_TIMES=2;//重试次数
}


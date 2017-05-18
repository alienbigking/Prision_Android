package com.gkzxhn.prision.testutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;


import com.gkzxhn.prision.uinttest.common.Constants;

import java.io.File;
import java.util.UUID;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**UiAutomator工具
 * Created by Raleigh.Luo on 17/4/26.
 */

public class Utils {
    /**启动本包名的App
     */
    public static void openLauncher(UiDevice mDevice) {
        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                Constants.WAIT_TIMEOUT);
    }


    /**通过包名打开App
     * @param packageName
     */
    public static void openAppByPackagename(String packageName) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
    /**通过包名打开App
     */
    public static void openBrowByPackagename(String url) {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent=new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /**截屏
     * @param mDevice
     */
    public static String takeScreenshot(UiDevice mDevice) {
        String name= UUID.randomUUID().toString().replace("-", "")+Constants.ATTACH_TYPE_IMAGE_POSTFIX_JPEG;
        String dir = Constants.SD_SCREENSHOT_PATH;
        File theDir = new File(dir);
        if (!theDir.exists()) { theDir.mkdir(); }
        mDevice.takeScreenshot(new File(String.format("%s/%s", dir, name)));
        return name;
    }
    public static boolean hasObjectById(UiDevice mDevice, String viewId){
        boolean result=false;
        BySelector selector = By.res(String.format("%s:id/%s", Constants.CURRENT_PACKAGENAME, viewId));
        result=mDevice.hasObject(selector);
        return result;
    }
    public static boolean hasObjectByText(UiDevice mDevice, String text){
        boolean result=false;
        BySelector selector = By.text(text);
        result=mDevice.hasObject(selector);
        return result;
    }

    /**获取控件
     * @param mDevice 当前设备
     * @param viewId 控件Id
     * @return
     * @throws InterruptedException
     */
    public static UiObject2 waitForObject(UiDevice mDevice, String viewId) throws InterruptedException {
        UiObject2 object = null;
        if(viewId!=null&&viewId.length()>0) {
            BySelector selector = By.res(String.format("%s:id/%s", Constants.CURRENT_PACKAGENAME, viewId));
            long time = System.currentTimeMillis();
            while (object == null) {
                object = mDevice.findObject(selector);//寻找控件
                if (object != null) break;
                Utils.delay();
                if (System.currentTimeMillis() - Constants.WAIT_TIMEOUT > time) {
                    fail();
                    ReportUtil.getInstance().saveInfo2File("测试异常结束，找不到控件Id："+viewId);
                }
            }
        }
        return object;
    }

    public static void delay() throws InterruptedException {
        Thread.sleep(Constants.WAIT_DELAY);
    }
    public static void delay(int delayTime) throws InterruptedException {
        Thread.sleep(delayTime);
    }

    public static boolean clickAndWaitForNewWindowById(UiDevice mDevice, String resourceId) throws InterruptedException {
        boolean result=false;
        try {
            result=mDevice.findObject(new UiSelector().resourceId(String.format("%s:id/%s", Constants.CURRENT_PACKAGENAME, resourceId))).clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static boolean clickAndWaitForNewWindowByText(UiDevice mDevice, String text) throws InterruptedException {
        boolean result=false;
        try {
            result=mDevice.findObject(new UiSelector().text(text)).clickAndWaitForNewWindow();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 检查是否需要授权
     */
    public static void  checkPerimission(UiDevice mDevice) throws InterruptedException {
        if(hasObjectByText(mDevice,"允许")){
            Utils.clickAndWaitForNewWindowByText(mDevice,"允许");
        }else if(hasObjectByText(mDevice,"ALLOW")){
            Utils.clickAndWaitForNewWindowByText(mDevice,"ALLOW");
        }
    }

    /**打开通知栏
     * @param mDevice
     */
    private void openNotification(UiDevice mDevice){
        mDevice.swipe(500, 0, 500, 600, 10);
    }

    public static boolean installApk(UiDevice mDevice){
        boolean result=false;
        try {
            UiObject installButton=null ;
            long time = System.currentTimeMillis();
            while (installButton == null) {
                if(hasObjectByText(mDevice,"继续")){
                    clickAndWaitForNewWindowByText(mDevice,"继续");
                }else if(hasObjectByText(mDevice,"CONTINUE")){
                    clickAndWaitForNewWindowByText(mDevice,"CONTINUE");
                }else if(hasObjectByText(mDevice,"下一步")){
                    clickAndWaitForNewWindowByText(mDevice,"下一步");
                }else  if(hasObjectByText(mDevice,"NEXT")){
                    clickAndWaitForNewWindowByText(mDevice,"NEXT");
                }else if(hasObjectByText(mDevice,"安装")){
                    installButton =mDevice.findObject(new UiSelector().text("安装").textMatches("([\u4e00-\u9fa5]{2,2})"));
                    if (installButton != null) break;
                }else if(hasObjectByText(mDevice,"INSTALL")){
                    installButton =mDevice.findObject(new UiSelector().text("INSTALL").textMatches("([\u4e00-\u9fa5]{2,2})"));
                    if (installButton != null) break;
                }
                Utils.delay();
                if (System.currentTimeMillis() - 2*Constants.WAIT_TIMEOUT > time) {
                    fail();
                    ReportUtil.getInstance().saveInfo2File("测试异常结束，安装apk失败");
                }
            }
            if (installButton.exists() && installButton.isEnabled()) {
                installButton.clickAndWaitForNewWindow();
            }
            int retryTime=0;
            //安装完成
            while(!hasObjectByText(mDevice,"打开")&&!hasObjectByText(mDevice,"OPEN")&&retryTime<Constants.RETRY_TIMES){
                delay(Constants.WAIT_TIMEOUT);
                retryTime++;
            }
            if(hasObjectByText(mDevice,"打开")||hasObjectByText(mDevice,"OPEN")) {
                if(hasObjectByText(mDevice,"打开"))clickAndWaitForNewWindowByText(mDevice,"打开");
                else  if(hasObjectByText(mDevice,"OPEN"))clickAndWaitForNewWindowByText(mDevice,"OPEN");
            }else{
                ReportUtil.getInstance().saveInfo2File("测试异常结束，安装App未成功");
                assertTrue(false);
            }
            result=true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断apk是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName,int versionCode) {
        boolean result=false;
        if (packageName!= null &&packageName.length()>0) {
            try {
                PackageManager manager = context.getPackageManager();
                ApplicationInfo info = manager.getApplicationInfo(packageName,
                        PackageManager.MATCH_UNINSTALLED_PACKAGES);
                PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
                if (versionCode == packageInfo.versionCode)
                    result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

}

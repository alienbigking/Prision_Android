package com.gkzxhn.prison.uinttest;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.View;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.keda.vconf.VConfVideoUI;
import com.gkzxhn.prison.keda.vconf.VConferenceManager;
import com.gkzxhn.prison.uinttest.common.Constants;
import com.gkzxhn.prison.testutils.ReportUtil;
import com.gkzxhn.prison.testutils.UnitUtils;
import com.gkzxhn.prison.testutils.Utils;
import com.gkzxhn.prison.uinttest.common.CustomCategory;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Raleigh.Luo on 17/5/4.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ConfVideoUnitTest {
    private UiDevice mDevice;
    @Rule
    public ActivityTestRule mActivityTestRule=new ActivityTestRule<VConfVideoUI>
            (VConfVideoUI.class){
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, VConfVideoUI.class);
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_VCONFNAME,"张三24(18163657553)");
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_E164NUM,"18163657553");
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_MACKCALL,true);
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_JOINCONF,false);
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_VCONFQUALITY,0);
            result.putExtra(com.gkzxhn.prison.common.Constants.TERMINAL_VCONFDURATION,0);
            return result;
        }
    };
    @Before
    public void before(){
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        //监听错误日志
        Espresso.setFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
                //将日志写到SD卡里面
                ReportUtil.getInstance().saveInfo2File(error);
                error.printStackTrace();
            }
        });
      VConferenceManager.isAvailableVCconf(true, true, true);

    }

    /**
     * 接通前挂断
     */
    @Test
    public void hangUpBeforeConnected(){
        if (Utils.hasObjectById(mDevice, "hang_img")){//挂断
            onView(withId(R.id.hang_img)).perform(click());
        }
    }
    @Test
    public void showIdcard() throws InterruptedException {
        while(!Utils.hasObjectById(mDevice, "exit_Img")) {
            Thread.sleep(Constants.WAIT_DELAY);
        }
        //缩小身份证
        UnitUtils.clickById(R.id.ll_check_id);
        //放大身份证
        UnitUtils.clickById(R.id.ll_check_id);
    }

    /**关闭麦克风
     * @throws InterruptedException
     */
    @Test
    public void closeMicrophone() throws InterruptedException {
        while(!Utils.hasObjectById(mDevice, "exit_Img")) {
            Thread.sleep(Constants.WAIT_DELAY);
        }
        UnitUtils.clickById(R.id.mute_text);
    }

    /**关闭声音
     * @throws InterruptedException
     */
    @Test
    public void turnSoundOff() throws InterruptedException {
        while(!Utils.hasObjectById(mDevice, "exit_Img")) {
            Thread.sleep(Constants.WAIT_DELAY);
        }
        UnitUtils.clickById(R.id.quiet_text);
    }
    /**
     * 接通后挂断
     */
    @Category(CustomCategory.NormalTestCategory.class)
    @Test
    public void hangUpAfterConnected() throws InterruptedException {
        while(!Utils.hasObjectById(mDevice, "exit_Img")){
            Utils.checkPerimission(mDevice);//检查是否需要授权
            Utils.delay();
            Thread.sleep(Constants.WAIT_DELAY);
        }
        //缩小身份证
        Utils.waitForObject(mDevice,"ll_check_id").click();
        //1分钟后挂断
        Thread.sleep(Constants.WAIT_TIMEOUT);
        Utils.clickAndWaitForNewWindowById(mDevice, "exit_Img");
        if(Utils.hasObjectById(mDevice,"cancel_video_dialog_tv_set") ){
            String text = "本次会见结束";
            /*************************Spinner解析 id和itemId**************************************/
            //选择码率 Spinner
            Utils.clickAndWaitForNewWindowById(mDevice, "spinner");
            //通过itemId和文字匹配
            UiObject type = mDevice.findObject(new UiSelector().resourceId("android:id/text1").text(text));
            try {
                Utils.delay();
                type.click();//最后，选择找到的项
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            /***********************************************************************/
            // 点击确定按钮
            Utils.waitForObject(mDevice,"cancel_video_dialog_tv_set").click();
        }
    }

}

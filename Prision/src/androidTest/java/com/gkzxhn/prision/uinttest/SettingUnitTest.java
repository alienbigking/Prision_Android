package com.gkzxhn.prision.uinttest;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.activity.SettingActivity;
import com.gkzxhn.prision.uinttest.common.Constants;
import com.gkzxhn.prision.testutils.ReportUtil;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Raleigh.Luo on 17/5/4.
 * 设置@Test按方法字母 @FixMethodOrder(MethodSorters.NAME_ASCENDING)
 * 设置@Test按默认执行，顺序无法预测 @FixMethodOrder(MethodSorters.DEFAULT)
 * ps:若某个@Test执行时关闭了指定的Activit，执行下一个@Test时Activity会重新启动
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class SettingUnitTest {
    private Activity mActivity;
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            SettingActivity.class);
    @Before
    public void before(){
        mActivity=mActivityRule.getActivity();
        //监听错误日志
        Espresso.setFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
                //将日志写到SD卡里面
                ReportUtil.getInstance().saveInfo2File(error);
                error.printStackTrace();
            }
        });
    }

    /**检测版本更新
     * @throws Exception
     */
    @Test
    public void checkUpdate() throws Exception {
        Thread.sleep(Constants.WAIT_DELAY);
        onView(ViewMatchers.withId(R.id.setting_layout_tv_update)).perform(click());
        Thread.sleep(Constants.WAIT_DELAY);
        //检测是否弹出了版本更新下载的对话框
        onView(withId(R.id.update_dialog_layout_tv_download)).check(matches(isDisplayed())).withFailureHandler(new FailureHandler() {
            @Override
            public void handle(Throwable error, Matcher<View> viewMatcher) {
            }
        });
        //需要版本更新,点击下载
        onView(withId(R.id.update_dialog_layout_tv_download)).perform(click());
    }

    /**注销登录帐号
     * @throws Exception
     */
    @Test
    public void loginoff() throws Exception {
        //注销
        Thread.sleep(Constants.WAIT_DELAY);
        onView(withId(R.id.setting_layout_tv_logout)).perform(click());
        Thread.sleep(Constants.WAIT_DELAY);
        //取消注销
        onView(withId(R.id.custom_dialog_layout_tv_confirm)).perform(click());
    }

    /**取消注销帐号
     * @throws Exception
     */
    @Test
    public void cancelLoginoff() throws Exception {
        //注销
        Thread.sleep(Constants.WAIT_DELAY);
        onView(withId(R.id.setting_layout_tv_logout)).perform(click());
        Thread.sleep(Constants.WAIT_DELAY);
        //取消注销
        onView(withId(R.id.custom_dialog_layout_tv_cancel)).perform(click());
    }

    /**设置终端
     * @throws Exception
     */
    @Test
    public void setTerminatel() throws Exception {
        //注销
        Thread.sleep(Constants.WAIT_DELAY);
        onView(withId(R.id.setting_layout_tv_end_setting)).perform(click());
    }


}

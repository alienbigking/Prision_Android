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
import android.view.View;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.CallUserActivity;
import com.gkzxhn.prison.uinttest.common.Constants;
import com.gkzxhn.prison.uinttest.common.CustomCategory;
import com.gkzxhn.prison.testutils.ReportUtil;
import com.gkzxhn.prison.testutils.Utils;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Raleigh.Luo on 17/5/4.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CallUserUnitTest {
    private UiDevice mDevice;
    @Rule
    public ActivityTestRule mActivityTestRule=new ActivityTestRule<CallUserActivity>
            (CallUserActivity.class){
        @Override
        protected Intent getActivityIntent() {
            //Intent 传值到CallUserActivity
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, CallUserActivity.class);
            result.putExtra(com.gkzxhn.prison.common.Constants.EXTRA,"149");
            result.putExtra(com.gkzxhn.prison.common.Constants.EXTRAS,"18163657553");
            result.putExtra(com.gkzxhn.prison.common.Constants.EXTRA_TAB,"tttt");
            return result;
        }
    };
    @Before
    public void before(){
        mDevice=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
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

    /**呼叫
     * @throws Exception
     */
    @Category(CustomCategory.NormalTestCategory.class)
    @Test
    public void callMeeting() throws Exception {
        while (!Utils.waitForObject(mDevice,"call_user_layout_bt_call").isEnabled()){
            Thread.sleep(Constants.WAIT_DELAY);
        }
        //呼叫按钮可点击的话，点击呼叫
        onView(withId(R.id.call_user_layout_bt_call)).check(matches(isEnabled())).
                perform(click());
    }
}

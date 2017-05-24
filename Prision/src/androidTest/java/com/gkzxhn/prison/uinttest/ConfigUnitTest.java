package com.gkzxhn.prison.uinttest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.ConfigActivity;
import com.gkzxhn.prison.uinttest.common.Accounts;
import com.gkzxhn.prison.testutils.ReportUtil;
import com.gkzxhn.prison.testutils.UnitUtils;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Raleigh.Luo on 17/5/4.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ConfigUnitTest {

    @Rule
    public ActivityTestRule mActivityTestRule=new ActivityTestRule<>(ConfigActivity.class);
    @Before
    public void before(){
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

    /**输入空的终端
     * @throws Exception
     */
    @Test
    public void emptyTerminal() throws Exception {
        //输入空值
        UnitUtils.inputTextById(R.id.config_layout_et_account,"");
        //点击保存按钮
        UnitUtils.clickById(R.id.config_layout_btn_save);

    }

    /**输入终端并选择码率
     * @throws Exception
     */
    @Test
    public void terminalWithRate() throws Exception {
        //输入空值
        UnitUtils.inputTextById(R.id.config_layout_et_account,"");
        //选择码率
        UnitUtils.clickById(R.id.config_layout_sp_rate);
        //找到码率值，并点击
        String rate=String.valueOf(Accounts.getInstance().TEST_TERMINALS_RATE);
        onData(allOf(is(instanceOf(String.class)), is(rate))).perform(click());
        //确定是否选中了该码率
        UnitUtils.checkSpinnerTextById(R.id.config_layout_sp_rate,rate);
        //点击保存按钮
        UnitUtils.clickById(R.id.config_layout_btn_save);
    }
}

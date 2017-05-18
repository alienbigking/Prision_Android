package com.gkzxhn.prision.uinttest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.activity.LoginActivity;
import com.gkzxhn.prision.uinttest.common.Accounts;
import com.gkzxhn.prision.uinttest.common.Constants;
import com.gkzxhn.prision.uinttest.common.CustomCategory;
import com.gkzxhn.prision.testutils.ReportUtil;
import com.gkzxhn.prision.testutils.UnitUtils;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Raleigh.Luo on 17/5/3.
 * 设置@Test按方法字母 @FixMethodOrder(MethodSorters.NAME_ASCENDING)
 * 设置@Test按默认执行，顺序无法预测 @FixMethodOrder(MethodSorters.DEFAULT)
 * ps:若某个@Test执行时关闭了指定的Activit，执行下一个@Test时Activity会重新启动
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SmallTest
public class LoginUnitTest {
    private String username,password;
    private final String errorUsername="errorUsername",errorPassword="errorPassword";
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);
    @Before
    public void before(){
        int index=0;
        username=Accounts.getInstance().ACCOUNTS[index];
        password=Accounts.getInstance().PASSWORDS[index];
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

    /** 正常登录 z最后验证，否则容易出问题，和Activity生命周期有关
     * @Category 分类为正常测试，以便于组合测试
     * @throws InterruptedException
     */
    @Category({CustomCategory.NormalTestCategory.class})
    @Test
    public void z_login() throws InterruptedException {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,username);
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_password,password);
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }

    /**空用户名和密码
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void emptyAccountAndPassword() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,"");
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_password,"");
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }

    /**空密码
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void emptyPassword() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,username);
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_password,"");
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }
    /**空用户名
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void emptyAccount() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,"");
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_password,password);
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }

    /**错误用户名
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void errorAccount() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,errorUsername);
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_username,password);
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }
    /**错误密码
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void errorPassword() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,username);
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_username,errorPassword);
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }
    /**错误用户名和密码
     * @throws Exception
     */
    @Category(CustomCategory.AbnormalTestCategory.class)
    @Test
    public void errorAccountAndPassword() throws Exception {
        //输入用户名和密码，并关闭键盘
        UnitUtils.inputTextById(R.id.loign_layout_et_username,errorUsername);
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.inputTextById(R.id.loign_layout_et_password,errorPassword);
        Thread.sleep(Constants.WAIT_DELAY);
        //点击登录按钮
        UnitUtils.clickById(R.id.loign_layout_btn_login);
    }


//    onView(withId(R.id.textView)).check(matches(withText("Hello, World!")));
//根据文本找到"开始体验"按钮,并判断是否可见
//ViewInteraction appCompatButton = onView(
//        allOf(withText("开始体验"), isDisplayed()));
}

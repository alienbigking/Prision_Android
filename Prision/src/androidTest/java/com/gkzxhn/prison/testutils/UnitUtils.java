package com.gkzxhn.prison.testutils;

import android.app.Activity;
import android.support.test.espresso.contrib.RecyclerViewActions;

import com.gkzxhn.prison.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.fail;

/**
 * Created by Raleigh.Luo on 17/5/4.
 */

public class UnitUtils {
    /**模拟用户的点击行为
     * @param id
     */
    public static void clickById(int id){
        onView(withId(id)).perform(click());
    }

    /**拖动view 比如Recyclerview
     * @param id
     */
    public static void swipeDownById(int id){
        onView(withId(R.id.common_list_layout_rv_list)).perform(swipeDown());
    }

    /**改变View的文本显示
     * @param id
     * @param text
     */
    public static void inputTextById(int id,String text){
        onView(withId(id)).perform(clearText(),typeText(text),closeSoftKeyboard());
    }

    /**检查View文本变化是否正确
     * @param id
     * @param text
     */
    public static void checkTextById(int id,String text){
        onView(withId(id)).check(matches(withText(text)));
    }

    /**检查Spinner是否选中了指定文本
     * @param id
     * @param text
     */
    public static void checkSpinnerTextById(int id,String text){
        onView(withId(id)).check(matches(withSpinnerText(containsString(text))));
    }

    /**验证Toast
     * @param activity
     * @param toastText
     */
    public static void checkToastByText(Activity activity,String toastText){
        onView(withText(toastText)).inRoot(withDecorView(not(activity.getWindow().getDecorView()))).check(matches(isDisplayed()));
        //显示：isDisplayed() 不显示：doesNotExist()
    }
    /**通过id点击RecyclerView中指定位置position，
     * @param id
     * @param position
     */
    public static void clickRecyclerViewItem(int id,int position){
        //        onView(withText(itemElementText)).check(matches(isDisplayed()));
//        onView(ViewMatchers.withId(R.id.common_list_layout_rv_list)).perform(RecyclerViewActions.scrollToHolder(isInTheMiddle()));
        onView(withId(id)).perform(
                RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

}

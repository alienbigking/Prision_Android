package com.gkzxhn.prison.uinttest;

import android.app.Activity;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.View;

import com.gkzxhn.prison.R;
import com.gkzxhn.prison.activity.MainActivity;
import com.gkzxhn.prison.uinttest.common.Accounts;
import com.gkzxhn.prison.uinttest.common.Constants;
import com.gkzxhn.prison.uinttest.common.CustomCategory;
import com.gkzxhn.prison.testutils.ConvertUtil;
import com.gkzxhn.prison.testutils.ReportUtil;
import com.gkzxhn.prison.testutils.UnitUtils;
import com.gkzxhn.prison.testutils.Utils;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by Raleigh.Luo on 17/5/3.
 *  需组合登录进行测试，－>MainSuiteTest
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MainUnitTest {
    private Activity mActivity;
    private UiDevice mDevice;
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
    @Before
    public void before(){
        Accounts.getInstance().init();
        mActivity=mActivityRule.getActivity();
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
    }

    /**跳转到指定日期
     * @throws Exception
     */
    @Category(CustomCategory.NormalTestCategory.class)
    @Test
    public void toDate() throws Exception {
        //测试近7天的
        for (String date : Accounts.getInstance().getMeetingDate()) {
            toDate(date);
        }
    }

    /**
     * 视频会见 找到一条数据就呼叫 break
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void toCallUser() throws InterruptedException {
        try {
            UiScrollable mRecyclerView = new UiScrollable(new UiSelector()
                    .className("android.support.v7.widget.RecyclerView"));
            for (String date : Accounts.getInstance().getMeetingDate()) {
                toDate(date);
                if(mRecyclerView.getChildCount()>0) {
                    //RecyclerView遍历
                    UiObject itemView = mRecyclerView.getChild(new UiSelector().instance(0));
                    itemView.click();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**取消会见
     * @throws InterruptedException
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void cancelMeeting() throws InterruptedException {
        //先切换终端
        try {
            UiScrollable mRecyclerView = new UiScrollable(new UiSelector()
                    .className("android.support.v7.widget.RecyclerView"));
            for (String date : Accounts.getInstance().getMeetingDate()) {
                toDate(date);
                if(mRecyclerView.getChildCount()>0) {
                    //点击取消按钮
                    onView(withId(R.id.common_list_layout_rv_list))
                            .perform(RecyclerViewActions.actionOnItem(
                                    hasDescendant(withId(R.id.main_item_layout_tv_cancel)), click()));
//                    onData(withId(R.id.common_list_layout_rv_list))
//                            .onChildView(withId(R.id.main_item_layout_tv_cancel)).perform(click());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void toDate(String date) throws InterruptedException {
        if (checkTerminal()) {
            //指定日期
            Calendar mDestinationYearMonth = ConvertUtil.getCalendar(date, null);
            //获取当前页面显示日期
            UiObject2 tvYearMonth = Utils.waitForObject(mDevice, "main_layout_tv_month");
            String currentYearMonth = tvYearMonth.getText();//当前年月
            Calendar mYearMonthDate = ConvertUtil.getCalendar(currentYearMonth, new SimpleDateFormat("yyyy年MM月"));
            if (mYearMonthDate != null) {
                //获取相差的月数，大于返回正数
                int differMonth = ConvertUtil.getMonthBycompareDate(mYearMonthDate.getTimeInMillis(), mDestinationYearMonth.getTimeInMillis());
                UiObject2 btnJump = null;
                if (differMonth == 0) {
                    //同年同月不需要翻转页面
                    operateLeft(mDevice, mDestinationYearMonth);
                    Thread.sleep(Constants.WAIT_DELAY);
//                    operateRight(mDevice);
                } else {
                    if (differMonth < 0) { //last month 左边翻转
                        UnitUtils.clickById(R.id.main_layout_btn_last);
                    } else { //next month 右边翻转
                        UnitUtils.clickById(R.id.main_layout_btn_next);
                    }
                    //绝对值相减的点击次数
                    for (int i = 0; i < Math.abs(differMonth); i++) {
                        btnJump.click();
                    }
                    //     UiObject object=null;
//                    object.swipeLeft()左右滑动
                    Thread.sleep(Constants.WAIT_DELAY);
                    toDate(date);
                }
            }
        }
    }


    /**测试刷新功能，包括左上角和下拉刷新
     *   @Category 分类为 非主功能
     * @throws Exception
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void refresh() throws Exception {
        Thread.sleep(Constants.WAIT_DELAY);
        //测试刷新功能
        UnitUtils.clickById(R.id.common_head_layout_iv_right);
        //下拉Recyclerview
        UnitUtils.swipeDownById(R.id.common_list_layout_rv_list);
        Thread.sleep(Constants.WAIT_DELAY);
    }

    /**上一个月
     * @Category 分类为 非主功能
     * @throws Exception
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void changeLastMonth()throws Exception{
        Calendar calender=Calendar.getInstance();
        checkMonthTitle(calender.getTimeInMillis());
        Thread.sleep(Constants.WAIT_DELAY);
        /**********上个月*************/
        calender.add(Calendar.MONTH,-1);//减一个月
        //上个月
        UnitUtils.clickById(R.id.main_layout_btn_last);
        //检查是否显示的是上一个月的年月
        checkMonthTitle(calender.getTimeInMillis());
    }

    /**下一个月
     *   @Category 分类为 非主功能
     * @throws Exception
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void changeNextMonth()throws Exception{
        Calendar calender=Calendar.getInstance();
        //检查是否显示的是当日年月
        checkMonthTitle(calender.getTimeInMillis());
        Thread.sleep(Constants.WAIT_DELAY);
        calender.add(Calendar.MONTH,1);//加一个月
        //下个月
        UnitUtils.clickById(R.id.main_layout_btn_next);
        //检查是否显示的是下一个月的年月
        checkMonthTitle(calender.getTimeInMillis());
    }



    /**点击设置按钮
     *  @Category 分类为 非主功能
     * @throws Exception
     */
    @Category(CustomCategory.NoPrimaryFunctionTestCategory.class)
    @Test
    public void toSetting() throws Exception {
        //设置
        Thread.sleep(Constants.WAIT_DELAY);
        UnitUtils.clickById(R.id.common_head_layout_iv_left);
    }
    private boolean checkTerminal(){
        return true;
    }
    private void checkMonthTitle(long timeInMillis){
        SimpleDateFormat df=new SimpleDateFormat(String.format("yyyy%sM%s",mActivity.getString(R.string.year),
                mActivity.getString(R.string.month)));
        String currentDate=ConvertUtil.getDateFromTimeInMillis(timeInMillis,df);
        UnitUtils.checkTextById(R.id.main_layout_tv_month,currentDate);
    }


    /**选择日期
     * @param mDevice
     */
    private  void operateLeft(UiDevice mDevice,Calendar calendar){
        UiCollection viewpager = new UiCollection(new UiSelector()
                .className("android.support.v4.view.ViewPager"));
        try {
            final int TOTAL_COL = 7; // 7列
            Rect calendarBounds=viewpager.getBounds();
            int calendarWidth=calendarBounds.right-calendarBounds.left;
            int calendarHeight=calendarBounds.bottom-calendarBounds.top;
            int with=calendarWidth/TOTAL_COL;
            int height=with;
            int position=getDayPostion(calendar);
            int currentCol=position%TOTAL_COL==0?TOTAL_COL:position%TOTAL_COL;
            int currentRow=position/TOTAL_COL+(position%TOTAL_COL==0?0:1);//当前行
            //公式(positon-1)*w+w/2   点击中心
            int x=calendarBounds.left+(2*currentCol-1)*with/2;
            int y=calendarBounds.top+(2*currentRow-1)*height/2;
            mDevice.click(x,y);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    private  int  getDayPostion(Calendar calendar) {
        int position=0;
        //当月1号是星期几
        Calendar firstDay=Calendar.getInstance();
        firstDay.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        firstDay.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        firstDay.set(Calendar.DAY_OF_MONTH,1);
        int firstDayWeek=firstDay.get(Calendar.DAY_OF_WEEK);
        //position＝day+1号之前的几天（firstDayWeek-Calendar.SUNDAY）
        position=firstDayWeek-Calendar.SUNDAY+calendar.get(Calendar.DAY_OF_MONTH);
        return position;

    }
    /**视频会见
     * @param mDevice
     */
    private  void operateRight(UiDevice mDevice)  {
        UiScrollable mRecyclerView = new UiScrollable(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));
        try {
            if(mRecyclerView.getChildCount()>0) {
                //RecyclerView遍历
                for(int i=0;i<mRecyclerView.getChildCount();i++) {
                    UiObject itemView = mRecyclerView.getChild(new UiSelector().instance(i));
                    itemView.click();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

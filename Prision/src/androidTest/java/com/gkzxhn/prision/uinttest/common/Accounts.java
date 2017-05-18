package com.gkzxhn.prision.uinttest.common;



import com.gkzxhn.prision.testutils.ConvertUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Raleigh.Luo on 17/4/27.
 */

public class Accounts {
    private static Accounts instance;
    private Map<String,String> loginAccount;
    private List<String> meetingDate;

    public final String[] ACCOUNTS={"9997","9999","9998"};
    public final String[] PASSWORDS={"9997","9999","9998"};
//    public final String[] ACCOUNTS={"gkzxhn01","gkzxhn02","gkzxhn03"};
//    public final String[] PASSWORDS={"123456","123456","123456"};
        private final String[] TEST_TERMINALS={"9997","9998","9999",};
    public final int TEST_TERMINALS_RATE=768;

    public void init(){
        initMeetingDate();
    }
    public static Accounts getInstance(){
        if(instance==null) instance=new Accounts();
        return instance;
    }

    private void initMeetingDate(){
        meetingDate=new ArrayList<>();
        Calendar currentDate=Calendar.getInstance();
//        currentDate.add(Calendar.DAY_OF_MONTH,-7);//测试前7天
//        for(int i=0;i<7;i++){
//            currentDate.add(Calendar.DAY_OF_MONTH,1);
//            meetingDate.add(ConvertUtil.getDateFromTimeInMillis(currentDate.getTimeInMillis(),null));
//        }
        currentDate.add(Calendar.DAY_OF_MONTH,7);
        //测试后7天
        for(int i=0;i<9;i++){
            meetingDate.add(ConvertUtil.getDateFromTimeInMillis(currentDate.getTimeInMillis(),null));
            currentDate.add(Calendar.DAY_OF_MONTH,1);
        }
    }

    public List<String> getMeetingDate() {
        return meetingDate;
    }

    public String[] getTestTerminals() {
        return TEST_TERMINALS;
    }
}

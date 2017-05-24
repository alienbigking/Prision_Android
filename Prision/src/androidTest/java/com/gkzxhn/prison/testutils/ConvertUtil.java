package com.gkzxhn.prison.testutils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Raleigh.Luo on 17/4/27.
 */

public class ConvertUtil {
    //默认格式
    private static final String DEFUALT_FORMATE="yyyy-MM-dd";
    /**字符串转long date
     * @param strDate
     * @return
     */
    public static Calendar getCalendar(String strDate, SimpleDateFormat df) {//strDate="20110107110227"
        Calendar calendar=Calendar.getInstance();
        try {
            Date dateTime = null;
            if(df==null) df = new SimpleDateFormat(DEFUALT_FORMATE);
            dateTime = df.parse(strDate);
            calendar.setTimeInMillis(dateTime.getTime());
        } catch (Exception e1) {}
        return  calendar;
    }
    public static String getDateFromTimeInMillis(long timeInMillis,SimpleDateFormat df) {
        String result="";
        if(timeInMillis>0) {
            try {
                Date date = new Date(timeInMillis);
                //英文格式时间格式化
                if(df==null) df = new SimpleDateFormat(DEFUALT_FORMATE);
                result = df.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**比较日期相差的天数
     * @param timeInMillis1
     * @param timeInMillis2
     * @return 相差的天数，timeInMillis2 大于 timeInMillis1  返回正数，否则返回负数，等于返回0
     */
    public static int compareDate(long timeInMillis1,long timeInMillis2){
        int result=-1;
        try{
            Calendar mCal1=Calendar.getInstance();
            mCal1.setTimeInMillis(timeInMillis1);
            Calendar mCal2=Calendar.getInstance();
            mCal2.setTimeInMillis(timeInMillis2);

            if(mCal2.get(Calendar.YEAR)==mCal1.get(Calendar.YEAR)){//同年
                result=mCal2.get(Calendar.DAY_OF_YEAR)-mCal1.get(Calendar.DAY_OF_YEAR);
            }else if(mCal2.get(Calendar.YEAR)>mCal1.get(Calendar.YEAR)){//下一年，大于今年
                //今年的最后一天
                int thisYearLastDay=mCal1.getActualMaximum(Calendar.DAY_OF_YEAR);
                //今年剩余的天数+下一年的年天数
                result=thisYearLastDay-mCal1.get(Calendar.DAY_OF_YEAR)+mCal2.get(Calendar.DAY_OF_YEAR);
            }else {//上一年，小于今年
                //上一年的最后一天
                int lastYearLastDay=mCal2.getActualMaximum(Calendar.DAY_OF_YEAR);
                //上一年剩余的天数+今年的年天数
                result=-(lastYearLastDay-mCal2.get(Calendar.DAY_OF_YEAR)+mCal1.get(Calendar.DAY_OF_YEAR));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**比较日期相差的月数
     * @param timeInMillis1
     * @param timeInMillis2
     * @return 相差的月数，timeInMillis2 大于 timeInMillis1  返回正数，否则返回负数，等于返回0
     */
    public static int getMonthBycompareDate(long timeInMillis1,long timeInMillis2){
        int result=-1;
        try{
            Calendar mCal1=Calendar.getInstance();
            mCal1.setTimeInMillis(timeInMillis1);
            Calendar mCal2=Calendar.getInstance();
            mCal2.setTimeInMillis(timeInMillis2);

            if(mCal2.get(Calendar.YEAR)==mCal1.get(Calendar.YEAR)){//同年
                result=mCal2.get(Calendar.MONTH)-mCal1.get(Calendar.MONTH);
            }else if(mCal2.get(Calendar.YEAR)>mCal1.get(Calendar.YEAR)){//下一年，大于今年
                result=12-mCal1.get(Calendar.MONTH)+12*(mCal2.get(Calendar.YEAR)-mCal1.get(Calendar.YEAR)-1)
                        +mCal2.get(Calendar.MONTH);
            }else {//上一年，小于今年
                result=-(mCal1.get(Calendar.MONTH)+12*(mCal1.get(Calendar.YEAR)-mCal2.get(Calendar.YEAR)-1)
                        +12-mCal2.get(Calendar.MONTH));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }
}

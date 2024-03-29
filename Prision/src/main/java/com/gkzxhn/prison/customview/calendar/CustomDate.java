package com.gkzxhn.prison.customview.calendar;


import java.io.Serializable;

/**
 * created by huangzhengneng on 2016/1/18
 * 日期类
 */
public class CustomDate implements Serializable {

    private static final long serialVersionUID = 1L;
    public int year;
    public int month;
    public int day;
    public int week;

    public CustomDate(int year,int month,int day){
        if(month > 12){
            month = 1;
            year++;
        }else if(month <1){
            month = 12;
            year--;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CustomDate(){
        this.year = DateUtil.getYear();
        this.month = DateUtil.getMonth();
        this.day = DateUtil.getCurrentMonthDay();
    }

    public static CustomDate modifiDayForObject(CustomDate date,int day){
        return new CustomDate(date.year,date.month,day);
    }
    @Override
    public String toString() {
        return year+"-"+getFomatterNumber(month)+"-"+getFomatterNumber(day);
    }
    private String getFomatterNumber(int number){
        String result=null;
        if(number>9){
            result=String.valueOf(number);
        }else{
            result="0"+number;
        }
        return result;

    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
package com.gkzxhn.prison.entity;

/**
 * Created by 方 on 2017/12/5.
 */

public class MeetingRoomInfo {
    /**
     * code : 200
     * data : {"terminal_number":"450101","room_number":"6623","host_password":"123456","metting_password":"654321","content":"6623##123456##654321"}
     */

    public int code;
    public DataBean data;

    public static class DataBean {
        /**
         * terminal_number : 450101
         * room_number : 6623
         * host_password : 123456
         * metting_password : 654321
         * content : 6623##123456##654321
         */

        public String terminal_number;
        public String room_number;
        public String host_password;
        public String metting_password;
        public String content;
    }
}

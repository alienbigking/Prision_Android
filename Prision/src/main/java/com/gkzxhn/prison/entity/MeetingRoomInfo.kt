package com.gkzxhn.prison.entity

/**
 * Created by 方 on 2017/12/5.
 */

class MeetingRoomInfo {
    /**
     * code : 200
     * data : {"terminal_number":"450101","room_number":"6623","host_password":"123456","metting_password":"654321","content":"6623##123456##654321"}
     */

    var code: Int = 0
    var data: DataBean? = null

    class DataBean {
        /**
         * terminal_number : 450101
         * room_number : 6623
         * host_password : 123456
         * metting_password : 654321
         * content : 6623##123456##654321
         */

        var terminal_number: String? = null
        var room_number: String? = null
        var host_password: String? = null
        var metting_password: String? = null
        var content: String? = null
    }
}

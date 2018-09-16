package com.gkzxhn.prison.entity

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

class MeetingDetailEntity {
    var name: String? = null//家属姓名
        get() {
            return if (field == "null") "" else field
        }
    var uuid: String? = null//身份证号码
        get() {
            return if (field == "null") "" else field
        }
    var id: String? = null//家属id
        get() {
            return if (field == "null") "" else field
        }
    var avatarUrl: String? = null//头像id
        get() {
            return if (field == "null") "" else field
        }
    var idCardFront: String? = null//身份证正面
        get() {
            return if (field == "null") "" else field
        }
    var idCardBack: String? = null//身份证反面
        get() {
            return if (field == "null") "" else field
        }
    var accessToken: String? = null//对方云信token
        get() {
            return if (field == "null") "" else field
        }
    var phone: String? = null// 电话号码
        get() {
            return if (field == "null") "" else field
        }
}

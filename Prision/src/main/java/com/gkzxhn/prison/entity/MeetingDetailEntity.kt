package com.gkzxhn.prison.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/13.
 */

class MeetingDetailEntity {
    var name: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var uuid: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var id: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var avatarUrl: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var idCardFront: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var idCardBack: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var accessToken: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var phone: String?=null// 电话号码
        get() {
            return if(field=="null")"" else field
        }
}

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
    var relationship: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("image_url")
    var imageUrl: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var accid: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("prisoner_number")
    var prisonerNumber: String? = null//囚号
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("prisoner_name")
    var prisonerName: String? = null//囚犯名字
        get() {
            return if(field=="null")"" else field
        }
    @Expose
    var phone: String?=null// 电话号码
        get() {
            return if(field=="null")"" else field
        }


}

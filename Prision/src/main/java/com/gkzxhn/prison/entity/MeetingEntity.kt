package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

class MeetingEntity {
    var id: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("prisoner_name")
    var name: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("meeting_time")
    var time: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var area: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("to")
    var yxAccount: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var remarks: String? = null//取消原因
        get() {
            return if(field=="null")"" else field
        }
    var status: String? = null//状态
        get() {
            return if(field=="null")"" else field
        }
}

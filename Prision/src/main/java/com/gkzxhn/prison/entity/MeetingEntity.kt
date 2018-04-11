package com.gkzxhn.prison.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

class MeetingEntity {
    var id: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var name: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("meeting_time")
    var time: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var familyId: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var prisonerNumber: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var prisonerId: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var jailName: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var jailId: String? = null
        get() {
            return if(field=="null")"" else field
        }
}

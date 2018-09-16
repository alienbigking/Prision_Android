package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

class MeetingEntity {
    var id: String? = null//会见ID
        get() {
            return if (field == "null") "" else field
        }
    var name: String? = null//囚犯名称
        get() {
            return if (field == "null") "" else field
        }
    @SerializedName("meetingTime")
    var time: String? = null//会见时间
        get() {
            return if (field == "null") "" else field
        }
    var familyId: String? = null//家属ID
        get() {
            return if (field == "null") "" else field
        }
    var prisonerNumber: String? = null//囚犯编号
        get() {
            return if (field == "null") "" else field
        }
    var prisonerId: String? = null//囚犯id
        get() {
            return if (field == "null") "" else field
        }
    var jailName: String? = null//监狱名称
        get() {
            return if (field == "null") "" else field
        }
    var jailId: String? = null//监狱id
        get() {
            return if (field == "null") "" else field
        }
}

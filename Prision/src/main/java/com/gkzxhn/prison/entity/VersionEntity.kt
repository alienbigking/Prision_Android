package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/20.
 */

class VersionEntity {
    var id:Int=0
    @SerializedName("versionNumber")
    var versionCode: Int = 0
    @SerializedName("versionCode")
    var versionName: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("download")
    var downloadUrl: String? = null
        get() {
            return if(field=="null")"" else field
        }
    var isForce: Int = 0

}

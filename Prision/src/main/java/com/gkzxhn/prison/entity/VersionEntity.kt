package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/20.
 */

class VersionEntity {
    @SerializedName("version_number")
    var versionCode: Int = 0
    @SerializedName("version_code")
    var versionName: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("download")
    var downloadUrl: String? = null
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("is_force")
    var isForce: Boolean = false

}

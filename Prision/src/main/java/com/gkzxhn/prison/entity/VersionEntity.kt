package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 17/4/20.
 */

class VersionEntity {
    var id:Int=0//记录id
    @SerializedName("versionNumber")
    var versionCode: Int = 0//版本号
    @SerializedName("versionCode")
    var versionName: String? = null//版本名
        get() {
            return if(field=="null")"" else field
        }
    @SerializedName("download")
    var downloadUrl: String? = null//下载地址
        get() {
            return if(field=="null")"" else field
        }
    var isForce: Int = 0//是否强制更新
    var description: String? = null//更新内容
        get() {
            return if(field=="null")"" else field
        }

}

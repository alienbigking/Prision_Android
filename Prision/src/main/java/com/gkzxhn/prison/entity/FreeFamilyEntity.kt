package com.gkzxhn.prison.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Raleigh.Luo on 18/4/11.
 */
class FreeFamilyEntity {
    var prisonerName:String?=null//囚犯名字
    var phone:String?=null//家属电话号码
    var prisonerNumber:String?=null//囚犯编号
    var name:String?=null//家属名字
    var prisonerId:String?=null//囚犯id
    @SerializedName("familyId")
    var id:String?=null//家属id
    var relationship:String?=null//关系
}
package com.gkzxhn.prison.entity

import java.io.Serializable


/**
 * Explanation:
 * @author LSX
 *    -----2018/9/13
 */
class MeetingMemberEntity :Serializable {

//    "id": 2,
//    "meetingId": 719,
//    "familyId": 909,
//    "familyName": "胡勇斌",
//    "familyPhone": "HWAFz8T2DjaTakwSgbBzxw==",
//    "familyUuid": "/X40obyhcJMiw2+5rjAyYkkeD20e+pRG",
//    "relationship": "rr",
//    "familyAvatarUrl": "http://123.57.7.159/image-server/avatars/avatar_1534901075419-1534901095700.jpg",
//    "familyIdCardFront": "http://123.57.7.159/image-server/uuids/idFront-1534901095990.jpg",
//    "familyIdCardBack": "http://123.57.7.159/image-server/uuids/idBack-1534901096035.jpg",
//    "familyRelationalProofUrl": ""

    var id: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var meetingId: String? = null//会见ID
        get() {
            return if (field == "null") "" else field
        }
    var familyId: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyName: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyPhone: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyUuid: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var relationship: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyAvatarUrl: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyIdCardFront: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyIdCardBack: String? = null//
        get() {
            return if (field == "null") "" else field
        }
    var familyRelationalProofUrl: String? = null//
        get() {
            return if (field == "null") "" else field
        }

}

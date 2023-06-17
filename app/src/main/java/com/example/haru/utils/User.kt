package com.example.haru.utils

import com.example.haru.data.model.Category
import com.example.haru.data.model.UserRelatedWithSnsData
import java.util.*
import kotlin.collections.ArrayList

//모든 User Id값은 여기 있는 값을 사용
//이 값은 사용자가 로그인할 때 서버에서 받아온다.
object User {
    var id: String = ""
    var name: String = ""  // sns 이름 = nickname
    var isPublicAccount: Boolean = true
    var haruId: String = ""  // 검색용 이름
    var email: String = ""
    var socialAccountType: String = ""
    var isPostBrowsingEnabled: Boolean = true
    var isAllowFeedLike: Int = 0
    var isAllowFeedComment: Int = 0
    var isAllowSearch: Boolean = true
    var isMaliciousUser: Boolean = false
    var createdAt: String = ""
    var accessToken: String = ""
    var amAlarmAprove: Boolean = true
    var amAlarmDate: String = ""
    var pmAlarmAprove: Boolean = true
    var pmAlarmDate: String = ""
    var categories: ArrayList<Category?> = arrayListOf(null, null)
    var isSignUp: Boolean = false
    override fun toString(): String {
        return "User : {id : $id}, " +
                "{name : $name}, " +
                "{isPublicAccount : $isPublicAccount}, " +
                "{haruId : $haruId}, " +
                "{email : $email}, " +
                "{socialAccountType : $socialAccountType}, " +
                "{isPostBrowsingEnabled : $isPostBrowsingEnabled}, " +
                "{isAllowFeedLike : $isAllowFeedLike}, " +
                "{isAllowFeedComment : $isAllowFeedComment}, " +
                "{isAllowSearch : $isAllowSearch}," +
                "{createdAt : $createdAt}, " +
                "{isSignUp : $isSignUp}, "
    }

    fun clear() {
        id = ""
        name = ""  // sns 이름 = nickname
        isPublicAccount = true
        haruId = ""  // 검색용 이름
        email = ""
        socialAccountType = ""
        isPostBrowsingEnabled = true
        isAllowFeedLike = 0
        isAllowFeedComment = 0
        isAllowSearch = true
        isMaliciousUser = false
        createdAt = ""
        accessToken = ""
        amAlarmAprove = true
        amAlarmDate = ""
        pmAlarmAprove = true
        pmAlarmDate = ""
        categories = arrayListOf(null, null)
        isSignUp = false
    }
}

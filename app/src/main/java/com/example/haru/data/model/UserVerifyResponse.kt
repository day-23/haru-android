package com.example.haru.data.model

import com.google.gson.annotations.SerializedName

data class UserVerifyResponse (
    val success: Boolean,
    val data: UserVerify
    )

data class UserVerify(
    val user : UserRelatedWithSnsData,
    val haruId : String,
    val email: String,
    val socialAccountType: String,
    val isPostBrowsingEnabled: Boolean,
    val isAllowFeedLike: Int,
    val isAllowFeedComment: Int,
    val isAllowSearch: Boolean,
    val isMaliciousUser: Boolean,
    val createdAt: String,
    val accessToken: String?,
    val isSignUp : Boolean
)

data class UserRelatedWithSnsData(
    val id: String,
    val name: String,
    val introduction: String,
    val profileImage: String,
    val postCount: Int,
    val friendCount: Int,
    val friendStatus: Int,
    val isPublicAccount: Boolean
)
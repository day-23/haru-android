package com.example.haru.data.model

data class FriendInfo(
    val id: String = "",
    val name: String = "",
    val profileImage : String? = "",
    val isAllowFeedComment : Int = 2,
    val isAllowFeedLike: Int = 2,
    var friendStatus: Int? = 0,
    val createdAt: String? = ""
)

package com.example.haru.data.model

data class MediaUser(
    val id : String = "",
    val name : String = "",
    val profileImage : String = "",
    val isAllowFeedLike: Int = 0,
    val isAllowFeedComment : Int = 0,
    val friendStatus :Int = 0
)

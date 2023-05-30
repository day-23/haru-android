package com.example.haru.data.model

data class MediaUser(
    val id : String,
    val name : String,
    val profileImage : String,
    val isAllowFeedLike: Int,
    val isAllowFeedComment : Int,
    val friendStatus :Int
)

package com.example.haru.data.model

data class FriendsResponse(
    val success : Boolean = false,
    val data: ArrayList<FriendInfo>,
    val pagination: pagination
)

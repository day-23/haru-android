package com.example.haru.data.model

data class Followbody(
    val acceptorId: String
)

data class UnFollowbody(
    val acceptorId: String
)

data class Friendbody(
    val requesterId: String
)

data class DelFriendBody(
    val friendId: String
)

data class BlockBody(
    val blockUserId: String
)
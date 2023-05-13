package com.example.haru.data.model

data class User(
    val id: String,
    val name: String,
    val introduction: String,
    val profileImage: String,
    var isFollowing: Boolean,
    val postCount: Int,
    val followerCount: Int,
    val followingCount: Int
)
package com.example.haru.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val introduction: String = "",
    val profileImage: String = "",
    val postCount: Int = 0,
    val friendCount: Int = 0,
    val friendStatus: Int = 0,
    val isPublicAccount: Boolean = true
)
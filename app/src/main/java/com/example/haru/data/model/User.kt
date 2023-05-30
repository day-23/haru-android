package com.example.haru.data.model

data class User(
    val id: String,
    val name: String,
    val introduction: String,
    val profileImage: String,
    val postCount: Int,
    val friendCount: Int,
    val friendStatus: Int,
    val isPublicAccount: Boolean
)
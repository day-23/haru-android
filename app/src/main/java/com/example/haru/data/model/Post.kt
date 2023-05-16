package com.example.haru.data.model

data class Post(
    val id: String,
    val user: User,
    val content: String,
    val images: ArrayList<Profile>,
    val hashTags: ArrayList<String>,
    var isLiked: Boolean,
    var likedCount: Int,
    var commentCount: Int,
    val createdAt: String,
    val updatedAt: String
)

package com.example.haru.data.model

data class Post(
    val id: String = "",
    val user: User = User(),
    val content: String = "",
    val isTemplatePost: String? = null,
    val images: ArrayList<Profile> = arrayListOf(),
    val hashTags: ArrayList<String> = arrayListOf(),
    var isLiked: Boolean = false,
    var isCommented: Boolean = false,
    var likedCount: Int = 0,
    var commentCount: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

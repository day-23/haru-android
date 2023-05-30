package com.example.haru.data.model

data class Media(
    val id : String,
    val user : MediaUser,
    val content: String,
    val templateUrl : String,
    val images : ArrayList<Profile>,
    val hashTags : ArrayList<String>,
    val isLiked : Boolean,
    val isCommented: Boolean,
    val likedCount : Int,
    val commentCount : Int,
    val createdAt: String,
    val updatedAt : String
)

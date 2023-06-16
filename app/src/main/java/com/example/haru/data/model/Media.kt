package com.example.haru.data.model

data class Media(
    val id : String = "",
    val user : MediaUser = MediaUser(),
    val content: String = "",
    val templateUrl : String? = "",
    val images : ArrayList<Profile> = arrayListOf(),
    val hashTags : ArrayList<String> = arrayListOf(),
    var isLiked : Boolean = false,
    val isCommented: Boolean = false,
    var likedCount : Int = 0,
    val commentCount : Int = 0,
    val createdAt: String = "",
    val updatedAt : String = ""
)

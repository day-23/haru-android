package com.example.haru.data.model

data class Profile(
    val id: String,
    val originalName: String,
    val url: String,
    val mimeType: String,
    val comments: ArrayList<Comments>
)

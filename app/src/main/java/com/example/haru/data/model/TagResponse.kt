package com.example.haru.data.model

data class TagResponse(
    val success: String,
    val data: List<Tag> = emptyList(),
    val error : Error? = null

)
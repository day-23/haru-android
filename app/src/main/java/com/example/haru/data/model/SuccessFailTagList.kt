package com.example.haru.data.model

data class SuccessFailTagList(
    val success : Boolean,
    val data : List<Tag>? = null
)

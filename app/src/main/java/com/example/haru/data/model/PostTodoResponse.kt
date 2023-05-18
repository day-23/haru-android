package com.example.haru.data.model

data class PostTodoResponse(
    val success: Boolean,
    val data: Todo? = null,
    val error: Error? = null
)
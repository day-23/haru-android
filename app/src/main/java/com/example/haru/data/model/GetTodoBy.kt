package com.example.haru.data.model

data class GetTodoBy(
    val success: Boolean,
    val data : List<Todo>? = null,
    val error: Error? = null
)

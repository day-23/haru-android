package com.example.haru.data.model

data class GetTodoResponse(
    val success: String,
    val data: List<Todo>
)
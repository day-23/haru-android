package com.example.haru.data.model

data class GetMainTodoResponse(
    val success: Boolean,
    val data: TodoList = TodoList(),
    val error: Error? = null
)
package com.example.haru.data.model

data class GetTodayTodo(
    val success : Boolean,
    val data: TodoList = TodoList(),
    val error: Error? = null
)

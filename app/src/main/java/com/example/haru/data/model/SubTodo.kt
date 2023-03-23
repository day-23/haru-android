package com.example.haru.data.model

data class SubTodo(
    val id: String,
    val content: String,
    val subTodoOrder: Int,
    val completed: Boolean
)
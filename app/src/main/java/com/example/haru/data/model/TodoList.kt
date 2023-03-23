package com.example.haru.data.model

data class TodoList(
    val flaggedTodos: List<Todo>,
    val taggedTodos: List<Todo>,
    val untaggedTodos: List<Todo>,
    val completedTodos: List<Todo>
)

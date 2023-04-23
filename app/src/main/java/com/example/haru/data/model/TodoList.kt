package com.example.haru.data.model

data class TodoList(
    val flaggedTodos: List<Todo> = emptyList(),
    val taggedTodos: List<Todo> = emptyList(),
    val untaggedTodos: List<Todo> = emptyList(),
    val completedTodos: List<Todo> = emptyList(),
    val todayTodos: List<Todo> = emptyList(),
    val endDatedTodos : List<Todo> = emptyList()
)

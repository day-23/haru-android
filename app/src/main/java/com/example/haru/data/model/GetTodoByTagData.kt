package com.example.haru.data.model

data class GetTodoByTagData(
    val flaggedTodos : List<Todo> = emptyList(),
    val unFlaggedTodos : List<Todo> = emptyList(),
    val completedTodos : List<Todo> = emptyList()
)

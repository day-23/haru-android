package com.example.haru.data.model

data class GetTodoByTag(
    val success: Boolean,
    val data: GetTodoByTagData = GetTodoByTagData(),
    val error: Error? = null
)

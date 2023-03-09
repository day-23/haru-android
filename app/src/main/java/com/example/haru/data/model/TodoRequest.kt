package com.example.haru.data.model

data class TodoRequest(
    val content: String,
    val memo: String,
    val todayTodo: Boolean,
    val flag: Boolean,
    val repeatOption: String?,
    val repeat: String?,
    )
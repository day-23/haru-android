package com.example.haru.data.model

import java.util.Date

data class Todo(
    val id: String,
    val content: String,
    val memo: String?,
    val todayTodo: Boolean,
    val flag: Boolean,
    val repeatOption: String?,
    val repeat: String?,
    val endDate: Date,
    val endDateTime: Date?,
    val createdAt: Date,
    val subTodos: List<SubTodo>,
    val tags: List<Tag>
)
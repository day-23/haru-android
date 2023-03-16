package com.example.haru.data.model

import java.util.Date

data class Todo(
    val id: String,
    val content: String,
    val memo: String?,
    val todayTodo: Boolean,
    val flag: Boolean,
    val repeatOption: String?,
    val repeatWeek: String?,
    val repeatMonth: String?,
    val repeatYear: String?,
    val endDate: String?,
    val endDateTime: String?,
    val createdAt: String,
    val subTodos: List<SubTodo>,
    val tags: List<Tag>
)
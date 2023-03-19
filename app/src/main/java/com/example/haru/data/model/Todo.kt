package com.example.haru.data.model

data class Todo (
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
    val repeatEnd: String?,
    val createdAt: String,
    val updatedAt: String?,
    val subTodos: List<SubTodo>,
    val tags: List<Tag>
)
package com.example.haru.data.model

import java.util.Date

data class Todo(
    val id: String,
    val content: String,
    val memo: String?,
    val todayTodo: Boolean,
    val flag: Boolean,
    val isSelectedEndDateTime: Boolean,
    val endDate: String?,
    val repeatEnd: String?,
    val todoOrder: Int,
    val completed: Boolean,
    val createdAt: String,
    val updatedAt: String?,
    val subTodos: List<SubTodo>,
    val alarms: List<Alarm>,
    val repeatOption: String?,
    val repeatValue: String?,
    val tags: List<Tag>
)
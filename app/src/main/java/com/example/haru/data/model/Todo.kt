package com.example.haru.data.model

import java.util.Date
data class Todo(
    val type: Int = 2,
    val id: String = "",
    val content: String = "",
    val memo: String = "",
    val todayTodo: Boolean = false,
    val flag: Boolean = false,
    val isAllDay: Boolean = false,
    val endDate: String? = null,
    val repeatEnd: String? = null,
    val todoOrder: Int = 0,
    val folded : Boolean = false,
    val completed: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String? = null,
    val subTodos: List<SubTodo> = emptyList(),
    val alarms: List<Alarm> = emptyList(),
    val repeatOption: String? = null,
    val repeatValue: String? = null,
    val tags: List<Tag> = emptyList()
)
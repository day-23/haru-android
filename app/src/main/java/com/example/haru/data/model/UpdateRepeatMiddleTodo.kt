package com.example.haru.data.model

data class UpdateRepeatMiddleTodo(
    val content: String = "",
    val memo: String? = "",
    val todayTodo: Boolean = false,
    val flag: Boolean = false,
    val isAllDay: Boolean = false,
    val endDate: String? = null,
    val repeatEnd: String? = null,
    val completed: Boolean = false,
    val subTodosCompleted : List<Boolean> = emptyList(),
    val subTodos: List<String> = emptyList(),
    val alarms: List<String> = emptyList(),
    val repeatOption: String? = null,
    val repeatValue: String? = null,
    val tags: List<String> = emptyList(),
    val changedDate : String,
    val nextEndDate : String
)

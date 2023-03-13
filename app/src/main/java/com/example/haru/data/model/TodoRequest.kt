package com.example.haru.data.model

import java.util.Date

data class TodoRequest(
    val content: String,
    val memo: String,
    val todayTodo: Boolean,
    val flag: Boolean,
    val endDate: Date? = null,
    val endDateTime: Date? = null,
    val repeatOption: String? = null,
    val repeat: String? = null,
    val repeatEnd: Date? = null,
    val tags: List<String>,
    val subTodos: List<String>,
    val alarms: List<String>,
    )
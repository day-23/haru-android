package com.example.haru.data.model

data class Schedule(
    val type: Int = 2,
    val id: String = "",
    val content: String = "",
    val memo: String? = "",
    val isAllDay: Boolean = false,
    val timeOption: String? = null,
    val repeatStart: String? = null,
    val repeatEnd: String? = null,
    val completed: Boolean = false,
    val createdAt: String = "",
    val updateAt: String = "",
    val category: Category? = null,
    val alarms: List<Alarm> = emptyList(),
    val repeatOption: String? = null,
    val repeatValue: String? = null
)
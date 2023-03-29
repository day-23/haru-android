package com.example.haru.data.model

data class Schedule(
    val id: String,
    val content: String,
    val memo: String? = "",
    val flag: Boolean = false,
    val timeOption: Boolean = false,
    val repeatStart: String,
    val repeatEnd: String,
    val createdAt: String,
    val category: Category,
    val alarms : List<Alarm> = emptyList(),
     val repeatOption : String? = null,
     val repeatValue : String? = null,
)


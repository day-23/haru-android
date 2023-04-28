package com.example.haru.data.model

data class PostSchedule(
    val content : String,
    val memo : String?,
    val isAllDay: Boolean,
    val repeatStart : String,
    val repeatEnd : String?,
    val repeatOption : String?,
    val repeatValue : String?,
    val categoryId : String?,
    val alarms : List<String>
)
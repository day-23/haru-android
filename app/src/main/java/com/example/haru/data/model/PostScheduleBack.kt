package com.example.haru.data.model

data class PostScheduleBack(
    val content : String,
    val memo : String,
    val categoryId : String?,
    val alarms : List<String>,
    val isAllDay : Boolean,
    val repeatOption : String?,
    val repeatValue: String?,
    val repeatStart : String,
    val repeatEnd : String,
    val preRepeatEnd: String
)
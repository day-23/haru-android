package com.example.haru.data.model

data class PostScheduleMiddle(
    val content : String,
    val memo : String,
    val categoryId : Category?,
    val alarms : List<String>,
    val isAllDay : Boolean,
    val repeatOption : String?,
    val repeatValue: String?,
    val repeatStart : String,
    val repeatEnd : String,
    val changedDate: String,
    val nextRepeatStart : String
)
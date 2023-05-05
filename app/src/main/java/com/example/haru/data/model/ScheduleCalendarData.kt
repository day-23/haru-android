package com.example.haru.data.model

data class ScheduleCalendarData(
    val schedule: Schedule,
    val position: Int,
    var cnt: Int? = null,
    var timeInterval: Int? = null
)
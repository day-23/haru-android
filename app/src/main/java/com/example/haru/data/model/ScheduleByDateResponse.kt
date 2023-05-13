package com.example.haru.data.model

data class ScheduleByDateResponse(
    val schedules: List<Schedule>,
    val holidays: List<Holiday>
)

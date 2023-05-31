package com.example.haru.data.model

data class GetSearchResponse(
    val success : Boolean,
    val data : TodoSchedule? = null,
    val error: Error? = null
)

data class TodoSchedule(
    val schedules : List<Schedule>,
    val todos : List<Todo>
)

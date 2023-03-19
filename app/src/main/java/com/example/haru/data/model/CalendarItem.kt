package com.example.haru.data.model

import java.util.Date

data class CalendarItem(
    var alpha: Float,
    var color: Int,
    var date: Date,
    var selected: Boolean = false,
    var todos: ArrayList<Todo?>? = null,
    //var schedule: Schedule = null,
)
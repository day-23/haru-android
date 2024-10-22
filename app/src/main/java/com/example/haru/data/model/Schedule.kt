package com.example.haru.data.model

import java.util.*

data class Schedule(
    val searchType : Int = 1,
    val type: Int = 2,
    val id: String = "",
    val content: String = "",
    val memo: String? = "",
    val isAllDay: Boolean = false,
    val timeOption: String? = null,
    var repeatStart: String? = null,
    var repeatEnd: String? = null,
    val createdAt: String = "",
    val updateAt: String = "",
    val category: Category? = null,
    val alarms: List<Alarm> = emptyList(),
    val repeatOption: String? = null,
    val repeatValue: String? = null,
    var location: Int? = null,
    var startTime: Date? = null,
    var endTime: Date? = null
)

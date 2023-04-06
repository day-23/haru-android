package com.example.haru.data.model

data class Schedule(
    val type: Int = 2,
    val id: String = "",
    var content: String = "",
    var memo: String? = "",
    var flag: Boolean = false,
    val timeOption: String? = null,
    var repeatStart: String? = null,
    var repeatEnd: String? = null,
    var completed: Boolean = false,
    val createdAt: String = "",
    var category: Category? = null,
    var alarms: List<Alarm> = emptyList(),
    var repeatOption: String? = null,
    var repeatValue: String? = null
)

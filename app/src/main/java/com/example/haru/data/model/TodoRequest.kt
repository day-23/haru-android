package com.example.haru.data.model

data class TodoRequest(
    var content: String,
    var memo: String,
    var todayTodo: Boolean,
    var flag: Boolean,
    var endDate: String? = null,
    var endDateTime: String? = null,
    var repeatOption: String? = null,
    var repeatWeek: String? = null,
    var repeatMonth: String? = null,
    var repeatEnd: String? = null,
    var tags: List<String>,
    var subTodos: List<String>,
    var alarms: List<String>,
)
package com.example.haru.data.model

data class TodoRequest(
    var content: String,
    var memo: String,
    var todayTodo: Boolean,
    var flag: Boolean,
    var isSelectedEndDateTime: Boolean,
    var endDate: String? = null,
    var repeatOption: String? = null,
    var repeatValue: String? = null,
    var repeatEnd: String? = null,
    var tags: List<String>,
    var subTodos: List<String>,
    var alarms: List<String>,
)
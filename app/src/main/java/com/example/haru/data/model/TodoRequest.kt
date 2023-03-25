package com.example.haru.data.model

data class TodoRequest(
    var content: String,
    var flag: Boolean,
    var subTodos: List<String>,
    var tags: List<String>,
    var todayTodo: Boolean,
    var isSelectedEndDateTime: Boolean,
    var endDate: String? = null,
    var alarms: List<String>,
    var repeatOption: String? = null,
    var repeatValue: String? = null,
    var repeatEnd: String? = null,
    var memo: String,
    )
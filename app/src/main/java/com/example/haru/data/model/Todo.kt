package com.example.haru.data.model

data class Todo(
    var type: Int = 2,
    var id: String = "",
    var content: String = "",
    var memo: String = "",
    var todayTodo: Boolean = false,
    var flag: Boolean = false,
    var isAllDay: Boolean = false,
    var endDate: String? = null,
    var repeatEnd: String? = null,
    var todoOrder: Int = 0,
    var folded : Boolean = false,
    var completed: Boolean = false,
    var createdAt: String = "",
    var updatedAt: String? = null,
    var subTodos: List<SubTodo> = emptyList(),
    var alarms: List<Alarm> = emptyList(),
    var repeatOption: String? = null,
    var repeatValue: String? = null,
    var tags: List<Tag> = emptyList(),
    var location: Int? = null
)
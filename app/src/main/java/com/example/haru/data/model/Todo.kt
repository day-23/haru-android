package com.example.haru.data.model

import java.util.Date

data class Todo(
    val id: Int,
    val content: String,
    val memo: String?,
    val todayTodo: Boolean,
    val flag: Boolean,
    val repeatOption: String?,
    val repeat: String?,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date?,
    val user: User,
//    val todolog,
//    val subtodo,
)
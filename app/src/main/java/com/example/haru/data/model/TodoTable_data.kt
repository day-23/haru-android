package com.example.haru.data.model

data class TodoTable_data(
    val id: String,
    val content : String,
    val endDate: String? = null,
    val completed: Boolean,
)

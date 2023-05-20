package com.example.haru.data.model

data class UpdateTodoResponse(
    val success : Boolean,
    val data : Todo? = null,
    val error: Error? = null
)

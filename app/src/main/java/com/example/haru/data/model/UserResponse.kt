package com.example.haru.data.model

data class UserResponse (
    val success: Boolean,
    val data : User? = null,
    val error : Error? = null
)
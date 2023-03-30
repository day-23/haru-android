package com.example.haru.data.model

data class SuccessFail(
    val success: Boolean,
    val error : Error? = null
)

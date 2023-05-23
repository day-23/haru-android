package com.example.haru.data.model

data class SuccessFailCount(
    val success : Boolean,
    val data : Int? = null,
    val error : Error? = null
)

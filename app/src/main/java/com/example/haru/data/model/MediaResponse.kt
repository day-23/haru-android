package com.example.haru.data.model

data class MediaResponse(
    val success : Boolean,
    val data: ArrayList<Media>,
    val pagination: pagination
)
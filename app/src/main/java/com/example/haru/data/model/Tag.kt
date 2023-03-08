package com.example.haru.data.model

import java.util.Date

data class Tag(
    var id: Int,
    var content: String,
    var createdAt: Date,
    var updatedAt: Date,
    var deletedAt: Date?
)
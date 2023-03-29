package com.example.haru.data.model

data class Category(
    val id: String,
    val content: String,
    val color: String? = null,
    val isSelected: Boolean = false,
)

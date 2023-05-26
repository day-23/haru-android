package com.example.haru.data.model

data class Comments(
    val id: String,
    val user: User,
    val content: String,
    val x : Int,
    val y: Int,
    var isPublic : Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class pagination(
    val totalItems: Int,
    val itemsPerPage: Int,
    val currentPage: Int,
    val totalPages: Int
)

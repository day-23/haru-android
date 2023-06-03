package com.example.haru.data.model

data class Template(
    val templateId: String,
    val templateTextColor: String,
    val content: String,
    val hashTags : ArrayList<String>
)

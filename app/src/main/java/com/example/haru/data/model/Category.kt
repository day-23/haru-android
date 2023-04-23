package com.example.haru.data.model

import java.io.Serializable

data class Category(
    val id:String,
    val content:String,
    val color:String?,
    val isSelected: Boolean
) : Serializable

package com.example.haru.data.model

import java.io.Serializable

data class Category(
    val id:String,
    var content:String,
    var color:String?,
    var isSelected: Boolean
) : Serializable

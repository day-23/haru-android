package com.example.haru.data.model

import androidx.activity.result.contract.ActivityResultContracts

data class SendPost(
    val id : String? = "",
    val images: ArrayList<Profile>? = arrayListOf(),
    val hashTags: ArrayList<String>? = arrayListOf(),
    val content: String? = "",
    val createdAt: String? = "",
    val updatedAt: String? = ""
)

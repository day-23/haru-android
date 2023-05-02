package com.example.haru.data.model

import okhttp3.MultipartBody

data class AddPost(
    val images : ArrayList<MultipartBody.Part>,
    val content : String? = "",
    val hashtags : ArrayList<String> = arrayListOf("")
)

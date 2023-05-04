package com.example.haru.data.model

import okhttp3.MultipartBody

//type 변경
data class AddPost(
    val images : MutableList<MultipartBody.Part>,
    val content : String,
    val hashtags : List<String>
)

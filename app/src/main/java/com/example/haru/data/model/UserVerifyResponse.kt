package com.example.haru.data.model

import com.google.gson.annotations.SerializedName

data class UserVerifyResponse (
    val success: Boolean,
    val data: UserVerify
    )


data class UserVerify(
    val id: String,
    val accessToken: String
)

package com.example.haru.data.model

import com.google.gson.annotations.SerializedName

data class UserKakaoAuthResponse (
    val success: Boolean,
    val data: UserKakaoAuth
    )


data class UserKakaoAuth(
    val id: String,
    val name : String,
    val cookie: String,
    val accessToken: String,
    val refreshToken: String,
)

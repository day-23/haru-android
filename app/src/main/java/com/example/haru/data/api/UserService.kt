package com.example.haru.data.api

import com.example.haru.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): User
}

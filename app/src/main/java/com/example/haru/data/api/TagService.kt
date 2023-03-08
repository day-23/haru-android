package com.example.haru.data.api

import com.example.haru.data.model.Tag
import com.example.haru.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface TagService {
    @GET("tag/{user}")
    suspend fun getTag(@Path("user") user: User): List<Tag>
}
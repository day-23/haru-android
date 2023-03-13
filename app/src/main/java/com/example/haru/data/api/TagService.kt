package com.example.haru.data.api

import com.example.haru.data.model.TagResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TagService {

    @GET("tag/{userId}/tags")
    fun getTag(@Path("userId") userId : String): Call<TagResponse>
}
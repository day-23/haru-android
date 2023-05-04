package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TagService {
    @GET("tag/{userId}/tags")
    fun getTag(@Path("userId") userId : String): Call<TagResponse>

    @POST("tag/{userId}/tag")
    fun createTag(@Path("userId") userId: String, @Body content : Content) : Call<SuccessFailTag>

    @POST("tag/{userId}/tags")
    fun createTagList(@Path("userId") userId: String, @Body contents: ContentList) : Call<SuccessFailTagList>
}
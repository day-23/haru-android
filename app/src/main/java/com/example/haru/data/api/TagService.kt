package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface TagService {
    @GET("tag/{userId}/tags")
    fun getTag(@Path("userId") userId : String): Call<TagResponse>

    @POST("tag/{userId}/tag")
    fun createTag(@Path("userId") userId: String, @Body content : Content) : Call<SuccessFailTag>

//    @POST("tag/{userId}/tags")
//    fun createTagList(@Path("userId") userId: String, @Body contents: ContentList) : Call<SuccessFailTagList>

    @HTTP(method = "DELETE", path = "tag/{userId}/tags", hasBody = true)
    fun deleteTagList(@Path("userId") userId: String, @Body tagItList : TagIdList) : Call<SuccessFail>

    @PATCH("tag/{userId}/{tagId}")
    fun updateTag(@Path("userId") userId: String, @Path("tagId") tagId: String, @Body updateContent: TagUpdate) : Call<SuccessFailTag>
}
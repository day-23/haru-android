package com.example.haru.data.api

import com.example.haru.data.model.PostResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {

    @GET("post/{userId}/posts/all")
    fun getPosts(@Path("userId") userId: String, @Query("page") page:String) : Call<PostResponse>

}
package com.example.haru.data.api

import com.example.haru.data.model.LikeResponse
import com.example.haru.data.model.PostResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {

    @GET("post/{userId}/posts/all")
    fun getPosts(@Path("userId") userId: String, @Query("page") page:String) : Call<PostResponse>

    @GET("post/{userId}/posts/user/{targetId}/feed")
    fun getMyFeed(@Path("userId") userId: String,
                  @Path("targetId") targetId : String,
                  @Query("page") page:String) : Call<PostResponse>

    @POST("post/{userId}/{postId}/like")
    fun postLike(@Path("userId") userId: String, @Path("postId") postId:String) : Call<LikeResponse>

}
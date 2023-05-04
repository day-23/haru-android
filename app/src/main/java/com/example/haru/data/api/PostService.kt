package com.example.haru.data.api

import com.example.haru.data.model.AddPost
import com.example.haru.data.model.AddPostResponse
import com.example.haru.data.model.LikeResponse
import com.example.haru.data.model.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @GET("post/{userId}/posts/all")
    fun getPosts(@Path("userId") userId: String, @Query("page") page:String) : Call<PostResponse>

    @GET("post/{userId}/posts/user/{targetId}/feed")
    fun getMyFeed(@Path("userId") userId: String,
                  @Path("targetId") targetId : String,
                  @Query("page") page:String) : Call<PostResponse>

    @POST("post/{userId}/{postId}/like")
    fun postLike(@Path("userId") userId: String, @Path("postId") postId:String) : Call<LikeResponse>

    @Multipart
    @POST("post/{userId}")
    fun addPost(
        @Path("userId") userId: String,
        @Part images: List<MultipartBody.Part>,
        @Part("content") content: RequestBody,
        @PartMap hashTags: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<AddPostResponse>
}
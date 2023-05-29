package com.example.haru.data.api

import com.example.haru.data.model.*
import com.example.haru.data.model.Tag
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PostService {
    @GET("post/{userId}/posts/all")
    fun getPosts(@Path("userId") userId: String, @Query("lastCreatedAt") lastCreatedAt:String) : Call<PostResponse>

    @GET("post/{userId}/posts/all?page=1")
    fun getFirstPosts(@Path("userId") userId: String) : Call<PostResponse>

    @GET("post/{userId}/posts/user/{targetId}/feed") //TODO:lastCreatedAt 처리 해주어야함
    fun getMyFeed(@Path("userId") userId: String,
                  @Path("targetId") targetId : String,
                  @Query("page") page:String) : Call<PostResponse>

    @GET("post/{userId}/posts/user/{targetId}/media?page=1")
    fun getFirstMedia(@Path("userId") userId: String, @Path("targetId") targetId: String) : Call<MediaResponse>

    @GET("post/{userId}/posts/user/{targetId}/media")
    fun getMedia(@Path("userId") userId: String, @Path("targetId") targetId: String, @Query("lastCreatedAt") lastCreatedAt: String) : Call<MediaResponse>

    @GET("comment/{userId}/{postId}/{imageId}/comments/all")
    fun getComments(
        @Path("userId") userId: String,
        @Path("postId") postId: String,
        @Path("imageId") imageId: String,
        @Query("lastCreatedAt") lastCreatedAt: String
    ): Call<CommentsResponse>

    @GET("comment/{userId}/{postId}/{imageId}/comments/all?page=1")
    fun getFirstComments(
        @Path("userId") userId: String,
        @Path("postId") postId: String,
        @Path("imageId") imageId: String,
    ): Call<CommentsResponse>

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
    @POST("comment/{userId}/{postId}/{imageId}")
    fun writeComments(
        @Path("userId") userId: String,
        @Path("postId") postId: String,
        @Path("imageId") imageId: String,
        @Body comment: CommentBody
    ): Call<WriteCommentResponse>

    @DELETE("post/{userId}/{postId}")
    fun deletePost(
        @Path("userId") userId: String,
        @Path("postId") postId: String
    ): Call<LikeResponse>

    @DELETE("comment/{writerId}/{commentId}")
    fun deleteComment(
        @Path("writerId") writerId: String,
        @Path("commentId") commentId: String,
    ): Call<EditCommentResponse>

    @PATCH("comment/{writerId}/{commentId}")
    fun changeComment(
        @Path("writerId") writerId: String,
        @Path("commentId") commentId: String,
        @Body body: PatchCommentBody
    ): Call<EditCommentResponse>

    @GET("post/{userId}/hashtags/{targetId}")
    fun getUserTags(
        @Path("userId") userId: String,
        @Path("targetId") targetId: String
    ): Call<TagResponse>

    @GET("post/{userId}/posts/user/{targetId}/media/hashtag/{tagId}?page=1")
    fun getFirstTagMedia(
        @Path("userId") userId: String,
        @Path("targetId") targetId: String,
        @Path("tagId") tagId: String
    ) : Call<MediaResponse>

}
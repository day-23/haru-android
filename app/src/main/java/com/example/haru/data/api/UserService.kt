package com.example.haru.data.api

import android.util.Log
import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): User
    //친구 요청
    @POST("friends/{userId}/request")
    fun requestFriend(@Path("userId") userId: String, @Body followingId : Followbody) : Call<FollowResponse>
    //요청 취소
    @HTTP(method = "DELETE", path = "friends/{userId}/request", hasBody = true)
    fun requestUnFriend(@Path("userId") userId: String, @Body followingId: UnFollowbody) : Call<FollowResponse>
    //친구 삭제
    @HTTP(method = "DELETE", path = "friends/{userId}", hasBody = true)
    fun requestDelFriend(@Path("userId") userId: String, @Body followingId: UnFollowbody) : Call<FollowResponse>

    //친구 목록
    @GET("friends/{userId}/{targetId}/")
    fun getFriendsList(@Path("userId") userId: String, @Path("targetId") targetId: String, @Query("lastCreatedAt") lastCreatedAt: String) : Call<FriendsResponse>
    @GET("friends/{userId}/{targetId}/")
    fun getFirstFriendsList(@Path("userId") userId: String, @Path("targetId") targetId: String, @Query("page") page: String) : Call<FriendsResponse>
}

package com.example.haru.data.api

import android.util.Log
import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //친구 요청
    @POST("friends/{userId}/request")
    fun requestFriend(@Path("userId") userId: String, @Body followingId : Followbody) : Call<FollowResponse>
    //요청 취소
    @HTTP(method = "DELETE", path = "friends/{userId}/request", hasBody = true)
    fun requestUnFriend(@Path("userId") userId: String, @Body followingId: UnFollowbody) : Call<FollowResponse>
    //친구 삭제
    @HTTP(method = "DELETE", path = "friends/{userId}", hasBody = true)
    fun requestDelFriend(@Path("userId") userId: String, @Body followingId: DelFriendBody) : Call<FollowResponse>
    //친구 수락
    @POST("friends/{userId}/accept")
    fun acceptFriend(@Path("userId") userId: String, @Body followingId : Friendbody) : Call<FollowResponse>
    //유저 차단
    @POST("friends/{userId}/block")
    fun blockUser(@Path("userId") userId: String, @Body blockId : BlockBody) : Call<FollowResponse>

    //친구 목록
    @GET("friends/{userId}/{targetId}/")
    fun getFriendsList(@Path("userId") userId: String, @Path("targetId") targetId: String, @Query("lastCreatedAt") lastCreatedAt: String) : Call<FriendsResponse>
    @GET("friends/{userId}/{targetId}/")
    fun getFirstFriendsList(@Path("userId") userId: String, @Path("targetId") targetId: String, @Query("page") page: String) : Call<FriendsResponse>

    //친구 신청 목록
    @GET("friends/{userId}/request")
    fun getRequestList(@Path("userId") userId: String, @Query("lastCreatedAt") lastCreatedAt: String) : Call<FriendsResponse>
    @GET("friends/{userId}/request")
    fun getFirstRequestList(@Path("userId") userId: String, @Query("page") page: String) : Call<FriendsResponse>

    @GET("post/{userId}/search/user/{targetId}")
    fun getSearchUserInfo(@Path("userId") userId: String, @Path("targetId") targetId: String) : Call<UserResponse>

    @PATCH("user/{userId}/setting")
    fun updateUserInfo(@Path("userId") userId: String, @Body body : Any) : Call<SuccessFail>

    @DELETE("user/{userId}")
    fun deleteUserAccount(@Path("userId") userId: String) : Call<SuccessFail>
}

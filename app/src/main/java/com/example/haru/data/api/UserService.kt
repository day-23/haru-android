package com.example.haru.data.api

import android.util.Log
import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): User

    @POST("follows/{userId}/follow")
    fun requestFollowing(@Path("userId") userId: String, @Body followingId : Followbody) : Call<FollowResponse>
    @HTTP(method = "DELETE", path = "follows/{userId}/following", hasBody = true)
    fun requestUnFollowing(@Path("userId") userId: String, @Body followingId: UnFollowbody) : Call<FollowResponse>
}

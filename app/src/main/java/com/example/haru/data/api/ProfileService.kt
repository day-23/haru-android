package com.example.haru.data.api

import com.example.haru.data.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @Multipart
    @PATCH("post/{userId}/profile/image")
    fun editProfile(@Path("userId") userId: String,
                    @Part image: MultipartBody.Part,
                    @Part("name")name:String,
                    @Part("introduction")introduction: String) : Call<GetProfileResponse>

    @PATCH("post/{userId}/profile")
    fun editProfileName(@Path("userId") userId: String,
                        @Body body: EditBody) : Call<GetProfileResponse>

    @GET("post/{userId}/info/images")
    fun getProfile(@Path("userId") userId: String) : Call<ProfileListResponse>

    @GET("post/{userId}/info/{targetId}")
    fun getUserInfo(@Path("userId") userId: String, @Path("targetId") targetId:String): Call<UserResponse>
}
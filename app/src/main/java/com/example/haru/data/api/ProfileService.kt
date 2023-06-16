package com.example.haru.data.api

import com.example.haru.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @Multipart
    @PATCH("post/{userId}/profile/image")
    fun editProfile(@Path("userId") userId: String,
                    @Part image: MultipartBody.Part,
                    @Part("name") name: RequestBody,
                    @Part("introduction") introduction: RequestBody) : Call<GetProfileResponse>

    @PATCH("post/{userId}/profile")
    fun editProfileName(@Path("userId") userId: String,
                        @Body body: EditBody) : Call<GetProfileResponse>

    @PATCH("post/{userId}/profile/init")
    fun editProfileInit(@Path("userId") userId: String,
                        @Body body: ProfileInitBody) : Call<UserVerifyResponse>

    @GET("post/{userId}/info/images")
    fun getProfile(@Path("userId") userId: String) : Call<ProfileListResponse>

    @GET("post/{userId}/info/{targetId}")
    fun getUserInfo(@Path("userId") userId: String, @Path("targetId") targetId:String): Call<UserResponse>

    @PATCH("post/{userId}/profile/init/name")
    fun testName(@Path("userId") userId: String, @Body body: UpdateName) : Call<TestInitResponse>

    @PATCH("post/{userId}/profile/init/haruId")
    fun testHaruId(@Path("userId") userId: String, @Body body: UpdateHaruId) : Call<TestInitResponse>
}
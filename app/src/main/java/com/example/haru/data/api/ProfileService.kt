package com.example.haru.data.api

import com.example.haru.data.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @Multipart
    @POST("post/{userId}/profile/image")
    fun editProfile(@Path("userId") userId: String, @Part imageFile: MultipartBody.Part) : Call<GetProfileResponse>

    @GET("post/{userId}/profile/images")
    fun getProfile(@Path("userId") userId: String) : Call<GetProfileResponse>
}
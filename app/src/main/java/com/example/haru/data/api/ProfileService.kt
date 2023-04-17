package com.example.haru.data.api

import com.example.haru.data.model.GetScheduleResponse
import com.example.haru.data.model.PostTodoResponse
import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.TodoRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileService {

    @Multipart
    @POST("post/{userId}/profile/image")
    fun editProfile(@Path("userId") userId: String, @Part imageFile: MultipartBody.Part) : Call<String>
}
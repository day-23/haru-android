package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface AllDoService {
    @POST("schedule/{userId}/schedules/date/all")
    fun getAllDoDates(@Path("userId") userId: String, @Body category:AlldoBodyCategory) : Call<PostAllDoResponse>
}
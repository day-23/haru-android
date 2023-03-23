package com.example.haru.data.api

import com.example.haru.data.model.GetScheduleResponse
import com.example.haru.data.model.PostTodoResponse
import com.example.haru.data.model.TodoRequest
import retrofit2.Call
import retrofit2.http.*

interface ScheduleService {
    @GET("schedule/{userId}/schedules/date")
    fun getScheduleDates(@Path("userId") userId: String, @Query("startDate") startDate:String, @Query("endDate") endDate:String) : Call<GetScheduleResponse>

    @POST("todo/{userId}")
    fun createSchedule(@Path("userId") userId: String, @Body todoRequest: TodoRequest) : Call<PostTodoResponse>
}
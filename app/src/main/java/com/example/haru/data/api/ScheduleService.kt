package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface ScheduleService {
    @POST("schedule/{userId}/schedules/date")
    fun getScheduleDates(@Path("userId") userId: String, @Body scheduleRequest: ScheduleRequest) : Call<GetScheduleResponse>

    @POST("schedule/{userId}")
    fun createSchedule(@Path("userId") userId: String, @Body postSchedule: PostSchedule) : Call<PostScheduleResponse>

    @HTTP(method = "DELETE", path="schedule/{userId}/{scheduleId}")
    fun deleteSchedule(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String) : Call<SuccessFail>
}
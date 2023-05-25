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

    @HTTP(method = "DELETE", path="schedule/{userId}/{scheduleId}/repeat/front")
    fun deleteScheduleFront(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body frontDelete: ScheduleFrontDelete) : Call<SuccessFail>

    @HTTP(method = "DELETE", path="schedule/{userId}/{scheduleId}/repeat/middle")
    fun deleteScheduleMiddle(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body middleDelete: ScheduleMiddleDelete) : Call<SuccessFail>

    @HTTP(method = "DELETE", path="schedule/{userId}/{scheduleId}/repeat/back")
    fun deleteScheduleBack(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body backDelete: ScheduleBackDelete) : Call<SuccessFail>

    @PATCH("schedule/{userId}/{scheduleId}")
    fun submitSchedule(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body postSchedule: PostSchedule) : Call<SuccessFail>

    @PUT("schedule/{userId}/{scheduleId}/repeat/front")
    fun submitScheduleFront(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body postSchedule: PostScheduleFront) : Call<SuccessFail>

    @PUT("schedule/{userId}/{scheduleId}/repeat/middle")
    fun submitScheduleMiddle(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body postSchedule: PostScheduleMiddle) : Call<SuccessFail>

    @PUT("schedule/{userId}/{scheduleId}/repeat/back")
    fun submitScheduleBack(@Path("userId") userId: String, @Path("scheduleId") scheduleId: String, @Body postSchedule: PostScheduleBack) : Call<SuccessFail>
}
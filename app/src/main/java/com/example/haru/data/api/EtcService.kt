package com.example.haru.data.api

import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.StatisticsResponse
import com.example.haru.data.model.SuccessFail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface EtcService {

//    https://api.23haru.com/todo/ysr/todos/statistics
    @POST("todo/{userId}/todos/statistics")
    fun getTodoStatistics(@Path("userId") userId : String, @Body body: ScheduleRequest) : Call<StatisticsResponse>

//    http://localhost:3000/user/c9b33f57-09ab-4bb8-9953-8993b1f1b050/setting
    @PATCH("user/{userId}/setting")
    fun updateUserInfo(@Path("userId") userId: String, @Body body : Any) : Call<SuccessFail>
}
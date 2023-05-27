package com.example.haru.data.api

import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.StatisticsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface EtcService {

//    https://api.23haru.com/todo/ysr/todos/statistics
    @POST("todo/{userId}/todos/statistics")
    fun getTodoStatistics(@Path("userId") userId : String, @Body body: ScheduleRequest) : Call<StatisticsResponse>
}
package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository() {
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getScheduleByDates(startDate:String, endDate:String, body: ScheduleRequest, callback:(todoData : List<Schedule>) -> Unit) = withContext(Dispatchers.IO) {
        val response = scheduleService.getScheduleDates(
            //"881c51d1-06f1-47ce-99b6-b5582594db12",
            "005224c0-eec1-4638-9143-58cbfc9688c5",
            startDate,
            endDate,
            body,
        ).execute()
        val data: GetScheduleResponse
        val todoData: List<Schedule>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            todoData = emptyList()
        }
        callback(todoData)
    }
}
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
            "jts",
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

    suspend fun postSchedule(body: PostSchedule, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.createSchedule(
                "ysr",
                body
            ).execute()

            if (response.isSuccessful) {
                Log.d("TAG", "Success to post schedule")
//                data = response.body()!!
//                schedule = data.data
            } else {
                Log.d("TAG", "Fail to post schedule")
//                schedule = null
            }

//            val data: PostScheduleResponse
//            val schedule: Schedule?
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}
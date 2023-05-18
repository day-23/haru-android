package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository() {
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getScheduleByDates(startDate:String, endDate:String, body: ScheduleRequest, callback:(todoData : ScheduleByDateResponse) -> Unit) = withContext(Dispatchers.IO) {
        val response = scheduleService.getScheduleDates(
            "jts",
            body,
        ).execute()
        val data: GetScheduleResponse
        val todoData: ScheduleByDateResponse

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            todoData = ScheduleByDateResponse(emptyList(), emptyList())
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

    suspend fun deleteSchedule(scheduleId: String, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.deleteSchedule(
                "ysr",
                scheduleId
            ).execute()

            if (response.isSuccessful) {
                Log.d("TAG", "Success to delete schedule")
            } else {
                Log.d("TAG", "Fail to delete schedule")
            }

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    suspend fun submitSchedule(scheduleId: String, postBody: PostSchedule, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.submitSchedule(
                "ysr",
                scheduleId,
                postBody
            ).execute()

            if (response.isSuccessful) {
                Log.d("TAG", "Success to submit schedule")
            } else {
                Log.d("TAG", "Fail to submit schedule")
            }

            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}
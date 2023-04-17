package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.GetScheduleResponse
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository() {
    private val scheduleService = RetrofitClient.profileService

//    suspend fun getScheduleByDates(startDate:String, endDate:String, body: ScheduleRequest, callback:(todoData : List<Schedule>) -> Unit) = withContext(
//        Dispatchers.IO) {
//        val response = scheduleService.editProfile(
//            //"881c51d1-06f1-47ce-99b6-b5582594db12",
//            "dd62593d-161b-45cb-9534-346cd5b5e556",
//            body,
//        ).execute()
//        val data: GetScheduleResponse
//        val todoData: List<Schedule>
//
//        if (response.isSuccessful) {
//            Log.d("TAG", "Success to get todos")
//            data = response.body()!!
//            todoData = data.data
//        } else {
//            Log.d("TAG", "Fail to get todos")
//            todoData = emptyList()
//        }
//        callback(todoData)
//    }
}
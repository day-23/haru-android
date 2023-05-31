package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository() {
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getScheduleTodoSearch(content: Content) = withContext(Dispatchers.IO) {
        val response = scheduleService.getScheduleTodoSearch(User.id, content).execute()
        var data = response.body()

        val searchData : GetSearchResponse? = if (response.isSuccessful) {
            Log.d("TAG", "Success to get SearchData")
            data
        } else {
            Log.d("TAG", "Fail to get SearchData")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetSearchResponse::class.java)
            data
        }
        searchData
    }

    suspend fun getScheduleByDates(startDate:String, endDate:String, body: ScheduleRequest, callback:(todoData : ScheduleByDateResponse) -> Unit) = withContext(Dispatchers.IO) {
        val response = scheduleService.getScheduleDates(
            User.id,
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
                    User.id,
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
                    User.id,
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

    suspend fun frontDeleteSchedule(scheduleId: String, frontDelete: ScheduleFrontDelete, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.deleteScheduleFront(
                User.id,
                scheduleId,
                frontDelete
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

    suspend fun middleDeleteSchedule(scheduleId: String, middleDelete: ScheduleMiddleDelete, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.deleteScheduleMiddle(
                User.id,
                scheduleId,
                middleDelete
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

    suspend fun backDeleteSchedule(scheduleId: String, backDelete: ScheduleBackDelete, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.deleteScheduleBack(
                User.id,
                scheduleId,
                backDelete
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
                User.id,
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

    suspend fun submitScheduleFront(scheduleId: String, postBodyFront: PostScheduleFront, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.submitScheduleFront(
                User.id,
                scheduleId,
                postBodyFront
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

    suspend fun submitScheduleMiddle(scheduleId: String, postBodyMiddle: PostScheduleMiddle, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.submitScheduleMiddle(
                User.id,
                scheduleId,
                postBodyMiddle
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

    suspend fun submitScheduleBack(scheduleId: String, postBodyBack: PostScheduleBack, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.submitScheduleBack(
                User.id,
                scheduleId,
                postBodyBack
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
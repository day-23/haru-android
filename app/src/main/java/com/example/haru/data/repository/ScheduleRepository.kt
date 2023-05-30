package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository() {
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getScheduleByDates(body: ScheduleRequest, callback:(todoData : ScheduleByDateResponse) -> Unit) = withContext(Dispatchers.IO) {
        val response = scheduleService.getScheduleDates(
            com.example.haru.utils.User.id,
            body,
        ).execute()
        val data: GetScheduleResponse
        val scheduleData: ScheduleByDateResponse

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            scheduleData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            scheduleData = ScheduleByDateResponse(emptyList(), emptyList())
        }
        callback(scheduleData)
    }

    suspend fun postSchedule(body: PostSchedule, callback: () -> Unit){
        withContext(Dispatchers.IO) {
            val response = scheduleService.createSchedule(
                    com.example.haru.utils.User.id,
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
                    com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
                com.example.haru.utils.User.id,
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
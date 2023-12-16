package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.StatisticsResponse
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.utils.Tags
import com.example.haru.utils.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EtcRepository {
    private val etcService = RetrofitClient.etcService

    suspend fun getTodoStatistics(
        body: ScheduleRequest,
        callback: (statisticsResponse: StatisticsResponse?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = etcService.getTodoStatistics(User.id, body).execute()
        var data = response.body()

        val statisticsResponse: StatisticsResponse? = if (response.isSuccessful) {
            Log.d(Tags.log, "Success to get Statistic")
            data
        } else {
            Log.d(Tags.log, "Fail to get Statistic")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, StatisticsResponse::class.java)
            data
        }
        callback(statisticsResponse)
    }
}
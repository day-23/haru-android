package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AllDoRepository() {
    private val allDoService = RetrofitClient.alldoService

    suspend fun getAllDoByDates(startDate:String, endDate:String, callback:(alldoData : AlldoData?) -> Unit) = withContext(Dispatchers.IO) {
        val response = allDoService.getAllDoDates(
            //"881c51d1-06f1-47ce-99b6-b5582594db12",
            "005224c0-eec1-4638-9143-58cbfc9688c5",
            AlldoBodyCategory(startDate, endDate),
        ).execute()

        val data: PostAllDoResponse
        val alldoData: AlldoData?

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            alldoData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            alldoData = null
        }
        callback(alldoData)
    }
}
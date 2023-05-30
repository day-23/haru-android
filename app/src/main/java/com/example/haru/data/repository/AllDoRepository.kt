package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AllDoRepository{
    private val allDoService = RetrofitClient.alldoService

    suspend fun getAllDoByDates(startDate:String, endDate:String, callback:(alldoData : AlldoData?) -> Unit) = withContext(Dispatchers.IO) {
        val response = allDoService.getAllDoDates(
            com.example.haru.utils.User.id,
            AlldoBodyCategory(startDate, endDate),
        ).execute()

        val data: PostAllDoResponse
        val alldoData: AlldoData?

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get all datas")
            data = response.body()!!
            alldoData = data.data
        } else {
            Log.d("TAG", "Fail to get all datas")
            alldoData = null
        }
        callback(alldoData)
    }
}
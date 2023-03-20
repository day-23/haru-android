package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.security.auth.callback.Callback

class TodoRepository() {
    private val todoService = RetrofitClient.todoService
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getTodo(startDate:String, endDate:String, callback:(todoData : List<Todo>) -> Unit) = withContext(Dispatchers.IO){
        val response = todoService.getTodoDates("881c51d1-06f1-47ce-99b6-b5582594db12",startDate,endDate).execute()
        val data: GetTodoResponse
        val todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else{
            Log.d("TAG", "Fail to get todos")
            todoData = emptyList()
        }
        callback(todoData)
    }

    suspend fun getSchedule(startDate:String, endDate:String, callback:(todoData : List<Todo>) -> Unit) = withContext(Dispatchers.IO){
        val response = scheduleService.getScheduleDates("881c51d1-06f1-47ce-99b6-b5582594db12",startDate,endDate).execute()
        val data: GetScheduleResponse
        val todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else{
            Log.d("TAG", "Fail to get todos")
            todoData = emptyList()
        }
        callback(todoData)
    }
}
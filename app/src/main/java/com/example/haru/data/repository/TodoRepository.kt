package com.example.haru.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoResponse
import com.example.haru.data.model.User
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoRepository() {
    private val todoService = RetrofitClient.todoService
    suspend fun getTodo(): List<Todo> = withContext(Dispatchers.IO){
        val response = todoService.getTodo("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data: TodoResponse
        var todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else{
            Log.d("TAG", "Fail to get todos")
            todoData = emptyList()
        }
        todoData
    }
}
package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
import com.example.haru.data.model.GetTodoResponse
import com.example.haru.data.model.PostTodoResponse
import com.example.haru.data.retrofit.RetrofitClient
import com.example.haru.viewmodel.RecyclerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository() {
    private val todoService = RetrofitClient.todoService
    suspend fun getTodo(): List<Todo> = withContext(Dispatchers.IO){
        val response = todoService.getTodo("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
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
        todoData
    }
//    = withContext(Dispatchers.IO)
    suspend fun createTodo(todoRequest: TodoRequest): Any = withContext(Dispatchers.IO){
        val response = todoService.createTodo("005224c0-eec1-4638-9143-58cbfc9688c5", todoRequest).execute()
        val data: PostTodoResponse
        val todoData : Any

        if (response.isSuccessful) {
            Log.d("TAG", "Success to create todo")
            data = response.body()!!
            todoData = data.data
            

        } else {
            Log.d("TAG", "Fail to create todo")
            todoData = "망함"
        }
        todoData
    }
}
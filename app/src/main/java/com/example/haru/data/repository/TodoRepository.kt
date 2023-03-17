package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
import com.example.haru.data.model.GetTodoResponse
import com.example.haru.data.model.PostTodoResponse
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.security.auth.callback.Callback

class TodoRepository() {
    private val todoService = RetrofitClient.todoService
    suspend fun getTodo(callback:(todoData : List<Todo>) -> Unit) = withContext(Dispatchers.IO){
        val response = todoService.getTodo("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetTodoResponse
        val todoData : List<Todo>
        Log.d("20191627", "여기는 getTodo")

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
    suspend fun createTodo(todoRequest: TodoRequest, callback: (todoData: Any) -> Unit) = withContext(Dispatchers.IO){
        val response = todoService.createTodo("005224c0-eec1-4638-9143-58cbfc9688c5", todoRequest).execute()
        val data: PostTodoResponse
        val todoData : Any

        if (response.isSuccessful) {
            Log.d("TAG", "Success to create todo")
            data = response.body()!!
            todoData = data.data
            callback(todoData)
        } else {
            Log.d("TAG", "Fail to create todo")
            todoData = "망함"
        }
    }

    suspend fun getTodoByTag(tagId: String) = withContext(Dispatchers.IO){
        val response = todoService.getTodoByTag("005224c0-eec1-4638-9143-58cbfc9688c5", tagId).execute()
        val data: GetTodoResponse
        val todoData: List<Todo>
        Log.d("20191627", "여기는 getTodoByTag")

        if (response.isSuccessful){
            Log.d("TAG", "Success to get Todo By Tag")
            data = response.body()!!
            todoData = data.data
        } else{
            Log.d("TAG", "Fail to get Todo By Tag")
            todoData = emptyList()
        }
        todoData
    }
}
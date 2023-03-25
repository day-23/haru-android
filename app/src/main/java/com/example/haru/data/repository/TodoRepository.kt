package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository() {
    private val todoService = RetrofitClient.todoService
    private val scheduleService = RetrofitClient.scheduleService

    suspend fun getTodoDates(startDate: String, endDate: String, callback:(todoData: List<Todo>) -> Unit) = withContext(Dispatchers.IO){
        val response = todoService.getTodoDates("005224c0-eec1-4638-9143-58cbfc9688c5", startDate, endDate).execute()
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

    suspend fun getTodoMain(callback:(todoData : TodoList) -> Unit) = withContext(Dispatchers.IO) {
        val response = todoService.getTodoMain("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetMainTodoResponse
        val todoData : TodoList
        Log.d("20191627", "여기는 getTodo")

        if (response.isSuccessful){
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else{
            Log.d("TAG", "Fail to get todos")
            todoData = TodoList(emptyList(),emptyList(),emptyList(),emptyList())
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
        val data: GetTodoByTag
        val todoData: GetTodoByTagData
        val todos : List<Todo>
        Log.d("20191627", "여기는 getTodoByTag")

        if (response.isSuccessful){
            Log.d("TAG", "Success to get Todo By Tag")
            data = response.body()!!
            todoData = data.data
            todos = todoData.todos
        } else{
            Log.d("TAG", "Fail to get Todo By Tag")
            todos = emptyList()
        }
        todos
    }

    suspend fun getTodoByFlag() = withContext(Dispatchers.IO){
        val response = todoService.getTodoByFlag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data : GetTodoBy
        val todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get Todo By Flag")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Flag")
            todoData = emptyList()
        }
        todoData
    }

    suspend fun getTodoByComplete() = withContext(Dispatchers.IO){
        val response = todoService.getTodoByCompleted("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data : GetTodoBy
        val todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get Todo By Completed")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Completed")
            todoData = emptyList()
        }
        todoData
    }

    suspend fun getTodoByUntag() = withContext(Dispatchers.IO){
        val response = todoService.getTodoByUntag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data : GetTodoBy
        val todoData : List<Todo>

        if (response.isSuccessful){
            Log.d("TAG", "Success to get Todo By Untag")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Untag")
            todoData = emptyList()
        }
        todoData
    }
}
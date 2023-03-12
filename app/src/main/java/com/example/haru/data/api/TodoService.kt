package com.example.haru.data.api

import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TodoService {
    @GET("todo/{userId}/todos")
    fun getTodo(@Path("userId") userId: String): Call<TodoResponse>
}
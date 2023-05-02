package com.example.haru.data.api

import com.example.haru.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface TodoService {
    @GET("todo/{userId}/todos/date")
    fun getTodoDates(@Path("userId") userId: String, @Query("startDate") startDate:String, @Query("endDate") endDate:String) : Call<GetTodoResponse>
  
    @GET("todo/{userId}/todos/main")
    fun getTodoMain(@Path("userId") userId: String) : Call<GetMainTodoResponse>

    @POST("todo/{userId}")
    fun createTodo(@Path("userId") userId: String, @Body todoRequest: TodoRequest) : Call<PostTodoResponse>

    @GET("todo/{userId}/todos/tag")
    fun getTodoByTag(@Path("userId") userId: String, @Query("tagId") tagId: String) : Call<GetTodoByTag>

    @GET("todo/{userId}/todos/main/flag")
    fun getTodoByFlag(@Path("userId") userId: String) : Call<GetTodoBy>

    @GET("todo/{userId}/todos/main/completed")
    fun getTodoByCompleted(@Path("userId") userId: String) : Call<GetTodoBy>

    @GET("todo/{userId}/todos/main/untag")
    fun getTodoByUntag(@Path("userId") userId: String) : Call<GetTodoBy>

    @PUT("todo/{userId}/{todoId}")
    fun putTodo(@Path("userId") userId: String, @Path("todoId") todoId : String, @Body todo : UpdateTodo) : Call<UpdateTodoResponse>

    @DELETE("todo/{userId}/{todoId}")
    fun deleteTodo(@Path("userId") userId: String, @Path("todoId") todoId : String) : Call<SuccessFail>

    @PATCH("todo/{userId}/flag/{todoId}")
    fun updateFlag(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body flag: Flag) : Call<SuccessFail>

    @PATCH("todo/{userId}/complete/todo/{todoId}")
    fun updateNotRepeatTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body completed: Completed) : Call<SuccessFail>

    @POST("todo/{userId}/todos/today")
    fun getTodayTodo(@Path("userId") userId: String, @Body endDate : EndDate) : Call<GetTodayTodo>

    @PATCH("todo/{userId}/complete/subtodo/{subTodoId}")
    fun updateSubTodo(@Path("userId") userId: String, @Path("subTodoId") subTodoId : String, @Body completed: Completed) : Call<SuccessFail>

    @PATCH("todo/{userId}/folded/{todoId}")
    fun updateFolded(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body folded: Folded) : Call<SuccessFail>

    @PATCH("todo/{userId}/complete/todo/{todoId}/repeat/front")
    fun updateRepeatTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body endDate: EndDate) : Call<SuccessFail>

    @HTTP(method = "DELETE", path = "todo/{userId}/todo/{todoId}/repeat/front", hasBody = true)
    fun deleteRepeatTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body endDate: EndDate) : Call<SuccessFail>
}
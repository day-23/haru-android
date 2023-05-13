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
    fun updateTodo(@Path("userId") userId: String, @Path("todoId") todoId : String, @Body todo : UpdateTodo) : Call<UpdateTodoResponse>

    @DELETE("todo/{userId}/{todoId}")
    fun deleteTodo(@Path("userId") userId: String, @Path("todoId") todoId : String) : Call<SuccessFail>

    @PATCH("todo/{userId}/flag/{todoId}")
    fun updateFlag(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body flag: Flag) : Call<SuccessFail>

    @PATCH("todo/{userId}/complete/todo/{todoId}")
    fun completeNotRepeatTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body completed: Completed) : Call<SuccessFail>

    @POST("todo/{userId}/todos/today")
    fun getTodayTodo(@Path("userId") userId: String, @Body frontEndDate : FrontEndDate) : Call<GetTodayTodo>

    @PATCH("todo/{userId}/complete/subtodo/{subTodoId}")
    fun completeSubTodo(@Path("userId") userId: String, @Path("subTodoId") subTodoId : String, @Body completed: Completed) : Call<SuccessFail>

    @PATCH("todo/{userId}/folded/{todoId}")
    fun updateFolded(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body folded: Folded) : Call<SuccessFail>

    @PATCH("todo/{userId}/complete/todo/{todoId}/repeat/front") //front 완료
    fun completeRepeatFrontTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body frontEndDate: FrontEndDate) : Call<SuccessFail>

    @HTTP(method = "DELETE", path = "todo/{userId}/todo/{todoId}/repeat/front", hasBody = true) // front 삭제
    fun deleteRepeatFrontTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body frontEndDate: FrontEndDate) : Call<SuccessFail>

    @PUT("todo/{userId}/todo/{todoId}/repeat/front") // front 수정
    fun updateRepeatFrontTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body updateRepeatFrontTodo: UpdateRepeatFrontTodo) : Call<SuccessFail>

    @HTTP(method = "DELETE", path = "todo/{userId}/todo/{todoId}/repeat/middle", hasBody = true) // middle 삭제
    fun deleteRepeatMiddleTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body middleEndDate: MiddleEndDate) : Call<SuccessFail>
//
    @PUT("todo/{userId}/todo/{todoId}/repeat/middle") // middle 수정
    fun updateRepeatMiddleTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body updateRepeatMiddleTodo: UpdateRepeatMiddleTodo) : Call<SuccessFail>
//
    @HTTP(method = "DELETE", path = "todo/{userId}/todo/{todoId}/repeat/back", hasBody = true) // back 삭제
    fun deleteRepeatBackTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body backRepeatEnd: BackRepeatEnd) : Call<SuccessFail>

    @PUT("todo/{userId}/todo/{todoId}/repeat/back") // back 수정
    fun updateRepeatBackTodo(@Path("userId") userId: String, @Path("todoId") todoId: String, @Body updateRepeatBackTodo: UpdateRepeatBackTodo) : Call<SuccessFail>

    @PATCH("todo/{userId}/order/todos")
    fun updateOrderMainTodo(@Path("userId") userId: String, @Body changeOrderTodo: ChangeOrderTodo) : Call<SuccessFail>

    @PATCH("todo/{userId}/order/todos/today")
    fun updateOrderTodayTodo(@Path("userId") userId: String, @Body changeOrderTodo: ChangeOrderTodo) : Call<SuccessFail>

    @PATCH("todo/{userId}/order/todos/tag")
    fun updateOrderTagTodo(@Path("userId") userId: String, @Body changeOrderTagTodo: ChangeOrderTagTodo) : Call<SuccessFail>
}
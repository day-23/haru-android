package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository() {
    private val todoService = RetrofitClient.todoService

    suspend fun getTodoDates(
        startDate: String,
        endDate: String,
        body: ScheduleRequest,
        callback: (todoData: List<Todo>) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response =
            todoService.getTodoDates(
                "dd62593d-161b-45cb-9534-346cd5b5e556",
//                "ysr",
                startDate,
                endDate
            )
                .execute()
        val data: GetTodoResponse
        val todoData: List<Todo>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            todoData = emptyList()
        }
        callback(todoData)
    }

    suspend fun getTodoMain(callback: (getMainTodoResponse: GetMainTodoResponse?) -> Unit) = withContext(Dispatchers.IO) {
        val response = todoService.getTodoMain("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data = response.body()

        val getMainTodoResponse = if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data
        } else {
            Log.d("TAG", "Fail to get todos")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetMainTodoResponse::class.java)
            data
        }
        callback(getMainTodoResponse)
    }


    suspend fun createTodo(
        calendar: Boolean,
        todoRequest: TodoRequest,
        callback: (postTodoResponse: PostTodoResponse?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val response =
                todoService.createTodo("005224c0-eec1-4638-9143-58cbfc9688c5", todoRequest)
                    .execute()
            var data = response.body()

            val postTodoResponse = if (response.isSuccessful) {
                Log.d("TAG", "Success to create todo")
                data
            } else {
                Log.d("TAG", "Fail to create todo")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, PostTodoResponse::class.java)
                data
            }

            if (calendar) {
                withContext(Dispatchers.Main) {
                    callback(postTodoResponse)
                }
            } else {
                callback(postTodoResponse)
            }
        }
    }

    suspend fun getTodoByTag(tagId: String, callback: (getTodoByTag: GetTodoByTag?) -> Unit) =
        withContext(Dispatchers.IO) {
            val response =
                todoService.getTodoByTag("005224c0-eec1-4638-9143-58cbfc9688c5", tagId).execute()
            var data = response.body()

            val getTodoByTag = if (response.isSuccessful) {
                Log.d("TAG", "Success to get Todo By Tag")
                data
            } else {
                Log.d("TAG", "Fail to get Todo By Tag")
                val error = response.errorBody()?.string()
                val gson = Gson()
                data = gson.fromJson(error, GetTodoByTag::class.java)
                data
            }
            callback(getTodoByTag)
        }

    suspend fun getTodoByFlag(callback: (getTodoBy : GetTodoBy?) -> Unit) = withContext(Dispatchers.IO) {
        val response = todoService.getTodoByFlag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data = response.body()

        val getTodoBy = if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Flag")
            data
        } else {
            Log.d("TAG", "Fail to get Todo By Flag")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetTodoBy::class.java)
            data
        }
        callback(getTodoBy)
    }

    suspend fun getTodoByComplete(callback : (getTodoBy: GetTodoBy?) -> Unit) = withContext(Dispatchers.IO) {
        val response =
            todoService.getTodoByCompleted("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data = response.body()

        val getTodoBy = if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Completed")
            data
        } else {
            Log.d("TAG", "Fail to get Todo By Completed")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetTodoBy::class.java)
            data
        }
        callback(getTodoBy)
    }

    suspend fun getTodoByUntag(callback : (getTodoBy: GetTodoBy?) -> Unit) = withContext(Dispatchers.IO) {
        val response = todoService.getTodoByUntag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        var data = response.body()

        val getTodoBy = if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Untag")
            data
        } else {
            Log.d("TAG", "Fail to get Todo By Untag")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetTodoBy::class.java)
            data
        }
        callback(getTodoBy)
    }

    suspend fun updateTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        todo: UpdateTodo,
        callback: (updateTodoResponse: UpdateTodoResponse?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateTodo(userId, todoId, todo).execute()
        var data = response.body()

        // null 값이 json으로 parsing 할때 null 값으로 안들어감

        val updateTodoResponse = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, UpdateTodoResponse::class.java)
            data
        }
        callback(updateTodoResponse)
    }

    suspend fun deleteTodo(
        userId: String,
        todoId: String,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteTodo(userId, todoId).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateFlag(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String, flag: Flag,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateFlag(userId, todoId, flag).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Flag")
            data
        } else {
            Log.d("TAG", "Fail to Update Flag")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun completeNotRepeatTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        completed: Completed,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeNotRepeatTodo(userId, todoId, completed).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete NotRepeatTodo Complete")
            data
        } else {
            Log.d("TAG", "Fail to Complete NotRepeatTodo Complete")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun getTodayTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        frontEndDate: FrontEndDate,
        callback: (getTodayTodo: GetTodayTodo?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.getTodayTodo(userId, frontEndDate).execute()
        var data = response.body()

        val getTodayTodo = if (response.isSuccessful) {
            Log.d("TAG", "Success to Get Today Todo")
            data
        } else {
            Log.d("TAG", "Fail to Get Today Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, GetTodayTodo::class.java)
            data
        }
        callback(getTodayTodo)
    }

    suspend fun completeSubTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        subTodoId: String,
        completed: Completed,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeSubTodo(userId, subTodoId, completed).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete SubTodo Complete")
            data
        } else {
            Log.d("TAG", "Fail to Complete SubTodo Complete")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateFolded(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        folded: Folded,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateFolded(userId, todoId, folded).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Folded")
            data
        } else {
            Log.d("TAG", "Fail to Update Folded")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun completeRepeatFrontTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeRepeatFrontTodo(userId, todoId, frontEndDate).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete Repeat Todo")
            data
        } else {
            Log.d("TAG", "Fail to Complete Repeat Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatFrontTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatFrontTodo: UpdateRepeatFrontTodo,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateRepeatFrontTodo(userId, todoId, updateRepeatFrontTodo).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Front Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Front Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatMiddleTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatMiddleTodo: UpdateRepeatMiddleTodo,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO){
        val response = todoService.updateRepeatMiddleTodo(userId, todoId, updateRepeatMiddleTodo).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Middle Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Middle Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatBackTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatBackTodo: UpdateRepeatBackTodo,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO){
        val response = todoService.updateRepeatBackTodo(userId, todoId, updateRepeatBackTodo).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Back Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Back Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatFrontTodo(
        userId: String,
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatFrontTodo(userId, todoId, frontEndDate).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Front Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Front Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatMiddleTodo(
        userId: String,
        todoId: String,
        middleEndDate: MiddleEndDate,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatMiddleTodo(userId, todoId, middleEndDate).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Middle Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Middle Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatBackTodo(
        userId: String,
        todoId: String,
        backRepeatEnd: BackRepeatEnd,
        callback: (successData: SuccessFail?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatBackTodo(userId, todoId, backRepeatEnd).execute()
        var data = response.body()

        val successData: SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Back Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Back Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        callback(successData)
    }

    suspend fun updateOrderMainTodo(
        userId: String,
        changeOrderTodo: ChangeOrderTodo
    ) = withContext(Dispatchers.IO){
        val response = todoService.updateOrderMainTodo(userId, changeOrderTodo).execute()
        var data = response.body()

        val successData : SuccessFail? = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Order Main Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Order Main Todo")
            val error = response.errorBody()?.string()
            val gson = Gson()
            data = gson.fromJson(error, SuccessFail::class.java)
            data
        }
        successData
    }
}
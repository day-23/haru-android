package com.example.haru.data.repository

import android.util.Log
import com.example.haru.data.model.*
import com.example.haru.data.retrofit.RetrofitClient
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
                startDate,
                endDate
            )//, body)
                .execute()
        val data: GetTodoResponse
        val todoData: List<Todo>
        Log.d("20191627", "여기는 getTodo")

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

    suspend fun getTodoMain(callback: (todoData: TodoList) -> Unit) = withContext(Dispatchers.IO) {
        val response = todoService.getTodoMain("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetMainTodoResponse
        val todoData: TodoList
        Log.d("20191627", "여기는 getTodo")

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get todos")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get todos")
            todoData = TodoList(emptyList(), emptyList(), emptyList(), emptyList())
        }
        callback(todoData)
    }


    suspend fun createTodo(
        calendar: Boolean,
        todoRequest: TodoRequest,
        callback: (todoData: Todo) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val response =
                todoService.createTodo("005224c0-eec1-4638-9143-58cbfc9688c5", todoRequest)
                    .execute()
            val data: PostTodoResponse
            val todoData: Todo

            if (response.isSuccessful) {
                Log.d("TAG", "Success to create todo")
                data = response.body()!!
                todoData = data.data!!
                Log.d("20191627", todoData.toString())
            } else {
                Log.d("TAG", "Fail to create todo")
                todoData = Todo()
            }

            if (calendar) {
                withContext(Dispatchers.Main) {
                    callback(todoData)
                }
            } else {
                callback(todoData)
            }
        }
    }

    suspend fun getTodoByTag(tagId: String, callback: (todoData: GetTodoByTagData) -> Unit) =
        withContext(Dispatchers.IO) {
            val response =
                todoService.getTodoByTag("005224c0-eec1-4638-9143-58cbfc9688c5", tagId).execute()
            val data: GetTodoByTag
            val todoData: GetTodoByTagData
            Log.d("20191627", "여기는 getTodoByTag")

            if (response.isSuccessful) {
                Log.d("TAG", "Success to get Todo By Tag")
                data = response.body()!!
                todoData = data.data
            } else {
                Log.d("TAG", "Fail to get Todo By Tag")
                todoData = GetTodoByTagData()
            }
            callback(todoData)
        }

    suspend fun getTodoByFlag() = withContext(Dispatchers.IO) {
        val response = todoService.getTodoByFlag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetTodoBy
        val todoData: List<Todo>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Flag")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Flag")
            todoData = emptyList()
        }
        todoData
    }

    suspend fun getTodoByComplete() = withContext(Dispatchers.IO) {
        val response =
            todoService.getTodoByCompleted("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetTodoBy
        val todoData: List<Todo>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Completed")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Completed")
            todoData = emptyList()
        }
        todoData
    }

    suspend fun getTodoByUntag() = withContext(Dispatchers.IO) {
        val response = todoService.getTodoByUntag("005224c0-eec1-4638-9143-58cbfc9688c5").execute()
        val data: GetTodoBy
        val todoData: List<Todo>

        if (response.isSuccessful) {
            Log.d("TAG", "Success to get Todo By Untag")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to get Todo By Untag")
            todoData = emptyList()
        }
        todoData
    }

    suspend fun updateTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        todo: UpdateTodo,
        callback: (todoData: Todo) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateTodo(userId, todoId, todo).execute()
        val data: UpdateTodoResponse
        val todoData: Todo

        // null 값이 json으로 parsing 할때 null 값으로 안들어감
        // retrofit에서 null 값을 null값으로 보낼 수 있는 방법을 찾아 시발려낭!

        if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Todo")
            data = response.body()!!
            todoData = data.data
        } else {
            Log.d("TAG", "Fail to Update Todo")
            todoData = Todo()
        }
        callback(todoData)
    }

    suspend fun deleteTodo(
        userId: String,
        todoId: String,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteTodo(userId, todoId).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Todo")
            data
        }
        callback(successData)
    }

    suspend fun updateFlag(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String, flag: Flag,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateFlag(userId, todoId, flag).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Flag")
            data
        } else {
            Log.d("TAG", "Fail to Update Flag")
            data
        }
        callback(successData)
    }

    suspend fun completeNotRepeatTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        completed: Completed,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeNotRepeatTodo(userId, todoId, completed).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete NotRepeatTodo Complete")
            data
        } else {
            Log.d("TAG", "Fail to Complete NotRepeatTodo Complete")
            data
        }
        callback(successData)
    }

    suspend fun getTodayTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        frontEndDate: FrontEndDate,
        callback: (todoList: TodoList) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.getTodayTodo(userId, frontEndDate).execute()
        val data: GetTodayTodo
        val todoList: TodoList

        if (response.isSuccessful) {
            Log.d("TAG", "Success to Get Today Todo")
            data = response.body()!!
            todoList = data.data
        } else {
            Log.d("TAG", "Fail to Get Today Todo")
            todoList = TodoList()
        }
        callback(todoList)
    }

    suspend fun completeSubTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        subTodoId: String,
        completed: Completed,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeSubTodo(userId, subTodoId, completed).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete SubTodo Complete")
            data
        } else {
            Log.d("TAG", "Fail to Complete SubTodo Complete")
            data
        }
        callback(successData)
    }

    suspend fun updateFolded(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        folded: Folded,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateFolded(userId, todoId, folded).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Folded")
            data
        } else {
            Log.d("TAG", "Fail to Update Folded")
            data
        }
        callback(successData)
    }

    suspend fun completeRepeatFrontTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.completeRepeatFrontTodo(userId, todoId, frontEndDate).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Complete Repeat Todo")
            data
        } else {
            Log.d("TAG", "Fail to Complete Repeat Todo")
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatFrontTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatFrontTodo: UpdateRepeatFrontTodo,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.updateRepeatFrontTodo(userId, todoId, updateRepeatFrontTodo).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Front Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Front Todo")
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatMiddleTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatMiddleTodo: UpdateRepeatMiddleTodo,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO){
        val response = todoService.updateRepeatMiddleTodo(userId, todoId, updateRepeatMiddleTodo).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Middle Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Middle Todo")
            data
        }
        callback(successData)
    }

    suspend fun updateRepeatBackTodo(
        userId: String = "005224c0-eec1-4638-9143-58cbfc9688c5",
        todoId: String,
        updateRepeatBackTodo: UpdateRepeatBackTodo,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO){
        val response = todoService.updateRepeatBackTodo(userId, todoId, updateRepeatBackTodo).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Update Repeat Back Todo")
            data
        } else {
            Log.d("TAG", "Fail to Update Repeat Back Todo")
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatFrontTodo(
        userId: String,
        todoId: String,
        frontEndDate: FrontEndDate,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatFrontTodo(userId, todoId, frontEndDate).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Front Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Front Todo")
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatMiddleTodo(
        userId: String,
        todoId: String,
        middleEndDate: MiddleEndDate,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatMiddleTodo(userId, todoId, middleEndDate).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Middle Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Middle Todo")
            data
        }
        callback(successData)
    }

    suspend fun deleteRepeatBackTodo(
        userId: String,
        todoId: String,
        backRepeatEnd: BackRepeatEnd,
        callback: (successData: SuccessFail) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = todoService.deleteRepeatBackTodo(userId, todoId, backRepeatEnd).execute()
        val data = response.body()!!

        val successData: SuccessFail = if (response.isSuccessful) {
            Log.d("TAG", "Success to Delete Repeat Back Todo")
            data
        } else {
            Log.d("TAG", "Fail to Delete Repeat Back Todo")
            data
        }
        callback(successData)
    }
}
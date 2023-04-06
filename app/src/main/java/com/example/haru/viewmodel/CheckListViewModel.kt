package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.*
import com.example.haru.data.repository.TagRepository
import com.example.haru.data.repository.TodoRepository
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class CheckListViewModel() :
    ViewModel() {
    private val todoRepository = TodoRepository()
    private val tagRepository = TagRepository()

    private val basicTag = listOf<Tag>(Tag("완료", "완료"), Tag("미분류", "미분류"))

    private val todoList = mutableListOf<Todo>()
    private val _todoDataList = MutableLiveData<List<Todo>>()
    private val _tagDataList = MutableLiveData<List<Tag>>()
    private val _flaggedTodos = MutableLiveData<List<Todo>>()
    private val _taggedTodos = MutableLiveData<List<Todo>>()
    private val _untaggedTodos = MutableLiveData<List<Todo>>()
    private val _completedTodos = MutableLiveData<List<Todo>>()

    private val _todoByTag = MutableLiveData<Boolean>()
    val todoByTag: LiveData<Boolean> = _todoByTag

    private val _todayTodo = MutableLiveData<List<Todo>>()
    val todayTodo: LiveData<List<Todo>> get() = _todayTodo
    private val todayList = mutableListOf<Todo>()

    val todoDataList: LiveData<List<Todo>> get() = _todoDataList
    val tagDataList: LiveData<List<Tag>> get() = _tagDataList
    val flaggedTodos: LiveData<List<Todo>> get() = _flaggedTodos
    val taggedTodos: LiveData<List<Todo>> get() = _taggedTodos
    val untaggedTodos: LiveData<List<Todo>> get() = _untaggedTodos
    val completedTodos: LiveData<List<Todo>> get() = _completedTodos

    var todoByTagItem: String? = null


    init {
        getTodoMain {
            flaggedTodos.value?.let { todoList.addAll(it) }
            taggedTodos.value?.let { todoList.addAll(it) }
            untaggedTodos.value?.let { todoList.addAll(it) }
            completedTodos.value?.let { todoList.addAll(it) }
            _todoDataList.postValue(todoList)
        }
        getTag()
    }

    fun getTag() {
        viewModelScope.launch {
            _tagDataList.value = basicTag + tagRepository.getTag()
        }
    }

    fun getTodoMain(callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.getTodoMain {
                todoList.clear()
                _flaggedTodos.postValue(listOf(Todo(type = 0)) + it.flaggedTodos + listOf(Todo(type = 3)))
                _taggedTodos.postValue(
                    listOf(
                        Todo(
                            type = 1,
                            content = "분류"
                        )
                    ) + it.taggedTodos + listOf(Todo(type = 3))
                )
                _untaggedTodos.postValue(
                    listOf(
                        Todo(
                            type = 1,
                            content = "미분류"
                        )
                    ) + it.untaggedTodos + listOf(Todo(type = 3))
                )
                _completedTodos.postValue(
                    listOf(
                        Todo(
                            type = 1,
                            content = "완료"
                        )
                    ) + it.completedTodos
                )
                _todoByTag.postValue(false)
                todoByTagItem = null
            }
            callback()
        }
    }

    fun getTodayTodo(endDate: String, callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.getTodayTodo(endDate = endDate) {
                todayList.clear()
                todayList.apply {
                    if (it.flaggedTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "중요"))
                        this.addAll(it.flaggedTodos)
                    }
                    if (it.todayTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "오늘 할 일"))
                        this.addAll(it.todayTodos)
                    }
                    if (it.endDatedTodos.isNotEmpty()) {
                        this.add(Todo(type = 4, content = "오늘 마감"))
                        this.addAll(it.endDatedTodos)
                    }
                    this.add(Todo(type = 3))
                }
                _todayTodo.postValue(todayList)
                callback()
            }
        }
    }


    fun addTodo(todoRequest: TodoRequest, callback: () -> Unit) {
        Log.d("20191627", todoRequest.toString())
        viewModelScope.launch {
            todoRepository.createTodo(todoRequest) {
                getTag()
                getTodoMain {
                    flaggedTodos.value?.let { todoList.addAll(it) }
                    taggedTodos.value?.let { todoList.addAll(it) }
                    untaggedTodos.value?.let { todoList.addAll(it) }
                    completedTodos.value?.let { todoList.addAll(it) }
                    _todoDataList.postValue(todoList)
                    callback()
                }
            }
        }
    }

    fun getTodoByTag(position: Int) {
        viewModelScope.launch {
            todoByTagItem = tagDataList.value!![position - 1].content
            _todoByTag.value = true
            _todoDataList.value = when (position) {
                1 -> listOf(
                    Todo(
                        type = 4,
                        content = todoByTagItem!!
                    )
                ) + todoRepository.getTodoByComplete()
                2 -> listOf(
                    Todo(
                        type = 4,
                        content = todoByTagItem!!
                    )
                ) + todoRepository.getTodoByUntag()
                else -> listOf(
                    Todo(
                        type = 4,
                        content = todoByTagItem!!
                    )
                ) + todoRepository.getTodoByTag(tagDataList.value!![position - 1].id)
            }
        }
    }

    fun getTodoByFlag() {
        viewModelScope.launch {
            todoByTagItem = "중요"
            _todoByTag.value = true
            _todoDataList.value = listOf(Todo(type = 0)) + todoRepository.getTodoByFlag()
        }
    }

    fun clear() {
        _flaggedTodos.value = emptyList()
        _taggedTodos.value = emptyList()
        _untaggedTodos.value = emptyList()
        _completedTodos.value = emptyList()
    }

    fun putTodo(todoId: String, todo: UpdateTodo, callback: () -> Unit) {
        viewModelScope.launch {
            val updateTodo = todoRepository.putTodo(todoId = todoId, todo = todo) {
                getTag()
                getTodoMain {
                    todoList.clear()
                    flaggedTodos.value?.let { todoList.addAll(it) }
                    taggedTodos.value?.let { todoList.addAll(it) }
                    untaggedTodos.value?.let { todoList.addAll(it) }
                    completedTodos.value?.let { todoList.addAll(it) }
                    _todoDataList.postValue(todoList)
                    callback()
                }
                Log.d("201916271", it.toString())
            }
        }
    }

    fun deleteTodo(todoId: String, position: Int, callback: () -> Unit) {
        viewModelScope.launch {
            val successData = todoRepository.deleteTodo(todoId = todoId) {
                if (it.success) {
                    todoList.removeAt(position)
                    _todoDataList.postValue(todoList)
                }
                callback()
            }
        }
    }

    fun updateFlag(flag: Flag, position: Int) {
        viewModelScope.launch {
            val successData =
                todoRepository.updateFlag(todoId = todoDataList.value!![position].id, flag = flag) {
                    if (it.success) {
                        val todo = todoList[position].copy(flag = flag.flag)
                        todoList.removeAt(position)

                        if (todo.flag && !todo.completed) {
                            todoList.add(1, todo)
                        } else {
                            if (todo.completed) {
                                val i = todoList.indexOf(Todo(type = 1, content = "완료"))
                                todoList.add(i + 1, todo)
                            } else {
                                when (todo.tags.isEmpty()) {
                                    true -> {
                                        val i = todoList.indexOf(Todo(type = 1, content = "미분류"))
                                        todoList.add(i + 1, todo)
                                    }
                                    false -> {
                                        val i = todoList.indexOf(Todo(type = 1, content = "분류"))
                                        todoList.add(i + 1, todo)
                                    }
                                }
                            }
                        }
                        _todoDataList.postValue(todoList)
                    }
                }
        }
    }

    fun updateNotRepeatTodo(completed: Completed, position: Int) {
        viewModelScope.launch {
            val successData = todoRepository.updateNotRepeatTodo(
                todoId = todoDataList.value!![position].id,
                completed = completed
            ) {
                if (it.success) {
                    val todo = todoList[position].copy(completed = completed.completed)
                    for (i in 0 until todo.subTodos.size)
                        todo.subTodos[i].completed = completed.completed
                    todoList.removeAt(position)

                    if (todo.completed)
                        todoList.add(todoList.indexOf(Todo(type = 1, content = "완료")) + 1, todo)
                    else {
                        val i = if (todo.flag) 0 else if (todo.tags.isEmpty()) todoList.indexOf(
                            Todo(
                                type = 1,
                                content = "미분류"
                            )
                        )
                        else todoList.indexOf(Todo(type = 1, content = "분류"))
                        todoList.add(i + 1, todo)
                    }
                    _todoDataList.postValue(todoList)
                }
            }
        }
    }

    fun updateSubTodo(completed: Completed, position: Int, subTodoPosition: Int) {
        viewModelScope.launch {
            val successData = todoRepository.updateSubTodo(
                subTodoId = todoDataList.value!![position].subTodos[subTodoPosition].id,
                completed = completed
            ) {
                if (it.success) {
                    todoList[position].subTodos[subTodoPosition].completed = completed.completed

                    _todoDataList.postValue(todoList)
                }
            }

        }
    }


}

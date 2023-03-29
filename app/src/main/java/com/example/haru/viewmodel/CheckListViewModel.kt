package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Alarm
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
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
                        type = 1,
                        content = todoByTagItem!!
                    )
                ) + todoRepository.getTodoByComplete()
                2 -> listOf(
                    Todo(
                        type = 1,
                        content = todoByTagItem!!
                    )
                ) + todoRepository.getTodoByUntag()
                else -> listOf(
                    Todo(
                        type = 1,
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

    fun resetAlarmTime(date: Date){

    }


}

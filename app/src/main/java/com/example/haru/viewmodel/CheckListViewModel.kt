package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
import com.example.haru.data.repository.TagRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val todoByTag : LiveData<Boolean> = _todoByTag

    val todoDataList : LiveData<List<Todo>> get() = _todoDataList
    val tagDataList: LiveData<List<Tag>> get() = _tagDataList
    val flaggedTodos: LiveData<List<Todo>> get() = _flaggedTodos
    val taggedTodos: LiveData<List<Todo>> get() = _taggedTodos
    val untaggedTodos: LiveData<List<Todo>> get() = _untaggedTodos
    val completedTodos: LiveData<List<Todo>> get() = _completedTodos

    var todoByTagItem : String? = null

    init {
        getTodoMain{
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
            todoRepository.getTodoMain{
                _flaggedTodos.postValue(it.flaggedTodos)
                _taggedTodos.postValue(it.taggedTodos)
                _untaggedTodos.postValue(it.untaggedTodos)
                _completedTodos.postValue(it.completedTodos)
                _todoByTag.postValue(false)
                todoByTagItem = null
            }
            callback()
        }
    }


    fun addTodo(todoRequest: TodoRequest, callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.createTodo(todoRequest){
                Log.d("20191627", it.toString())
                getTag()
                getTodoMain{
                    callback()
                }
            }
        }
    }

    fun getTodoByTag(position: Int){
        viewModelScope.launch {
            todoByTagItem = tagDataList.value!![position - 1].content
            _todoDataList.value= todoRepository.getTodoByTag(tagDataList.value!![position - 1].id)
            _todoByTag.value = true
        }
    }

    fun clear(){
        _flaggedTodos.value = emptyList()
        _taggedTodos.value = emptyList()
        _untaggedTodos.value = emptyList()
        _completedTodos.value = emptyList()
    }
}

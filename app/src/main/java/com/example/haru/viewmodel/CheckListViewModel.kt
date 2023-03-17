package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
import com.example.haru.data.repository.TagRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch

class CheckListViewModel() :
    ViewModel() {
    private val todoRepository = TodoRepository()
    private val tagRepository = TagRepository()

    private val basicTag = listOf<Tag>(Tag("완료", "완료"), Tag("미분류", "미분류"))

    private val _tagDataList = MutableLiveData<List<Tag>>()
    private val _todoDataList = MutableLiveData<List<Todo>>()

    val tagDataList: LiveData<List<Tag>> get() = _tagDataList
    val todoDataList: LiveData<List<Todo>> get() = _todoDataList

    init {
        getTodo{}
        getTag()
    }

    fun getTag() {
        viewModelScope.launch {
            _tagDataList.value = basicTag + tagRepository.getTag()
        }
    }

    fun getTodo(callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.getTodo{
                _todoDataList.postValue(it)
                callback()
            }
        }
    }

    fun addTodo(todoRequest: TodoRequest, callback: () -> Unit) {
        viewModelScope.launch {
            todoRepository.createTodo(todoRequest){
                Log.d("20191627", it.toString())
                getTag()
                getTodo{
                    callback()
                }
            }
        }
    }

    fun getTodoByTag(position: Int){
        viewModelScope.launch {
            _todoDataList.value= todoRepository.getTodoByTag(tagDataList.value!![position].id)
        }
    }

}

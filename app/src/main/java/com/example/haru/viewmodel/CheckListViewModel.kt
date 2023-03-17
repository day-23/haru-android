package com.example.haru.viewmodel

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
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
            _tagDataList.value = tagRepository.getTag()
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

}

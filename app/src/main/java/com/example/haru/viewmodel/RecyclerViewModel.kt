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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RecyclerViewModel() :
    ViewModel() {
    private val todoRepository = TodoRepository()
    private val tagRepository = TagRepository()

    private val _tagDataList = MutableLiveData<List<Tag>>()
    private val _todoDataList = MutableLiveData<List<Todo>>()

    val tagDataList: LiveData<List<Tag>> get() = _tagDataList
    val todoDataList: LiveData<List<Todo>> get() = _todoDataList

    init {
        getTodo()
        getTag()
    }

//    fun updateTodo(){
//        getTodo()
//    }
//
//    fun updateTag(){
//        getTag()
//    }

    private fun getTag() {
        viewModelScope.launch {
            _tagDataList.value = tagRepository.getTag()
        }
    }

    private fun getTodo() {
        viewModelScope.launch {
            _todoDataList.value = todoRepository.getTodo()
        }
    }

}

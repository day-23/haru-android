package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.Todo
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.runBlocking

class TodoRecyclerViewModel() :
    ViewModel() {
    private val todoRepository = TodoRepository()

    //    private lateinit var list: List<Todo>
    private val _dataList = MutableLiveData<List<Todo>>()
    val dataList: LiveData<List<Todo>> get() = _dataList

    init {
        getTodo()
    }

    private fun getTag() {
//        this@TodoRecyclerViewModel.list =
//        _dataList.value = this@TodoRecyclerViewModel.list
//            tagRepository.getTag(user = user)
    }

    private fun getTodo() {
        runBlocking {
            _dataList.value = todoRepository.getTodo()
            Log.d("20191627", _dataList.value.toString())
        }
    }
}

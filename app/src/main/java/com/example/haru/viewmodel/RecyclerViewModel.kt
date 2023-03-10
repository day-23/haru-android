package com.example.haru.viewmodel

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.api.UserService
import com.example.haru.data.model.Tag
import com.example.haru.data.model.User
import com.example.haru.data.repository.TagRepository
import java.util.*
import kotlin.collections.ArrayList

//, private val tagRepository: TagRepository
class RecyclerViewModel(private val mode: Int) :
    ViewModel() {
    private lateinit var list: List<Any?>
    private val _dataList = MutableLiveData<List<Any?>>()
    val dataList: LiveData<List<Any?>> = _dataList


    companion object {
        const val requestTagList = 0
        const val requestTodoList = 1
    }

    init {
        when (mode) {
            requestTagList -> {
                getTag()
            }
            requestTodoList -> {
                getTodo()
            }
        }
    }
//    user: User
    private fun getTag() {
        this@RecyclerViewModel.list = arrayListOf(
            Tag(
                id = 1,
                content = "공부",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 2,
                content = "청소",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null,
            ),
            Tag(
                id = 3,
                content = "식사",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 4,
                content = "게임",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 5,
                content = "회사",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 6,
                content = "집",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 7,
                content = "완료",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            ),
            Tag(
                id = 8,
                content = "중요",
                createdAt = Date(System.currentTimeMillis()),
                updatedAt = Date(System.currentTimeMillis()),
                deletedAt = null
            )
        )
        _dataList.value = this@RecyclerViewModel.list
//            tagRepository.getTag(user = user)
    }

    private fun getTodo(){

    }
}

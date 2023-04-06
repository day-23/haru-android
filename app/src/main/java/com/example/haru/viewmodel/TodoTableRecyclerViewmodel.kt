package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoTable_data
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class TodoTableRecyclerViewmodel : ViewModel() {
    private val todoRepository = TodoRepository()

    private val _TodoItemList = MutableLiveData<ArrayList<List<Todo>>>()
    val TodoItemList : LiveData<ArrayList<List<Todo>>>
        get() = _TodoItemList

    private val _TodoDataList = MutableLiveData<ArrayList<ArrayList<Todo>>>()
    val TodoDataList : LiveData<ArrayList<ArrayList<Todo>>>
        get() = _TodoDataList

    val dayslist = ArrayList<String>()
    val calendar = Calendar.getInstance()

    fun init_value(){
        Daylist(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

        getTodo(dayslist)
    }

    fun getTodo(date : ArrayList<String>){
        viewModelScope.launch {
            var IndexList: ArrayList<ArrayList<Todo>> = arrayListOf(
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
                arrayListOf(),
            )

            //GET 쿼리 전송
            todoRepository.getTodoDates(date[0], date[6]) {
                val TodoList = it
                Log.d("GET1", "${TodoList}, ${date[0]}, ${date[6]} ")

                //내용 추출
                for(data in TodoList){
                    val year = data.endDate?.slice(IntRange(0,3))
                    val month = data.endDate?.slice(IntRange(5,6))
                    val day = data.endDate?.slice(IntRange(8,9))
                    val result = year+month+day

                    if(data.endDate != null){ //날짜 정보가 있는 경우만 담음

                        when(result){
                            date[0] -> IndexList[0].add(data)
                            date[1] -> IndexList[1].add(data)
                            date[2] -> IndexList[2].add(data)
                            date[3] -> IndexList[3].add(data)
                            date[4] -> IndexList[4].add(data)
                            date[5] -> IndexList[5].add(data)
                            date[6] -> IndexList[6].add(data)
                        }
                    }
                }
            }
            _TodoDataList.postValue(IndexList)
        }
    }

    fun Daylist(year: Int, month: Int, day: Int) {
        val dayList = ArrayList<String>()
        var d = day
        dayslist.clear()
        calendar.set(year, month - 1, day)
        val lastOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        val currentOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = SimpleDateFormat("E").format(Date(year - 1900, month, day))
        var QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, day-1))

        when(dayOfWeek){
            "일","Sun"-> d
            "월","Mon"-> d -= 1
            "화","Tue"-> d -= 2
            "수","Wed"-> d -= 3
            "목","Thu"-> d -= 4
            "금","Fri"-> d -= 5
            "토","Sat"-> d -= 6
        }

        var addday = 0
        for (i: Int in 1 .. 7){
            if(d < 1){ //날짜가 지난달일때
                addday = (lastOfMonth - abs(d))
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month-1, addday))
            }
            else if(d > currentOfMonth){ //날짜가 다음달일때
                addday = (d - currentOfMonth)
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month+1, addday))
            }
            else{ // 날짜가 이번달일때
                addday = d
                QueryDate = SimpleDateFormat("yyyyMMdd").format(Date(year - 1900, month, addday))
            }

            dayslist.add(QueryDate)

            d += 1
        }
    }

}
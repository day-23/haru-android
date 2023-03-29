package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.Todo
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

    private val _TodoContentsList = MutableLiveData<ArrayList<ArrayList<String>>>()
    val TodoContentsList : LiveData<ArrayList<ArrayList<String>>>
        get() = _TodoContentsList

    val dayslist = ArrayList<String>()
    val calendar = Calendar.getInstance()

    fun init_value(){
        Daylist(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

        getTodo(dayslist)

        Log.d("daylist", "${TodoContentsList.value}")
    }

    fun getTodo(date : ArrayList<String>){
        viewModelScope.launch {
            var TodoList = ArrayList<List<Todo>>()
            var ContentList = ArrayList<ArrayList<String>>()


            //GET 쿼리 전송
            for(Date in date) {
                todoRepository.getTodoDates(Date, Date) {
                    Log.d("GET1", "${it.size}")
                    TodoList.add(it)
                }
            }
            _TodoItemList.postValue(TodoList)

            //내용 추출
            for(content in TodoList){
                var Textlist = ArrayList<String>()

                if(content.size > 0){
                    for(i : Int in 0 .. content.size-1){
                        Textlist.add(content.get(i).content)
                    }
                    ContentList.add(Textlist)
                }
                else{
                    ContentList.add(Textlist)
                }
            }
            _TodoContentsList.postValue(ContentList)
            Log.d("GET1", "$ContentList")
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
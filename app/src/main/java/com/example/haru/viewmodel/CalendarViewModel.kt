package com.example.haru.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.CalendarItem
import com.example.haru.data.model.Todo
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val todoRepository = TodoRepository()

    val _liveDataList = MutableLiveData<List<CalendarItem>>()
    val liveDataList: MutableLiveData<List<CalendarItem>> get() = _liveDataList

    fun init_viewModel(year:Int, month:Int){
        var startDate = ""
        var endDate = ""
        var maxi = 5

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        var UserData = ArrayList<CalendarItem>()

        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)

                if(i == 5 && k == 0 && calendar.get(Calendar.MONTH) != month){
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                    endDate = format.format(calendar.time)
                    maxi = 4
                    break
                }

                if(i == 0 && k == 0){
                    startDate = format.format(calendar.time)
                }

                if(i == 5 && k == 6){
                    endDate = format.format(calendar.time)
                }

                when(k % 7) {
                    0 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f, Color.RED,calendar.time))
                        } else {
                            UserData.add(CalendarItem(1f,Color.RED,calendar.time))
                        }
                    }
                    6 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f,Color.BLUE,calendar.time))
                        } else {
                            UserData.add(CalendarItem(1f,Color.BLUE,calendar.time))
                        }
                    }
                    else -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f,Color.BLACK,calendar.time))
                        } else {
                            UserData.add(CalendarItem(1f,Color.BLACK,calendar.time))
                        }
                    }
                }
            }

            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        getTodo(UserData, startDate, endDate, maxi)
    }

    fun getTodo(userData: ArrayList<CalendarItem>, startDate:String, endDate:String, maxi:Int){
        viewModelScope.launch {
            todoRepository.getTodo(startDate,endDate){

                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)

                for (todo in it){
                    var cnt = 0
                    var repeatEnd:Date? = null

                    val endDate = format.parse(todo.endDate)
                    val repeatWeek = todo.repeatWeek
                    val repeatMonth = todo.repeatMonth

                    if(todo.repeatEnd != null){
                        repeatEnd = format.parse(todo.repeatEnd)
                    }

                    if(repeatEnd != null) {
                        for (i in 0..maxi) {
                            for (k in 0..6) {
                                if (userData[i * 7 + k].alpha > 0.9f) {
                                    cnt += 1

                                    if (cnt <= repeatEnd.date) {
                                        if (repeatWeek != null && repeatWeek[userData[i * 7 + k].date.day] == '1') {
                                            if (userData[i * 7 + k].todos == null) {
                                                userData[i * 7 + k].todos = ArrayList<Todo?>()
                                            }

                                            userData[i * 7 + k].todos!!.add(todo)
                                        } else if (repeatMonth != null && repeatMonth[cnt - 1] == '1') {
                                            if (userData[i * 7 + k].todos == null) {
                                                userData[i * 7 + k].todos = ArrayList<Todo?>()
                                            }

                                            userData[i * 7 + k].todos!!.add(todo)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        for (i in 0..maxi) {
                            for (k in 0..6) {
                                if (userData[i * 7 + k].alpha > 0.9f) {
                                    cnt += 1

                                    if (cnt == endDate.date) {
                                        if (userData[i * 7 + k].todos == null) {
                                            userData[i * 7 + k].todos = ArrayList<Todo?>()
                                        }

                                        userData[i * 7 + k].todos!!.add(todo)
                                    }
                                }
                            }
                        }
                    }
                }

                _liveDataList.postValue(userData)
            }
        }
    }
}
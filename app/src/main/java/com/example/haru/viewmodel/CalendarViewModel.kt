package com.example.haru.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.CalendarItem
import com.example.haru.data.model.ContentMark
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
                    val repeatOption = todo.repeatOption
                    val repeatValue = todo.repeatValue

                    if(todo.repeatEnd != null){
                        repeatEnd = format.parse(todo.repeatEnd)
                    }

                    if(repeatOption != null) {
                        for (i in 0..maxi) {
                            for (k in 0..6) {
                                if (userData[i * 7 + k].alpha > 0.9f) {
                                    cnt += 1

                                    if (cnt <= repeatEnd!!.date) {
                                        if (repeatOption == "매주" && repeatValue!![userData[i * 7 + k].date.day] == '1') {
                                            if (userData[i * 7 + k].todos == null) {
                                                userData[i * 7 + k].todos = ArrayList()
                                            }

                                            userData[i * 7 + k].todos!!.add(ContentMark(todo,true))
                                        } else if (repeatOption == "매달" && repeatValue!![cnt - 1] == '1') {
                                            if (userData[i * 7 + k].todos == null) {
                                                userData[i * 7 + k].todos = ArrayList()
                                            }

                                            userData[i * 7 + k].todos!!.add(ContentMark(todo,true))
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
                                            userData[i * 7 + k].todos = ArrayList()
                                        }

                                        userData[i * 7 + k].todos!!.add(ContentMark(todo,true))
                                    }
                                }
                            }
                        }
                    }
                }

                for(i in 0 until userData.size){
                    Log.d("변경 전 데이타",userData[i].todos.toString())
                }

                for (i in 0 until userData.size-1){
                    for(j in i+1 until userData.size){
                        if (userData[i].todos != null && userData[j].todos != null){
                            for(k in 0 until userData[i].todos!!.size){
                                if(userData[i].todos!![k].todo != null &&
                                    userData[i].todos!![k].mark &&
                                        userData[j].todos!!.contains(userData[i].todos!![k])){
                                    if(userData[j].todos!!.size > k){
                                        val containIndex: Int = userData[j].todos!!.indexOf(userData[i].todos!![k])

                                        var tmp = userData[j].todos!![k]
                                        userData[j].todos!![k] = userData[j].todos!![containIndex]
                                        userData[j].todos!![containIndex] = tmp

                                        userData[j].todos!![k].mark = false
                                        Log.d("apply 적용", userData[j].todos!![k].mark.toString())
                                    }
                                }
                            }
                        } else {
                            break
                        }
                    }
                }

                for(i in 0 until userData.size){
                    Log.d("변경 후 데이타",userData[i].todos.toString())
                }

                _liveDataList.postValue(userData)
            }
        }
    }
}
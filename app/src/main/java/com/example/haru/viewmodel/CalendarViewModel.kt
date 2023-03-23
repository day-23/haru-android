package com.example.haru.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val todoRepository = TodoRepository()

    val _liveDateList = MutableLiveData<List<CalendarDate>>()
    val liveDateList: MutableLiveData<List<CalendarDate>> get() = _liveDateList

    val _liveContentList = MutableLiveData<List<ContentMark>>()
    val liveContentList: MutableLiveData<List<ContentMark>> get() = _liveContentList

    fun init_viewModel(year:Int, month:Int){
        var startDate = ""
        var endDate = ""
        var maxi = 5

        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        var dateList = ArrayList<CalendarDate>()

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
                            dateList.add(CalendarDate(0.4f, Color.RED,calendar.time))
                        } else {
                            dateList.add(CalendarDate(1f,Color.RED,calendar.time))
                        }
                    }
                    6 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateList.add(CalendarDate(0.4f,Color.BLUE,calendar.time))
                        } else {
                            dateList.add(CalendarDate(1f,Color.BLUE,calendar.time))
                        }
                    }
                    else -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateList.add(CalendarDate(0.4f,Color.BLACK,calendar.time))
                        } else {
                            dateList.add(CalendarDate(1f,Color.BLACK,calendar.time))
                        }
                    }
                }
            }

            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        _liveDateList.postValue(dateList)

        getTodo(startDate, endDate, maxi)
    }

    fun getTodo(startDate:String, endDate:String, maxi:Int){
        viewModelScope.launch {
            todoRepository.getTodoDates(startDate,endDate){

                var contentList = ArrayList<CalendarContent>()

                val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                val dateformat = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)

                for (i in 0 until (maxi+1)*7){
                    contentList.add(CalendarContent(ArrayList()))
                }

                for(todo in it){
                    val repeatDate = Array((maxi+1)*7){false}
                    val createdAt = serverformat.parse(todo.createdAt)
                    val serverendDate = serverformat.parse(todo.endDate)
                    var repeateEnd:Date

                    val repeatOption = todo.repeatOption
                    var repeatValue = ""

                    if(repeatOption != null){
                        if(todo.repeatEnd != null) {
                            repeateEnd = serverformat.parse(todo.repeatEnd)
                        }
                        
                        repeatValue = todo.repeatValue!!

                        val calendar = Calendar.getInstance()

                        when(repeatOption){
                            "매일" -> {
                                var dailycnt = 0
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)


                                while (calendar.time.compareTo(repeateEnd) <= 0 && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        if (dailycnt == 0) {
                                            repeatDate[cnt] = true
                                        }

                                        dailycnt++

                                        if (dailycnt.toString() == repeatValue) {
                                            dailycnt = 0
                                        }
                                    }

                                    cnt++
                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "매주" -> {
                                var weeklycnt = 0
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)


                                while (calendar.time.compareTo(repeateEnd) <= 0 && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        if (repeatValue[weeklycnt] == '1') {
                                            repeatDate[cnt] = true
                                        }
                                    }

                                    weeklycnt++
                                    cnt++

                                    if(weeklycnt == 7) weeklycnt = 0

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "2주마다" -> {
                                var weeklycnt = 0
                                var cnt = 0
                                var twoweek = true

                                calendar.time = dateformat.parse(startDate)


                                while (calendar.time.compareTo(repeateEnd) <= 0 && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        if (repeatValue[weeklycnt] == '1' && twoweek) {
                                            repeatDate[cnt] = true
                                        }
                                    }

                                    cnt++
                                    weeklycnt++

                                    if(weeklycnt == 7) {
                                        weeklycnt = 0
                                        twoweek = !twoweek
                                    }

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "매달" -> {
                                var monthlycnt = createdAt.date-1
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)
                                Log.d("startDate",startDate)
                                Log.d("createdAt",createdAt.toString())
                                Log.d("endDate", endDate)

                                while (calendar.time.compareTo(repeateEnd) <= 0 && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        Log.d("repeatValue",repeatValue)
                                        if (repeatValue[monthlycnt] == '1') {
                                            repeatDate[cnt] = true
                                        }

                                        monthlycnt++

                                        if(monthlycnt == 31) monthlycnt = 0
                                    }

                                    cnt++

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "매년" -> {
                                var cnt = 0

                                val tempStartDate = dateformat.parse(startDate)
                                Log.d("tempStart", tempStartDate.toString())

                                calendar.time = tempStartDate

                                while (calendar.time.compareTo(repeateEnd) <= 0 && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    Log.d("calendar", calendar.time.toString())

                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                            if(calendar.get(Calendar.DAY_OF_MONTH) == tempStartDate.day) repeatDate[cnt] = true
                                        }
                                    }

                                    cnt++

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }
                        }

                        for(i in 0..repeatDate.size-1){
                            Log.d("repeateDate", repeatDate[i].toString())
                        }
                    } else {
                        val calendar = Calendar.getInstance()

                        var cnt = 0
                        val tempStartDate = dateformat.parse(startDate)
                        calendar.time = tempStartDate

                        while (calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                            if(calendar.time.compareTo(serverendDate) == 0){
                                repeatDate[cnt] = true
                            }

                            cnt++

                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }

                    for(i in 0 until repeatDate.size){
                        if(repeatDate[i]){
                            contentList[i].todos.add(todo)
                        }
                    }
                }

                for (i in 0 until contentList.size)
                    Log.d("변경 전 contentLst", contentList[i].toString())

                var newcontentList = ArrayList<ContentMark>()

                for (i in 0 until contentList.size - 1) {
                    for (j in 0 until contentList[i].todos.size) {
                        newcontentList.add(
                            ContentMark(
                                contentList[i].todos[j],
                                i,
                                1
                            )
                        )

                        for (k in i + 1 until contentList.size) {
                            if (contentList[k].todos.contains(contentList[i].todos[j])) {
                                newcontentList[newcontentList.lastIndex].cnt++

                                val position = contentList[k].todos.indexOf(contentList[i].todos[j])
                                contentList[k].todos.removeAt(position)
                            } else break
                        }
                    }
                }

                for (i in 0 until contentList.size)
                    Log.d("변경 후 contentLst", contentList[i].toString())

                for (i in 0 until newcontentList.size)
                    Log.d("newcontentList", newcontentList[i].toString())

                _liveContentList.postValue(newcontentList)

                /*for (todo in it){
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
                    if(userData[i].todos != null) {
                        for (k in 0 until userData[i].todos!!.size) {
                            var cnt = 0
                            for (j in i + 1 until userData.size) {
                                if (userData[j].todos != null) {
                                    if (userData[i].todos!![k].todo != null &&
                                        userData[i].todos!![k].mark &&
                                        userData[j].todos!!.contains(userData[i].todos!![k])
                                    ) {
                                        cnt++

                                        if (userData[j].todos!!.size > k) {
                                            val containIndex: Int =
                                                userData[j].todos!!.indexOf(userData[i].todos!![k])

                                            var tmp = userData[j].todos!![k]

                                            userData[j].todos!![k] =
                                                userData[j].todos!![containIndex]
                                            userData[j].todos!![containIndex] = tmp

                                            userData[j].todos!![k].mark = false

                                            userData[i]
                                        }
                                    }
                                } else {
                                    break
                                }
                            }
                        }
                    }
                }

                for(i in 0 until userData.size){
                    Log.d("변경 후 데이타",userData[i].todos.toString())
                }

                _liveDataList.postValue(userData)*/
            }
        }
    }
}
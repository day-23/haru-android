package com.example.haru.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.ScheduleRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val todoRepository = TodoRepository()
    private val scheduleRepository = ScheduleRepository()

    val _liveDateList = MutableLiveData<List<CalendarDate>>()
    val liveDateList: MutableLiveData<List<CalendarDate>> get() = _liveDateList

    val _liveTodoCalendarList = MutableLiveData<List<TodoCalendarData>>()
    val liveTodoCalendarList: MutableLiveData<List<TodoCalendarData>> get() = _liveTodoCalendarList

    val _liveScheduleCalendarList = MutableLiveData<List<ScheduleCalendarData>>()
    val liveScheduleCalendarList: MutableLiveData<List<ScheduleCalendarData>> get() = _liveScheduleCalendarList

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
        Log.d("calendar", calendar.time.toString())

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
                            dateList.add(CalendarDate(Color.rgb(0xFD,0xBB,0xCD),calendar.time))
                        } else {
                            dateList.add(CalendarDate(Color.rgb(0xF7,0x1E,0x58),calendar.time))
                        }
                    }
                    6 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateList.add(CalendarDate(Color.rgb(0xBB,0xE7,0xFF),calendar.time))
                        } else {
                            dateList.add(CalendarDate(Color.rgb(0x1D,0xAF,0xFF),calendar.time))
                        }
                    }
                    else -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            dateList.add(CalendarDate(Color.rgb(0xBA,0xBA,0xBA),calendar.time))
                        } else {
                            dateList.add(CalendarDate(Color.rgb(0x70,0x70,0x70),calendar.time))
                        }
                    }
                }
            }

            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        _liveDateList.postValue(dateList)

        getTodo(startDate, endDate, maxi)
        getSchedule(startDate, endDate, maxi)
    }

    fun getTodo(startDate:String, endDate:String, maxi:Int){
        viewModelScope.launch {
            todoRepository.getTodoDates(startDate,endDate){

                var contentList = ArrayList<CalendarTodo>()

                val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                val dateformat = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)

                for (i in 0 until (maxi+1)*7){
                    contentList.add(CalendarTodo(ArrayList()))
                }

                for(todo in it){
                    val repeatDate = Array((maxi+1)*7){false}
                    val createdAt = serverformat.parse(todo.createdAt)
                    val serverendDate = serverformat.parse(todo.endDate)
                    var repeateEnd:Date? = null

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


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
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


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
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


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
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
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)

                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(createdAt) >= 0) {
                                        if (repeatValue[calendar.time.date-1] == '1') {
                                            repeatDate[cnt] = true
                                        }
                                    }

                                    cnt++

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "매년" -> {
                                var cnt = 0

                                val tempStartDate = dateformat.parse(startDate)

                                calendar.time = tempStartDate

                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
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

                var newcontentList = ArrayList<TodoCalendarData>()

                for (i in 0 until contentList.size - 1) {
                    for (j in 0 until contentList[i].todos.size) {
                        newcontentList.add(
                            TodoCalendarData(
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

                _liveTodoCalendarList.postValue(newcontentList)
            }
        }
    }

    fun getSchedule(startDate:String, endDate:String, maxi:Int){
        viewModelScope.launch {
            scheduleRepository.getScheduleByDates(startDate,endDate){

                var contentList = ArrayList<CalendarSchedule>()

                val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                val dateformat = SimpleDateFormat("yyyyMMdd", Locale.KOREAN)

                for (i in 0 until (maxi+1)*7){
                    contentList.add(CalendarSchedule(ArrayList()))
                }

                for(schedule in it){
                    val repeatDate = Array((maxi+1)*7){false}

                    var repeatStart:Date? = null
                    var repeateEnd:Date? = null

                    val repeatOption = schedule.repeatOption
                    var repeatValue = ""

                    if(schedule.repeatEnd != null) {
                        repeateEnd = serverformat.parse(schedule.repeatEnd)
                    }

                    if(schedule.repeatStart != null){
                        repeatStart = serverformat.parse(schedule.repeatStart)
                    }

                    if(repeatOption != null){
                        repeatValue = schedule.repeatValue!!

                        val calendar = Calendar.getInstance()

                        when(repeatOption){
                            "매일" -> {
                                var dailycnt = 0
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(repeatStart) >= 0) {
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


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(repeatStart) >= 0) {
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


                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(repeatStart) >= 0) {
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
                                var cnt = 0

                                calendar.time = dateformat.parse(startDate)

                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(repeatStart) >= 0) {
                                        if (repeatValue[calendar.time.date-1] == '1') {
                                            repeatDate[cnt] = true
                                        }
                                    }

                                    cnt++

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }

                            "매년" -> {
                                var cnt = 0

                                val tempStartDate = dateformat.parse(startDate)

                                calendar.time = tempStartDate

                                while ((repeateEnd == null ||calendar.time.compareTo(repeateEnd) <= 0) && calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                                    if(calendar.time.compareTo(repeatStart) >= 0) {
                                        if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                            if(calendar.get(Calendar.DAY_OF_MONTH) == tempStartDate.day) repeatDate[cnt] = true
                                        }
                                    }

                                    cnt++

                                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }
                        }
                    } else {
                        val calendar = Calendar.getInstance()

                        var cnt = 0
                        val tempStartDate = dateformat.parse(startDate)
                        calendar.time = tempStartDate

                        while (calendar.time.compareTo(dateformat.parse(endDate)) <= 0){
                            if(calendar.time.compareTo(repeatStart) >= 0 && calendar.time.compareTo(repeateEnd) <= 0){
                                repeatDate[cnt] = true
                            }

                            cnt++

                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }

                    for(i in 0 until repeatDate.size){
                        if(repeatDate[i]){
                            contentList[i].todos.add(schedule)
                        }
                    }
                }

                var newcontentList = ArrayList<ScheduleCalendarData>()

                for (i in 0 until contentList.size - 1) {
                    for (j in 0 until contentList[i].todos.size) {
                        newcontentList.add(
                            ScheduleCalendarData(
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

                _liveScheduleCalendarList.postValue(newcontentList)
            }
        }
    }
}
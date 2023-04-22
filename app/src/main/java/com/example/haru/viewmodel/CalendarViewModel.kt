package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.AllDoRepository
import com.example.haru.data.repository.ScheduleRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
//    private val todoRepository = TodoRepository()
//    private val scheduleRepository = ScheduleRepository()
    private val alldoRepository = AllDoRepository()

    val _liveTodoCalendarList = MutableLiveData<List<CalendarTodo>>()
    val liveTodoCalendarList: MutableLiveData<List<CalendarTodo>> get() = _liveTodoCalendarList

    val _liveScheduleCalendarList = MutableLiveData<List<ScheduleCalendarData>>()
    val liveScheduleCalendarList: MutableLiveData<List<ScheduleCalendarData>> get() = _liveScheduleCalendarList

    fun init_viewModel(startDate: String, endDate: String, maxi: Int){
        getAlldo(startDate, endDate, maxi)
//        getTodo(startDate, endDate, maxi)
//        getSchedule(startDate, endDate, maxi)
    }

    fun date_comparison(first_date: Date, second_date: Date): Int{
        first_date.hours = 0
        first_date.minutes = 0
        first_date.seconds = 0

        second_date.hours = 0
        second_date.minutes = 0
        second_date.seconds = 0

        return first_date.compareTo(second_date)
    }

    fun getAlldo(startDate: String, endDate: String, maxi: Int){
        viewModelScope.launch {
            alldoRepository.getAllDoByDates(startDate, endDate){
                if(it != null){
                    Log.d("alldoit", it.toString())
                    val todoList = ArrayList<CalendarTodo>()
                    val scheduleList = ArrayList<CalendarSchedule>()

                    val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                    val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.KOREAN)

                    for (i in 0 until (maxi+1)*7){
                        todoList.add(CalendarTodo(ArrayList()))
                        scheduleList.add(CalendarSchedule(ArrayList()))
                    }

                    for (todo in it.todos) {
                        val repeatDate = Array((maxi + 1) * 7) { false }
                        val createdAt = serverformat.parse(todo.createdAt)

                        var serverendDate: Date? = null
                        var repeateEnd: Date? = null

                        val repeatOption = todo.repeatOption
                        var repeatValue = ""

                        if (todo.endDate != null) {
                            serverendDate = serverformat.parse(todo.endDate)
                        }

                        if (repeatOption != null) {
                            if (todo.repeatEnd != null) {
                                repeateEnd = serverformat.parse(todo.repeatEnd)
                            }

                            repeatValue = todo.repeatValue!!

                            val calendar = Calendar.getInstance()

                            when (repeatOption) {
                                "매일" -> {
                                    var dailycnt = 0
                                    var cnt = 0

                                    calendar.time = dateformat.parse(startDate)


                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time,repeateEnd
                                        ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                        ) <= 0
                                    ) {
                                        if (date_comparison(calendar.time, createdAt) >= 0) {
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


                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, createdAt
                                        ) >= 0) {
                                            if (repeatValue[weeklycnt] == '1') {
                                                repeatDate[cnt] = true
                                            }
                                        }

                                        weeklycnt++
                                        cnt++

                                        if (weeklycnt == 7) weeklycnt = 0

                                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                                    }
                                }

                                "2주마다" -> {
                                    var weeklycnt = 0
                                    var cnt = 0
                                    var twoweek = true

                                    calendar.time = dateformat.parse(startDate)


                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(endDate)) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, createdAt
                                        ) >= 0) {
                                            if (repeatValue[weeklycnt] == '1' && twoweek) {
                                                repeatDate[cnt] = true
                                            }
                                        }

                                        cnt++
                                        weeklycnt++

                                        if (weeklycnt == 7) {
                                            weeklycnt = 0
                                            twoweek = !twoweek
                                        }

                                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                                    }
                                }

                                "매달" -> {
                                    var cnt = 0

                                    calendar.time = dateformat.parse(startDate)

                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(endDate)) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, createdAt)
                                            >= 0) {
                                            if (repeatValue[calendar.time.date - 1] == '1') {
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

                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0) {
                                        if (date_comparison(
                                                calendar.time, createdAt
                                        ) >= 0) {
                                            if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                                if (calendar.get(Calendar.DAY_OF_MONTH) == tempStartDate.day) repeatDate[cnt] =
                                                    true
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
                            tempStartDate.date += 1
                            calendar.time = tempStartDate

                            while (date_comparison(
                                    calendar.time, dateformat.parse(
                                        endDate
                                    )
                            ) <= 0) {
                                if (serverendDate != null && date_comparison(
                                        calendar.time, serverendDate
                                ) == 0
                                ) {
                                    Log.d("endDate 날짜","가져 옴")
                                    repeatDate[cnt] = true
                                } else if (date_comparison(
                                        calendar.time, Date()
                                ) == 0) {
                                    Log.d("endDate 날짜", "못 가져옴")
                                    repeatDate[cnt] = true
                                }

                                cnt++

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        for (i in 0 until repeatDate.size) {
                            if (repeatDate[i]) {
                                todoList[i].todos.add(todo)
                            }
                        }
                    }

                    for (schedule in it.schedules) {
                        val repeatDate = Array((maxi + 1) * 7) { false }

                        var repeatStart: Date? = null
                        var repeateEnd: Date? = null

                        val repeatOption = schedule.repeatOption
                        var repeatValue = schedule.repeatValue

                        if (schedule.repeatEnd != null) {
                            repeateEnd = serverformat.parse(schedule.repeatEnd)
                        }

                        if (schedule.repeatStart != null) {
                            repeatStart = serverformat.parse(schedule.repeatStart)
                        }

                        if (repeatOption != null && repeatValue != null) {
                            val calendar = Calendar.getInstance()

                            when (repeatOption) {
                                "매일" -> {
                                    var dailycnt = 0
                                    var cnt = 0

                                    calendar.time = dateformat.parse(startDate)

                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0
                                    ) {
                                        if (date_comparison(calendar.time, repeatStart!!) >= 0) {
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


                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, repeatStart!!
                                        ) >= 0) {
                                            if (repeatValue[weeklycnt] == '1') {
                                                repeatDate[cnt] = true
                                            }
                                        }

                                        weeklycnt++
                                        cnt++

                                        if (weeklycnt == 7) weeklycnt = 0

                                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                                    }
                                }

                                "2주마다" -> {
                                    var weeklycnt = 0
                                    var cnt = 0
                                    var twoweek = true

                                    calendar.time = dateformat.parse(startDate)


                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, repeatStart!!
                                        ) >= 0) {
                                            if (repeatValue[weeklycnt] == '1' && twoweek) {
                                                repeatDate[cnt] = true
                                            }
                                        }

                                        cnt++
                                        weeklycnt++

                                        if (weeklycnt == 7) {
                                            weeklycnt = 0
                                            twoweek = !twoweek
                                        }

                                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                                    }
                                }

                                "매달" -> {
                                    var cnt = 0

                                    calendar.time = dateformat.parse(startDate)

                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, repeatStart!!
                                        ) >= 0) {
                                            if (repeatValue[calendar.time.date - 1] == '1') {
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

                                    while ((repeateEnd == null || date_comparison(
                                            calendar.time, repeateEnd
                                    ) <= 0) && date_comparison(
                                            calendar.time, dateformat.parse(
                                                endDate
                                            )
                                    ) <= 0
                                    ) {
                                        if (date_comparison(
                                                calendar.time, repeatStart!!
                                        ) >= 0) {
                                            if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                                if (calendar.get(Calendar.DAY_OF_MONTH) == tempStartDate.day) repeatDate[cnt] =
                                                    true
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

                            while (date_comparison(calendar.time, dateformat.parse(
                                    endDate
                            )) <= 0) {
                                if (date_comparison(
                                        calendar.time, repeatStart!!
                                ) >= 0 && (repeateEnd == null || date_comparison(calendar.time, repeateEnd) <= 0)
                                ) {
                                    repeatDate[cnt] = true
                                }

                                cnt++

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        for (i in 0 until repeatDate.size) {
                            if (repeatDate[i]) {
                                scheduleList[i].schedules.add(schedule)
                            }
                        }
                    }

                    val newscheduleList = ArrayList<ScheduleCalendarData>()

                    for (i in 0 until scheduleList.size - 1) {
                        for (j in 0 until scheduleList[i].schedules.size) {
                            newscheduleList.add(
                                ScheduleCalendarData(
                                    scheduleList[i].schedules[j],
                                    i,
                                    1
                                )
                            )

                            for (k in i + 1 until scheduleList.size) {
                                if (scheduleList[k].schedules.contains(scheduleList[i].schedules[j])) {
                                    newscheduleList[newscheduleList.lastIndex].cnt++

                                    val position = scheduleList[k].schedules.indexOf(scheduleList[i].schedules[j])
                                    scheduleList[k].schedules.removeAt(position)
                                } else break
                            }
                        }
                    }

                    _liveTodoCalendarList.postValue(todoList)
                    _liveScheduleCalendarList.postValue(newscheduleList)
                }
            }
        }
    }
}
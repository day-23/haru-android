package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.AllDoRepository
import com.example.haru.data.repository.CategoryRepository
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
    private val categoryRepository = CategoryRepository()
    private val ScheduleRepository = ScheduleRepository()

    val _liveCategoryList = MutableLiveData<List<Category>>()
    val liveCategoryList: MutableLiveData<List<Category>> get() = _liveCategoryList

    val _liveTodoCalendarList = MutableLiveData<List<CalendarTodo>>()
    val liveTodoCalendarList: MutableLiveData<List<CalendarTodo>> get() = _liveTodoCalendarList

    val _liveTodoList = MutableLiveData<List<Todo>>()
    val liveTodoList: MutableLiveData<List<Todo>> get() = _liveTodoList

    val _liveScheduleCalendarList = MutableLiveData<List<ScheduleCalendarData>>()
    val liveScheduleCalendarList: MutableLiveData<List<ScheduleCalendarData>> get() = _liveScheduleCalendarList

    val _liveScheduleList = MutableLiveData<List<Schedule>>()
    val liveScheduleList: MutableLiveData<List<Schedule>> get() = _liveScheduleList

    fun getCategories(){
        viewModelScope.launch {
            categoryRepository.getCategories {
                _liveCategoryList.postValue(it)
            }
        }
    }

    fun postCategory(content: String, color: String, callback:(category: Category?) -> Unit){
        viewModelScope.launch {
            categoryRepository.postCategory(content, color){
                callback(it)
            }
        }
    }

    fun postSchedule(body: PostSchedule, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.postSchedule(body){
//                if(it != null) {
//                    scheduleCalendarList.add(
//                        ScheduleCalendarData(
//                            it,
//                            28,
//                            1
//                        )
//                    )
//
//                    _liveScheduleCalendarList.postValue(scheduleCalendarList)
//                }

                callback()
            }
        }
    }

    fun updateCategory(updateCategory: Category){
        viewModelScope.launch {
            categoryRepository.updateCategory(updateCategory){}
        }
    }

    fun deleteCategory(categoryId: String){
        viewModelScope.launch {
            categoryRepository.deleteCategory(categoryId){}
        }
    }

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

    fun getAlldoDay(startDate: String, endDate:String){
        viewModelScope.launch {
            alldoRepository.getAllDoByDates(startDate, endDate){
                if(it != null){
                    val todoList = it.todos
                    val scheduleList = it.schedules

                    _liveTodoList.postValue(todoList)
                    _liveScheduleList.postValue(scheduleList)
                }
            }
        }
    }

    fun getAlldo(startDate: String, endDate: String, maxi: Int){
        viewModelScope.launch {
            alldoRepository.getAllDoByDates(startDate, endDate){
                if(it != null){
                    val todoList = ArrayList<CalendarTodo>()
                    val scheduleList = ArrayList<ScheduleCalendarData>()

                    val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
                    val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.KOREAN)

                    for (i in 0 until (maxi+1)*7){
                        todoList.add(CalendarTodo(ArrayList()))
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
                                        if (date_comparison(
                                                calendar.time, serverendDate!!
                                            ) >= 0
                                        ) {
                                            repeatDate[cnt] = true
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

                                "격주" -> {
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
                            if(!repeatValue.contains("T")) {
                                val calendar = Calendar.getInstance()

                                when (repeatOption) {
                                    "매일" -> {
                                        Log.d("매일","개수")
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
                                                ) >= 0
                                            ) {
                                                scheduleList.add(
                                                    ScheduleCalendarData(
                                                        schedule,
                                                        cnt,
                                                        1
                                                    )
                                                )
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
                                                ) >= 0
                                            ) {
                                                if (repeatValue[weeklycnt] == '1') {
                                                    scheduleList.add(ScheduleCalendarData(
                                                        schedule,
                                                        cnt,
                                                        1
                                                    ))
                                                }
                                            }

                                            weeklycnt++
                                            cnt++

                                            if (weeklycnt == 7) weeklycnt = 0

                                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                                        }
                                    }

                                    "격주" -> {
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
                                                ) >= 0
                                            ) {
                                                if (repeatValue[weeklycnt] == '1' && twoweek) {
                                                    scheduleList.add(ScheduleCalendarData(
                                                        schedule,
                                                        cnt,
                                                        1
                                                    ))
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
                                                ) >= 0
                                            ) {
                                                if (repeatValue[calendar.time.date - 1] == '1') {
                                                    scheduleList.add(ScheduleCalendarData(
                                                        schedule,
                                                        cnt,
                                                        1
                                                    ))
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
                                                ) >= 0
                                            ) {
                                                if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                                    if (calendar.get(Calendar.DAY_OF_MONTH) == tempStartDate.day)
                                                        scheduleList.add(ScheduleCalendarData(
                                                            schedule,
                                                            cnt,
                                                            1
                                                        ))
                                                }
                                            }

                                            cnt++

                                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                                        }
                                    }
                                }
                            } else {
                                val newRepeatValue = repeatValue.replace("T","")
                                val calendar = Calendar.getInstance()

                                when (repeatOption) {
                                    "매주"->{
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
                                        ){
                                            if (date_comparison(
                                                    calendar.time, repeatStart!!
                                                ) == 0
                                            ){
                                                val calendarTmp = Calendar.getInstance()
                                                calendarTmp.time = repeatStart
                                                calendarTmp.add(Calendar.DAY_OF_MONTH,7)
                                                repeatStart = calendarTmp.time

                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule,
                                                    cnt,
                                                    null,
                                                    newRepeatValue.toInt()
                                                ))
                                            }

                                            cnt++
                                            calendar.add(Calendar.DAY_OF_MONTH,1)
                                        }
                                    }

                                    "격주"->{
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
                                        ){
                                            if (date_comparison(
                                                    calendar.time, repeatStart!!
                                                ) == 0
                                            ){
                                                val calendarTmp = Calendar.getInstance()
                                                calendarTmp.time = repeatStart
                                                calendarTmp.add(Calendar.DAY_OF_MONTH,14)
                                                repeatStart = calendarTmp.time

                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule,
                                                    cnt,
                                                    null,
                                                    newRepeatValue.toInt()
                                                ))
                                            }

                                            cnt++
                                            calendar.add(Calendar.DAY_OF_MONTH,1)
                                        }
                                    }

                                    "매달"->{
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
                                        ){
                                            if (date_comparison(
                                                    calendar.time, repeatStart!!
                                                ) == 0
                                            ){
                                                val calendarTmp = Calendar.getInstance()
                                                calendarTmp.time = repeatStart
                                                calendarTmp.add(Calendar.MONTH,1)
                                                repeatStart = calendarTmp.time

                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule,
                                                    cnt,
                                                    null,
                                                    newRepeatValue.toInt()
                                                ))
                                            }

                                            cnt++
                                            calendar.add(Calendar.DAY_OF_MONTH,1)
                                        }
                                    }

                                    "매년"->{
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
                                        ){
                                            if (date_comparison(
                                                    calendar.time, repeatStart!!
                                                ) == 0
                                            ){
                                                val calendarTmp = Calendar.getInstance()
                                                calendarTmp.time = repeatStart
                                                calendarTmp.add(Calendar.YEAR,1)
                                                repeatStart = calendarTmp.time

                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule,
                                                    cnt,
                                                    null,
                                                    newRepeatValue.toInt()
                                                ))
                                            }

                                            cnt++
                                            calendar.add(Calendar.DAY_OF_MONTH,1)
                                        }
                                    }
                                }
                            }
                        } else {
                            var start = false
                            val calendar = Calendar.getInstance()

                            var startcnt = 0
                            var daycnt = 0
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
                                    if(!start) {
                                        startcnt = cnt
                                        start = true
                                    }

                                    daycnt++
                                }

                                cnt++

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }

                            scheduleList.add(ScheduleCalendarData(
                                schedule,
                                startcnt,
                                daycnt
                            ))
                        }
                    }

                    _liveTodoCalendarList.postValue(todoList)
                    _liveScheduleCalendarList.postValue(scheduleList)
                }
            }
        }
    }
}
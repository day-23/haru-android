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
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val alldoRepository = AllDoRepository()
    private val categoryRepository = CategoryRepository()
    private val TodoRepository = TodoRepository()
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

    val _liveHolidaysList = MutableLiveData<List<Holiday>>()
    val liveHolidaysList: MutableLiveData<List<Holiday>> get() = _liveHolidaysList

    fun completeNotRepeatTodo(todoId: String,
                              completed: Completed,
                              callback: () -> Unit){
        viewModelScope.launch {
            TodoRepository.completeNotRepeatTodo(todoId, completed){
                callback()
            }
        }
    }

    fun completeRepeatFrontTodo(todoId: String,
                              endDate: FrontEndDate,
                              callback: () -> Unit){
        viewModelScope.launch {
            TodoRepository.completeRepeatFrontTodo(todoId, endDate){
                callback()
            }
        }
    }

    fun completeRepeatMiddleTodo(todoId: String,
                              endDate: MiddleCompleteEndDate,
                              callback: () -> Unit){
        viewModelScope.launch {
            TodoRepository.completeRepeatMiddleTodo(todoId, endDate){
                callback()
            }
        }
    }

    fun completeRepeatBackTodo(todoId: String,
                               endDate: BackCompleteEndDate,
                              callback: () -> Unit){
        viewModelScope.launch {
            TodoRepository.completeRepeatBackTodo(todoId, endDate){
                callback()
            }
        }
    }

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

    fun submitSchedule(schedule: String, postSchedule: PostSchedule, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.submitSchedule(schedule, postSchedule){
                callback()
            }
        }
    }

    fun submitScheduleFront(schedule: String, postScheduleFront: PostScheduleFront, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.submitScheduleFront(schedule, postScheduleFront){
                callback()
            }
        }
    }

    fun submitScheduleMiddle(schedule: String, postScheduleMiddle: PostScheduleMiddle, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.submitScheduleMiddle(schedule, postScheduleMiddle){
                callback()
            }
        }
    }

    fun submitScheduleBack(schedule: String, postScheduleBack: PostScheduleBack, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.submitScheduleBack(schedule, postScheduleBack){
                callback()
            }
        }
    }

    fun postSchedule(body: PostSchedule, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.postSchedule(body){
                callback()
            }
        }
    }

    fun deleteSchedule(scheduleId: String, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.deleteSchedule(scheduleId){
                callback()
            }
        }
    }

    fun deleteFrontSchedule(scheduleId: String, frontDelete: ScheduleFrontDelete, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.frontDeleteSchedule(scheduleId, frontDelete){
                callback()
            }
        }
    }

    fun deleteMiddleSchedule(scheduleId: String, middleDelete: ScheduleMiddleDelete, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.middleDeleteSchedule(scheduleId, middleDelete){
                callback()
            }
        }
    }

    fun deleteBackSchedule(scheduleId: String, backDelete: ScheduleBackDelete, callback: () -> Unit){
        viewModelScope.launch {
            ScheduleRepository.backDeleteSchedule(scheduleId, backDelete){
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
                val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
                val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)

                if(it != null){
                    val todoList = ArrayList<Todo>()
                    val scheduleList = ArrayList<Schedule>()

                    val startdateFormat = todayDateFormat.parse(startDate)

                    for(i in 0 until it.todos.size){
                        it.todos[i].endDate = FormatDate.calendarFormat(it.todos[i].endDate!!)

                        if(it.todos[i].repeatEnd != null){
                            it.todos[i].repeatEnd = FormatDate.calendarFormat(it.todos[i].repeatEnd!!)
                        }

                        if (it.todos[i].endDate != null){
                            var today: String = it.todos[i].endDate!!
                            var todayDate = serverformat.parse(today)

                            if(it.todos[i].repeatOption == null ||
                                it.todos[i].repeatValue == null ||
                                it.todos[i].repeatOption == "매일" ){
                                todoList.add(it.todos[i].copy())
                            } else {
                                Log.d("반복투두", it.todos[i].toString())
                                while (todayDate != null && date_comparison(todayDate, startdateFormat) < 0) {
                                    when (it.todos[i].repeatOption) {
                                        "매주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                it.todos[i].repeatValue!!,
                                                1,
                                                today,
                                                it.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                it.todos[i].repeatValue!!,
                                                2,
                                                today,
                                                it.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            Log.d("반복투두", todayDate.toString())
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                it.todos[i].repeatValue!!,
                                                today,
                                                it.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                it.todos[i].repeatValue!!,
                                                today,
                                                it.todos[i].repeatEnd
                                            )

                                            if(todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }

                                if(todayDate == null){
                                    todayDate = serverformat.parse(today)
                                }

                                if(todayDate != null && date_comparison(todayDate, startdateFormat) == 0){
                                    todoList.add(it.todos[i].copy())
                                }
                            }
                        } else {
                            todoList.add(it.todos[i].copy())
                        }
                    }

                    for(i in 0 until it.schedules.size){
                        val schedule = it.schedules[i]
                        schedule.repeatStart = FormatDate.calendarFormat(schedule.repeatStart!!)
                        schedule.repeatEnd = FormatDate.calendarFormat(schedule.repeatEnd!!)

                        var today: String
                        var todayDate: Date?

                        if(schedule.repeatOption == null ||
                                schedule.repeatValue == null ||
                                schedule.repeatOption == "매일"){
                            if(schedule.repeatOption == null){
                                val endtime = serverformat.parse(schedule.repeatEnd!!)

                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                schedule.startTime = todayDate
                                schedule.endTime = endtime
                            } else if(schedule.repeatOption == "매일"){
                                val endtime = serverformat.parse(schedule.repeatEnd!!)

                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                schedule.startTime = todayDate
                                todayDate.hours = endtime.hours
                                todayDate.minutes = endtime.minutes
                                todayDate.seconds = endtime.seconds
                                schedule.endTime = todayDate
                            }

                            scheduleList.add(schedule.copy())
                        } else {
                            if(schedule.repeatValue.contains("T")){
                                today = schedule.repeatStart!!
                                todayDate = serverformat.parse(today)

                                while (true) {
                                    when (schedule.repeatOption) {
                                        "매주" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                scheduleList.add(schedule.copy())
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                1,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                scheduleList.add(schedule.copy())
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                2,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                scheduleList.add(schedule.copy())
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            val scheduleT = Calendar.getInstance()
                                            scheduleT.time = todayDate
                                            scheduleT.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                                            if(date_comparison(todayDate!!, startdateFormat) <= 0 &&
                                                date_comparison(scheduleT.time, startdateFormat) >= 0){
                                                schedule.startTime = todayDate
                                                schedule.endTime = scheduleT.time
                                                scheduleList.add(schedule.copy())
                                                break
                                            }

                                            todayDate = FormatDate.nextStartDateEveryYear(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }
                            } else {
                                today = schedule.repeatStart!!
                                val starttime = serverformat.parse(schedule.repeatStart!!)
                                val endtime = serverformat.parse(schedule.repeatEnd!!)
                                todayDate = serverformat.parse(today)

                                while (todayDate != null && date_comparison(
                                        todayDate,
                                        startdateFormat
                                    ) < 0
                                ) {
                                    when (schedule.repeatOption) {
                                        "매주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                1,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "격주" -> {
                                            todayDate = FormatDate.nextStartDateEveryWeek(
                                                schedule.repeatValue,
                                                2,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매달" -> {
                                            todayDate = FormatDate.nextStartDateEveryMonth(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }

                                        "매년" -> {
                                            todayDate = FormatDate.nextStartDateEveryYear(
                                                schedule.repeatValue,
                                                today,
                                                schedule.repeatEnd!!
                                            )

                                            if (todayDate == null) break

                                            today = serverformat.format(todayDate)
                                        }
                                    }
                                }

                                today = FormatDate.calendarFormat(today)

                                if(todayDate == null){
                                    todayDate = serverformat.parse(today)
                                }

                                if (todayDate != null && date_comparison(
                                        todayDate,
                                        startdateFormat
                                    ) == 0
                                ) {
                                    todayDate.hours = starttime.hours
                                    todayDate.minutes = starttime.minutes
                                    todayDate.seconds = starttime.seconds

                                    Log.d("반복이슈", todayDate.toString())

                                    schedule.startTime = todayDate.clone() as Date

                                    todayDate.hours = endtime.hours
                                    todayDate.minutes = endtime.minutes
                                    todayDate.seconds = endtime.seconds

                                    Log.d("반복이슈", todayDate.toString())

                                    schedule.endTime = todayDate.clone() as Date
                                    scheduleList.add(schedule.copy())
                                }
                            }
                        }
                    }

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
                        if (todo.endDate != null){
                            Log.d("데이터 포맷", "날짜 바뀌기 전: "+todo.endDate!!)
                            todo.endDate = FormatDate.calendarFormat(todo.endDate!!)
                            Log.d("데이터 포맷", "날짜 바뀐 후: "+todo.endDate!!)
                        }

                        if(todo.repeatEnd != null){
                            todo.repeatEnd = FormatDate.calendarFormat(todo.repeatEnd!!)
                        }

                        val repeatDate = Array((maxi + 1) * 7) { false }

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
                                                calendar.time, serverendDate!!
                                            ) >= 0
                                        ){
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
                                                calendar.time, serverendDate!!
                                            ) >= 0
                                        ){
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
                                                calendar.time, serverendDate!!
                                            ) >= 0
                                        ){
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
                                                calendar.time, serverendDate!!
                                            ) >= 0
                                        ){
                                            if (repeatValue[calendar.get(Calendar.MONTH)] == '1') {
                                                if (calendar.get(Calendar.DAY_OF_MONTH) == serverendDate.date)
                                                    repeatDate[cnt] = true
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

                            while (date_comparison(
                                    calendar.time, dateformat.parse(
                                        endDate
                                    )
                            ) <= 0) {
                                if (serverendDate != null && date_comparison(
                                        calendar.time, serverendDate
                                ) == 0
                                ) {
                                    repeatDate[cnt] = true
                                } else if (date_comparison(
                                        calendar.time, Date()
                                ) == 0) {
                                    repeatDate[cnt] = true
                                }

                                cnt++

                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        for (i in 0 until repeatDate.size) {
                            if (repeatDate[i]) {
                                todoList[i].todos.add(todo.copy())
                            }
                        }
                    }

                    for (schedule in it.schedules) {
                        if (schedule.repeatStart != null){
                            schedule.repeatStart = FormatDate.calendarFormat(schedule.repeatStart!!)
                        }

                        if(schedule.repeatEnd != null){
                            schedule.repeatEnd = FormatDate.calendarFormat(schedule.repeatEnd!!)
                        }

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
                                                        schedule.copy(),
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
                                                        schedule.copy(),
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
                                                        schedule.copy(),
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
                                                        schedule.copy(),
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
                                                    if (calendar.get(Calendar.DAY_OF_MONTH) == repeatStart.date) {
                                                        scheduleList.add(
                                                            ScheduleCalendarData(
                                                                schedule.copy(),
                                                                cnt,
                                                                1
                                                            )
                                                        )
                                                    }
                                                }
                                            }

                                            cnt++

                                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                                        }
                                    }
                                }
                            } else {
                                val newRepeatValue = repeatValue.replace("T","")
                                val repeatstart = serverformat.parse(schedule.repeatStart)
                                val calendar = Calendar.getInstance()

                                calendar.time = repeatstart

                                calendar.add(Calendar.MILLISECOND, newRepeatValue.toInt())

                                val intervaldate = calendar.timeInMillis - repeatstart.time

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
                                            val startCalendar = Calendar.getInstance()
                                            startCalendar.time = repeatStart

                                            while (date_comparison(startCalendar.time, calendar.time) < 0){
                                                startCalendar.add(Calendar.DAY_OF_MONTH, 7)
                                            }

                                            if (date_comparison(
                                                    calendar.time, startCalendar.time!!
                                                ) == 0
                                            ){
                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule.copy(),
                                                    cnt,
                                                    null,
                                                    intervaldate.toInt()
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
                                            val startCalendar = Calendar.getInstance()
                                            startCalendar.time = repeatStart

                                            while (date_comparison(startCalendar.time, calendar.time) < 0){
                                                startCalendar.add(Calendar.DAY_OF_MONTH, 14)
                                            }

                                            if (date_comparison(
                                                    calendar.time, startCalendar.time!!
                                                ) == 0
                                            ){
                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule.copy(),
                                                    cnt,
                                                    null,
                                                    intervaldate.toInt()
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
                                            val startCalendar = Calendar.getInstance()
                                            startCalendar.time = repeatStart

                                            while (date_comparison(startCalendar.time, calendar.time) < 0){
                                                startCalendar.add(Calendar.MONTH, 1)
                                            }

                                            if (date_comparison(
                                                    calendar.time, startCalendar.time!!
                                                ) == 0
                                            ){
                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule.copy(),
                                                    cnt,
                                                    null,
                                                    intervaldate.toInt()
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
                                            val startCalendar = Calendar.getInstance()
                                            startCalendar.time = repeatStart

                                            while (date_comparison(startCalendar.time, calendar.time) < 0){
                                                startCalendar.add(Calendar.YEAR, 1)
                                            }

                                            if (date_comparison(
                                                    calendar.time, startCalendar.time!!
                                                ) == 0
                                            ){
                                                scheduleList.add(ScheduleCalendarData(
                                                    schedule.copy(),
                                                    cnt,
                                                    null,
                                                    intervaldate.toInt()
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
                                schedule.copy(),
                                startcnt,
                                daycnt
                            ))
                        }
                    }

                    _liveHolidaysList.postValue(it.holidays)
                    _liveTodoCalendarList.postValue(todoList)
                    _liveScheduleCalendarList.postValue(scheduleList)
                }
            }
        }
    }
}
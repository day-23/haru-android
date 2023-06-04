package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoList
import com.example.haru.data.model.TodoTable_data
import com.example.haru.data.repository.TodoRepository
import com.example.haru.utils.FormatDate
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


    fun date_comparison(first_date: Date, second_date: Date): Int{
        first_date.hours = 0
        first_date.minutes = 0
        first_date.seconds = 0

        second_date.hours = 0
        second_date.minutes = 0
        second_date.seconds = 0

        return first_date.compareTo(second_date)
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
            val startDate = "${date[0].slice(IntRange(0, 3))}" + "-" + "${date[0].slice(IntRange(4,5))}" + "-" + "${date[0].slice(IntRange(6,7))}" + "T00:00:00+09:00"
            val endDate = "${date[6].slice(IntRange(0, 3))}" + "-" + "${date[6].slice(IntRange(4,5))}" + "-" + "${date[6].slice(IntRange(6,7))}" + "T00:00:00+09:00"
            val body = ScheduleRequest(startDate, endDate)

            val serverformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
            val dateformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.KOREAN)

            //GET 쿼리 전송
            todoRepository.getTodoDates(body) {
                val TodoList = it
                Log.d("GET1", "${TodoList}, ${date[0]}, ${date[6]}")

                for (todo in TodoList) {
                    if (todo.endDate != null){
                        Log.d("데이터 포맷", "날짜 바뀌기 전: "+todo.endDate!!)
                        todo.endDate = FormatDate.calendarFormat(todo.endDate!!)
                        Log.d("데이터 포맷", "날짜 바뀐 후: "+todo.endDate!!)
                    }

                    if(todo.repeatEnd != null){
                        todo.repeatEnd = FormatDate.calendarFormat(todo.repeatEnd!!)
                    }

                    val repeatDate = Array((1) * 7) { false }

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

//                    for (i in 0 until repeatDate.size) {
//                        if (repeatDate[i]) {
//                            todoList[i].todos.add(todo.copy())
//                        }

                        val year = todo.endDate?.slice(IntRange(0,3))
                        val month = todo.endDate?.slice(IntRange(5,6))
                        val day = todo.endDate?.slice(IntRange(8,9))
                        val result = year+month+day


                        Log.d("GET1", "${result}, ${todo.content}, ${todo}")
                        if(todo.endDate != null){ //날짜 정보가 있는 경우만 담음

                            when(result){
                                date[0] -> IndexList[0].add(todo)
                                date[1] -> IndexList[1].add(todo)
                                date[2] -> IndexList[2].add(todo)
                                date[3] -> IndexList[3].add(todo)
                                date[4] -> IndexList[4].add(todo)
                                date[5] -> IndexList[5].add(todo)
                                date[6] -> IndexList[6].add(todo)
                            }
                        }
//                    }
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
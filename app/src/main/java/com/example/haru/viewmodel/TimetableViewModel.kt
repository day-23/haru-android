package com.example.haru.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.timetable_data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class TimetableViewModel: ViewModel() {

    val timetableData = MutableLiveData<ArrayList<timetable_data>>()
    val days = MutableLiveData<ArrayList<Int>>()
    val calendar = Calendar.getInstance()

    init {
        timetableData.value = ArrayList()
        days.value = ArrayList()
    }

    fun getTimetableData() {
        val data = ArrayList<timetable_data>()
        for (i: Int in 1..23) {
            if (i < 12) {
                if(i == 1)
                    data.add(timetable_data("${i}\n오전"))
                else
                    data.add(timetable_data("${i}"))
            } else {
                if (i == 12) {
                    data.add(timetable_data("12\n오후"))
                } else {
                    data.add(timetable_data("${i - 12}\n"))
                }
            }
        }
        timetableData.value = data
    }

    fun getDays(year: Int, month: Int, day: Int) {
        val dayList = ArrayList<Int>()
        var d = day
        var lastOfMonth = 0

        calendar.set(year, month - 1, day)
        lastOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(year, month, day)
        val dayOfWeek = SimpleDateFormat("E").format(Date(year - 1900, month, day))

        when(dayOfWeek){
            "Mon"-> d -= 1
            "Tue"-> d -= 2
            "Wed"-> d -= 3
            "Thu"-> d -= 4
            "Fri"-> d -= 5
            "Sat"-> d -= 6
            "Sun"-> d
        }

        for (i: Int in 1 .. 7){
            if(d < 1){
                dayList.add(lastOfMonth - abs(d))
            }
            else if(d > lastOfMonth){
                dayList.add(d - lastOfMonth)
            }
            else{
                dayList.add(d)
            }
            d += 1
        }
        days.value = dayList
    }

    fun updateMonth(year: Int, month: Int) {
        calendar.set(year, month - 1, 1)
        getDays(year, month, 1)
        getTimetableData()
    }
}
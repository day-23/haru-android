package com.example.haru.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.CalendarItem
import java.util.*

class CalendarViewModel : ViewModel() {
    private val _dateList = MutableLiveData<List<CalendarItem>>()
    val dataList: LiveData<List<CalendarItem>> = _dateList

    init {

    }

    @SuppressLint("NotifyDataSetChanged")
    public fun requestDate(year:Int, month:Int){
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val tempMonth: Int = calendar.get(Calendar.MONTH)

        var dayList = arrayListOf<CalendarItem>()

        //달력의 아이템마다 값을 입력
        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                dayList.add(CalendarItem(calendar.time))
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        _dateList.value = dayList
    }
}
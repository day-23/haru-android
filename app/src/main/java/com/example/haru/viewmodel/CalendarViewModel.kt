package com.example.haru.viewmodel

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.CalendarItem
import java.util.*

class CalendarViewModel : ViewModel() {
    val _liveDataList = MutableLiveData<List<CalendarItem>>()
    val liveDataList: MutableLiveData<List<CalendarItem>> get() = _liveDataList

    init{
        var calendar = Calendar.getInstance()
        calendar.time = Date()

        init_viewModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    }

    fun init_viewModel(year:Int, month:Int){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var UserData = ArrayList<CalendarItem>()

        for(i in 0..5) {
            for(k in 0..6) {
                calendar.add(Calendar.DAY_OF_MONTH, (1-calendar.get(Calendar.DAY_OF_WEEK)) + k)
                Log.d("calendar",calendar.get(Calendar.MONTH).toString())
                Log.d("month",month.toString())

                when(k % 7) {
                    0 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f,Color.RED,calendar.time.date.toString()))
                        } else {
                            UserData.add(CalendarItem(1f,Color.RED,calendar.time.date.toString()))
                        }
                    }
                    6 -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f,Color.BLUE,calendar.time.date.toString()))
                        } else {
                            UserData.add(CalendarItem(1f,Color.BLUE,calendar.time.date.toString()))
                        }
                    }
                    else -> {
                        if(calendar.get(Calendar.MONTH) != month) {
                            UserData.add(CalendarItem(0.4f,Color.BLACK,calendar.time.date.toString()))
                        } else {
                            UserData.add(CalendarItem(1f,Color.BLACK,calendar.time.date.toString()))
                        }
                    }
                }
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1)
        }

        _liveDataList.value = UserData
    }
}
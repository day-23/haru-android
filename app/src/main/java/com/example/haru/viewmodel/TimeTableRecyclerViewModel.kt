package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.timetable_data

class TimeTableRecyclerViewModel : ViewModel() {
    private val _TimeList = MutableLiveData<ArrayList<timetable_data>>()
    val times : LiveData<ArrayList<timetable_data>>
        get() = _TimeList

    private var items = ArrayList<timetable_data>()

    init{
        for (i: Int in 1..23) {
            if (i < 12) {
                if(i == 1)
                    items.add(timetable_data("${i}\n오전"))
                else
                    items.add(timetable_data("${i}"))
            } else {
                if (i == 12) {
                    items.add(timetable_data("12\n오후"))
                } else {
                    items.add(timetable_data("${i - 12}\n"))
                }
            }
        }
        _TimeList.value = items
    }



}
package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Todotable_date
import com.example.haru.data.model.timetable_data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodoTableRecyclerViewmodel {
    private val _MonthList = MutableLiveData<ArrayList<Todotable_date>>()
    val MonthList : LiveData<ArrayList<Todotable_date>>
        get() = _MonthList
    val dayslist = ArrayList<Todotable_date>()
    val calendar = Calendar.getInstance()
    var daysofmonth = 0

    var strWeek = ""

    init{
        updateMonth(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun updateMonth(year: Int, month: Int, day: Int){
        calendar.set(year, month, day)
        dayslist.clear()
        daysofmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        Log.d("update", "$year, $month, $day, $daysofmonth")
        for(i: Int in 1.. daysofmonth){
            val dayOfWeek = SimpleDateFormat("E").format(Date(year - 1900, month, i))

            when(dayOfWeek){
                "Mon"-> strWeek = "월"
                "Tue"-> strWeek = "화"
                "Wed"-> strWeek = "수"
                "Thu"-> strWeek = "목"
                "Fri"-> strWeek = "금"
                "Sat"-> strWeek = "토"
                "Sun"-> strWeek = "일"
            }
            dayslist.add(Todotable_date(strWeek, i.toString()))
        }
        _MonthList.value = dayslist
    }

    fun getTodo(date : ArrayList<String>){
        Log.d("Days", "${date.get(0)}")
    }


}
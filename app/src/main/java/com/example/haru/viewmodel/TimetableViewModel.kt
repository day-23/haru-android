package com.example.haru.viewmodel

import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Timetable_date
import com.example.haru.data.model.User
import com.example.haru.data.model.timetable_data
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class TimetableViewModel(val context : Context): ViewModel() {

    private val _Dates = MutableLiveData<ArrayList<Timetable_date>>()
    val Dates : LiveData<ArrayList<Timetable_date>>
        get() = _Dates

    private val _Days = MutableLiveData<ArrayList<Int>>()
    val Days : LiveData<ArrayList<Int>>
        get() = _Days

    private val _Selected = MutableLiveData<ArrayList<Timetable_date>>()
    val Selected : LiveData<ArrayList<Timetable_date>>
        get() = _Selected

    val calendar = Calendar.getInstance()
    var days: ArrayList<Int> = ArrayList()

    init {

    }

    fun buttonClick(){
        showDatePickerDialog()
    }

    fun showDatePickerDialog() {
        val datePicker = DatePicker(context)
        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(datePicker)
            .setPositiveButton("apply") { dialog, _ ->
                val year = datePicker.year
                val month = datePicker.month
                var day = datePicker.dayOfMonth
//                val selectedYear =
//                    SimpleDateFormat("yyyy년 mm월 dd일").format(Date(year - 1900, month, day))
//                val selectedMonth = SimpleDateFormat("M월").format(Date(year - 1900, month, day))

                Selected =
                getDays(year, month, day)
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
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
        days = dayList
    }

}
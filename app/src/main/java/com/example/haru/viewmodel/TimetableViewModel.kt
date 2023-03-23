package com.example.haru.viewmodel

import android.content.Context
import android.provider.ContactsContract.RawContacts.Data
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

    private val _Days = MutableLiveData<ArrayList<String>>()
    val Days : LiveData<ArrayList<String>>
        get() = _Days

    private val _Selected = MutableLiveData<Timetable_date>()
    val Selected : LiveData<Timetable_date>
        get() = _Selected

    val calendar = Calendar.getInstance()

    var dayslist: ArrayList<String> = ArrayList()

    init {
        _Selected.value = Timetable_date(calendar.get(Calendar.YEAR).toString()+"년" , (calendar.get(Calendar.MONTH)+1).toString()+"월", calendar.get(Calendar.DAY_OF_MONTH).toString()+"일")

        Daylist(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        _Days.value = dayslist
    }

    //날짜정보//
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
                Daylist(year, month, day)
                _Days.value = dayslist
                _Selected.value = Timetable_date(year.toString()+"년", (month+1).toString()+"월", day.toString()+"일")
                dialog.dismiss()
            }
            .setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
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

        when(dayOfWeek){
            "Mon"-> d -= 1
            "Tue"-> d -= 2
            "Wed"-> d -= 3
            "Thu"-> d -= 4
            "Fri"-> d -= 5
            "Sat"-> d -= 6
            "Sun"-> d
        }

        var addday = 0
        for (i: Int in 1 .. 7){
            if(d < 1){
                addday = (lastOfMonth - abs(d))

            }
            else if(d > currentOfMonth){
                addday = (d - currentOfMonth)
            }
            else{
                addday = d
            }
            dayslist.add(addday.toString())
            d += 1
        }
    }
    //날짜정보//

}
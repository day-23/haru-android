package com.example.haru.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.utils.FormatDate
import java.util.*

class EtcViewModel : ViewModel() {

    private val _todayYearMonth = MutableLiveData<String>()
    val todayYearMonth : LiveData<String> = _todayYearMonth

    var year : Int = 0
    var month : Int = 0

    fun setTodayYearMonth() {
        FormatDate.cal.time = Date()
        FormatDate.cal.set(Calendar.DAY_OF_MONTH, 1)
        year = FormatDate.cal.get(Calendar.YEAR)
        month = FormatDate.cal.get(Calendar.MONTH) + 1
        _todayYearMonth.value = year.toString() + month.toString()
    }

    fun addSubTodayYearMonth(type : Boolean) { // type = true면 덧셈, false면 뺄셈
        val tmp = if (type) 1 else -1
        FormatDate.cal.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, tmp)
            year = FormatDate.cal.get(Calendar.YEAR)
            month = FormatDate.cal.get(Calendar.MONTH) + 1
        }
        _todayYearMonth.value = year.toString() + month.toString()
    }

}
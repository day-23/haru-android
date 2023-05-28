package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.ScheduleRequest
import com.example.haru.data.model.StatisticsResponse
import com.example.haru.data.repository.EtcRepository
import com.example.haru.utils.FormatDate
import kotlinx.coroutines.launch
import java.util.*

class EtcViewModel : ViewModel() {
    private val etcRepository = EtcRepository()

    private val _todayYearMonth = MutableLiveData<String>()
    val todayYearMonth : LiveData<String> = _todayYearMonth

    private val _itemCount = MutableLiveData<Pair<Int?, Int?>>()
    val itemCount : LiveData<Pair<Int?, Int?>> = _itemCount

    private val _completedCount = MutableLiveData<Int>()
    val completedCount : LiveData<Int> = _completedCount

    private val _totalItemCount = MutableLiveData<Int>()
    val totalItemCount : LiveData<Int> = _totalItemCount

    var year : Int = 0
    var month : Int = 0

    fun getTodoStatistics(body : ScheduleRequest, callback : () -> Unit) {
        viewModelScope.launch {
            etcRepository.getTodoStatistics(body){
                if (it?.success == true) {
                    _itemCount.postValue(Pair(it.data?.completed, it.data?.totalItems))
                } else {
                    Log.e("20191627", it.toString())
                }
                callback()
            }
        }
    }
    fun setTodayYearMonth() {
        val startDate : Date
        val endDate : Date
        FormatDate.cal.apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.DAY_OF_MONTH, 1)
            year = get(Calendar.YEAR)
            month = get(Calendar.MONTH) + 1

            startDate = time

            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            endDate = time
        }
        getTodoStatistics(body = ScheduleRequest(startDate.toString(), endDate.toString())){
            _todayYearMonth.postValue(year.toString() + month.toString())
        }
    }

    fun addSubTodayYearMonth(type : Boolean) { // type = true면 덧셈, false면 뺄셈
        val tmp = if (type) 1 else -1
        val startDate : Date
        val endDate : Date

        FormatDate.cal.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            add(Calendar.MONTH, tmp)
            year = FormatDate.cal.get(Calendar.YEAR)
            month = FormatDate.cal.get(Calendar.MONTH) + 1

            startDate = time

            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            endDate = time
        }
        getTodoStatistics(body = ScheduleRequest(startDate.toString(), endDate.toString())){
            _todayYearMonth.postValue(year.toString() + month.toString())
        }
    }

}
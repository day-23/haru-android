package com.example.haru.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.example.haru.data.model.Alarm
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoRequest
import com.example.haru.data.model.UpdateTodo
import com.example.haru.utils.FormatDate
import java.text.FieldPosition
import java.util.*

class TodoAddViewModel(checkListViewModel: CheckListViewModel) : ViewModel() {
    private val checklistViewModel: CheckListViewModel

    private val repeatOptionList = listOf<String>("매일", "매주", "2주마다", "매월", "매년")

    private val _flagTodo = MutableLiveData<Boolean>(false)
    val flagTodo: LiveData<Boolean> = _flagTodo

    private val _completedTodo = MutableLiveData<Boolean>(false)
    val completedTodo: LiveData<Boolean> = _completedTodo

    private val _todayTodo = MutableLiveData<Boolean>(false)
    val todayTodo: LiveData<Boolean> = _todayTodo

    private val _isSelectedEndDateTime = MutableLiveData<Boolean>(false)
    val isSelectedEndDateTime: LiveData<Boolean> = _isSelectedEndDateTime

    private val _endDateSwitch = MutableLiveData<Boolean>()
    val endDateSwitch: LiveData<Boolean> = _endDateSwitch

    private val _endDate = MutableLiveData<Date>(Date())
    val endDate: LiveData<Date> = _endDate

    private val _endTime = MutableLiveData<Date>(Date())
    val endTime: LiveData<Date> = _endTime

    private val _alarmSwitch = MutableLiveData<Boolean>(false)
    val alarmSwitch: LiveData<Boolean> = _alarmSwitch

    private val _repeatSwitch = MutableLiveData<Boolean>()
    val repeatSwitch: LiveData<Boolean> = _repeatSwitch

    private val _repeatEndDateSwitch = MutableLiveData<Boolean>(false)
    val repeatEndDateSwitch: LiveData<Boolean> = _repeatEndDateSwitch

    private val _repeatOption = MutableLiveData<Int?>()
    val repeatOption: LiveData<Int?> = _repeatOption

    private val _repeatValue = MutableLiveData<String?>()
    val repeatValue: LiveData<String?> = _repeatValue

    private val _alarmDate = MutableLiveData<Date>(Date())
    val alarmDate: LiveData<Date> = _alarmDate

    private val _alarmTime = MutableLiveData<Date>(Date())
    val alarmTime: LiveData<Date> = _alarmTime

    private val _repeatEndDate = MutableLiveData<Date>(Date())
    val repeatEndDate: LiveData<Date> = _repeatEndDate

    var tagList: MutableList<String> = mutableListOf()
    var subTodos: MutableList<String> = mutableListOf()

    var tag: String = ""
    var content: String = ""

    var memo: String = ""

    //    var repeatValue: String? = null
    var endDateStr: String? = null
    var alarmDateTimeStr: String? = null
    var repeatEndDateStr: String? = null

    var endTimeLayoutHeight: Int = 0

    var repeatSetLayoutHeight: Int = 0
    var repeatOptionHeight: Int = 0
    var repeatWeekHeight: Int = 0
    var repeatEndDateHeight: Int = 0
    var gridMonthHeight: Int = 0
    var gridYearHeight: Int = 0

    lateinit var clickedTodo: Todo

    init {
        this.checklistViewModel = checkListViewModel
    }

    fun setClickTodo(position: Int) {
        clickedTodo = checklistViewModel.todoDataList.value!![position]
        _completedTodo.value = clickedTodo.completed
        _flagTodo.value = clickedTodo.flag
        _todayTodo.value = clickedTodo.todayTodo
        _isSelectedEndDateTime.value = clickedTodo.isAllDay
        if (clickedTodo.endDate != null) {
            _endDate.value = FormatDate.strToDate(clickedTodo.endDate!!)
            _endDateSwitch.value = true
        }
        if (isSelectedEndDateTime.value!!)
            _endTime.value = FormatDate.strToDate(clickedTodo.endDate!!)

        //alarm
        if (clickedTodo.alarms != emptyList<Alarm>()) {
            _alarmSwitch.value = true
            _alarmDate.value = FormatDate.strToDate(clickedTodo.alarms[0].time)
            _alarmTime.value = FormatDate.strToDate(clickedTodo.alarms[0].time)
        }

        //repeat
        if (clickedTodo.repeatOption in repeatOptionList) {
            _repeatSwitch.value = true
            _repeatOption.value = repeatOptionList.indexOf(clickedTodo.repeatOption)
            _repeatValue.value = clickedTodo.repeatValue
        } else _repeatSwitch.value = false

        if (clickedTodo.repeatEnd != null) {
            _repeatEndDateSwitch.value = true
            _repeatEndDate.value = FormatDate.strToDate(clickedTodo.repeatEnd!!)
        }

        content = clickedTodo.content
        memo = clickedTodo.memo
        for (i in 0 until clickedTodo.tags.size) {
            tag += "${clickedTodo.tags[i].content} "
        }
    }

    fun setCompleteTodo() {
        _completedTodo.value = (_completedTodo.value == false)
    }

    fun setFlagTodo() {
        _flagTodo.value = (_flagTodo.value == false)
    }

    fun setTodayTodo() {
        _todayTodo.value = (_todayTodo.value == false)
    }

    fun setIsSelectedEndDateTime() {
        _isSelectedEndDateTime.value = (_isSelectedEndDateTime.value == false)
    }

    fun setEndDateSwitch() {
        if (_endDateSwitch.value == null) _endDateSwitch.value = true
        else _endDateSwitch.value = (_endDateSwitch.value == false)

        if (endDateSwitch.value == false) _isSelectedEndDateTime.value = false
    }

    fun setAlarmSwitch() {
        _alarmSwitch.value = (_alarmSwitch.value == false)
    }

    fun setRepeatSwitch() {
        if (_repeatSwitch.value == null) _repeatSwitch.value = true
        else _repeatSwitch.value = (_repeatSwitch.value == false)

    }

    fun setRepeatEndSwitch() {
        _repeatEndDateSwitch.value = (_repeatEndDateSwitch.value == false)
    }

    fun setRepeatOpt(num: Int) {
        _repeatOption.value = num
        when (num) {
            0 -> _repeatValue.value = null
            1, 2 -> _repeatValue.value = String.format("%-7s", "").replace(' ', '0')
            3 -> _repeatValue.value = String.format("%-31s", "").replace(' ', '0')
            4 -> _repeatValue.value = String.format("%-12s", "").replace(' ', '0')
        }
    }

    fun setTime(id: Int, date: Date) {
        if (id == 0) _endTime.value = date else _alarmTime.value = date
    }

    fun setDate(id: Int, date: Date) {
        when (id) {
            0 -> _endDate.value = date
            1 -> _alarmDate.value = date
            else -> _repeatEndDate.value = date
        }
    }

    fun setEndTimeHeight(h: Int) {
        endTimeLayoutHeight = h
    }

    fun setRepeatSetLayoutH(h: Int) {
        repeatSetLayoutHeight = h
    }

    fun setRepeatOptionH(h: Int) {
        repeatOptionHeight = h
    }

    fun setWeekHeight(h: Int) {
        repeatWeekHeight = h
    }

    fun setMonthHeight(h: Int) {
        gridMonthHeight = h
    }

    fun setYearHeight(h: Int) {
        gridYearHeight = h
    }

    fun setRepeatEndDateH(h: Int) {
        repeatEndDateHeight = h
    }

    fun readyToSubmit() {
        if (tag == "" || tag.replace(" ", "") == "")
            tagList = mutableListOf()
        else tagList = tag.split(" ") as MutableList<String>

        endDateStr = if (endDateSwitch.value == true) {
            if (!isSelectedEndDateTime.value!!) FormatDate.dateToStr(endDate.value!!)
            else {
                FormatDate.dateToStr(endDate.value!!).substring(
                    0,
                    10
                ) + FormatDate.dateToStr(endTime.value!!).substring(10)
            }
        } else {
            null
        }

        alarmDateTimeStr =
            if (alarmSwitch.value == true && alarmDate.value != null && alarmTime.value != null)
                FormatDate.dateToStr(alarmDate.value!!).substring(
                    0,
                    10
                ) + FormatDate.dateToStr(alarmTime.value!!).substring(10)
            else null

        repeatEndDateStr =
            if (repeatSwitch.value == true && repeatEndDateSwitch.value == true && repeatEndDate.value != null)
                FormatDate.dateToStr(repeatEndDate.value!!)
            else null
    }

    fun setRepeatVal(position: Int) {
        val repeatValue = _repeatValue.value
        _repeatValue.value = if (repeatValue!![position] == '1')
            repeatValue.substring(0, position) + '0' + repeatValue.substring(position + 1)
        else repeatValue.substring(0, position) + '1' + repeatValue.substring(position + 1)
    }

    private fun createTodoData(): TodoRequest {
        return TodoRequest(
            content = content,
            memo = memo,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) repeatOptionList[repeatOption.value!!] else "null",
            repeatValue = repeatValue.value,
            repeatEnd = repeatEndDateStr,
            tags = tagList,
            subTodos = subTodos,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!)
        )
    }

    fun addTodo(callback: () -> Unit) {
        checklistViewModel.addTodo(createTodoData()) {
            callback()
        }
    }

    private fun createUpdateTodoData(): UpdateTodo {
        return UpdateTodo(
            content = content,
            memo = memo,
            completed = completedTodo.value!!,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) repeatOptionList[repeatOption.value!!] else "null",
            repeatValue = repeatValue.value,
            repeatEnd = repeatEndDateStr,
            tags = tagList,
            subTodos = subTodos,
            subTodosCompleted = emptyList(),
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!)
            )
    }

    fun updateTodo(callback: () -> Unit) {
        checklistViewModel.putTodo(clickedTodo.id, createUpdateTodoData()){
            callback()
        }
    }

}
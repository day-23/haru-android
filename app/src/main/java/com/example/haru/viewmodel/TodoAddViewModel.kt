package com.example.haru.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.haru.data.model.*
import com.example.haru.utils.FormatDate
import java.util.*

class TodoAddViewModel(checkListViewModel: CheckListViewModel) : ViewModel() {
    private val checklistViewModel: CheckListViewModel

    private val repeatOptionList = listOf("매일", "매주", "격주", "매달", "매년")

    private val _flagTodo = MutableLiveData<Boolean>(false)
    val flagTodo: LiveData<Boolean> = _flagTodo

    private val _completedTodo = MutableLiveData<Boolean>(false)
    val completedTodo: LiveData<Boolean> = _completedTodo

    private val _todayTodo = MutableLiveData<Boolean>(false)
    val todayTodo: LiveData<Boolean> = _todayTodo

    private val _subTodoList = MutableLiveData<List<String>>()
    val subTodoList: LiveData<List<String>> = _subTodoList

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

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    private val _tagLiveData = MutableLiveData<List<String>>()
    val tagLiveData: LiveData<List<String>> = _tagLiveData

    private val tagList: MutableList<String> = mutableListOf()
    var subTodos: MutableList<String> = mutableListOf()
    var subTodoCnt: Int = 0
    var subTodoClickPosition = -1

    var subTodoCompleted = mutableListOf<Boolean>()

    var day: Int? = null
    var selectDateFlag = false

    var tag: String = ""
    var content: String = ""

    var memo: String = ""

    var endDateStr: String? = null
    var alarmDateTimeStr: String? = null
    var repeatEndDateStr: String? = null
    var repeatValueStr: String? = null

    var endTimeLayoutHeight: Int = 0
    var repeatSetLayoutHeight: Int = 0
    var repeatOptionHeight: Int = 0
    var repeatWeekHeight: Int = 0
    var repeatEndDateHeight: Int = 0
    var gridMonthHeight: Int = 0
    var gridYearHeight: Int = 0

    var clickedTodo: Todo? = null

    var calculateDateFlag = false

    init {
        this.checklistViewModel = checkListViewModel
    }

    private fun getRepeatOptionStr(position: Int?): String? {
        return if (position != null) repeatOptionList[position] else null
    }

    fun setSelectDate(date: Date) {
        selectDateFlag = true
        _selectedDate.value = date
    }

    fun setToday(today: Boolean?) {
        if (today == true) {
            _endDateSwitch.value = true
            _todayTodo.value = true
        }
    }

    fun setClickTodo(id: String? = null, todo: Todo? = null) {

        if (todo == null) {
            clickedTodo = checklistViewModel.todoDataList.value!!.find {
                it.id == id
            }
            if (clickedTodo == null)
                clickedTodo = checklistViewModel.todayTodo.value!!.find { it.id == id }!!
            if (clickedTodo!!.repeatOption != null)
                clickedTodo!!.location = 0
        } else {
            clickedTodo = todo
        }

        _completedTodo.value = clickedTodo!!.completed
        _flagTodo.value = clickedTodo!!.flag
        _todayTodo.value = clickedTodo!!.todayTodo
        _isSelectedEndDateTime.value = clickedTodo!!.isAllDay

        if (clickedTodo!!.subTodos.isNotEmpty()) {
            subTodos.clear()
            subTodoCnt = 0
            for (i in 0 until clickedTodo!!.subTodos.size) {
                subTodoCnt++
                subTodos.add(clickedTodo!!.subTodos[i].content)
                subTodoCompleted.add(clickedTodo!!.subTodos[i].completed)
            }
            _subTodoList.value = subTodos
        }

        if (clickedTodo!!.endDate != null) {
            _endDate.value = FormatDate.strToDate(clickedTodo!!.endDate!!)
            Log.d("20191627", endDate.value.toString())
            _endDateSwitch.value = true
        }
//        else
//            _endDateSwitch.value = false

        if (isSelectedEndDateTime.value!!)
            _endTime.value = FormatDate.strToDate(clickedTodo!!.endDate!!)

        //alarm
        if (clickedTodo!!.alarms != emptyList<Alarm>()) {
            _alarmSwitch.value = true
            _alarmDate.value = FormatDate.strToDate(clickedTodo!!.alarms[0].time)
            _alarmTime.value = FormatDate.strToDate(clickedTodo!!.alarms[0].time)
        }

        //repeat
        if (clickedTodo!!.repeatOption in repeatOptionList) {
            _repeatSwitch.value = true
            _repeatOption.value = repeatOptionList.indexOf(clickedTodo!!.repeatOption)
            _repeatValue.value = clickedTodo!!.repeatValue
        }
//        else _repeatSwitch.value = false

        if (clickedTodo!!.repeatEnd != null) {
            _repeatEndDateSwitch.value = true
            _repeatEndDate.value = FormatDate.strToDate(clickedTodo!!.repeatEnd!!)
        }

        content = clickedTodo!!.content
        memo = clickedTodo!!.memo
        for (i in 0 until clickedTodo!!.tags.size)
            tagList.add(clickedTodo!!.tags[i].content)
        _tagLiveData.value = tagList
    }

    fun setFlagTodo() {
        _flagTodo.value = (_flagTodo.value == false)
    }

    fun setTodayTodo() {
        _todayTodo.value = (_todayTodo.value == false)
    }

    fun plusSubTodo(content: String = "") {
        subTodoCnt += 1
        subTodos.add(content)
        _subTodoList.value = subTodos
    }

    fun deleteSubTodo() {
        if (subTodoCnt == 0) return
        subTodoCnt -= 1
        subTodos.removeAt(subTodoClickPosition)
        _subTodoList.value = subTodos
    }

    fun setSubTodoPosition(position: Int) {
        subTodoClickPosition = position
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
        if (_repeatSwitch.value == null) {
            _repeatSwitch.value = true
        } else _repeatSwitch.value = (_repeatSwitch.value == false)

        if (repeatSwitch.value == true)
            setRepeatOpt(0)
        selectDateFlag = false
    }

    fun setRepeatEndSwitch() {
        _repeatEndDateSwitch.value = (_repeatEndDateSwitch.value == false)
    }

    fun setRepeatOpt(num: Int) {
        _repeatOption.value = num
        selectDateFlag = false
        when (num) {
            0 -> _repeatValue.value = "1"
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
            0 -> {
                _endDate.value = date
                if (repeatEndDate.value != null && endDate.value!!.after(repeatEndDate.value))
                    setDate(2, endDate.value!!)
            }
            1 -> _alarmDate.value = date
            else -> _repeatEndDate.value = date
        }
    }

    fun addTagList(): Boolean {
        if (tag == "" || tag.replace(" ", "") == "")
            return false
        tagList.add(tag.replace(" ", ""))
        _tagLiveData.value = tagList
        return true
    }

    fun subTagList(item: String) {
        tagList.remove(item)
        _tagLiveData.value = tagList
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

    fun setStr(str : String){
        endDateStr = str
    }

    fun readyToSubmit() {
        Log.d("20191627", subTodos.toString())
        for (i in 0 until subTodos.size)
            if (subTodos[i] == "" || subTodos[i].replace(" ", "") == "")
                subTodos[i] = ""

        subTodos.removeAll(listOf(""))


//        tagList = if (tag == "" || tag.replace(" ", "") == "")
//            mutableListOf()
//        else {
//            tag = tag.replace("\\s+".toRegex(), " ")
//            tag.split(" ") as MutableList<String>
//        }

        endDateStr = if (endDateSwitch.value == true) {
            if (!isSelectedEndDateTime.value!!) FormatDate.dateToStr(endDate.value!!)
            else {
                FormatDate.dateToStr(endDate.value)!!.substring(
                    0,
                    10
                ) + FormatDate.dateToStr(endTime.value)!!.substring(10)
            }
        } else {
            null
        }

        alarmDateTimeStr =
            if (alarmSwitch.value == true && alarmDate.value != null && alarmTime.value != null)
                FormatDate.dateToStr(alarmDate.value)!!.substring(
                    0,
                    10
                ) + FormatDate.dateToStr(alarmTime.value)!!.substring(10)
            else null

        repeatEndDateStr =
            if (repeatSwitch.value == true && repeatEndDateSwitch.value == true && repeatEndDate.value != null)
                FormatDate.dateToStr(repeatEndDate.value!!)
            else null

        repeatValueStr = repeatValue.value
        if (repeatValueStr != null) {
            repeatValueStr = repeatValueStr!!.replace('2', '0')
            if (!repeatValueStr!!.contains('1')) {
                _repeatOption.value = null
                _repeatValue.value = null
                repeatEndDateStr = null
            }
        }
    }

    fun setRepeatVal(position: Int, value: Char? = null) {
        // value가 2이면 선택 불가, null이면 그냥 값보고 판단
        val repeatValue: String = _repeatValue.value!!
        val changeValue =
            value ?: if (repeatValue[position] == '1' || repeatValue[position] == '2')
                '0'
            else '1'
        if (value == null)
            selectDateFlag = false
        _repeatValue.value =
            repeatValue.substring(0, position) + changeValue + repeatValue.substring(position + 1)
    }

    private fun createTodo(): TodoRequest {
        return TodoRequest(
            content = content,
            memo = memo,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) getRepeatOptionStr(
                repeatOption.value!!
            ) else null,
            repeatValue = if (repeatSwitch.value == true) repeatValueStr else null,
            repeatEnd = repeatEndDateStr,
            tags = if (tagLiveData.value == null) emptyList() else tagLiveData.value!!,
            subTodos = subTodos,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!)
        )
    }

    fun addTodo(calendar: Boolean = false, callback: (Boolean?) -> Unit) {
        checklistViewModel.addTodo(createTodo(), calendar) {
            callback(it)
        }
    }

    private fun createUpdateTodo(): UpdateTodo {
        return UpdateTodo(
            content = content,
            memo = memo,
            completed = completedTodo.value!!,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) getRepeatOptionStr(
                repeatOption.value!!
            ) else null,
            repeatValue = repeatValueStr,
            repeatEnd = repeatEndDateStr,
            tags = if (tagLiveData.value == null) emptyList() else tagLiveData.value!!,
            subTodos = subTodos,
            subTodosCompleted = subTodoCompleted,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!)
        )
    }

    private fun createUpdateRepeatFrontTodo(nextEndDate: String): UpdateRepeatFrontTodo {
        return UpdateRepeatFrontTodo(
            content = content,
            memo = memo,
            completed = completedTodo.value!!,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) getRepeatOptionStr(
                repeatOption.value
            ) else null,
            repeatValue = repeatValueStr,
//            repeatValue.value,
            repeatEnd = repeatEndDateStr,
            tags = if (tagLiveData.value == null) emptyList() else tagLiveData.value!!,
            subTodos = subTodos,
            subTodosCompleted = subTodoCompleted,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!),
            nextEndDate = nextEndDate
        )
    }

    private fun createUpdateRepeatMiddleTodo(
        changedDate: String,
        nextEndDate: String
    ): UpdateRepeatMiddleTodo {
        return UpdateRepeatMiddleTodo(
            content = content,
            memo = memo,
            completed = completedTodo.value!!,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) getRepeatOptionStr(
                repeatOption.value
            ) else null,
            repeatValue = repeatValueStr,
//            repeatValue.value,
            repeatEnd = repeatEndDateStr,
            tags = if (tagLiveData.value == null) emptyList() else tagLiveData.value!!,
            subTodos = subTodos,
            subTodosCompleted = subTodoCompleted,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!),
            changedDate = changedDate,
            nextEndDate = nextEndDate
        )
    }

    private fun createUpdateRepeatBackTodo(preEndDate: String): UpdateRepeatBackTodo {
        return UpdateRepeatBackTodo(
            content = content,
            memo = memo,
            completed = completedTodo.value!!,
            todayTodo = todayTodo.value!!,
            flag = flagTodo.value!!,
            isAllDay = isSelectedEndDateTime.value ?: false,
            endDate = endDateStr,
            repeatOption = if (repeatSwitch.value == true && repeatOption.value != null) getRepeatOptionStr(
                repeatOption.value
            ) else null,
            repeatValue = repeatValueStr,
//            repeatValue.value,
            repeatEnd = repeatEndDateStr,
            tags = if (tagLiveData.value == null) emptyList() else tagLiveData.value!!,
            subTodos = subTodos,
            subTodosCompleted = subTodoCompleted,
            alarms = if (alarmDateTimeStr == null) emptyList() else listOf(alarmDateTimeStr!!),
            preRepeatEnd = preEndDate
        )
    }

    fun updateTodo(callback: () -> Unit) {
        checklistViewModel.updateTodo(clickedTodo!!.id, createUpdateTodo()) {
            callback()
        }
    }

    fun updateRepeatFrontTodo(callback: () -> Unit) {
        val nextEndDate = findNextEndDate()
        repeatValueStr = null
        repeatEndDateStr = null
        _repeatOption.value = null

        if (nextEndDate != null) {
            val nextEndDateStr = FormatDate.dateToStr(nextEndDate)!!
            checklistViewModel.updateRepeatFrontTodo(
                todoId = clickedTodo!!.id,
                updateRepeatFrontTodo = createUpdateRepeatFrontTodo(nextEndDateStr)
            ) {
                callback()
            }
        } else checklistViewModel.updateTodo(
            todoId = clickedTodo!!.id,
            todo = createUpdateTodo()
        ) { callback() }
    }

    fun updateRepeatMiddleTodo(callback: () -> Unit) {
        val nextEndDate = findNextEndDate()
        val changedDate = clickedTodo?.endDate
        repeatValueStr = null
        repeatEndDateStr = null
        _repeatOption.value = null

        if (nextEndDate != null && changedDate != null) {
            val nextEndDateStr = FormatDate.dateToStr(nextEndDate)!!
            checklistViewModel.updateRepeatMiddleTodo(
                todoId = clickedTodo!!.id,
                updateRepeatMiddleTodo = createUpdateRepeatMiddleTodo(changedDate, nextEndDateStr)
            ) {
                callback()
            }
        } else {
            checklistViewModel.updateTodo(
                todoId = clickedTodo!!.id,
                todo = createUpdateTodo()
            ) { callback() }
        }
    }

    fun updateRepeatBackTodo(atTimeTable: Boolean = false, callback: () -> Unit) {
        var preEndDate = FormatDate.preEndDate(
            clickedTodo?.endDate,
            clickedTodo?.repeatOption,
            clickedTodo?.repeatValue
        )

        if(atTimeTable)
        {
            repeatValueStr = null
            repeatEndDateStr = null
            _repeatOption.value = null
        }
        if (preEndDate == null) {
            Log.d("20191627", "TodoAddViewModel -> UpdateRepeatBackTodo에서 preEndDate가 null")
            checklistViewModel.updateTodo(
                todoId = clickedTodo!!.id,
                todo = createUpdateTodo()
            ) { callback() }
        } else {
            val preEndDateStr = FormatDate.dateToStr(preEndDate)!!
            checklistViewModel.updateRepeatBackTodo(
                todoId = clickedTodo!!.id,
                updateRepeatBackTodo = createUpdateRepeatBackTodo(preEndDateStr)
            ) { callback() }
        }

    }

    fun deleteTodo(callback: () -> Unit) {
        checklistViewModel.deleteTodo(todoId = clickedTodo!!.id) {
            callback()
        }
    }

    private fun findNextEndDate(): Date? {
        val nextEndDate = when (clickedTodo!!.repeatOption) {
            "매일" -> {
                FormatDate.nextEndDate(clickedTodo!!.endDate, clickedTodo!!.repeatEnd)
            }
            "매주" -> {
                FormatDate.nextEndDateEveryWeek(
                    clickedTodo!!.repeatValue!!,
                    1,
                    clickedTodo!!.endDate,
                    clickedTodo!!.repeatEnd
                )
            }
            "격주" -> {
                FormatDate.nextEndDateEveryWeek(
                    clickedTodo!!.repeatValue!!,
                    2,
                    clickedTodo!!.endDate,
                    clickedTodo!!.repeatEnd
                )
            }
            "매달" -> {
                FormatDate.nextEndDateEveryMonth(
                    clickedTodo!!.repeatValue!!,
                    clickedTodo!!.endDate,
                    clickedTodo!!.repeatEnd
                )
            }
            "매년" -> {
                FormatDate.nextEndDateEveryYear(
                    clickedTodo!!.repeatValue!!,
                    clickedTodo!!.endDate,
                    clickedTodo!!.repeatEnd
                )
            }
            else -> null
        }
        return nextEndDate
    }

    private fun findPreEndDate(): Date? {  // 프론트와 백이 같은 Todo에서는 예외를 처리해줘야한다.
        return null
    }

    fun deleteRepeatFrontTodo(callback: () -> Unit) {
        val nextEndDate = findNextEndDate()
        Log.d("20191627", nextEndDate.toString())
        if (nextEndDate != null) {
            val nextEndDateStr = FormatDate.dateToStr(nextEndDate)
            checklistViewModel.deleteRepeatFrontTodo(
                todoId = clickedTodo!!.id,
                frontEndDate = FrontEndDate(nextEndDateStr!!)
            ) {
                callback()
            }
        } else
            checklistViewModel.deleteTodo(todoId = clickedTodo!!.id) { callback() }
    }

    fun deleteRepeatMiddleTodo(callback: () -> Unit) {
        val nextEndDate = findNextEndDate()
        val removedDate = clickedTodo!!.endDate

        if (nextEndDate != null && removedDate != null) {
            val nextEndDateStr = FormatDate.dateToStr(nextEndDate)
            checklistViewModel.deleteRepeatMiddleTodo(
                todoId = clickedTodo!!.id,
                middleEndDate = MiddleEndDate(removedDate, nextEndDateStr!!)
            ) { callback() }
        } else {
            Log.d("20191627", "TodoAddViewModel -> deleteRepeatMiddleTodo에서 비상상황 발생!!")
        }
    }

    fun deleteRepeatBackTodo(callback: () -> Unit) {
        val backRepeatEnd = FormatDate.preEndDate(
            clickedTodo!!.endDate!!,
            clickedTodo!!.repeatOption!!,
            clickedTodo!!.repeatValue!!
        )
        if (backRepeatEnd != null) {
            checklistViewModel.deleteRepeatBackTodo(
                todoId = clickedTodo!!.id,
                backRepeatEnd = BackRepeatEnd(FormatDate.dateToStr(backRepeatEnd)!!)
            ) {
                callback()
            }
        } else {
            Log.d("20191627", "TodoAddViewModel -> deleteRepeatBackTodo에서 비상상황 발생!!")
        }

    }

//    fun checkChangeEndDate(): Boolean { // 마감일의 년, 월, 일을 검사 -> 시간까지 확인하는 거면 시간과 분까지 확인
//        val changeYear = endDateStr?.substring(0, 4)
//        val changeMonth = endDateStr?.substring(5, 7)
//        val changeDay = endDateStr?.substring(8, 10)
//
//        val date = FormatDate.dateToStr(FormatDate.strToDate(clickedTodo!!.endDate))
//        val year = date?.substring(0, 4)
//        val month = date?.substring(5, 7)
//        val day = date?.substring(8, 10)
//
//        if (changeYear != year || changeMonth != month || changeDay != day) {
//            return true
//        }
//        if (clickedTodo!!.isAllDay) {
//            if (isSelectedEndDateTime.value != true)
//                return true
//            val changeHour = endDateStr?.substring(11, 13)
//            val changeMinute = endDateStr?.substring(14, 16)
//            val hour = date?.substring(11, 13)
//            val minute = date?.substring(14, 16)
//
//            if (changeHour != hour || changeMinute != minute)
//                return true
//        } else {
//            if (isSelectedEndDateTime.value == true)
//                return true
//        }
//        return false
//    }

    fun checkChangeRepeat(): Boolean {
        // repeatOption이 바뀌었는지, repeatValue가 바뀌었는지, repeatEndDate가 변경되었는지 (년, 월, 일만 검사)
        if (clickedTodo!!.repeatOption != getRepeatOptionStr(repeatOption.value)) // repeatOption의 변경 확인
            return true
        if (clickedTodo!!.repeatValue != repeatValue.value) // repeatValue 변경 확인
            return true

        val changeYear = repeatEndDateStr?.substring(0, 4)
        val changeMonth = repeatEndDateStr?.substring(5, 7)
        val changeDay = repeatEndDateStr?.substring(8, 10)

        val date = FormatDate.dateToStr(FormatDate.strToDate(clickedTodo!!.repeatEnd))
        val year = date?.substring(0, 4)
        val month = date?.substring(5, 7)
        val day = date?.substring(8, 10)
        if (changeYear != year || changeMonth != month || changeDay != day)
            return true

        return false
    }

    fun checkChangeData(): Boolean { // 마감일, 반복옵션 외의 다른 것들이 변경되었는지를 확인한다.
        return false
    }

}
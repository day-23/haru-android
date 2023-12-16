package com.example.haru.view.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.data.model.UpdateTodo
import com.example.haru.utils.Alarm
import com.example.haru.utils.FormatDate
import com.example.haru.view.adapter.TodotableAdapter
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TimetableViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import com.example.haru.viewmodel.TodoTableRecyclerViewmodel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TodoTimeTableTodoDragListener (todoreviewModel: TodoTableRecyclerViewmodel,
                                     timetableviewModel:TimetableViewModel,
                                     val lifecycle: LifecycleOwner,
                                     val context: Context) : View.OnDragListener {
    val todoRecyclerViewModel = todoreviewModel
    val timeTableViewModel = timetableviewModel
    val todoAddViewmodel = TodoAddViewModel()

    override fun onDrag(view: View, event: DragEvent): Boolean {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)

        val viewSource = event.localState as View
        var targetRecyclerView: RecyclerView
        val sourceRecyclerView: RecyclerView = viewSource.parent.parent as RecyclerView

        if (event.action == DragEvent.ACTION_DROP) {
            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            sourceRecyclerView.setBackgroundColor(Color.TRANSPARENT)
            if (targetRecyclerView.id != sourceRecyclerView.id) {
                val sourceAdapter = sourceRecyclerView.adapter as TodotableAdapter?
                val sourcePosition = sourceRecyclerView.getChildAdapterPosition(viewSource.parent as View)
                val targetAdapter = targetRecyclerView.adapter as TodotableAdapter?

                var sourceList: ArrayList<Todo> = ArrayList()
                sourceAdapter?.getItems()?.let { sourceList = it }
                val todo = sourceList[sourcePosition]
                sourceAdapter?.removeItem(sourcePosition)
                var targetList: ArrayList<Todo> = ArrayList()
                targetAdapter?.setItem(todo)


                /* nextEndDate, location 구분 */
                val afterMoveDate = targetAdapter?.curDate!!
                val preMoveDate = sourceAdapter?.curDate!!

                val preYear = preMoveDate.substring(0, 4)
                val preMonth = preMoveDate.substring(4, 6)
                val preDay = preMoveDate.substring(6, 8)
                val preDateInfo = "${preYear}-${preMonth}-${preDay}T00:00:00+09:00"


                val endDateTime = todo.endDate?.substring(11,19)
                val afterYear = afterMoveDate.substring(0, 4)
                val afterMonth = afterMoveDate.substring(4, 6)
                val afterDay = afterMoveDate.substring(6, 8)
                val newEndDate = "${afterYear}-${afterMonth}-${afterDay}T${endDateTime}+09:00"

                /* 수정 api 쏘기 */
                if(todo.repeatOption == null){
                    val updateTodo = UpdateTodo(
                        todo.content,
                        todo.memo,
                        todo.todayTodo,
                        todo.flag,
                        todo.isAllDay,
                        newEndDate,
                        todo.repeatEnd,
                        todo.completed,
                        todo.subTodos.map { it.completed },
                        todo.subTodos.map{ it.content },
                        todo.alarms.map { it.time },
                        todo.repeatOption,
                        todo.repeatValue,
                        todo.tags.map{it.content}
                    )
                    CheckListViewModel.updateTodo(
                        todo.id,
                        updateTodo
                    ){
                        todoRecyclerViewModel.getTodo(timeTableViewModel.Dates.value!!)
                        Alarm.initAlarm(lifecycle, context)
                    }
                }else{
                    calculateTodoNextEndDateAndLocation(todo, preDateInfo)
                    /* nextEndDate 구해서 API 호출하기 */

                    todoAddViewmodel.setClickTodo(todo = todo)
                    todoAddViewmodel.readyToSubmit()
                    todoAddViewmodel.setStr(newEndDate)

                    when(todo.location){
                        0 -> { //front
                            todoAddViewmodel.updateRepeatFrontTodo {
                                todoRecyclerViewModel.getTodo(timeTableViewModel.Dates.value!!)
                                Alarm.initAlarm(lifecycle, context)
                            }
                        }
                        1 -> { //middle
                            todoAddViewmodel.updateRepeatMiddleTodo{
                                todoRecyclerViewModel.getTodo(timeTableViewModel.Dates.value!!)
                                Alarm.initAlarm(lifecycle, context)
                            }
                        }
                        2-> {//back
                            todoAddViewmodel.updateRepeatBackTodo(true){
                                todoRecyclerViewModel.getTodo(timeTableViewModel.Dates.value!!)
                                Alarm.initAlarm(lifecycle, context)
                            }
                        }
                    }
                }



                Log.d("dragTodo", "onDragEnd, location: ${todo}")

            }
            return true
        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED){
            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            return true
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED){
            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            targetRecyclerView.setBackgroundColor(Color.TRANSPARENT)
        }
        return true
    }



    private fun calculateTodoNextEndDateAndLocation(todo: Todo, todayTodo : String){
        val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
        val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
        val koreaFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

        var todoendDate = ""

        if (todo.endDate != null) {
            val enddate = serverDateFormat.parse(todo.endDate)
            val backendDate =
                serverDateFormat.parse(FormatDate.calendarBackFormat(todo.endDate!!))

            if (enddate.date != backendDate.date) {
                val calendar = Calendar.getInstance()
                val today = todayDateFormat.parse(todayTodo)

                calendar.apply {
                    time = backendDate
                    set(Calendar.YEAR, today.year)
                    set(Calendar.MONTH, today.month)
                    set(Calendar.DAY_OF_MONTH, today.date)
                    add(Calendar.DAY_OF_MONTH, -1)
                }

                val formatdate = calendar.time
                formatdate.year += 1900

                todoendDate = serverDateFormat.format(formatdate)
            } else {
                val calendar = Calendar.getInstance()
                val today = todayDateFormat.parse(todayTodo)

                calendar.apply {
                    time = backendDate
                    set(Calendar.YEAR, today.year)
                    set(Calendar.MONTH, today.month)
                    set(Calendar.DAY_OF_MONTH, today.date)
                }

                val formatdate = calendar.time
                formatdate.year += 1900

                todoendDate = serverDateFormat.format(formatdate)
            }
        }

        if (todo.endDate != null && todo.repeatOption != null) {
            val end = serverDateFormat.parse(todo.endDate)
            val today = todayDateFormat.parse(todayTodo)

            if (end.year == today.year && end.month == today.month && end.date == today.date) {
                todo.location = 0 // front
                todo.endDate = todoendDate

                return
            }
        }

        if(todo.endDate != null &&
            todo.repeatEnd != null &&
            todo.repeatOption != null &&
            todo.repeatValue != null) {

            val today = todayDateFormat.parse(todayTodo)
            val beforeFormatToday = FormatDate.calendarBackFormat(serverDateFormat.format(today))
            val beforeFormatEnd = FormatDate.calendarBackFormat(todo.repeatEnd!!)

            var preData: Date? = null
            var nextData: Date? = null

            when(todo.repeatOption){
                "매일"->{
                    preData = FormatDate.preEndDate(beforeFormatToday,todo.repeatOption, todo.repeatValue!!)
                    nextData = FormatDate.nextEndDate(beforeFormatToday, beforeFormatEnd)
                }

                "매주"->{
                    preData = FormatDate.preEndDate(beforeFormatToday,todo.repeatOption, todo.repeatValue!!)
                    nextData = FormatDate.nextEndDateEveryWeek(todo.repeatValue,1,beforeFormatToday, beforeFormatEnd)
                }

                "격주"->{
                    preData = FormatDate.preEndDate(beforeFormatToday,todo.repeatOption, todo.repeatValue!!)
                    nextData = FormatDate.nextEndDateEveryWeek(todo.repeatValue, 2, beforeFormatToday, beforeFormatEnd)
                }

                "매달"->{
                    preData = FormatDate.preEndDate(beforeFormatToday,todo.repeatOption, todo.repeatValue!!)
                    nextData = FormatDate.nextEndDateEveryMonth(todo.repeatValue!!,beforeFormatToday, beforeFormatEnd)
                }

                "매년"->{
                    preData = FormatDate.preEndDate(beforeFormatToday,todo.repeatOption, todo.repeatValue!!)
                    nextData = FormatDate.nextEndDateEveryYear(todo.repeatValue!!, beforeFormatToday, beforeFormatEnd)
                }
            }

            if(preData == null && nextData == null){
                return
            }
        }

        if(todo.repeatEnd != null && todo.repeatOption != null){
            val today = todayDateFormat.parse(todayTodo)
            val beforeFormatToday =
                FormatDate.calendarBackFormat(serverDateFormat.format(today))
            val beforeFormatEnd = FormatDate.calendarBackFormat(todo.repeatEnd!!)

            var nextData: Date? = null

            when (todo.repeatOption) {
                "매일" -> {
                    nextData = FormatDate.nextEndDate(beforeFormatToday, beforeFormatEnd)
                }

                "매주" -> {
                    nextData = FormatDate.nextEndDateEveryWeek(
                        todo.repeatValue,
                        1,
                        beforeFormatToday,
                        beforeFormatEnd
                    )
                }

                "격주" -> {
                    nextData = FormatDate.nextEndDateEveryWeek(
                        todo.repeatValue,
                        2,
                        beforeFormatToday,
                        beforeFormatEnd
                    )
                }

                "매달" -> {
                    nextData = FormatDate.nextEndDateEveryMonth(
                        todo.repeatValue!!,
                        beforeFormatToday,
                        beforeFormatEnd
                    )
                }

                "매년" -> {
                    nextData = FormatDate.nextEndDateEveryYear(
                        todo.repeatValue!!,
                        beforeFormatToday,
                        beforeFormatEnd
                    )
                }
            }

            if (nextData == null) {
                todo.location = 2
                todo.endDate = todoendDate

                return
            }
        }

        if (todo.repeatOption != null) {
            todo.location = 1 // middle
            todo.endDate = todoendDate

            return
        }

    }
}
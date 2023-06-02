package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.utils.FormatDate
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import java.text.SimpleDateFormat
import java.util.*

class AdapterSimpleTodo(
    todos: List<Todo>,
    val activity: FragmentActivity,
    val todayTodo: String,
    val dialog: Dialog,
    val adapter: AdapterMonth
) : RecyclerView.Adapter<AdapterSimpleTodo.DetailView>() {
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    private lateinit var todos: List<Todo>
    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
    val format = SimpleDateFormat("yyyy-MM-dd")

    init {
        this.todos = todos
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterSimpleTodo.DetailView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_simple_todo,
            parent,
            false
        )

        return DetailView(view)
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: DetailView, position: Int) {
        val todo = todos[position]

        val detailTodoComplete = holder.itemView.findViewById<ImageView>(R.id.detail_todo_complete)
        val detailTodoContentTv =
            holder.itemView.findViewById<TextView>(R.id.detail_todo_content_tv)
        val detailTodoTagsTv = holder.itemView.findViewById<TextView>(R.id.detail_todo_tags_tv)
        val detailTodoFlagImv = holder.itemView.findViewById<ImageView>(R.id.detail_todo_flag_imv)
        val todoCorrectionBtn = holder.itemView.findViewById<LinearLayout>(R.id.todo_correction_btn)

        if (todo.completed) {
            detailTodoComplete.setBackgroundResource(R.drawable.circle_check)
//            detailTodoContentTv.setPaintFlags(detailTodoContentTv.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
//
//            detailTodoContentTv.setTextColor(Color.parseColor("#F1F1F5"))
//            detailTodoTagsTv.setTextColor(Color.parseColor("#F1F1F5"))
        } else {
            detailTodoComplete.setBackgroundResource(R.drawable.circle_uncheck)
        }

        detailTodoContentTv.text = todo.content

        var tag = ""

        for (i in todo.tags) {
            tag += i.content + " "
        }

        if (tag.length > 0) tag = tag.dropLast(0)

        if(tag != "") {
            detailTodoTagsTv.visibility = View.VISIBLE
            detailTodoTagsTv.text = tag
        } else {
            detailTodoTagsTv.visibility = View.GONE
            detailTodoTagsTv.text = ""
        }

        if(todo.flag){
            detailTodoFlagImv.setBackgroundResource(R.drawable.star_check)
        } else {
            detailTodoFlagImv.setBackgroundResource(R.drawable.star_uncheck)
        }

        detailTodoComplete.setOnClickListener {
            val calendarViewModel = CalendarViewModel()

            if(todo.repeatOption != null && !todo.completed){
                val end = serverDateFormat.parse(todo.endDate)
                var today = todayDateFormat.parse(todayTodo)
                val frontOk = today.clone() as Date

                today.hours = end.hours
                today.minutes = end.minutes
                today.seconds = end.seconds

                if (todo.endDate != null) {
                    val enddate = serverDateFormat.format(today)

                    when(todo.repeatOption){
                        "매일"->{
                            today = FormatDate.nextStartDate(enddate, todo.repeatEnd)
                        }

                        "매주"->{
                            today = FormatDate.nextStartDateEveryWeek(todo.repeatValue!!, 1, enddate, todo.repeatEnd)
                        }

                        "격주"->{
                            today = FormatDate.nextStartDateEveryWeek(todo.repeatValue!!, 2, enddate, todo.repeatEnd)
                        }

                        "매달"->{
                            today = FormatDate.nextStartDateEveryMonth(todo.repeatValue!!, enddate, todo.repeatEnd)
                        }

                        "매년"->{
                            today = FormatDate.nextStartDateEveryYear(todo.repeatValue!!, enddate, todo.repeatEnd)
                        }
                    }

                    if (end.year == frontOk.year && end.month == frontOk.month && end.date == frontOk.date) {
                        if(today == null){
                            calendarViewModel.completeNotRepeatTodo(todo.id, Completed(!todo.completed)){
                                todos[position].completed = !todos[position].completed
                                notifyItemChanged(position)
                                adapter.notifyDataSetChanged()
                            }

                            return@setOnClickListener
                        }

                        val nextStartDay = todayDateFormat.format(today)

                        calendarViewModel.completeRepeatFrontTodo(todo.id, FrontEndDate(nextStartDay)){
                            todos[position].completed = !todos[position].completed
                            notifyItemChanged(position)
                            adapter.notifyDataSetChanged()
                        }
                        return@setOnClickListener
                    }

                    if (today == null) {
                        today = todayDateFormat.parse(todayTodo)

                        today.hours = end.hours
                        today.minutes = end.minutes
                        today.seconds = end.seconds

                        val preStartDay = FormatDate.preStartDate(
                            serverDateFormat.format(today),
                            todo.repeatOption,
                            todo.repeatValue
                        )

                        calendarViewModel.completeRepeatBackTodo(todo.id,
                            BackCompleteEndDate(
                                todayDateFormat.format(preStartDay)
                            )
                        ){
                            todos[position].completed = !todos[position].completed
                            notifyItemChanged(position)
                            adapter.notifyDataSetChanged()
                        }
                        return@setOnClickListener
                    }

                    val completedDay = todayDateFormat.parse(todayTodo)
                    completedDay.hours = end.hours
                    completedDay.minutes = end.minutes
                    completedDay.seconds = end.seconds

                    calendarViewModel.completeRepeatMiddleTodo(
                        todo.id,
                        MiddleCompleteEndDate(
                            todayDateFormat.format(completedDay),
                            todayDateFormat.format(today)
                        )
                    ){
                        todos[position].completed = !todos[position].completed
                        notifyItemChanged(position)
                        adapter.notifyDataSetChanged()
                    }
                }
            } else {
                calendarViewModel.completeNotRepeatTodo(todo.id, Completed(!todo.completed)){
                    todos[position].completed = !todos[position].completed
                    notifyItemChanged(position)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        detailTodoFlagImv.setOnClickListener {
            val checkListViewModel = CheckListViewModel()
            checkListViewModel.updateFlag(Flag(!todo.flag), todo.id) { flag, successData ->}

            todos[position].flag = !todos[position].flag

            notifyItemChanged(position)
        }

        todoCorrectionBtn.setOnClickListener {
            val checklistviewmodel = CheckListViewModel()

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
                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            ChecklistItemFragment(checklistviewmodel, todo.id, todo)
                        )
                        .addToBackStack(null)
                        .commit()

                    dialog.dismiss()
                    return@setOnClickListener
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
                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            ChecklistItemFragment(checklistviewmodel, todo.id, todo)
                        )
                        .addToBackStack(null)
                        .commit()

                    dialog.dismiss()
                    return@setOnClickListener
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

                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            ChecklistItemFragment(checklistviewmodel, todo.id, todo)
                        )
                        .addToBackStack(null)
                        .commit()

                    dialog.dismiss()
                    return@setOnClickListener
                }
            }

            if (todo.repeatOption != null) {
                todo.location = 1 // middle
                todo.endDate = todoendDate

                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragments_frame,
                        ChecklistItemFragment(checklistviewmodel, todo.id, todo)
                    )
                    .addToBackStack(null)
                    .commit()

                dialog.dismiss()
                return@setOnClickListener
            }

            activity.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragments_frame,
                    ChecklistItemFragment(checklistviewmodel, todo.id, todo)
                )
                .addToBackStack(null)
                .commit()

            dialog.dismiss()
        }
    }
}
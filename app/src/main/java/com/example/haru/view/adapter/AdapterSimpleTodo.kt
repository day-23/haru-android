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
    val todos: List<Todo>,
    val activity: FragmentActivity,
    val todayTodo: String,
    val dialog: Dialog
) : RecyclerView.Adapter<AdapterSimpleTodo.DetailView>() {
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)
    val format = SimpleDateFormat("yyyy-MM-dd")

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
            detailTodoContentTv.setPaintFlags(detailTodoContentTv.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            detailTodoContentTv.setTextColor(Color.parseColor("#F1F1F5"))
            detailTodoTagsTv.setTextColor(Color.parseColor("#F1F1F5"))
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
        }

        detailTodoComplete.setOnClickListener {
            val calendarViewModel = CalendarViewModel()

            if(todo.repeatOption != null){
                if (todo.endDate != null && todo.repeatOption != null) {
                    val end = serverDateFormat.parse(todo.endDate)
                    val today = todayDateFormat.parse(todayTodo)

                    if (end.year == today.year && end.month == today.month && end.date == today.date) {
                        calendarViewModel.completeRepeatFrontTodo(todo.id, FrontEndDate(todayTodo)){}
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


                        calendarViewModel.completeRepeatBackTodo(todo.id, BackCompleteEndDate(todayTodo)){}
                        return@setOnClickListener
                    }

                    calendarViewModel.completeRepeatMiddleTodo(todo.id, MiddleCompleteEndDate(todayTodo, todayDateFormat.format(nextData))){}
                }
            } else {
                calendarViewModel.completeNotRepeatTodo(todo.id, Completed(!todo.completed)){}

                todos[position].completed = !todos[position].completed
                notifyItemChanged(position)
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

            Log.d("todoLocation", todo.toString())

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
                    Log.d("todoLocation", "front")
                    todo.location = 0 // front
                    Log.d("20191630", "front")
                    Log.d("20191630", todo.endDate.toString())

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
                    Log.d("20191630", "location 예외처리 항목")

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
                    Log.d("todoLocation", "back")
                    todo.endDate = todoendDate
                    Log.d("20191630", "back")
                    Log.d("20191630", todo.endDate.toString())

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
                Log.d("todoLocation", "middle")
                todo.location = 1 // middle
                todo.endDate = todoendDate
                Log.d("20191630", "middle")
                Log.d("20191630", todo.endDate.toString())

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
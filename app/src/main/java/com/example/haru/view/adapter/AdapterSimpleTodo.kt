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
import com.example.haru.data.model.EndDate
import com.example.haru.data.model.Flag
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.Todo
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import java.text.SimpleDateFormat
import java.util.*

class AdapterSimpleTodo(val todos: List<Todo>,
                        val activity: FragmentActivity,
                        val todayTodo: String,
                        val dialog: Dialog) : RecyclerView.Adapter<AdapterSimpleTodo.DetailView>(){
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSimpleTodo.DetailView {
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
        val detailTodoContentTv = holder.itemView.findViewById<TextView>(R.id.detail_todo_content_tv)
        val detailTodoTagsTv = holder.itemView.findViewById<TextView>(R.id.detail_todo_tags_tv)
        val detailTodoFlagImv = holder.itemView.findViewById<ImageView>(R.id.detail_todo_flag_imv)
        val todoCorrectionBtn = holder.itemView.findViewById<LinearLayout>(R.id.todo_correction_btn)

        if(todo.completed) {
            detailTodoComplete.setBackgroundResource(R.drawable.circle_check)
            detailTodoContentTv.setPaintFlags(detailTodoContentTv.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            detailTodoContentTv.setTextColor(Color.parseColor("#F1F1F5"))
            detailTodoTagsTv.setTextColor(Color.parseColor("#F1F1F5"))
        }

        detailTodoContentTv.text = todo.content

        var tag = ""

        for(i in todo.tags){
            tag += i.content+" "
        }

        if(tag.length > 0) tag = tag.dropLast(0)

        if(todo.flag){
            detailTodoFlagImv.setBackgroundResource(R.drawable.star_check)
        }

        detailTodoComplete.setOnClickListener {

        }

        detailTodoFlagImv.setOnClickListener {
            val checkListViewModel = CheckListViewModel()
            checkListViewModel.updateFlag(Flag(!todo.flag), todo.id)

            todos[position].flag = !todos[position].flag

            notifyItemChanged(position)
        }

        todoCorrectionBtn.setOnClickListener {
            val checklistviewmodel = CheckListViewModel()

            if(todo.repeatEnd != null && todo.endDate != null){
                if(todo.repeatEnd == todo.endDate ){
                    todo.repeatValue = null
                    todo.repeatOption = null

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

            if(todo.endDate != null && todo.repeatOption != null){
                val calendar = Calendar.getInstance()
                calendar.time = serverDateFormat.parse(todo.endDate)
                calendar.add(Calendar.HOUR, 9)
                val end = calendar.time
                val today = todayDateFormat.parse(todayTodo)

                if(end.year == today.year && end.month == today.month && end.date == today.date){
                    Log.d("todoLocation", "front")
                    todo.location = 0

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
                val calendar = Calendar.getInstance()
                calendar.time = serverDateFormat.parse(todo.repeatEnd)
                calendar.add(Calendar.HOUR, 9)

                val end = calendar.time
                val today = todayDateFormat.parse(todayTodo)

                if(end.year == today.year && end.month == today.month && end.date == today.date){
                    todo.location = 2

                    Log.d("todoLocation", "end")

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

            if(todo.repeatOption != null){
                todo.location = 2

                Log.d("todoLocation", "middle")

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

            Log.d("todoLocation", "단일투두")

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
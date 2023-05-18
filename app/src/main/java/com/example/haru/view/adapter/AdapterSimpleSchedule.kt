package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.Schedule
import com.example.haru.utils.FormatDate
import com.example.haru.view.calendar.CalendarItemFragment
import com.example.haru.view.checklist.ChecklistItemFragment
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class AdapterSimpleSchedule(val schedules: List<Schedule>,
                            val activity: FragmentActivity,
                            val todayTodo: String,
                            val dialog: Dialog,
                            val categories: List<Category?>
) : RecyclerView.Adapter<AdapterSimpleSchedule.DetailView>(){
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREAN)
    val todayDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSimpleSchedule.DetailView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_simple_schedule,
            parent,
            false
        )

        return DetailView(view)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onBindViewHolder(holder: DetailView, position: Int) {
        val schedule = schedules[position]

        val detailScheduleCategoryImv = holder.itemView.findViewById<ImageView>(R.id.detail_schedule_category_imv)
        val detailScheduleContentTv = holder.itemView.findViewById<TextView>(R.id.detail_schedule_content_tv)
        val detailScheduleTimeTv = holder.itemView.findViewById<TextView>(R.id.detail_schedule_time_tv)

        if(schedule.isAllDay){
            detailScheduleTimeTv.text = "하루종일"
        }

        if(schedule.category == null){
            val drawable = detailScheduleCategoryImv.background as VectorDrawable
            drawable.setColorFilter(Color.parseColor("#1DAFFF"), PorterDuff.Mode.SRC_ATOP)
        } else {
            val drawable = detailScheduleCategoryImv.background as VectorDrawable
            drawable.setColorFilter(Color.parseColor(schedule.category.color), PorterDuff.Mode.SRC_ATOP)
        }

        holder.itemView.setOnClickListener {
            if (schedule.repeatOption == null) {
                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragments_frame,
                        CalendarItemFragment(schedule, categories)
                    )
                    .addToBackStack(null)
                    .commit()

                dialog.dismiss()
                return@setOnClickListener
            } else{
                val startDate = serverDateFormat.parse(schedule.repeatStart)
                val today = todayDateFormat.parse(todayTodo)

                if (startDate.year == today.year &&
                    startDate.month == today.month &&
                    startDate.date == today.date
                ) {
                    schedule.location = 0
                    Log.d("20191630", "스케줄 프론트")

                    activity.supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragments_frame,
                            CalendarItemFragment(schedule, categories)
                        )
                        .addToBackStack(null)
                        .commit()

                    dialog.dismiss()
                    return@setOnClickListener
                }

//                if ()

                schedule.location = 1
                Log.d("20191630","스케줄 미들")

                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragments_frame,
                        CalendarItemFragment(schedule, categories)
                    )
                    .addToBackStack(null)
                    .commit()

                dialog.dismiss()
                return@setOnClickListener
            }
        }

        detailScheduleContentTv.text = schedule.content
    }
}
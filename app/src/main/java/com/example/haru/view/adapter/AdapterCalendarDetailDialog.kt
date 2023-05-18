package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.PostSchedule
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class AdapterCalendarDetailDialog(val lifecycleOwner: LifecycleOwner,
                                  val thisViewpager:ViewPager2,
                                  val startDate:Date,
                                  val adapter:AdapterMonth,
                                  val activity: FragmentActivity,
                                  val dialog: Dialog,
                                  val categories: List<Category?>
                                  ) : RecyclerView.Adapter<AdapterCalendarDetailDialog.DetailView>(){
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCalendarDetailDialog.DetailView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_detail_dialog,
            parent,
            false
        )

        return DetailView(view)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: DetailView, position: Int) {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

        val miniBackImv = holder.itemView.findViewById<ImageView>(R.id.mini_back_imv)
        val miniNextImv = holder.itemView.findViewById<ImageView>(R.id.mini_next_imv)
        val detailScheduleRecyclerView = holder.itemView.findViewById<RecyclerView>(R.id.detail_schedule_recyclerview)
        val detailTodoRecyclerView = holder.itemView.findViewById<RecyclerView>(R.id.detail_todo_recyclerview)
        val detailDayTv = holder.itemView.findViewById<TextView>(R.id.detail_day_tv)
        val simpleScheduleInputEt = holder.itemView.findViewById<EditText>(R.id.simple_schedule_input_et)
        val simpleScheduleBtn = holder.itemView.findViewById<Button>(R.id.simple_schedule_btn)

        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_MONTH, position - Int.MAX_VALUE/2)
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startDate = format.format(calendar.time)+"T00:00:00+09:00"

        detailDayTv.text = calendar.time.date.toString()+"일 " +
                when(calendar.time.day){
                    0 -> "일"
                    1 -> "월"
                    2 -> "화"
                    3 -> "수"
                    4 -> "목"
                    5 -> "금"
                    6 -> "토"
                    else -> "?"
                } + "요일"

        simpleScheduleInputEt.hint = (calendar.time.month+1).toString()+"월 " +
                calendar.time.date.toString() +
                "일 일정 추가"

        simpleScheduleInputEt.addTextChangedListener {
            if(simpleScheduleInputEt.text.length == 0){
                simpleScheduleBtn.visibility = View.INVISIBLE
            } else {
                simpleScheduleBtn.visibility = View.VISIBLE
            }
        }

        simpleScheduleBtn.setOnClickListener {
            val calendarviewmodel = CalendarViewModel()
            calendarviewmodel.postSchedule(
                PostSchedule(
                    simpleScheduleInputEt.text.toString(),
                    "",
                    true,
                    startDate,
                    startDate,
                    null,
                    null,
                    null,
                    emptyList()
                )
            ){
                notifyItemChanged(position)
                adapter.notifyDataSetChanged()
            }
        }

        val endDate = format.format(calendar.time)+"T23:59:55+09:00"

        miniBackImv.setOnClickListener {
            thisViewpager.setCurrentItem(position-1, true)
        }

        miniNextImv.setOnClickListener {
            thisViewpager.setCurrentItem(position+1, true)
        }

        val calendarViewModel = CalendarViewModel()

        calendarViewModel.getAlldoDay(startDate, endDate)

        calendarViewModel.liveTodoList.observe(lifecycleOwner) {livetodo->
            calendarViewModel.liveScheduleList.observe(lifecycleOwner){liveschedule->
                detailScheduleRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                detailScheduleRecyclerView.adapter = AdapterSimpleSchedule(liveschedule,activity, startDate, dialog, categories)

                detailTodoRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                detailTodoRecyclerView.adapter = AdapterSimpleTodo(livetodo,activity, startDate, dialog)
            }
        }
    }
}
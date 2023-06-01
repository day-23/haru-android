package com.example.haru.view.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Schedule
import com.example.haru.databinding.FragmentSearchScheduleHeaderBinding
import com.example.haru.databinding.ListItemSimpleScheduleBinding

class SearchScheduleAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val header = 0
    private val item = 1

    private val diffCallback = object : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int): Int = diffUtil.currentList[position].searchType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            header -> HeaderViewHolder(
                FragmentSearchScheduleHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            item -> ScheduleViewHolder(
                ListItemSimpleScheduleBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int = diffUtil.currentList.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val schedule = diffUtil.currentList[position]

        when (holder) {
            is ScheduleViewHolder -> holder.bind(schedule)
            is HeaderViewHolder -> {}
        }
    }

    inner class ScheduleViewHolder(val binding : ListItemSimpleScheduleBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(item : Schedule){
                    Log.e("20191627", "$item")
                    if (item.isAllDay)
                        binding.detailScheduleTimeTv.text = "하루종일"
                    else{
//                        var scheduleTime = ""
//                        scheduleTime += (item.startTime!!.month+1).toString()+"월 " + item.startTime!!.date.toString()+"일 "
//                        scheduleTime += if(item.startTime!!.hours < 12) "오전 " else "오후 "
//                        scheduleTime +=
//                            if (item.startTime!!.hours == 0 || item.startTime!!.hours == 12) "12"
//                            else if (item.startTime!!.hours > 12) (item.startTime!!.hours-12).toString()
//                            else item.startTime!!.hours.toString()
//
//                        scheduleTime += ":"
//                        scheduleTime +=
//                            if (item.startTime!!.minutes < 10) "0"+item.startTime!!.minutes.toString()
//                            else item.startTime!!.minutes.toString()
//
//                        scheduleTime += " - "
//
//                        scheduleTime += (item.endTime!!.month+1).toString()+"월 " + item.endTime!!.date.toString()+"일 "
//                        scheduleTime += if(item.endTime!!.hours < 12) "오전 " else "오후 "
//                        scheduleTime +=
//                            if (item.endTime!!.hours == 0 || item.endTime!!.hours == 12) "12"
//                            else if (item.endTime!!.hours > 12) (item.endTime!!.hours-12).toString()
//                            else item.endTime!!.hours.toString()
//
//                        scheduleTime += ":"
//                        scheduleTime +=
//                            if (item.endTime!!.minutes < 10) "0"+item.endTime!!.minutes.toString()
//                            else item.endTime!!.minutes.toString()

                        binding.detailScheduleTimeTv.text = "test"
//                            scheduleTime
                    }

                    if(item.category == null){
                        val drawable = binding.detailScheduleCategoryImv.background as VectorDrawable
                        drawable.setColorFilter(Color.parseColor("#1DAFFF"), PorterDuff.Mode.SRC_ATOP)
                    } else {
                        val drawable = binding.detailScheduleCategoryImv.background as VectorDrawable
                        drawable.setColorFilter(Color.parseColor(item.category.color), PorterDuff.Mode.SRC_ATOP)
                    }
                }
            }
    inner class HeaderViewHolder(val binding : FragmentSearchScheduleHeaderBinding) :
            RecyclerView.ViewHolder(binding.root) {}

    fun setDataList(dataList : List<Schedule>?){
        if (dataList == null)
            return
        diffUtil.submitList(dataList as MutableList<Schedule>)
    }
}
package com.example.haru.view.adapter

import android.content.Context
import android.content.res.ColorStateList
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
import com.example.haru.databinding.FragmentSearchScheduleBinding
import com.example.haru.databinding.FragmentSearchScheduleHeaderBinding
import com.example.haru.databinding.ListItemSimpleScheduleBinding
import com.example.haru.utils.FormatDate
import java.util.*

class SearchScheduleAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return when (viewType) {
            header -> HeaderViewHolder(
                FragmentSearchScheduleHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            item -> ScheduleViewHolder(
                FragmentSearchScheduleBinding.inflate(
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

    inner class ScheduleViewHolder(val binding: FragmentSearchScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Schedule) {
            Log.e("20191627", "$item")

            binding.tvScheduleContent.text = item.content

            if (item.isAllDay)
                binding.tvScheduleTime.text = "하루종일"
            else {
                val startDate: Date
                val endDate: Date
                if (item.repeatOption == null && item.repeatValue == null) {
                    // repeatOption, repeatValue 가 null이면 반복하지 않는 일정
                    startDate = FormatDate.strToDate(item.repeatStart)!!
                    endDate = FormatDate.strToDate(item.repeatEnd)!!
                } else { // 둘중 하나라도 null이 아니면 반복하는 일정
                    val today = Date()
                    if (item.repeatValue?.contains('T') == true) {
                        // interval을 가지는 반복하는 일정
                        val interval = item.repeatValue.substring(1).toInt()
                        val rangeDate = FormatDate.getIntervalDate(
                            interval,
                            item.repeatStart!!,
                            item.repeatEnd!!,
                            item.repeatOption!!,
                            today
                        )
                        startDate = rangeDate.first
                        endDate = rangeDate.second
                    } else {
                        // 반복하는 일정인데 인터벌이 아닌경우
                        when (item.repeatOption) {
                            "매일" -> {}
                            "매주" -> {}
                            "격주" -> {}
                            "매달" -> {}
                            "매년" -> {}
                        }
                        startDate = Date()
                        endDate = Date()
                    }
                }

                val startDateStr = FormatDate.simpleCalendarToStr(startDate)
                val endDateStr = FormatDate.simpleCalendarToStr(endDate)
                binding.tvScheduleTime.text = "$startDateStr - $endDateStr"

            }

            if (item.category == null) {
                binding.ivCategoryColor.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#1DAFFF"))
            } else {
                binding.ivCategoryColor.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(item.category.color))

            }
        }
    }

    inner class HeaderViewHolder(val binding: FragmentSearchScheduleHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    fun setDataList(dataList: List<Schedule>?) {
        if (dataList == null)
            return
        Log.e("20191627", dataList.toString())
        diffUtil.submitList(dataList as MutableList<Schedule>)
    }
}
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
                if (item.repeatOption == null && item.repeatValue == ""){
                    val startDate = FormatDate.strToDate(item.repeatStart)!!
                    val endDate = FormatDate.strToDate(item.repeatEnd)!!

                    val startDateStr = FormatDate.simpleCalendarToStr(startDate)
                    val endDateStr = FormatDate.simpleCalendarToStr(endDate)
                    binding.tvScheduleTime.text = "$startDateStr - $endDateStr"
                } else binding.tvScheduleTime.text = "test"

            }

            if (item.category == null) {
                binding.ivCategoryColor.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#1DAFFF"))
//                        val drawable = binding.ivCategoryColor.background as VectorDrawable
//                        drawable.setColorFilter(Color.parseColor("#1DAFFF"), PorterDuff.Mode.SRC_ATOP)
            } else {
                binding.ivCategoryColor.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(item.category.color))
//                        val drawable = binding.ivCategoryColor.background as VectorDrawable
//                        drawable.setColorFilter(Color.parseColor(item.category.color), PorterDuff.Mode.SRC_ATOP)
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
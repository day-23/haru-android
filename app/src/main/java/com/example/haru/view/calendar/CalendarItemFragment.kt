package com.example.haru.view.calendar

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.data.model.Schedule
import com.example.haru.databinding.FragmentCalendarItemBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemFragment(val schedule: Schedule) : Fragment() {
    private lateinit var binding: FragmentCalendarItemBinding

    private val calendarDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA)
    private val dateformat = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarItemBinding.inflate(inflater)

        initView()

        return binding.root
    }

    fun initView(){
        //내용 가져오기
        binding.scheduleContentEt.setText(schedule.content)

        //카테고리 가져오기
        val drawable = binding.categoryChooseIv.background as VectorDrawable

        if(schedule.category == null){
            drawable.setColorFilter(Color.parseColor("#1DAFFF"), PorterDuff.Mode.SRC_ATOP)
        } else {
            drawable.setColorFilter(Color.parseColor(schedule.category.color), PorterDuff.Mode.SRC_ATOP)
        }

        //하루종일 확인 후 반복 구간 설정
        if(schedule.isAllDay){
            binding.alldaySwitch.isChecked = true

            binding.alldayIv.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.alldayTv.setTextColor(Color.parseColor("#191919"))

            binding.repeatStartTimeBtn.visibility = View.GONE
            binding.repeatEndTimeBtn.visibility = View.GONE
        }

        //반복 설정이면서
        if(schedule.repeatOption != null && schedule.repeatValue != null) {
            binding.repeatSwitchSchedule.isChecked = true
            binding.repeatEndLayout.visibility = View.VISIBLE
            //구간이 이틀 이상 연속이라 repeatValue에 인터벌 값으로 들어갔을 때
            if(!schedule.repeatValue.contains("T")) {
                val calendar = Calendar.getInstance()
                val repeatstart = calendarDateFormatter.parse(schedule.repeatStart)
                calendar.time = repeatstart

                calendar.add(Calendar.MILLISECOND, schedule.repeatValue.replace("T","").toInt())

                val repeatend = calendar.time
                repeatend.year += 1900

                val repeatendcontent = calendarDateFormatter.parse(schedule.repeatEnd)

                if (repeatendcontent.year < 2100) {
                    binding.repeatEndDateSwitchSchedule.isChecked = true
                    binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#191919"))
                    binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE

                    val trulyRepeatEnd = calendarDateFormatter.parse(schedule.repeatEnd)
                    binding.btnRepeatEndDateSchedule.text = dateformat.format(trulyRepeatEnd)
                }

                binding.repeatStartDateBtn.text = dateformat.format(repeatstart)
                binding.repeatEndDateBtn.text = dateformat.format(repeatend)
            } else { // 반복이지만 하루 일정일 때
                val repeatstart = calendarDateFormatter.parse(schedule.repeatStart!!)
                val repeatend = calendarDateFormatter.parse(schedule.repeatEnd!!)

                binding.repeatStartDateBtn.text = dateformat.format(repeatstart)
                binding.repeatEndDateBtn.text = dateformat.format(repeatstart)

                if(repeatend.year < 2100) {
                    binding.repeatEndDateSwitchSchedule.isChecked = true
                    binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#191919"))
                    binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE

                    binding.btnRepeatEndDateSchedule.text = dateformat.format(repeatend)
                }
            }
        } else {// 반복 일정이 아닐 때
            val repeatstart = calendarDateFormatter.parse(schedule.repeatStart!!)
            val repeatend = calendarDateFormatter.parse(schedule.repeatEnd!!)

            binding.repeatStartDateBtn.text = dateformat.format(repeatstart)
            binding.repeatEndDateBtn.text = dateformat.format(repeatend)
        }

        //알람이 1개 이상 존재할 때
        if(schedule.alarms.size > 0){
            binding.alarmSwitchSchedule.isChecked = true

            binding.alarmIv.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.alarmTv.setTextColor(Color.parseColor("#191919"))
        }

        //반복이 설정되어 있을 때
        if(schedule.repeatOption != null){
            binding.repeatSwitchSchedule.isChecked = true

            binding.repeatIvSchedule.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.repeatTvSchedule.setTextColor(Color.parseColor("#191919"))

            when(schedule.repeatOption){
                "매일"->{

                }

                "매주"->{

                }

                "격주"->{

                }

                "매달"->{

                }

                "매년"->{

                }
            }
        }

        if(schedule.memo != null && schedule.memo != ""){
            binding.ivMemoIcon.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
        }
    }
}
package com.example.haru.view.calendar

import BaseActivity
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.GridLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentCalendarItemBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.view.customDialog.CustomTimeDialog
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemFragment(
    val schedule: Schedule,
    var todayDate: Date
) : Fragment() {
    private lateinit var binding: FragmentCalendarItemBinding

    init {
        Log.e("20191627", schedule.toString())
    }

    interface SearchCallback {
        fun updateCallback(callback: () -> Unit)
    }

    var searchCallback: SearchCallback? = null


    private val serverFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREAN)

    private val calendarDateFormatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA)
    private val dateformat = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)
    val timeParser = SimpleDateFormat("a h:mm", Locale.KOREAN)

    private val repeatStartCalendar = Calendar.getInstance()
    private val repeatEndCalendar = Calendar.getInstance()

    private var category: Category? = schedule.category

    private var initIsAllday = false
    private var isAllday = false

    private var repeatOption = -1

    private var initRepeatSwitch = false

    private var weeksValue = arrayListOf(
        false, false, false, false, false, false, false
    )

    private var monthsValue = arrayListOf(
        false, false, false, false, false, false, false,
        false, false, false, false, false, false, false,
        false, false, false, false, false, false, false,
        false, false, false, false, false, false, false,
        false, false, false
    )

    private var yearsValue = arrayListOf(
        false, false, false, false, false, false, false,
        false, false, false, false, false
    )

    private var initStartDate = ""
    private var initEndDate = ""
    private var initStartTime = ""
    private var initEndTime = ""

    fun dateDifference(d1: Date, d2: Date): Int {
        // Create calendar objects
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar1.time = d1
        calendar2.time = d2

        // Set the dates for the calendar objects
        calendar1.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        calendar2.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val differenceInMillis = Math.abs(calendar2.timeInMillis - calendar1.timeInMillis)

        val differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24)

        return differenceInDays.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivity.hideNavi(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        MainActivity.hideNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarItemBinding.inflate(inflater)

        initView()
        touchEvent()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.calendarAddFragmentParentLayout.id)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.calendarAddFragmentParentLayout.id)
    }

    fun optionChange() {
        binding.btnEveryDaySchedule.visibility = View.VISIBLE

        if (repeatOption == 1 || repeatOption == 2) {
            binding.everyWeekLayout.visibility = View.VISIBLE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        } else if (repeatOption == 3) {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.VISIBLE
            binding.gridYearSchedule.visibility = View.GONE
        } else if (repeatOption == 4) {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.VISIBLE
        } else {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        }
    }

    fun onOptionClick(opt: Int) {
        for (i in 0 until binding.repeatOptionSelectSchedule.childCount) {
            if (i == opt)
                binding.repeatOptionSelectSchedule.getChildAt(i).backgroundTintList = null
            else
                binding.repeatOptionSelectSchedule.getChildAt(i).backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.transparent
                        )
                    )
        }

        if (opt == 1 || opt == 2) {
            if (binding.repeatStartDateBtn.text == binding.repeatEndDateBtn.text) {
                binding.everyWeekLayout.visibility = View.VISIBLE
            }

            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        } else if (opt == 3) {
            binding.everyWeekLayout.visibility = View.GONE

            if (binding.repeatStartDateBtn.text == binding.repeatEndDateBtn.text) {
                binding.gridMonthSchedule.visibility = View.VISIBLE
            }

            binding.gridYearSchedule.visibility = View.GONE
        } else if (opt == 4) {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE

            if (binding.repeatStartDateBtn.text == binding.repeatEndDateBtn.text) {
                binding.gridYearSchedule.visibility = View.VISIBLE
            }
        } else {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        }

        repeatOption = opt
    }

    @SuppressLint("ResourceAsColor")
    fun touchEvent() {
        val dateParser = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)

        binding.repeatStartDateBtn.setOnClickListener {
            val datePicker = CustomCalendarDialog(repeatStartCalendar.time)
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        repeatStartCalendar.set(year, month, day)

                        binding.repeatStartDateBtn.text =
                            dateParser.format(repeatStartCalendar.time)

                        if (binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()) {
                            var flag = false
                            if (repeatStartCalendar.time.after(repeatEndCalendar.time)) {
                                repeatEndCalendar.set(year, month, day)
                                binding.repeatEndDateBtn.text =
                                    dateParser.format(repeatEndCalendar.time)
                                flag = true
                            }

                            val repeatEndDateText =
                                binding.btnRepeatEndDateSchedule.text.toString().substring(
                                    0,
                                    binding.btnRepeatEndDateSchedule.text.toString().length - 2
                                )

                            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                            val repeatEndDateCalendar = Calendar.getInstance()
                            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

                            if (repeatStartCalendar.time.month != repeatEndCalendar.time.month) {
                                binding.gridMonthSchedule.visibility = View.GONE
                                binding.btnEveryMonthSchedule.visibility = View.GONE
                                if (repeatOption == -3) repeatOption = -1
                            } else {
                                binding.btnEveryMonthSchedule.visibility = View.VISIBLE
                            }

                            val startDate = repeatStartCalendar.time.clone() as Date
                            startDate.hours = 0
                            startDate.minutes = 0
                            startDate.seconds = 0

                            val endDate = repeatEndCalendar.time.clone() as Date
                            endDate.hours = 0
                            endDate.minutes = 0
                            endDate.seconds = 0

                            //일주일 이상 차이나면 반복 해제
                            if ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24) > 6) {
                                binding.repeatSwitchSchedule.isChecked = false
                                binding.repeatSwitchSchedule.isClickable = false

                                binding.repeatTvSchedule.setTextColor(Color.LTGRAY)
                                binding.repeatIvSchedule.backgroundTintList =
                                    ColorStateList.valueOf(Color.LTGRAY)

                                binding.repeatOptionSelectSchedule.visibility = View.GONE
                                binding.repeatEndLayout.visibility = View.GONE
                                binding.everyWeekLayout.visibility = View.GONE
                                binding.gridMonthSchedule.visibility = View.GONE
                                binding.gridYearSchedule.visibility = View.GONE
                                repeatOption = -1
                                return
                            } else {
                                binding.repeatSwitchSchedule.isClickable = true
                            }

                            if (repeatStartCalendar.time.after(repeatEndDateCalendar.time)) {
                                binding.btnRepeatEndDateSchedule.text =
                                    dateParser.format(repeatStartCalendar.time)
                            }

                            if (flag) {
                                optionChange()
                                return
                            }

                            if (repeatOption == 0) repeatOption = -1

                            binding.btnEveryDaySchedule.visibility = View.GONE
                            binding.everyWeekLayout.visibility = View.GONE
                            binding.gridMonthSchedule.visibility = View.GONE
                            binding.gridYearSchedule.visibility = View.GONE
                        } else {
                            optionChange()
                        }
                    }
                }
            datePicker.show(parentFragmentManager, null)
        }



        binding.repeatStartTimeBtn.setOnClickListener {
            val timePicker = CustomTimeDialog(repeatStartCalendar.time)
            timePicker.timePickerClick = object : CustomTimeDialog.TimePickerClickListener {
                override fun onClick(
                    timeDivider: NumberPicker,
                    hourNumberPicker: NumberPicker,
                    minuteNumberPicker: NumberPicker
                ) {
                    val timeDivision = timeDivider.value
                    var hour = hourNumberPicker.value
                    val minute = minuteNumberPicker.value
                    if (timeDivision == 0) {
                        if (hour == 11)
                            hour = 0
                        else hour++
                    } else {
                        if (hour == 11)
                            hour++
                        else hour += 13
                    }

                    repeatStartCalendar.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute * 5)
                    }

                    binding.repeatStartTimeBtn.text = timeParser.format(repeatStartCalendar.time)
                }
            }
            timePicker.show(parentFragmentManager, null)
        }



        binding.repeatEndDateBtn.setOnClickListener {
            val datePicker = CustomCalendarDialog(repeatEndCalendar.time, repeatStartCalendar.time)
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        repeatEndCalendar.set(year, month, day)

                        binding.repeatEndDateBtn.text = dateParser.format(repeatEndCalendar.time)

                        if (binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()) {
                            val repeatEndDateText =
                                binding.btnRepeatEndDateSchedule.text.toString().substring(
                                    0,
                                    binding.btnRepeatEndDateSchedule.text.toString().length - 2
                                )

                            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                            val repeatEndDateCalendar = Calendar.getInstance()
                            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

                            if (repeatEndCalendar.time.after(repeatEndDateCalendar.time)) {
                                binding.btnRepeatEndDateSchedule.text =
                                    dateParser.format(repeatEndCalendar.time)
                            }

                            if (repeatStartCalendar.time.month != repeatEndCalendar.time.month) {
                                binding.gridMonthSchedule.visibility = View.GONE
                                binding.btnEveryMonthSchedule.visibility = View.GONE
                                if (repeatOption == -3) repeatOption = -1
                            } else {
                                binding.btnEveryMonthSchedule.visibility = View.VISIBLE
                            }

                            val startDate = repeatStartCalendar.time.clone() as Date
                            startDate.hours = 0
                            startDate.minutes = 0
                            startDate.seconds = 0

                            val endDate = repeatEndCalendar.time.clone() as Date
                            endDate.hours = 0
                            endDate.minutes = 0
                            endDate.seconds = 0

                            //일주일 이상 차이나면 반복 해제
                            if ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24) > 6) {
                                binding.repeatSwitchSchedule.isChecked = false
                                binding.repeatSwitchSchedule.isClickable = false

                                binding.repeatTvSchedule.setTextColor(Color.LTGRAY)
                                binding.repeatIvSchedule.backgroundTintList =
                                    ColorStateList.valueOf(Color.LTGRAY)

                                binding.repeatOptionSelectSchedule.visibility = View.GONE
                                binding.repeatEndLayout.visibility = View.GONE
                                binding.everyWeekLayout.visibility = View.GONE
                                binding.gridMonthSchedule.visibility = View.GONE
                                binding.gridYearSchedule.visibility = View.GONE
                                repeatOption = -1
                                return
                            } else {
                                binding.repeatSwitchSchedule.isClickable = true
                            }

                            if (repeatOption == 0) repeatOption = -1

                            binding.btnEveryDaySchedule.visibility = View.GONE
                            binding.everyWeekLayout.visibility = View.GONE
                            binding.gridMonthSchedule.visibility = View.GONE
                            binding.gridYearSchedule.visibility = View.GONE
                        } else {
                            optionChange()
                        }
                    }
                }
            datePicker.show(parentFragmentManager, null)
        }



        binding.repeatEndTimeBtn.setOnClickListener {
            val timePicker = CustomTimeDialog(repeatEndCalendar.time)
            timePicker.timePickerClick = object : CustomTimeDialog.TimePickerClickListener {
                override fun onClick(
                    timeDivider: NumberPicker,
                    hourNumberPicker: NumberPicker,
                    minuteNumberPicker: NumberPicker
                ) {
                    val timeDivision = timeDivider.value
                    var hour = hourNumberPicker.value
                    val minute = minuteNumberPicker.value
                    if (timeDivision == 0) {
                        if (hour == 11)
                            hour = 0
                        else hour++
                    } else {
                        if (hour == 11)
                            hour++
                        else hour += 13
                    }

                    repeatEndCalendar.apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute * 5)
                    }

                    binding.repeatEndTimeBtn.text = timeParser.format(repeatEndCalendar.time)
                }
            }
            timePicker.show(parentFragmentManager, null)
        }

        binding.btnCloseSchedule.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        binding.alldaySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAllday = true
                binding.alldayIv.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))
                binding.alldayTv.setTextColor(Color.parseColor("#191919"))

                binding.repeatStartTimeBtn.visibility = View.GONE
                binding.repeatEndTimeBtn.visibility = View.GONE
            } else {
                isAllday = false
                binding.alldayIv.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)
                binding.alldayTv.setTextColor(Color.LTGRAY)

                binding.repeatStartTimeBtn.visibility = View.VISIBLE
                binding.repeatEndTimeBtn.visibility = View.VISIBLE
            }
        }



        binding.repeatSwitchSchedule.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.repeatTvSchedule.setTextColor(Color.parseColor("#191919"))
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))

                binding.repeatOptionSelectSchedule.visibility = View.VISIBLE
                binding.repeatEndLayout.visibility = View.VISIBLE

                if (repeatOption == 1 || repeatOption == 2) {
                    binding.everyWeekLayout.visibility = View.VISIBLE
                } else if (repeatOption == 3) {
                    binding.gridMonthSchedule.visibility = View.VISIBLE
                } else if (repeatOption == 4) {
                    binding.gridYearSchedule.visibility = View.VISIBLE
                }
            } else {
                binding.repeatTvSchedule.setTextColor(Color.LTGRAY)
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)

                binding.repeatOptionSelectSchedule.visibility = View.GONE
                binding.repeatEndLayout.visibility = View.GONE
                binding.everyWeekLayout.visibility = View.GONE
                binding.gridMonthSchedule.visibility = View.GONE
                binding.gridYearSchedule.visibility = View.GONE

                repeatOption = -1
                onOptionClick(-1)
            }
        }



        binding.btnEveryDaySchedule.setOnClickListener { onOptionClick(0) }
        binding.btnEveryWeekSchedule.setOnClickListener { onOptionClick(1) }
        binding.btnEvery2WeekSchedule.setOnClickListener { onOptionClick(2) }
        binding.btnEveryMonthSchedule.setOnClickListener { onOptionClick(3) }
        binding.btnEveryYearSchedule.setOnClickListener { onOptionClick(4) }

        binding.repeatEndDateSwitchSchedule.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#191919"))
                binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE
                binding.btnRepeatEndDateSchedule.text = dateformat.format(repeatEndCalendar.time)
            } else {
                binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#DBDBDB"))
                binding.btnRepeatEndDateSchedule.visibility = View.GONE
            }
        }

        binding.categoryChooseIv.setOnClickListener {
            val dlg = CategoryChooseDialog(null, this) {
                category = it

                val drawable = binding.categoryChooseIv.background as VectorDrawable
                drawable.setColorFilter(Color.parseColor(it.color), PorterDuff.Mode.SRC_ATOP)
            }

            dlg.show(parentFragmentManager, null)
        }



        binding.btnRepeatEndDateSchedule.setOnClickListener {
            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                0,
                binding.btnRepeatEndDateSchedule.text.toString().length - 2
            )

            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
            val repeatEndDateCalendar = Calendar.getInstance()
            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

            val datePicker =
                CustomCalendarDialog(repeatEndDateCalendar.time, repeatEndCalendar.time)
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        binding.btnRepeatEndDateSchedule.text = dateParser.format(calendar.time)
                    }
                }
            datePicker.show(parentFragmentManager, null)
        }

        binding.deleteScheduleTv.setOnClickListener {
            val deleteDialg = DeleteOptionScheduleFragment(schedule.location) {
                val calendarviewmodel = CalendarViewModel()

                when (it) {
                    1 -> {// 이 일정만 삭제
                        when (schedule.location) {
                            0 -> {//front
                                var next = schedule.repeatStart
                                var nextDate: Date? = null

                                when (schedule.repeatOption) {
                                    "매일" -> {
                                        nextDate = FormatDate.nextStartDate(
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            1,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "격주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            2,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매달" -> {
                                        nextDate = FormatDate.nextStartDateEveryMonth(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매년" -> {
                                        nextDate = FormatDate.nextStartDateEveryYear(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }
                                }

                                if (nextDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)
                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            nextDate
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                nextDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                nextDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != nextDate.date) {
                                                    nextDate.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != nextDate.month) {
                                                    nextDate.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != nextDate.date) {
                                                    nextDate.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    calendarviewmodel.deleteFrontSchedule(
                                        schedule.id,
                                        ScheduleFrontDelete(
                                            serverFormatter.format(nextDate)
                                        )
                                    ) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    calendarviewmodel.deleteSchedule(schedule.id) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                }
                            }

                            1 -> {//middle
                                var next = calendarDateFormatter.format(todayDate)
                                var nextDate: Date? = null

                                when (schedule.repeatOption) {
                                    "매일" -> {
                                        nextDate = FormatDate.nextStartDate(
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            1,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "격주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            2,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매달" -> {
                                        nextDate = FormatDate.nextStartDateEveryMonth(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매년" -> {
                                        nextDate = FormatDate.nextStartDateEveryYear(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }
                                }

                                if (nextDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)

                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            nextDate
                                        )

                                        val datediff2 = dateDifference(
                                            repeatStartDate,
                                            todayDate
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                nextDate = cal.time

                                                cal.time = todayDate
                                                cal.add(Calendar.DATE, -(datediff2 % 7))
                                                todayDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                nextDate = cal.time

                                                cal.time = todayDate
                                                cal.add(Calendar.DATE, -(datediff2 % 14))
                                                todayDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != nextDate.date) {
                                                    nextDate.date = repeatStartDate.date
                                                }

                                                if (repeatStartDate.date != todayDate.date) {
                                                    todayDate.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != nextDate.month) {
                                                    nextDate.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.month != todayDate.month) {
                                                    todayDate.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != nextDate.date) {
                                                    nextDate.date = repeatStartDate.date
                                                }

                                                if (repeatStartDate.date != todayDate.date) {
                                                    todayDate.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    todayDate.hours = schedule.startTime!!.hours
                                    todayDate.minutes = schedule.startTime!!.minutes
                                    todayDate.seconds = schedule.startTime!!.seconds

                                    nextDate!!.hours = schedule.startTime!!.hours
                                    nextDate!!.minutes = schedule.startTime!!.minutes
                                    nextDate!!.seconds = schedule.startTime!!.seconds

                                    calendarviewmodel.deleteMiddleSchedule(
                                        schedule.id,
                                        ScheduleMiddleDelete(
                                            serverFormatter.format(todayDate),
                                            serverFormatter.format(nextDate)
                                        )
                                    ) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                }
                            }

                            2 -> {//back
                                var pre = calendarDateFormatter.format(todayDate)
                                var preDate: Date? = null

                                preDate = FormatDate.preStartDate(
                                    pre,
                                    schedule.repeatOption,
                                    schedule.repeatValue
                                )

                                if (preDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)
                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            preDate
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = preDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                preDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = preDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                preDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != preDate.date) {
                                                    preDate.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != preDate.month) {
                                                    preDate.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != preDate.date) {
                                                    preDate.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    preDate!!.hours = schedule.startTime!!.hours
                                    preDate!!.minutes = schedule.startTime!!.minutes
                                    preDate!!.seconds = schedule.startTime!!.seconds

                                    calendarviewmodel.deleteBackSchedule(
                                        schedule.id,
                                        ScheduleBackDelete(
                                            serverFormatter.format(preDate)
                                        )
                                    ) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                }
                            }
                        }
                    }

                    0, 2 -> {// 삭제하기, 전부 삭제하기
                        calendarviewmodel.deleteSchedule(schedule.id) {
                            if (searchCallback != null)
                                searchCallback?.updateCallback {
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                            else requireActivity().supportFragmentManager.popBackStack()
                        }
                    }

                    3 -> {//이후 삭제하기
                        var pre = calendarDateFormatter.format(todayDate)
                        var preDate: Date? = null

                        preDate = FormatDate.preStartDate(
                            pre,
                            schedule.repeatOption,
                            schedule.repeatValue
                        )

                        if (preDate != null) {
                            if (schedule.repeatValue != null && schedule.repeatValue.contains("T")) {
                                val repeatStartDate =
                                    calendarDateFormatter.parse(schedule.repeatStart)
                                val datediff = dateDifference(
                                    repeatStartDate,
                                    preDate
                                )

                                when (schedule.repeatOption) {
                                    "매주" -> {
                                        val cal = Calendar.getInstance()
                                        cal.time = preDate
                                        cal.add(Calendar.DATE, -(datediff % 7))
                                        preDate = cal.time
                                    }

                                    "격주" -> {
                                        val cal = Calendar.getInstance()
                                        cal.time = preDate
                                        cal.add(Calendar.DATE, -(datediff % 14))
                                        preDate = cal.time
                                    }

                                    "매달" -> {
                                        if (repeatStartDate.date != preDate.date) {
                                            preDate.date = repeatStartDate.date
                                        }
                                    }

                                    "매년" -> {
                                        if (repeatStartDate.month != preDate.month) {
                                            preDate.month = repeatStartDate.month
                                        }

                                        if (repeatStartDate.date != preDate.date) {
                                            preDate.date = repeatStartDate.date
                                        }
                                    }
                                }
                            }

                            preDate!!.hours = schedule.startTime!!.hours
                            preDate!!.minutes = schedule.startTime!!.minutes
                            preDate!!.seconds = schedule.startTime!!.seconds

                            calendarviewmodel.deleteBackSchedule(
                                schedule.id,
                                ScheduleBackDelete(
                                    serverFormatter.format(preDate)
                                )
                            ) {
                                if (searchCallback != null)
                                    searchCallback?.updateCallback {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                else requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                    }
                }
            }

            deleteDialg.show(parentFragmentManager, deleteDialg.tag)
        }

        binding.scheduleContentEt.addTextChangedListener {
            if (it.toString() == "") {
                binding.btnSubmitSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#ACACAC"))
            } else {
                binding.btnSubmitSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))
            }
        }

        binding.btnSubmitSchedule.setOnClickListener {
            if (binding.scheduleContentEt.text.toString().replace(" ", "") == "") {
                Toast.makeText(requireContext(), "일정을 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (repeatEndCalendar.time.time - repeatStartCalendar.time.time < 1000 * 60 * 30) {
                Toast.makeText(requireContext(), "일정은 30분 이상 차이나야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var option: String? = ""
            var repeatStartDate = ""
            var repeatEndDate = ""
            var repeatvalue: String? = ""

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val repeatEndDateBtnFormat = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)
            val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)
            val timeFormat2 = SimpleDateFormat("HH:mm", Locale.KOREA)

            val initRepeatStartDate = repeatStartCalendar.time.clone() as Date
            val initRepeatEndDate = repeatEndCalendar.time.clone() as Date

            var flag = false

            while (true) {
                if (binding.repeatEndDateSwitchSchedule.isChecked) {
                    val realEndDate = repeatEndDateBtnFormat.parse(
                        binding.btnRepeatEndDateSchedule.text.toString()
                    )!!

                    realEndDate.hours = 23
                    realEndDate.minutes = 59

                    if (binding.repeatEndDateSwitchSchedule.isChecked &&
                        realEndDate.before(repeatStartCalendar.time)
                    ) {
                        repeatStartCalendar.time = initRepeatStartDate
                        repeatEndCalendar.time = initRepeatEndDate

                        Toast.makeText(
                            requireContext(),
                            "반복 종료일은 반복시작일 이후여야 합니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }

                if (!binding.alldaySwitch.isChecked) {
                    val startTime = timeFormat.parse(binding.repeatStartTimeBtn.text.toString())
                    val endTime = timeFormat.parse(binding.repeatEndTimeBtn.text.toString())

                    if (!binding.repeatSwitchSchedule.isChecked) {
                        repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                                "T" +
                                timeFormat2.format(startTime) +
                                ":00+09:00"
                        repeatEndDate = dateFormat.format(repeatEndCalendar.time) +
                                "T" +
                                timeFormat2.format(endTime) +
                                ":00+09:00"
                    } else if (!binding.repeatEndDateSwitchSchedule.isChecked) {
                        repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                                "T" +
                                timeFormat2.format(startTime) +
                                ":00+09:00"
                        val calendarClone = repeatStartCalendar.clone() as Calendar
                        calendarClone.set(Calendar.YEAR, 2200)

                        repeatEndDate = dateFormat.format(calendarClone.time) +
                                "T" +
                                timeFormat2.format(endTime) +
                                ":00+09:00"
                    } else {
                        repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                                "T" +
                                timeFormat2.format(startTime) +
                                ":00+09:00"
                        repeatEndDate = dateFormat.format(
                            repeatEndDateBtnFormat.parse(
                                binding.btnRepeatEndDateSchedule.text.toString()
                            )
                        ) +
                                "T" +
                                timeFormat2.format(endTime) +
                                ":00+09:00"
                    }
                } else {
                    if (!binding.repeatSwitchSchedule.isChecked) {
                        repeatStartDate =
                            dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                        repeatEndDate =
                            dateFormat.format(repeatEndCalendar.time) + "T23:59:55+09:00"
                    } else if (!binding.repeatEndDateSwitchSchedule.isChecked) {
                        repeatStartDate =
                            dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"

                        val calendarClone = repeatEndCalendar.clone() as Calendar
                        calendarClone.set(Calendar.YEAR, 2200)

                        repeatEndDate = dateFormat.format(calendarClone.time) + "T23:59:55+09:00"
                    } else {
                        repeatStartDate =
                            dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                        repeatEndDate = dateFormat.format(
                            repeatEndDateBtnFormat.parse(
                                binding.btnRepeatEndDateSchedule.text.toString()
                            )
                        ) + "T23:59:55+09:00"
                    }
                }

                option = when (repeatOption) {
                    0 -> "매일"
                    1 -> "매주"
                    2 -> "격주"
                    3 -> "매달"
                    4 -> "매년"
                    else -> null
                }

                if (binding.repeatStartDateBtn.text.toString() == binding.repeatEndDateBtn.text.toString()) {
                    if (option != null) {
                        when (repeatOption) {
                            0 -> {
                                repeatvalue = "1"
                            }
                            1, 2 -> {
                                for (i in 0..6) {
                                    if (weeksValue[i]) repeatvalue += "1"
                                    else repeatvalue += "0"
                                }
                            }

                            3 -> {
                                for (i in 0..30) {
                                    if (monthsValue[i]) repeatvalue += "1"
                                    else repeatvalue += "0"
                                }
                            }

                            4 -> {
                                for (i in 0..11) {
                                    if (yearsValue[i]) repeatvalue += "1"
                                    else repeatvalue += "0"
                                }
                            }
                        }
                    }
                } else {
                    if (binding.alldaySwitch.isChecked) {
                        if (option != null) {
                            repeatEndCalendar.apply {
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 55)
                            }

                            repeatvalue =
                                "T" + ((repeatEndCalendar.time.time - repeatStartCalendar.time.time).toInt() / 1000)
                                    .toString()
                        }
                    } else {
                        if (option != null) {
                            val startTime =
                                timeFormat.parse(binding.repeatStartTimeBtn.text.toString())
                            val endTime = timeFormat.parse(binding.repeatEndTimeBtn.text.toString())

                            repeatStartCalendar.set(
                                Calendar.HOUR_OF_DAY,
                                startTime.hours
                            )

                            repeatStartCalendar.set(
                                Calendar.MINUTE,
                                startTime.minutes
                            )

                            repeatEndCalendar.set(
                                Calendar.HOUR_OF_DAY,
                                endTime.hours
                            )

                            repeatEndCalendar.set(
                                Calendar.MINUTE,
                                endTime.minutes
                            )

                            repeatvalue =
                                "T" + ((repeatEndCalendar.time.time - repeatStartCalendar.time.time).toInt() / 1000)
                                    .toString()
                        }
                    }
                }

                if (repeatvalue == "") repeatvalue = null

                if (repeatvalue != null && !repeatvalue.contains("T") && !flag) {
                    when (option) {
                        "매주", "격주" -> {
                            while (!weeksValue[repeatStartCalendar.get(Calendar.DAY_OF_WEEK) - 1]) {
                                repeatStartCalendar.add(Calendar.DATE, 1)
                                repeatEndCalendar.add(Calendar.DATE, 1)
                            }

                            flag = true
                            continue
                        }

                        "매달" -> {
                            while (!monthsValue[repeatStartCalendar.get(Calendar.DAY_OF_MONTH) - 1]) {
                                repeatStartCalendar.add(Calendar.DATE, 1)
                                repeatEndCalendar.add(Calendar.DATE, 1)
                            }

                            flag = true
                            continue
                        }

                        "매년" -> {
                            while (!yearsValue[repeatStartCalendar.get(Calendar.MONTH)]) {
                                repeatStartCalendar.add(Calendar.MONTH, 1)
                                repeatEndCalendar.add(Calendar.MONTH, 1)
                            }

                            flag = true
                            continue
                        }
                    }
                }

                break
            }

            var sizeOption = 0

            if (schedule.location == null) {//반복이 아닐 때
                sizeOption = 0
            } else {
//                if (binding.repeatStartDateBtn.text.toString() != initStartDate ||
//                    binding.repeatEndDateBtn.text.toString() != initEndDate ||
//                    initIsAllday != isAllday ||
//                    (!isAllday && binding.repeatStartTimeBtn.text.toString() != initStartTime) ||
//                    (!isAllday && binding.repeatEndTimeBtn.text.toString() != initEndTime) ||
//
//                ) { // start 또는 end가 바뀌었을 때
//                    if (schedule.location == 0) {//front
//                        sizeOption = 1
//                    } else { // middle, back
//                        sizeOption = 2
//                    }
//                } else if(repeatvalue != schedule.repeatValue || option != schedule.repeatOption){// 반복 옵션이 바뀌었을 때
                if (repeatvalue != schedule.repeatValue ||
                    option != schedule.repeatOption ||
                    initRepeatSwitch != binding.repeatSwitchSchedule.isChecked
                ) {
                    if (schedule.location == 0) {// front
                        sizeOption = 1
                    } else if (schedule.location == 1) { // middle
                        sizeOption = 2
                    } else { // back
                        sizeOption = 3
                    }
                } else { // 디폴트
                    if (schedule.location == 0) { // front
                        sizeOption = 4
                    } else if (schedule.location == 1) { // middle
                        sizeOption = 5
                    } else { // back
                        sizeOption = 6
                    }
                }
            }

            val updateDialog = UpdateOptionScheduleFragment(sizeOption) {
                when (it) {
                    1 -> {// 이 일정만 수정하기
                        when (schedule.location) {
                            0 -> {//front
                                val next = schedule.repeatStart
                                var nextDate: Date? = null

                                when (schedule.repeatOption) {
                                    "매일" -> {
                                        nextDate = FormatDate.nextStartDate(
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            1,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "격주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            2,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매달" -> {
                                        nextDate = FormatDate.nextStartDateEveryMonth(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매년" -> {
                                        nextDate = FormatDate.nextStartDateEveryYear(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }
                                }

                                if (nextDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)
                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            nextDate!!
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                nextDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                nextDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != nextDate!!.date) {
                                                    nextDate!!.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != nextDate!!.month) {
                                                    nextDate!!.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != nextDate!!.date) {
                                                    nextDate!!.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    val calendarviewmodel = CalendarViewModel()

                                    calendarviewmodel.submitScheduleFront(
                                        schedule.id, PostScheduleFront(
                                            binding.scheduleContentEt.text.toString(),
                                            binding.etMemoSchedule.text.toString(),
                                            category?.id,
                                            if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                serverFormatter.format(Date())
                                            ) else emptyList(),
                                            isAllday,
                                            null,
                                            null,
                                            serverFormatter.format(schedule.startTime),
                                            serverFormatter.format(schedule.endTime),
                                            serverFormatter.format(nextDate)
                                        )
                                    ) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                } else {
                                    val startTime =
                                        timeFormat.parse(binding.repeatStartTimeBtn.text.toString())
                                    val endTime =
                                        timeFormat.parse(binding.repeatEndTimeBtn.text.toString())

                                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                                            "T" +
                                            timeFormat2.format(startTime) +
                                            ":00+09:00"
                                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) +
                                            "T" +
                                            timeFormat2.format(endTime) +
                                            ":00+09:00"

                                    val calendarviewmodel = CalendarViewModel()

                                    if (category != null) {
                                        calendarviewmodel.submitSchedule(
                                            schedule.id, PostSchedule(
                                                binding.scheduleContentEt.text.toString(),
                                                binding.etMemoSchedule.text.toString(),
                                                isAllday,
                                                repeatStartDate,
                                                repeatEndDate,
                                                null,
                                                null,
                                                category!!.id,
                                                if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                    serverFormatter.format(Date())
                                                ) else emptyList()
                                            )
                                        ) {
                                            if (searchCallback != null)
                                                searchCallback?.updateCallback {
                                                    requireActivity().supportFragmentManager.popBackStack()
                                                }
                                            else requireActivity().supportFragmentManager.popBackStack()
                                        }
                                    } else {
                                        calendarviewmodel.submitSchedule(
                                            schedule.id, PostSchedule(
                                                binding.scheduleContentEt.text.toString(),
                                                binding.etMemoSchedule.text.toString(),
                                                isAllday,
                                                repeatStartDate,
                                                repeatEndDate,
                                                null,
                                                null,
                                                null,
                                                if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                    serverFormatter.format(Date())
                                                ) else emptyList()
                                            )
                                        ) {
                                            if (searchCallback != null)
                                                searchCallback?.updateCallback {
                                                    requireActivity().supportFragmentManager.popBackStack()
                                                }
                                            else requireActivity().supportFragmentManager.popBackStack()
                                        }
                                    }
                                }
                            }

                            1 -> {//middle
                                var next = calendarDateFormatter.format(todayDate)
                                var nextDate: Date? = null

                                when (schedule.repeatOption) {
                                    "매일" -> {
                                        nextDate = FormatDate.nextStartDate(
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            1,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "격주" -> {
                                        nextDate = FormatDate.nextStartDateEveryWeek(
                                            schedule.repeatValue!!,
                                            2,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매달" -> {
                                        nextDate = FormatDate.nextStartDateEveryMonth(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }

                                    "매년" -> {
                                        nextDate = FormatDate.nextStartDateEveryYear(
                                            schedule.repeatValue!!,
                                            next!!,
                                            schedule.repeatEnd!!
                                        )
                                    }
                                }

                                if (nextDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)

                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            nextDate!!
                                        )

                                        val datediff2 = dateDifference(
                                            repeatStartDate,
                                            todayDate
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                nextDate = cal.time

                                                cal.time = todayDate
                                                cal.add(Calendar.DATE, -(datediff2 % 7))
                                                todayDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = nextDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                nextDate = cal.time

                                                cal.time = todayDate
                                                cal.add(Calendar.DATE, -(datediff2 % 14))
                                                todayDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != nextDate!!.date) {
                                                    nextDate!!.date = repeatStartDate.date
                                                }

                                                if (repeatStartDate.date != todayDate.date) {
                                                    todayDate.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != nextDate!!.month) {
                                                    nextDate!!.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.month != todayDate.month) {
                                                    todayDate.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != nextDate!!.date) {
                                                    nextDate!!.date = repeatStartDate.date
                                                }

                                                if (repeatStartDate.date != todayDate.date) {
                                                    todayDate.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    todayDate.hours = schedule.startTime!!.hours
                                    todayDate.minutes = schedule.startTime!!.minutes
                                    todayDate.seconds = schedule.startTime!!.seconds

                                    nextDate!!.hours = schedule.startTime!!.hours
                                    nextDate!!.minutes = schedule.startTime!!.minutes
                                    nextDate!!.seconds = schedule.startTime!!.seconds

                                    val calendarviewmodel = CalendarViewModel()

                                    if (category != null) {
                                        calendarviewmodel.submitScheduleMiddle(
                                            schedule.id, PostScheduleMiddle(
                                                binding.scheduleContentEt.text.toString(),
                                                binding.etMemoSchedule.text.toString(),
                                                category!!.id,
                                                if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                    serverFormatter.format(Date())
                                                ) else emptyList(),
                                                isAllday,
                                                null,
                                                null,
                                                serverFormatter.format(schedule.startTime),
                                                serverFormatter.format(schedule.endTime),
                                                serverFormatter.format(todayDate),
                                                serverFormatter.format(nextDate)
                                            )
                                        ) {
                                            if (searchCallback != null)
                                                searchCallback?.updateCallback {
                                                    requireActivity().supportFragmentManager.popBackStack()
                                                }
                                            else requireActivity().supportFragmentManager.popBackStack()
                                        }
                                    } else {
                                        calendarviewmodel.submitScheduleMiddle(
                                            schedule.id, PostScheduleMiddle(
                                                binding.scheduleContentEt.text.toString(),
                                                binding.etMemoSchedule.text.toString(),
                                                null,
                                                if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                    serverFormatter.format(Date())
                                                ) else emptyList(),
                                                isAllday,
                                                null,
                                                null,
                                                serverFormatter.format(schedule.startTime),
                                                serverFormatter.format(schedule.endTime),
                                                serverFormatter.format(todayDate),
                                                serverFormatter.format(nextDate)
                                            )
                                        ) {
                                            if (searchCallback != null)
                                                searchCallback?.updateCallback {
                                                    requireActivity().supportFragmentManager.popBackStack()
                                                }
                                            else requireActivity().supportFragmentManager.popBackStack()
                                        }
                                    }
                                }
                            }

                            2 -> {//back
                                var pre = calendarDateFormatter.format(todayDate)
                                var preDate: Date? = null

                                preDate = FormatDate.preStartDate(
                                    pre,
                                    schedule.repeatOption,
                                    schedule.repeatValue
                                )

                                if (preDate != null) {
                                    if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                            "T"
                                        )
                                    ) {
                                        val repeatStartDate =
                                            calendarDateFormatter.parse(schedule.repeatStart)
                                        val datediff = dateDifference(
                                            repeatStartDate,
                                            preDate!!
                                        )

                                        when (schedule.repeatOption) {
                                            "매주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = preDate
                                                cal.add(Calendar.DATE, -(datediff % 7))
                                                preDate = cal.time
                                            }

                                            "격주" -> {
                                                val cal = Calendar.getInstance()
                                                cal.time = preDate
                                                cal.add(Calendar.DATE, -(datediff % 14))
                                                preDate = cal.time
                                            }

                                            "매달" -> {
                                                if (repeatStartDate.date != preDate!!.date) {
                                                    preDate!!.date = repeatStartDate.date
                                                }
                                            }

                                            "매년" -> {
                                                if (repeatStartDate.month != preDate!!.month) {
                                                    preDate!!.month = repeatStartDate.month
                                                }

                                                if (repeatStartDate.date != preDate!!.date) {
                                                    preDate!!.date = repeatStartDate.date
                                                }
                                            }
                                        }
                                    }

                                    val calendarviewmodel = CalendarViewModel()

                                    preDate!!.hours = schedule.startTime!!.hours
                                    preDate!!.minutes = schedule.startTime!!.minutes
                                    preDate!!.seconds = schedule.startTime!!.seconds

                                    calendarviewmodel.submitScheduleBack(
                                        schedule.id, PostScheduleBack(
                                            binding.scheduleContentEt.text.toString(),
                                            binding.etMemoSchedule.text.toString(),
                                            category?.id,
                                            if (binding.alarmSwitchSchedule.isChecked) listOf(
                                                serverFormatter.format(Date())
                                            ) else emptyList(),
                                            isAllday,
                                            null,
                                            null,
                                            serverFormatter.format(schedule.startTime),
                                            serverFormatter.format(schedule.endTime),
                                            serverFormatter.format(preDate)
                                        )
                                    ) {
                                        if (searchCallback != null)
                                            searchCallback?.updateCallback {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        else requireActivity().supportFragmentManager.popBackStack()
                                    }
                                }
                            }
                        }
                    }

                    0, 2 -> {// 수정, 일정 전부 수정하기
                        val calendarviewmodel = CalendarViewModel()

                        if (category != null) {
                            calendarviewmodel.submitSchedule(
                                schedule.id, PostSchedule(
                                    binding.scheduleContentEt.text.toString(),
                                    binding.etMemoSchedule.text.toString(),
                                    isAllday,
                                    repeatStartDate,
                                    repeatEndDate,
                                    option,
                                    repeatvalue,
                                    category!!.id,
                                    if (binding.alarmSwitchSchedule.isChecked) listOf(
                                        serverFormatter.format(Date())
                                    ) else emptyList()
                                )
                            ) {
                                if (searchCallback != null)
                                    searchCallback?.updateCallback {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                else requireActivity().supportFragmentManager.popBackStack()
                            }
                        } else {
                            calendarviewmodel.submitSchedule(
                                schedule.id, PostSchedule(
                                    binding.scheduleContentEt.text.toString(),
                                    binding.etMemoSchedule.text.toString(),
                                    isAllday,
                                    repeatStartDate,
                                    repeatEndDate,
                                    option,
                                    repeatvalue,
                                    null,
                                    if (binding.alarmSwitchSchedule.isChecked) listOf(
                                        serverFormatter.format(Date())
                                    ) else emptyList()
                                )
                            ) {
                                if (searchCallback != null)
                                    searchCallback?.updateCallback {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                else requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                    }

                    3 -> {// 이 이후로 수정하기
                        var pre = calendarDateFormatter.format(todayDate)
                        var preDate: Date? = null

                        preDate = FormatDate.preStartDate(
                            pre,
                            schedule.repeatOption,
                            schedule.repeatValue
                        )

                        if (preDate != null) {
                            if (schedule.repeatValue != null && schedule.repeatValue.contains(
                                    "T"
                                )
                            ) {
                                val repeatStartDate =
                                    calendarDateFormatter.parse(schedule.repeatStart)
                                val datediff = dateDifference(
                                    repeatStartDate,
                                    preDate!!
                                )

                                when (schedule.repeatOption) {
                                    "매주" -> {
                                        val cal = Calendar.getInstance()
                                        cal.time = preDate
                                        cal.add(Calendar.DATE, -(datediff % 7))
                                        preDate = cal.time
                                    }

                                    "격주" -> {
                                        val cal = Calendar.getInstance()
                                        cal.time = preDate
                                        cal.add(Calendar.DATE, -(datediff % 14))
                                        preDate = cal.time
                                    }

                                    "매달" -> {
                                        if (repeatStartDate.date != preDate!!.date) {
                                            preDate!!.date = repeatStartDate.date
                                        }
                                    }

                                    "매년" -> {
                                        if (repeatStartDate.month != preDate!!.month) {
                                            preDate!!.month = repeatStartDate.month
                                        }

                                        if (repeatStartDate.date != preDate!!.date) {
                                            preDate!!.date = repeatStartDate.date
                                        }
                                    }
                                }
                            }

                            preDate!!.hours = schedule.startTime!!.hours
                            preDate!!.minutes = schedule.startTime!!.minutes
                            preDate!!.seconds = schedule.startTime!!.seconds

                            val calendarviewmodel = CalendarViewModel()

                            calendarviewmodel.submitScheduleBack(
                                schedule.id, PostScheduleBack(
                                    binding.scheduleContentEt.text.toString(),
                                    binding.etMemoSchedule.text.toString(),
                                    category?.id,
                                    if (binding.alarmSwitchSchedule.isChecked) listOf(
                                        serverFormatter.format(Date())
                                    ) else emptyList(),
                                    isAllday,
                                    option,
                                    repeatvalue,
                                    serverFormatter.format(schedule.startTime),
                                    repeatEndDate,
                                    serverFormatter.format(preDate)
                                )
                            ) {
                                if (searchCallback != null)
                                    searchCallback?.updateCallback {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                else requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                    }
                }
            }

            updateDialog.show(parentFragmentManager, updateDialog.tag)
        }

        binding.alarmSwitchSchedule.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                binding.alarmTv.setTextColor(Color.parseColor("#191919"))
                binding.alarmIv.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))
            } else {
                binding.alarmTv.setTextColor(Color.LTGRAY)
                binding.alarmIv.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)
            }
        }

        binding.scheduleContentEt.addTextChangedListener {
            if(it.toString().length > 50){
                binding.scheduleContentEt.setText(it.toString().substring(0,50))
                binding.scheduleContentEt.setSelection(50)
            }
        }

        binding.etMemoSchedule.addTextChangedListener {
            if (it.toString().length > 500) {
                binding.etMemoSchedule.setText(it.toString().substring(0, 500))
                binding.etMemoSchedule.setSelection(500)
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun initView() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        //내용 가져오기
        binding.scheduleContentEt.setText(schedule.content)
        binding.gridMonthSchedule.visibility = View.GONE
        binding.gridYearSchedule.visibility = View.GONE

        //카테고리 가져오기
        val drawable = binding.categoryChooseIv.background as VectorDrawable

        if (schedule.category == null) {
            drawable.setColorFilter(Color.parseColor("#AAD7FF"), PorterDuff.Mode.SRC_ATOP)
        } else {
            drawable.setColorFilter(
                Color.parseColor(schedule.category.color),
                PorterDuff.Mode.SRC_ATOP
            )
        }

        //메모 가져오기
        if (schedule.memo != null && schedule.memo != "") {
            binding.ivMemoIcon.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))

            binding.etMemoSchedule.setText(schedule.memo)
        }

        //하루종일 확인 후 반복 구간 설정
        if (schedule.isAllDay) {
            isAllday = true
            binding.alldaySwitch.isChecked = true

            binding.alldayIv.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.alldayTv.setTextColor(Color.parseColor("#191919"))

            binding.repeatStartTimeBtn.visibility = View.GONE
            binding.repeatEndTimeBtn.visibility = View.GONE
        }

        //반복 설정이면서
        if (schedule.repeatOption != null && schedule.repeatValue != null) {
            binding.repeatSwitchSchedule.isChecked = true
            binding.repeatEndLayout.visibility = View.VISIBLE

            //구간이 이틀 이상 연속이라 repeatValue에 인터벌 값으로 들어갔을 때
            if (schedule.repeatValue.contains("T")) {
                val calendar = Calendar.getInstance()
                val repeatstart = calendarDateFormatter.parse(schedule.repeatStart)
                calendar.time = repeatstart

                calendar.add(
                    Calendar.SECOND,
                    schedule.repeatValue.replace("T", "").toInt()
                )

                val repeatend = calendar.time

                val repeatendcontent = calendarDateFormatter.parse(schedule.repeatEnd)

                //year을 가져오면 원래 년도에서 1900을 뺀 값이 나오기 때문에 2100 대신에 200을 넣어 계산
                if (repeatendcontent.year < 200) {
                    binding.repeatEndDateSwitchSchedule.isChecked = true
                    binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#191919"))
                    binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE

                    val trulyRepeatEnd = calendarDateFormatter.parse(schedule.repeatEnd)
                    binding.btnRepeatEndDateSchedule.text = dateformat.format(trulyRepeatEnd)
                }

                repeatStartCalendar.time = repeatstart
                binding.repeatStartDateBtn.text = dateformat.format(schedule.startTime)

                repeatEndCalendar.time = repeatend
                binding.repeatEndDateBtn.text = dateformat.format(schedule.endTime)
            } else { // 반복이지만 하루 일정일 때
                val repeatstart = calendarDateFormatter.parse(schedule.repeatStart!!)
                val repeatend = calendarDateFormatter.parse(schedule.repeatEnd!!)

                repeatStartCalendar.time = repeatstart
                binding.repeatStartDateBtn.text = dateformat.format(schedule.startTime)

                repeatEndCalendar.time = repeatstart

                repeatEndCalendar.apply {
                    set(Calendar.HOUR_OF_DAY, repeatend.hours)
                    set(Calendar.MINUTE, repeatend.minutes)
                    set(Calendar.SECOND, repeatend.seconds)
                }

                binding.repeatEndDateBtn.text = dateformat.format(schedule.endTime)

                if (repeatend.year < 200) {
                    binding.repeatEndDateSwitchSchedule.isChecked = true
                    binding.repeatEndDateScheduleTv.setTextColor(Color.parseColor("#191919"))
                    binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE

                    binding.btnRepeatEndDateSchedule.text = dateformat.format(repeatend)
                }
            }
        } else {// 반복 일정이 아닐 때
            val repeatstart = calendarDateFormatter.parse(schedule.repeatStart!!)
            val repeatend = calendarDateFormatter.parse(schedule.repeatEnd!!)

            repeatStartCalendar.time = repeatstart
            binding.repeatStartDateBtn.text = dateformat.format(repeatstart)

            repeatEndCalendar.time = repeatend
            binding.repeatEndDateBtn.text = dateformat.format(repeatend)
        }

        //알람이 1개 이상 존재할 때
        if (schedule.alarms.size > 0) {
            binding.alarmSwitchSchedule.isChecked = true

            binding.alarmIv.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.alarmTv.setTextColor(Color.parseColor("#191919"))
        }

        if (binding.repeatStartDateBtn.text != binding.repeatEndDateBtn.text) {
            val dateParser = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)

            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                0,
                binding.btnRepeatEndDateSchedule.text.toString().length - 2
            )

            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
            val repeatEndDateCalendar = Calendar.getInstance()
            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

            if (repeatEndCalendar.time.after(repeatEndDateCalendar.time)) {
                binding.btnRepeatEndDateSchedule.text = dateParser.format(repeatEndCalendar.time)
            }

            if (repeatStartCalendar.time.month != repeatEndCalendar.time.month) {
                binding.gridMonthSchedule.visibility = View.GONE
                binding.btnEveryMonthSchedule.visibility = View.GONE
                if (repeatOption == -3) repeatOption = -1
            } else {
                binding.btnEveryMonthSchedule.visibility = View.VISIBLE
            }

            val startDate = repeatStartCalendar.time.clone() as Date
            startDate.hours = 0
            startDate.minutes = 0
            startDate.seconds = 0

            val endDate = repeatEndCalendar.time.clone() as Date
            endDate.hours = 0
            endDate.minutes = 0
            endDate.seconds = 0

            //일주일 이상 차이나면 반복 해제
            if ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24) > 6) {
                binding.repeatSwitchSchedule.isChecked = false
                binding.repeatSwitchSchedule.isClickable = false

                binding.repeatTvSchedule.setTextColor(Color.LTGRAY)
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)

                binding.repeatOptionSelectSchedule.visibility = View.GONE
                binding.repeatEndLayout.visibility = View.GONE
                binding.everyWeekLayout.visibility = View.GONE
                binding.gridMonthSchedule.visibility = View.GONE
                binding.gridYearSchedule.visibility = View.GONE
                repeatOption = -1
                return
            } else {
                binding.repeatSwitchSchedule.isClickable = true
            }

            if (repeatOption == 0) repeatOption = -1

            binding.btnEveryDaySchedule.visibility = View.GONE
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        }

        //반복이 설정되어 있을 때
        if (schedule.repeatOption != null) {
            binding.repeatSwitchSchedule.isChecked = true
            binding.repeatOptionSelectSchedule.visibility = View.VISIBLE

            binding.repeatIvSchedule.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#191919"))
            binding.repeatTvSchedule.setTextColor(Color.parseColor("#191919"))

            when (schedule.repeatOption) {
                "매일" -> {
                    onOptionClick(0)
                }

                "매주" -> {
                    onOptionClick(1)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            weeksValue[i] = schedule.repeatValue[i] == '1'
                        }
                    }
                }

                "격주" -> {
                    onOptionClick(2)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            weeksValue[i] = schedule.repeatValue[i] == '1'
                        }
                    }
                }

                "매달" -> {
                    onOptionClick(3)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            monthsValue[i] = schedule.repeatValue[i] == '1'
                        }
                    }
                }

                "매년" -> {
                    onOptionClick(4)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            yearsValue[i] = schedule.repeatValue[i] == '1'
                        }
                    }
                }
            }
        }

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER

            if (!monthsValue[i - 1]) {
                textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
            }

            textView.setOnClickListener {
                if (!monthsValue[i - 1]) {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.highlight
                        )
                    )
                    monthsValue[i - 1] = true
                } else {
                    textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
                    monthsValue[i - 1] = false
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.gridMonthSchedule.addView(textView, params)
        }

        for (i in 1..12) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.YearMonth, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER

            if (!yearsValue[i - 1]) {
                textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            } else {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
            }

            textView.setOnClickListener {
                if (!yearsValue[i - 1]) {
                    textView.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.highlight
                        )
                    )
                    yearsValue[i - 1] = true
                } else {
                    textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
                    yearsValue[i - 1] = false
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.gridYearSchedule.addView(textView, params)
        }

        for (i in 0..6) {
            val view = binding.everyWeekLayout.getChildAt(i) as TextView

            if (!weeksValue[i]) {
                view.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            } else {
                view.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
            }

            view.setOnClickListener {
                if (!weeksValue[i]) {
                    weeksValue[i] = true
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                } else {
                    weeksValue[i] = false
                    view.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
                }
            }
        }

        initStartDate = binding.repeatStartDateBtn.text.toString()
        initEndDate = binding.repeatEndDateBtn.text.toString()

        initStartTime = binding.repeatStartTimeBtn.text.toString()
        initEndTime = binding.repeatEndTimeBtn.text.toString()

        initIsAllday = binding.alldaySwitch.isChecked

        initRepeatSwitch = binding.repeatSwitchSchedule.isChecked

        binding.repeatStartTimeBtn.text = timeParser.format(repeatStartCalendar.time)
        binding.repeatEndTimeBtn.text = timeParser.format(repeatEndCalendar.time)

        if (repeatStartCalendar.time.month != repeatEndCalendar.time.month) {
            binding.gridMonthSchedule.visibility = View.GONE
            binding.btnEveryMonthSchedule.visibility = View.GONE
        }
    }
}
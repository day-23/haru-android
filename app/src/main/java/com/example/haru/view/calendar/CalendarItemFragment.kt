package com.example.haru.view.calendar

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.PostSchedule
import com.example.haru.data.model.Schedule
import com.example.haru.databinding.FragmentCalendarInputBinding
import com.example.haru.databinding.FragmentCalendarItemBinding
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemFragment(val schedule: Schedule,
                           val categories: List<Category?>) : Fragment() {
    private lateinit var binding: FragmentCalendarItemBinding

    private val calendarDateFormatter =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA)
    private val dateformat = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)
    val timeParser = SimpleDateFormat("aa hh:mm", Locale.KOREAN)

    private val repeatStartCalendar = Calendar.getInstance()
    private val repeatEndCalendar = Calendar.getInstance()

    private var category: Category? = null

    private var isAllday = false

    private var initRepeatOption: String? = null

    private var repeatOption = -1

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            if (binding.repeatStartDateBtn.text.substring(5, 7) ==
                binding.repeatEndDateBtn.text.substring(5, 7)
            ) {
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
        binding.repeatStartDateBtn.setOnClickListener {
            val year = repeatStartCalendar.get(Calendar.YEAR)
            val month = repeatStartCalendar.get(Calendar.MONTH)
            val day = repeatStartCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year2, month2, dayOfMonth ->
                    repeatStartCalendar.set(Calendar.YEAR, year2)
                    repeatStartCalendar.set(Calendar.MONTH, month2)
                    repeatStartCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    binding.repeatStartDateBtn.text = dateformat.format(repeatStartCalendar.time)

                    if (binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()) {
                        if (repeatOption == 0) repeatOption = -1

                        binding.btnEveryDaySchedule.visibility = View.GONE
                        binding.everyWeekLayout.visibility = View.GONE
                        binding.gridMonthSchedule.visibility = View.GONE

                        if (binding.repeatStartDateBtn.text.toString().substring(5, 7) !=
                            binding.repeatEndDateBtn.text.toString().substring(5, 7)
                        ) {
                            binding.gridYearSchedule.visibility = View.GONE
                        } else if (repeatOption == 4) {
                            binding.gridYearSchedule.visibility = View.VISIBLE
                        }
                    } else {
                        optionChange()
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.repeatStartTimeBtn.setOnClickListener {
            val hour = repeatStartCalendar.get(Calendar.HOUR)
            val minute = repeatStartCalendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute2 ->
                    repeatStartCalendar.set(Calendar.HOUR, hourOfDay)
                    repeatStartCalendar.set(Calendar.MINUTE, minute2)

                    binding.repeatStartTimeBtn.text = timeParser.format(repeatStartCalendar.time)
                },
                hour,
                minute,
                false
            )

            timePickerDialog.show()
        }

        binding.repeatEndDateBtn.setOnClickListener {
            val year = repeatEndCalendar.get(Calendar.YEAR)
            val month = repeatEndCalendar.get(Calendar.MONTH)
            val day = repeatEndCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year2, month2, dayOfMonth ->
                    repeatEndCalendar.set(Calendar.YEAR, year2)
                    repeatEndCalendar.set(Calendar.MONTH, month2)
                    repeatEndCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    binding.repeatEndDateBtn.text = dateformat.format(repeatEndCalendar.time)

                    if (binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()) {
                        if (repeatOption == 0) repeatOption = -1
                        binding.btnEveryDaySchedule.visibility = View.GONE
                        binding.everyWeekLayout.visibility = View.GONE
                        binding.gridMonthSchedule.visibility = View.GONE

                        if (binding.repeatStartDateBtn.text.toString().substring(5, 7) !=
                            binding.repeatEndDateBtn.text.toString().substring(5, 7)
                        ) {
                            binding.gridYearSchedule.visibility = View.GONE
                        } else if (repeatOption == 4) {
                            binding.gridYearSchedule.visibility = View.VISIBLE
                        }
                    } else {
                        optionChange()
                    }
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        binding.repeatEndTimeBtn.setOnClickListener {
            val hour = repeatEndCalendar.get(Calendar.HOUR)
            val minute = repeatEndCalendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute2 ->
                    repeatEndCalendar.set(Calendar.HOUR, hourOfDay)
                    repeatEndCalendar.set(Calendar.MINUTE, minute2)

                    binding.repeatEndTimeBtn.text = timeParser.format(repeatEndCalendar.time)
                },
                hour,
                minute,
                false
            )

            timePickerDialog.show()
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
            }
        }

        binding.btnEveryDaySchedule.setOnClickListener { onOptionClick(0) }
        binding.btnEveryWeekSchedule.setOnClickListener { onOptionClick(1) }
        binding.btnEvery2WeekSchedule.setOnClickListener { onOptionClick(2) }
        binding.btnEveryMonthSchedule.setOnClickListener { onOptionClick(3) }
        binding.btnEveryYearSchedule.setOnClickListener { onOptionClick(4) }

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER

            textView.setOnClickListener {
                if (!monthsValue[i - 1]) {
                    textView.setTextColor(R.color.highlight)
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

        binding.gridMonthSchedule.visibility = View.GONE

        for (i in 1..12) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.YearMonth, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER

            textView.setOnClickListener {
                if (!yearsValue[i - 1]) {
                    textView.setTextColor(R.color.highlight)
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

        binding.gridYearSchedule.visibility = View.GONE

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
            val dlg = CategoryChooseDialog(null, this)

            dlg.show(categories) {
                category = it

                val drawable = binding.categoryChooseIv.background as VectorDrawable
                drawable.setColorFilter(Color.parseColor(it.color), PorterDuff.Mode.SRC_ATOP)
            }
        }

        binding.btnRepeatEndDateSchedule.setOnClickListener {
            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                0,
                binding.btnRepeatEndDateSchedule.text.toString().length - 2
            )

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val repeatEndDateCalendar = Calendar.getInstance()
            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

            val year = repeatEndDateCalendar.get(Calendar.YEAR)
            val month = repeatEndDateCalendar.get(Calendar.MONTH)
            val day = repeatEndDateCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year2, month2, dayOfMonth ->
                    val calendar = Calendar.getInstance()

                    calendar.set(Calendar.YEAR, year2)
                    calendar.set(Calendar.MONTH, month2)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    binding.btnRepeatEndDateSchedule.text = dateformat.format(calendar.time)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        for (i in 0..6) {
            binding.everyWeekLayout.getChildAt(i).setOnClickListener {
                val view = it as TextView

                if (!weeksValue[i]) {
                    weeksValue[i] = true
                    view.setTextColor(R.color.highlight)
                } else {
                    weeksValue[i] = false
                    view.setTextColor(R.color.light_gray)
                }
            }
        }

        binding.deleteScheduleTv.setOnClickListener {
            val calendarviewmodel = CalendarViewModel()

            calendarviewmodel.deleteSchedule(schedule.id) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        binding.btnSubmitSchedule.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

            var repeatStartDate = ""
            var repeatEndDate = ""

            if (!binding.alldaySwitch.isChecked) {
                if (!binding.repeatSwitchSchedule.isChecked) {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) +
                            "T" +
                            binding.repeatEndTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatEndTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"
                } else if (!binding.repeatEndDateSwitchSchedule.isChecked) {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"
                    val calendarClone = repeatStartCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR, 100)

                    repeatEndDate = dateFormat.format(calendarClone.time) +
                            "T" +
                            binding.repeatEndTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatEndTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"

                    Log.d("20191630", repeatEndDate)
                } else {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"
                    repeatEndDate = binding.btnRepeatEndDateSchedule.text.toString().substring(
                        0,
                        binding.btnRepeatEndDateSchedule.text.toString().length - 2
                    ) +
                            "T" +
                            binding.repeatEndTimeBtn.text.toString().substring(3, 5) +
                            ":" +
                            binding.repeatEndTimeBtn.text.toString().substring(6, 8) +
                            ":00+09:00"

                    Log.d("반복 날짜", repeatEndDate)
                }
            } else {
                if (!binding.repeatSwitchSchedule.isChecked) {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) + "T00:00:00+09:00"
                } else if (!binding.repeatEndDateSwitchSchedule.isChecked) {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"

                    val calendarClone = repeatEndCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR, 100)

                    repeatEndDate = dateFormat.format(calendarClone.time) + "T00:00:00+09:00"
                    Log.d("20191630", repeatEndDate)
                } else {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = binding.btnRepeatEndDateSchedule.text.toString().substring(
                        0,
                        binding.btnRepeatEndDateSchedule.text.toString().length - 2
                    ) + "T00:00:00+09:00"
                }
            }

            val option = when (repeatOption) {
                0 -> "매일"
                1 -> "매주"
                2 -> "격주"
                3 -> "매달"
                4 -> "매년"
                else -> null
            }

            var repeatvalue: String? = null

            if (binding.repeatStartDateBtn.text.toString() == binding.repeatEndDateBtn.text.toString()) {
                if (option != null) {
                    repeatvalue = when (repeatOption) {
                        0 -> "1"
                        1, 2 -> weeksValue.map {
                            if (it) 1
                            else 0
                        }.joinToString { it.toString() }

                        3 -> monthsValue.map {
                            if (it) 1
                            else 0
                        }.joinToString { it.toString() }

                        4 -> yearsValue.map {
                            if (it) 1
                            else 0
                        }.joinToString { it.toString() }
                        else -> null
                    }
                }
            } else {
                if (binding.alldaySwitch.isChecked) {
                    repeatvalue =
                        "T" + (repeatEndCalendar.time.time - repeatStartCalendar.time.time).toString()
                } else {
                    repeatStartCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(3, 5).toInt()
                    )

                    repeatStartCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(6, 8).toInt()
                    )

                    repeatEndCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(3, 5).toInt()
                    )

                    repeatEndCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(6, 8).toInt()
                    )

                    repeatvalue =
                        "T" + (repeatEndCalendar.time.time - repeatStartCalendar.time.time).toString()
                }
            }

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
                        emptyList()
                    )
                ) {
                    requireActivity().supportFragmentManager.popBackStack()
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
                        emptyList()
                    )
                ) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    fun initView() {
        //초기 반복 옵션 값 저장
        initRepeatOption = schedule.repeatOption
        //내용 가져오기
        binding.scheduleContentEt.setText(schedule.content)

        //카테고리 가져오기
        val drawable = binding.categoryChooseIv.background as VectorDrawable

        if (schedule.category == null) {
            drawable.setColorFilter(Color.parseColor("#1DAFFF"), PorterDuff.Mode.SRC_ATOP)
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
                    Calendar.MILLISECOND,
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
                binding.repeatStartDateBtn.text = dateformat.format(repeatstart)

                repeatEndCalendar.time = repeatend
                binding.repeatEndDateBtn.text = dateformat.format(repeatend)
            } else { // 반복이지만 하루 일정일 때
                val repeatstart = calendarDateFormatter.parse(schedule.repeatStart!!)
                val repeatend = calendarDateFormatter.parse(schedule.repeatEnd!!)

                repeatStartCalendar.time = repeatstart
                binding.repeatStartDateBtn.text = dateformat.format(repeatstart)

                repeatEndCalendar.time = repeatstart
                binding.repeatEndDateBtn.text = dateformat.format(repeatstart)

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
            binding.btnEveryDaySchedule.visibility = View.GONE
        }

        //반복이 설정되어 있을 때
        if (schedule.repeatOption != null) {
            binding.repeatSwitchSchedule.isChecked = true

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
                            if (weeksValue[i]) {
                                val childView =
                                    binding.everyWeekLayout.getChildAt(i) as TextView
                                childView.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.highlight
                                    )
                                )
                            }
                        }
                    }
                }

                "격주" -> {
                    onOptionClick(2)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            weeksValue[i] = schedule.repeatValue[i] == '1'
                            if (weeksValue[i]) {
                                val childView =
                                    binding.everyWeekLayout.getChildAt(i) as TextView
                                childView.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.highlight
                                    )
                                )
                            }
                        }
                    }
                }

                "매달" -> {
                    onOptionClick(3)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            monthsValue[i] = schedule.repeatValue[i] == '1'

                            if (monthsValue[i]) {
                                val childeView =
                                    binding.gridMonthSchedule.getChildAt(i) as TextView
                                childeView.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.highlight
                                    )
                                )
                            }
                        }
                    }
                }

                "매년" -> {
                    onOptionClick(4)

                    if (schedule.repeatValue != null && !schedule.repeatValue.contains("T")) {
                        for (i in 0 until schedule.repeatValue.length) {
                            yearsValue[i] = schedule.repeatValue[i] == '1'

                            if (yearsValue[i]) {
                                val childView =
                                    binding.gridYearSchedule.getChildAt(i) as TextView
                                childView.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.highlight
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.haru.view.checklist

import android.animation.ValueAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.GridLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.haru.R
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.Category
import com.example.haru.data.model.PostSchedule
import com.example.haru.databinding.FragmentCalendarInputBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import com.example.haru.view.calendar.CategoryChooseDialog
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.view.customDialog.CustomTimeDialog
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class CalendarAddFragment(private val activity: Activity,
                          private val categories: List<Category?>,
                          private val adapter: AdapterMonth,
                          private val initStartDate: Date?=null,
                          private val initEndDate:Date?=null) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCalendarInputBinding

    private val repeatStartCalendar = Calendar.getInstance()
    private val repeatEndCalendar = Calendar.getInstance()

    private var category: Category? = null

    private var isAllday = false

    private var repeatOption = -1

    private var weeksValue = arrayListOf(
        false,false,false,false,false,false,false
    )

    private var monthsValue = arrayListOf(
        false,false,false,false,false,false,false,
        false,false,false,false,false,false,false,
        false,false,false,false,false,false,false,
        false,false,false,false,false,false,false,
        false,false,false
    )

    private var yearsValue = arrayListOf(
        false,false,false,false,false,false,false,
        false,false,false,false,false
    )

    companion object {
        const val TAG: String = "로그"

        fun newInstance(activity: Activity, categories: List<Category?>, adapter: AdapterMonth): CalendarAddFragment {
            return CalendarAddFragment(activity, categories, adapter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarAddFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarInputBinding.inflate(inflater)

        val dateParser = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)
        val timeParser = SimpleDateFormat("aa hh:mm", Locale.KOREAN)

        if(initStartDate != null && initEndDate != null){
            repeatStartCalendar.set(initStartDate.year+1900, initStartDate.month, initStartDate.date)
            repeatEndCalendar.set(initEndDate.year+1900, initEndDate.month, initEndDate.date)
        }

        binding.repeatStartDateBtn.text = dateParser.format(repeatStartCalendar.time)
        binding.repeatEndDateBtn.text = dateParser.format(repeatEndCalendar.time)
        binding.btnRepeatEndDateSchedule.text = dateParser.format(repeatEndCalendar.time)

        if(binding.repeatStartDateBtn.text != binding.repeatEndDateBtn.text){
            binding.btnEveryDaySchedule.visibility = View.GONE
        }

        if (repeatStartCalendar.time.minutes < 30){
            repeatStartCalendar.set(Calendar.MINUTE, 30)
        } else {
            repeatStartCalendar.set(Calendar.MINUTE, 0)
            repeatStartCalendar.add(Calendar.HOUR, 1)
        }

        if(repeatStartCalendar.time.month == repeatEndCalendar.time.month &&
                repeatEndCalendar.time.date == repeatEndCalendar.time.date){
            repeatEndCalendar.time = repeatStartCalendar.time.clone() as Date
            repeatEndCalendar.add(Calendar.HOUR, 1)
        } else {
            if (repeatEndCalendar.time.minutes < 30){
                repeatEndCalendar.set(Calendar.MINUTE, 30)
            } else {
                repeatEndCalendar.set(Calendar.MINUTE, 0)
                repeatEndCalendar.add(Calendar.HOUR, 1)
            }
        }

        binding.repeatStartTimeBtn.text = timeParser.format(repeatStartCalendar.time)
        binding.repeatEndTimeBtn.text = timeParser.format(repeatEndCalendar.time)

        binding.repeatStartDateBtn.setOnClickListener {
            val datePicker = CustomCalendarDialog(repeatStartCalendar.time)
            datePicker.calendarClick =
                object : CustomCalendarDialog.CalendarClickListener {
                    override fun onClick(view: View, year: Int, month: Int, day: Int) {
                        repeatStartCalendar.set(year, month, day)

                        binding.repeatStartDateBtn.text = dateParser.format(repeatStartCalendar.time)

                        if(binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()){
                            var flag = false
                            if (repeatStartCalendar.time.after(repeatEndCalendar.time)){
                                repeatEndCalendar.set(year,month,day)
                                binding.repeatEndDateBtn.text = dateParser.format(repeatEndCalendar.time)
                                flag = true
                            }

                            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                                0,
                                binding.btnRepeatEndDateSchedule.text.toString().length-2
                            )

                            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                            val repeatEndDateCalendar = Calendar.getInstance()
                            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

                            if(repeatStartCalendar.time.after(repeatEndDateCalendar.time)){
                                binding.btnRepeatEndDateSchedule.text = dateParser.format(repeatStartCalendar.time)
                            }

                            if(flag) return

                            if(repeatOption == 0) repeatOption = -1

                            binding.btnEveryDaySchedule.visibility = View.GONE
                            binding.everyWeekLayout.visibility = View.GONE
                            binding.gridMonthSchedule.visibility = View.GONE

                            if(binding.repeatStartDateBtn.text.toString().substring(5,7) !=
                                    binding.repeatEndDateBtn.text.toString().substring(5,7)) {
                                binding.gridYearSchedule.visibility = View.GONE
                            } else if(repeatOption == 4){
                                binding.gridYearSchedule.visibility = View.VISIBLE
                            }
                        } else {
                            optionChange(binding)
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

                        if(binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()){
                            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                                0,
                                binding.btnRepeatEndDateSchedule.text.toString().length-2
                            )

                            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                            val repeatEndDateCalendar = Calendar.getInstance()
                            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

                            if(repeatEndCalendar.time.after(repeatEndDateCalendar.time)){
                                binding.btnRepeatEndDateSchedule.text = dateParser.format(repeatEndCalendar.time)
                            }

                            if(repeatOption == 0) repeatOption = -1

                            binding.btnEveryDaySchedule.visibility = View.GONE
                            binding.everyWeekLayout.visibility = View.GONE
                            binding.gridMonthSchedule.visibility = View.GONE

                            if(binding.repeatStartDateBtn.text.toString().substring(5,7) !=
                                binding.repeatEndDateBtn.text.toString().substring(5,7)) {
                                binding.gridYearSchedule.visibility = View.GONE
                            } else if(repeatOption == 4){
                                binding.gridYearSchedule.visibility = View.VISIBLE
                            }
                        } else {
                            optionChange(binding)
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

        binding.btnCloseSchedule.setOnClickListener { dismiss() }

        binding.alldaySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
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
            if(isChecked){
                binding.repeatTvSchedule.setTextColor(Color.parseColor("#191919"))
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))

                binding.repeatOptionSelectSchedule.visibility = View.VISIBLE
                binding.repeatEndLayout.visibility = View.VISIBLE

                if(repeatOption == 1 || repeatOption == 2){
                    binding.everyWeekLayout.visibility = View.VISIBLE
                } else if(repeatOption == 3){
                    binding.gridMonthSchedule.visibility = View.VISIBLE
                } else if(repeatOption == 4){
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

        binding.btnEveryDaySchedule.setOnClickListener{onOptionClick(binding, 0)}
        binding.btnEveryWeekSchedule.setOnClickListener{onOptionClick(binding, 1)}
        binding.btnEvery2WeekSchedule.setOnClickListener{onOptionClick(binding, 2)}
        binding.btnEveryMonthSchedule.setOnClickListener{onOptionClick(binding, 3)}
        binding.btnEveryYearSchedule.setOnClickListener{onOptionClick(binding, 4)}

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER

            textView.setOnClickListener {
                if(!monthsValue[i-1]){
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                    monthsValue[i-1] = true
                } else {
                    textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
                    monthsValue[i-1] = false
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
                if(!yearsValue[i-1]){
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                    yearsValue[i-1] = true
                } else {
                    textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
                    yearsValue[i-1] = false
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
            if(isChecked){
                binding.btnRepeatEndDateSchedule.visibility = View.VISIBLE
                binding.btnRepeatEndDateSchedule.text = dateParser.format(repeatEndCalendar.time)
            } else {
                binding.btnRepeatEndDateSchedule.visibility = View.GONE
            }
        }

        binding.categoryChooseIv.setOnClickListener {
            val dlg = CategoryChooseDialog(this)

            dlg.show(categories){
                category = it

                val drawable = binding.categoryChooseIv.background as VectorDrawable
                drawable.setColorFilter(Color.parseColor(it.color),PorterDuff.Mode.SRC_ATOP)
            }
        }

        binding.btnRepeatEndDateSchedule.setOnClickListener {
            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                0,
                binding.btnRepeatEndDateSchedule.text.toString().length-2
            )

            val dateFormat = SimpleDateFormat("yyyy.MM.dd")
            val repeatEndDateCalendar = Calendar.getInstance()
            repeatEndDateCalendar.time = dateFormat.parse(repeatEndDateText)

            val datePicker = CustomCalendarDialog(repeatEndDateCalendar.time, repeatEndCalendar.time)
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

        for(i in 0..6){
            binding.everyWeekLayout.getChildAt(i).setOnClickListener {
                val view = it as TextView

                if(!weeksValue[i]){
                    weeksValue[i] = true
                    view.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                } else {
                    weeksValue[i] = false
                    view.setTextColor(Color.LTGRAY)
                }
            }
        }

        binding.btnSubmitSchedule.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)
            val repeatEndDateBtnFormat = SimpleDateFormat("yyyy.MM.dd EE", Locale.KOREAN)
            val timeFormat = SimpleDateFormat("a HH:mm", Locale.KOREA)
            val timeFormat2 = SimpleDateFormat("HH:mm", Locale.KOREA)

            var repeatStartDate = ""
            var repeatEndDate = ""

            if(!binding.alldaySwitch.isChecked) {
                val startTime = timeFormat.parse(binding.repeatStartTimeBtn.text.toString())
                val endTime = timeFormat.parse(binding.repeatEndTimeBtn.text.toString())

                if(!binding.repeatSwitchSchedule.isChecked) {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            timeFormat2.format(startTime)+
                            ":00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) +
                            "T" +
                            timeFormat2.format(endTime)+
                            ":00+09:00"
                } else if(!binding.repeatEndDateSwitchSchedule.isChecked){
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            timeFormat2.format(startTime)+
                            ":00+09:00"
                    val calendarClone = repeatStartCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR,100)

                    repeatEndDate = dateFormat.format(calendarClone.time) +
                            "T" +
                            timeFormat2.format(endTime)+
                            ":00+09:00"
                } else {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            timeFormat2.format(startTime)+
                            ":00+09:00"
                    repeatEndDate = dateFormat.format(
                        repeatEndDateBtnFormat.parse(
                            binding.btnRepeatEndDateSchedule.text.toString()
                        )
                    ) +
                            "T" +
                            timeFormat2.format(endTime)+
                            ":00+09:00"
                }

                Log.d("반복 일정", repeatStartDate)
                Log.d("반복 일정", repeatEndDate)
            } else {
                if(!binding.repeatSwitchSchedule.isChecked) {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) + "T23:59:55+09:00"
                } else if(!binding.repeatEndDateSwitchSchedule.isChecked){
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"

                    val calendarClone = repeatEndCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR,100)

                    repeatEndDate = dateFormat.format(calendarClone.time) + "T23:59:55+09:00"
                } else {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = dateFormat.format(
                        repeatEndDateBtnFormat.parse(
                            binding.btnRepeatEndDateSchedule.text.toString()
                        )
                    )+ "T23:59:55+09:00"

                    Log.d("20191630",repeatEndDate)
                }
            }

            val option = when(repeatOption){
                0->"매일"
                1->"매주"
                2->"격주"
                3->"매달"
                4->"매년"
                else -> null
            }

            var repeatvalue: String? = ""

            if(binding.repeatStartDateBtn.text.toString() == binding.repeatEndDateBtn.text.toString()) {
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

                        else -> {
                            repeatvalue = null
                        }
                    }
                }
            } else {
                if(binding.alldaySwitch.isChecked){
                    repeatEndCalendar.apply {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 55)
                    }
                    repeatvalue = "T"+(repeatEndCalendar.time.time - repeatStartCalendar.time.time).toInt().toString()
                } else {
                    val startTime = timeFormat.parse(binding.repeatStartTimeBtn.text.toString())
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

                    repeatvalue = "T"+(repeatEndCalendar.time.time - repeatStartCalendar.time.time).toInt().toString()
                }
            }

            val calendarViewModel = CalendarViewModel()

            Log.d("20191630", repeatStartDate)
            Log.d("20191630", repeatEndDate)

            calendarViewModel.postSchedule(
                PostSchedule(
                    binding.scheduleContentEt.text.toString(),
                    binding.etMemoSchedule.text.toString(),
                    isAllday,
                    repeatStartDate,
                    repeatEndDate,
                    option,
                    repeatvalue,
                    category?.id,
                    emptyList()
                )
            ) {
                adapter.notifyDataSetChanged()
                dismiss()
            }
        }

        return binding.root
    }

    fun optionChange(binding: FragmentCalendarInputBinding){
        binding.btnEveryDaySchedule.visibility = View.VISIBLE

        if(repeatOption == 1 || repeatOption == 2){
            binding.everyWeekLayout.visibility = View.VISIBLE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        } else if(repeatOption == 3) {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.VISIBLE
            binding.gridYearSchedule.visibility = View.GONE
        } else if(repeatOption == 4){
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.VISIBLE
        } else {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        }
    }

    fun onOptionClick(binding: FragmentCalendarInputBinding, opt:Int){
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

        if(opt == 1 || opt == 2){
            if(binding.repeatStartDateBtn.text == binding.repeatEndDateBtn.text) {
                binding.everyWeekLayout.visibility = View.VISIBLE
            }

            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        } else if(opt == 3) {
            binding.everyWeekLayout.visibility = View.GONE

            if(binding.repeatStartDateBtn.text == binding.repeatEndDateBtn.text) {
                binding.gridMonthSchedule.visibility = View.VISIBLE
            }

            binding.gridYearSchedule.visibility = View.GONE
        } else if(opt == 4){
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE

            if(binding.repeatStartDateBtn.text.substring(5,7) ==
                binding.repeatEndDateBtn.text.substring(5,7)) {
                binding.gridYearSchedule.visibility = View.VISIBLE
            }
        } else {
            binding.everyWeekLayout.visibility = View.GONE
            binding.gridMonthSchedule.visibility = View.GONE
            binding.gridYearSchedule.visibility = View.GONE
        }

        repeatOption = opt
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 85 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}
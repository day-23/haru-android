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
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.haru.R
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.Category
import com.example.haru.data.model.PostSchedule
import com.example.haru.databinding.FragmentCalendarInputBinding
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import com.example.haru.view.calendar.CategoryChooseDialog
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

        repeatStartCalendar.set(Calendar.HOUR, 0)
        repeatStartCalendar.set(Calendar.MINUTE, 0)
        repeatStartCalendar.set(Calendar.SECOND,0)

        repeatEndCalendar.set(Calendar.HOUR, 0)
        repeatEndCalendar.set(Calendar.MINUTE, 0)
        repeatEndCalendar.set(Calendar.SECOND,0)

        val dateParser = SimpleDateFormat("yyyy-MM-dd EE", Locale.KOREAN)
        val timeParser = SimpleDateFormat("aa hh:mm", Locale.KOREAN)

        if(initStartDate != null && initEndDate != null){
            repeatStartCalendar.time = initStartDate
            repeatEndCalendar.time = initEndDate
        }

        binding.repeatStartDateBtn.text = dateParser.format(repeatStartCalendar.time)
        binding.repeatEndDateBtn.text = dateParser.format(repeatEndCalendar.time)

        if(binding.repeatStartDateBtn.text != binding.repeatEndDateBtn.text){
            binding.btnEveryDaySchedule.visibility = View.GONE
        }

        binding.repeatStartTimeBtn.text = timeParser.format(repeatStartCalendar.time)
        binding.repeatEndTimeBtn.text = timeParser.format(repeatEndCalendar.time)

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

                    binding.repeatStartDateBtn.text = dateParser.format(repeatStartCalendar.time)

                    if(binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()){
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

                    binding.repeatEndDateBtn.text = dateParser.format(repeatEndCalendar.time)

                    if(binding.repeatStartDateBtn.text.toString() != binding.repeatEndDateBtn.text.toString()){
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
                    textView.setTextColor(Color.CYAN)
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
                    textView.setTextColor(Color.CYAN)
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

                binding.categoryEt.text = it.content
            }
        }

        binding.btnRepeatEndDateSchedule.setOnClickListener {
            val repeatEndDateText = binding.btnRepeatEndDateSchedule.text.toString().substring(
                0,
                binding.btnRepeatEndDateSchedule.text.toString().length-2
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

                    binding.btnRepeatEndDateSchedule.text = dateParser.format(calendar.time)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        for(i in 0..6){
            binding.everyWeekLayout.getChildAt(i).setOnClickListener {
                val view = it as TextView

                if(!weeksValue[i]){
                    weeksValue[i] = true
                    view.setTextColor(Color.CYAN)
                } else {
                    weeksValue[i] = false
                    view.setTextColor(Color.LTGRAY)
                }
            }
        }

        binding.btnSubmitSchedule.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

            var repeatStartDate = ""
            var repeatEndDate = ""

            if(!binding.alldaySwitch.isChecked) {
                if(!binding.repeatSwitchSchedule.isChecked || !binding.repeatEndDateSwitchSchedule.isChecked) {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) +
                            "T" +
                            binding.repeatEndTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatEndTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"
                } else if(!binding.repeatEndDateSwitchSchedule.isChecked){
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"
                    val calendarClone = repeatStartCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR,100)

                    repeatEndDate = dateFormat.format(calendarClone.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"
                } else {
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"
                    repeatEndDate = binding.btnRepeatEndDateSchedule.text.toString().substring(
                        0,
                        binding.btnRepeatEndDateSchedule.text.toString().length-2
                    ) +
                            "T" +
                            binding.repeatStartTimeBtn.text.toString().substring(3,5) +
                            ":" +
                            binding.repeatStartTimeBtn.text.toString().substring(6,8) +
                            ":00+09:00"

                    Log.d("반복 날짜", repeatEndDate)
                }
            } else {
                if(!binding.repeatSwitchSchedule.isChecked || !binding.repeatEndDateSwitchSchedule.isChecked) {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = dateFormat.format(repeatEndCalendar.time) + "T00:00:00+09:00"
                } else if(!binding.repeatEndDateSwitchSchedule.isChecked){
                    repeatStartDate = dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"

                    val calendarClone = repeatStartCalendar.clone() as Calendar
                    calendarClone.add(Calendar.YEAR,100)

                    repeatEndDate = dateFormat.format(calendarClone.time) + "T00:00:00+09:00"
                } else {
                    repeatStartDate =
                        dateFormat.format(repeatStartCalendar.time) + "T00:00:00+09:00"
                    repeatEndDate = binding.btnRepeatEndDateSchedule.text.toString().substring(
                        0,
                        binding.btnRepeatEndDateSchedule.text.toString().length-2
                    ) + "T00:00:00+09:00"

                    Log.d("repeatEndDate",repeatEndDate)
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

            var repeatvalue: String? = null

            if(binding.repeatStartDateBtn.text.toString() == binding.repeatEndDateBtn.text.toString()) {
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
                if(binding.alldaySwitch.isChecked){
                    repeatvalue = "T"+(repeatEndCalendar.time.time - repeatStartCalendar.time.time).toString()
                } else {
                    repeatStartCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(3,5).toInt()
                    )

                    repeatStartCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(6,8).toInt()
                    )

                    repeatEndCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(3,5).toInt()
                    )

                    repeatEndCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        binding.repeatStartTimeBtn.text.toString().substring(6,8).toInt()
                    )

                    repeatvalue = "T"+((repeatEndCalendar.time.time - repeatStartCalendar.time.time)/(1000 * 60 * 60 * 24)).toString()
                }
            }

            val calendarViewModel = CalendarViewModel()

            if(category != null) {
                calendarViewModel.postSchedule(
                    PostSchedule(
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
                    adapter.notifyDataSetChanged()
                    dismiss()
                }
            } else {
                calendarViewModel.postSchedule(
                    PostSchedule(
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
                    adapter.notifyDataSetChanged()
                    dismiss()
                }
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
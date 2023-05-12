package com.example.haru.view.customDialog


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.haru.R
import com.example.haru.databinding.CustomCalendarBinding
import com.example.haru.utils.FormatDate
import java.util.*

class CustomCalendarDialog(date: Date? = null, endDate: Date? = null) : DialogFragment() {
    private lateinit var binding: CustomCalendarBinding

    private var changedMonth: Int = -1
    private var changedYear: Int = -1
    private var startDay: DayOfWeek? = null
//    private var endDay: DayOfWeek? = null

    private var maxDay: Int = -1

    private var nowYear: Int = -1
    private var nowMonth: Int = -1
    private var nowDay: Int = -1

    private var endDateYear: Int = -1
    private var endDateMonth: Int = -1
    private var endDateDay: Int = -1

//    private var standardYear = -1
//    private var standardMonth = -1
//    private var standardDay = -1

    interface CalendarClickListener {
        fun onClick(view: View, year: Int, month: Int, day: Int)
    }

    var calendarClick: CalendarClickListener? = null

    init {
        if (endDate != null) {
            FormatDate.cal.time = endDate
            endDateYear = FormatDate.cal.get(Calendar.YEAR)
            endDateMonth = FormatDate.cal.get(Calendar.MONTH)
            endDateDay = FormatDate.cal.get(Calendar.DAY_OF_MONTH)
        }

        FormatDate.cal.time = Date()

        if (date != null)
            FormatDate.cal.time = date

        nowYear = FormatDate.cal.get(Calendar.YEAR)
        changedYear = nowYear

        nowMonth = FormatDate.cal.get(Calendar.MONTH)
        changedMonth = nowMonth

        nowDay = FormatDate.cal.get(Calendar.DAY_OF_MONTH)
    }

    enum class DayOfWeek {
        SUN, MON, TUES, WED, THUR, FRI, SAT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CustomCalendarBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Custom Date Picker", Toast.LENGTH_SHORT).show()

        initCalendar()

        binding.ivDownArrow.visibility = View.INVISIBLE

        binding.ivLeftArrow.setOnClickListener {
            updateCalendar(-1)
            binding.tvDate.text = "${changedYear}년 ${changedMonth + 1}월"
        }

        binding.ivRightArrow.setOnClickListener {
            updateCalendar(1)
            binding.tvDate.text = "${changedYear}년 ${changedMonth + 1}월"
        }

    }

    override fun onResume() {
        super.onResume()

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width = 0.7f
        val height = 0.45f
        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    private fun initCalendar() {
//        if (standardYear == changedYear && standardMonth == changedMonth)
//            binding.ivLeftArrow.visibility = View.GONE

        FormatDate.cal.set(Calendar.DAY_OF_MONTH, 1)

        startDay = when (FormatDate.cal.get(Calendar.DAY_OF_WEEK) - 1) {
            0 -> DayOfWeek.SUN
            1 -> DayOfWeek.MON
            2 -> DayOfWeek.TUES
            3 -> DayOfWeek.WED
            4 -> DayOfWeek.THUR
            5 -> DayOfWeek.FRI
            6 -> DayOfWeek.SAT
            else -> null
        }

        maxDay = FormatDate.cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        FormatDate.cal.set(Calendar.DAY_OF_MONTH, maxDay)

        // 달력을 생성할 때의 년과 달이 endDate의 년과 달과 동일한지 비교
        val beforeFlag = nowYear == endDateYear && nowMonth == endDateMonth

        var days = 1
        var flag = false
        for (i in 0 until 6) {
            val layoutInflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val addView =
                layoutInflater.inflate(R.layout.custom_calendar_days_layout, null) as LinearLayout
            if (maxDay + 1 == days)
                addView.visibility = View.GONE

            for (k in 0 until addView.childCount) {
                val view = addView.getChildAt(k) as TextView
                view.setOnClickListener { // 날짜가 눈에 보이고, 색깔이 date_text인 것만 클릭 기능 수행
                    if (it.visibility == View.VISIBLE && view.currentTextColor == ContextCompat.getColor(
                            requireContext(),
                            R.color.date_text
                        )
                    ) {
                        calendarClick?.onClick(
                            it,
                            changedYear,
                            changedMonth,
                            (it as TextView).text.toString().toInt()
                        )
                        dismiss()
                    }
                }
                if (i == 0 && k == startDay!!.ordinal) flag = true
                if (flag) {
                    view.visibility = View.VISIBLE
                    view.text = days.toString()
                    if (nowDay == days) view.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.highlight)
                    ) else if (beforeFlag && days < endDateDay)
                        view.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_gray
                            )
                        )
                    days++
                }
                if (maxDay + 1 == days)
                    flag = false
            }
            binding.daysParentLayout.addView(addView)
        }
        binding.tvDate.text = "${changedYear}년 ${changedMonth + 1}월"
    }

    private fun updateCalendar(flag: Int) {  // 1이면 증가, -1이면 감소
        if (changedMonth == -1 || changedYear == -1 || maxDay == -1)
            return

        if (flag == 1) { // 오른쪽으로 이동
            if (changedMonth >= 11) {
                changedMonth = 0
                changedYear++
            } else changedMonth++

            startDay = DayOfWeek.values()[(startDay!!.ordinal + maxDay) % 7]   // 변경될 달의 시작 요일
            FormatDate.cal.set(changedYear, changedMonth, 1)
            maxDay = FormatDate.cal.getActualMaximum(Calendar.DAY_OF_MONTH)  // 변경될 달의 최대 일자


        } else if (flag == -1) {  // 왼쪽으로 이동
            if (changedMonth <= 0) {
                changedMonth = 11
                changedYear--
            } else changedMonth--

            FormatDate.cal.set(changedYear, changedMonth, 1)
            maxDay = FormatDate.cal.getActualMaximum(Calendar.DAY_OF_MONTH)  // 변경될 달의 최대 일자
            val toPlusNumber =
                if (startDay!!.ordinal - maxDay < -28) 35 else 28 // beforeStart = 7 * ? + Start - Max
            startDay =
                DayOfWeek.values()[startDay!!.ordinal - maxDay + toPlusNumber] // 변경 될 달의 날짜 시작 요일
        }

        var days = 1
        var isEnabled = false

        for (i in 0 until 6) {
            val layout = binding.daysParentLayout.getChildAt(i + 1) as LinearLayout
            if (maxDay + 1 == days)
                layout.visibility = View.GONE
            else layout.visibility = View.VISIBLE
            for (k in 0 until 7) {
                val view = layout.getChildAt(k) as TextView
                if (i == 0 && k == startDay!!.ordinal) isEnabled = true
                if (isEnabled) {
                    view.visibility = View.VISIBLE
                    view.text = days.toString()
                    if (nowYear == changedYear && nowMonth == changedMonth && nowDay == days)
                        view.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.highlight
                            )
                        )
                    else if (endDateYear > changedYear) // 마감일의 년도가 변경해야하는 달력의 년도보다 클 때
                        view.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_gray
                            )
                        )
                    else if (endDateYear == changedYear && endDateMonth > changedMonth)
                        view.setTextColor( // 마감일의 년도와 변경해야하는 달력의 년도가 같고, 마감일의 달이 더 클때
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_gray
                            )
                        )
                    else if (endDateYear == changedYear && endDateMonth == changedMonth && days < endDateDay)
                        view.setTextColor( // 마감일의 년도와 월이 변경해야하는 달력의 년도와 월과 같을 때
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.light_gray
                            )
                        )
                    else
                        view.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.date_text
                            )
                        )
                    days++
                    if (maxDay + 1 == days)
                        isEnabled = false
                } else {
                    view.visibility = View.INVISIBLE
                }
            }
        }
    }


}
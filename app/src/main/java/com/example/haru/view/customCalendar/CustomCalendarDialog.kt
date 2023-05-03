package com.example.haru.view.customCalendar


import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.haru.R
import com.example.haru.databinding.CustomCalendarBinding
import com.example.haru.utils.FormatDate
import java.util.*

class CustomCalendarDialog(date : Date? = null) : DialogFragment() {
    private lateinit var binding: CustomCalendarBinding
    private var changedMonth: Int = -1
    private var changedYear: Int = -1
    private var startDay: DayOfWeek? = null
//    private var endDay: DayOfWeek? = null

    private var maxDay : Int = -1

    private var nowYear : Int = -1
    private var nowMonth : Int = -1
    private var nowDay : Int = -1

    init {
        if (date == null)
            FormatDate.cal.time = Date()
        else
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

//        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Custom Date Picker", Toast.LENGTH_SHORT).show()


        initCalendar()
        binding.tvDate.text = "${changedYear}년 ${changedMonth + 1}월"

        binding.ivDownArrow.setOnClickListener {

        }

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


//        window?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//
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

//        endDay = when (FormatDate.cal.get(Calendar.DAY_OF_WEEK) - 1) {
//            0 -> DayOfWeek.SUN
//            1 -> DayOfWeek.MON
//            2 -> DayOfWeek.TUES
//            3 -> DayOfWeek.WED
//            4 -> DayOfWeek.THUR
//            5 -> DayOfWeek.FRI
//            6 -> DayOfWeek.SAT
//            else -> null
//        }


        var days = 1
        var flag = false
        for (i in 0 until 6) {
            val layoutInflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val addView =
                layoutInflater.inflate(R.layout.custom_calendar_days_layout, null) as LinearLayout
            if (maxDay + 1 == days)
                addView.visibility = View.GONE
            else
                for (k in 0 until addView.childCount) {
                    if (i == 0 && k == startDay!!.ordinal) flag = true
                    if (flag) {
                        (addView.getChildAt(k) as TextView).text = days.toString()
                        if (nowDay == days) (addView.getChildAt(k) as TextView).setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.highlight)
                        )
                        days++
                    }
                    if (maxDay + 1 == days)
                        break
                }
            binding.daysParentLayout.addView(addView)
        }
    }

    fun updateCalendar(flag: Int) {  // 1이면 증가, -1이면 감소
        if (changedMonth == -1 || changedYear == -1 || maxDay == -1)
            return

        if (flag == 1) {
            if (changedMonth >= 11) {
                changedMonth = 0
                changedYear++
            } else changedMonth++

            FormatDate.cal.set(changedYear, changedMonth, 1)

            startDay = DayOfWeek.values()[(startDay!!.ordinal + maxDay) % 7]   // 변경될 달의 시작 요일

            maxDay = FormatDate.cal.getActualMaximum(Calendar.DAY_OF_MONTH)  // 변경될 달의 최대 일자

            var days = 1
            var flag = false

            for(i in 0 until 6){
                val layout = binding.daysParentLayout.getChildAt(i + 1) as LinearLayout
                if (maxDay + 1 == days)
                    layout.visibility = View.GONE
                else layout.visibility = View.VISIBLE
                for(k in 0 until 7){
                    val view = layout.getChildAt(k) as TextView
                    if (i == 0 && k == startDay!!.ordinal) flag = true
                    if (flag){
                        view.visibility = View.VISIBLE
                        view.text = days.toString()
                        if (nowYear == changedYear && nowMonth == changedMonth && nowDay == days)
                            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.highlight))
                        else if (nowDay == days)
                            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.date_text))
                        days++
                        if (maxDay + 1 == days)
                            flag = false
                    } else {
                        view.visibility = View.INVISIBLE
                    }
                }
            }


        }
    }


}
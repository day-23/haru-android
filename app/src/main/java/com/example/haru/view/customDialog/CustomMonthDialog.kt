package com.example.haru.view.customDialog

import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.NumberPicker
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.haru.R
import com.example.haru.databinding.CustomMonthPickerBinding
import com.example.haru.databinding.CustomTimePickerBinding
import com.example.haru.utils.FormatDate
import java.util.*

class CustomMonthDialog(date: Date? = null) : DialogFragment() {
    private lateinit var binding: CustomMonthPickerBinding
    private var yearList: Array<String> = Array(200) { "" }
    private var monthList: Array<String> = Array(12) { "" }
    private var startYear: Int = 11
    private var startMonth: Int = 0

    interface MonthPickerClickListener {
        fun onClick(yearNumberPicker : NumberPicker, monthNumberPicker: NumberPicker)
    }

    var monthPickerClick: MonthPickerClickListener? = null

    init {
        if (date != null) {
            FormatDate.cal.time = date
            startYear = FormatDate.cal.get(Calendar.YEAR)
            startMonth = FormatDate.cal.get(Calendar.MONTH)
        }

        for (i in 0 until 200) {
            val year = (i + 1923).toString()+"년"
            yearList[i] = year
        }

        for (i in 0 until 12) {
            monthList[i] = (i+1).toString()+"월"
        }
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
        binding = CustomMonthPickerBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Custom Month Picker", Toast.LENGTH_SHORT).show()

        binding.tvDate.text = "${startYear}년 ${startMonth+1}월"

        binding.npYearPick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 199
            value = startYear-1923
            displayedValues = yearList
        }

        binding.npMonthPick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 11
            value = startMonth
            displayedValues = monthList
        }

        binding.emptyView.setOnClickListener {
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        monthPickerClick?.onClick(binding.npYearPick, binding.npMonthPick)
        super.onCancel(dialog)
    }

    override fun onResume() {
        super.onResume()

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width = 0.7f
        val height = 0.5f
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
}
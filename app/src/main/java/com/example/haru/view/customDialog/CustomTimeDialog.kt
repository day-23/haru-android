package com.example.haru.view.customDialog

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.haru.R
import com.example.haru.databinding.CustomTimePickerBinding
import com.example.haru.utils.FormatDate
import java.util.*

class CustomTimeDialog(date: Date? = null) : DialogFragment() {
    private lateinit var binding: CustomTimePickerBinding
    private var hourList: Array<String> = Array(12) { "" }
    private var minuteList: Array<String> = Array(12) { "" }
    private var timeList: Array<String> = Array(2) { "" }
    private var startHour: Int = 11
    private var startMinute: Int = 0
    private var flag = 1   // 0이면 오전, 1이면 오후

    interface TimePickerClickListener {
        fun onClick(timeDivider : NumberPicker, hourNumberPicker: NumberPicker, minuteNumberPicker: NumberPicker)
    }

    var timePickerClick: TimePickerClickListener? = null

    init {
        if (date != null) {
            FormatDate.cal.time = date
            startHour = FormatDate.cal.get(Calendar.HOUR_OF_DAY) // 0 ~ 23
            startMinute = FormatDate.cal.get(Calendar.MINUTE) // 0 ~ 59
            val remain = startMinute % 5
            if (remain < 3)
                startMinute -= remain
            else startMinute += (5 - remain)
            startMinute /= 5
            if (startHour > 12) {
                // 오후로 설정 하고 index = startHour % 12 - 1
                startHour = startHour % 12 - 1
                flag = 1
            } else if (startHour == 12) {
                // index = 11 오후로 설정
                startHour = 11
                flag = 1
            } else if (startHour == 0) {
                // 오전으로 설정하고 index = 11
                startHour = 11
                flag = 0
            } else if (startHour < 12) {
                // indxe = startHour - 1 오전으로 설정
                startHour -= 1
                flag = 0
            }
        }

        timeList[0] = "오전"
        timeList[1] = "오후"

        for (i in 0 until 12) {
            val hour = (i + 1).toString()
            hourList[i] = if (hour.length < 2) "0$hour" else hour
        }

        for (i in 0 until 12) {
            val minute = (i * 5).toString()
            minuteList[i] = if (minute.length < 2) "0$minute" else minute
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
        binding = CustomTimePickerBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Toast.makeText(requireContext(), "Custom Time Picker", Toast.LENGTH_SHORT).show()

        binding.timeDivision.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 1
            value = flag
            displayedValues = timeList
            wrapSelectorWheel = false

        }
//        binding.timeDivision.

        binding.npHourPick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 11
            value = startHour
            displayedValues = hourList
            setOnValueChangedListener { numberPicker, oldVal, newVal ->
                if ((oldVal == 0 && newVal == 11) || (oldVal == 11 && newVal == 0)){
                    if (binding.timeDivision.value == 1)
                        binding.timeDivision.value = 0
                    else binding.timeDivision.value = 1
//                    if (binding.timeDivision.value == 0){
//                        animateNumberPicker(binding.timeDivision, binding.timeDivision.height, binding.timeDivision.height + binding.timeDivision.height / 3)
//                        binding.timeDivision.value = 1
//                    }
//                    else{
//                        animateNumberPicker(binding.timeDivision, binding.timeDivision.height, binding.timeDivision.height - binding.timeDivision.height / 3)
//                        binding.timeDivision.value = 0
//                    }
//                    this.value = 1
                }
            }
        }

        binding.npMinutePick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 11
            value = startMinute
            displayedValues = minuteList
        }

        binding.btnPositive.setOnClickListener {
            if (timePickerClick != null) {
                timePickerClick?.onClick(binding.timeDivision, binding.npHourPick, binding.npMinutePick)
                dismiss()
            }
        }

        binding.btnNegative.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width = 0.7f
        val height = 0.3f
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

//    private fun animateNumberPicker(numberPicker: NumberPicker, start: Int, end: Int) {
//        val valueAnimator = ValueAnimator.ofInt(start, end)
//        valueAnimator.duration = 400
//        valueAnimator.addUpdateListener {
//            numberPicker.translationY = it.animatedValue as Float
//        }
//        valueAnimator.start()
//    }
}
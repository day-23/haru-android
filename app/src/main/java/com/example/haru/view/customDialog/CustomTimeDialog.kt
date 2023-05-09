package com.example.haru.view.customDialog

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.haru.databinding.CustomTimePickerBinding
import kotlin.math.min

class CustomTimeDialog : DialogFragment() {
    private lateinit var binding : CustomTimePickerBinding
    private var hourList : Array<String> = Array(24){""}
    private var minuteList : Array<String> = Array(12){""}

    init {
        for(i in 0 until 24){
            val hour = i.toString()
            hourList[i] = if (hour.length < 2) "0$hour" else hour
        }

        for (i in 0 until 12){
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

        Log.d("20191627", hourList.toString())
        Log.d("20191627", minuteList.toString())

        binding.npHourPick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 23
            value = 12
            displayedValues = hourList
            wrapSelectorWheel = false
        }

        binding.npMinutePick.apply {
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 0
            maxValue = 11
            displayedValues = minuteList
            wrapSelectorWheel = false
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
}
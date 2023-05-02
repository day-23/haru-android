package com.example.haru.view.customCalendar


import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.haru.R
import com.example.haru.databinding.CustomCalendarBinding

class CustomCalendarDialog : DialogFragment() {
    private lateinit var binding : CustomCalendarBinding

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

        var days = 1
        for(i in 0 until 6){
            val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val addView = layoutInflater.inflate(R.layout.custom_calendar_days_layout, null) as LinearLayout
            for(k in 0 until addView.childCount){
                (addView.getChildAt(k) as TextView).text = days.toString()
                days++
            }
            binding.daysParentLayout.addView(addView)

        }
    }

    override fun onResume() {
        super.onResume()

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
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



}
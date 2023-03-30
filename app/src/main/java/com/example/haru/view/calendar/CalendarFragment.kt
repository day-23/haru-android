package com.example.haru.view.calendar

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.view.adapter.AdapterMonth
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapterMonth: AdapterMonth

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CalendarFragment - onCreateView() called")

        binding = FragmentCalendarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarTodayTv = view.findViewById<TextView>(R.id.calendar_today_tv)
        val categoryButtonImv = view.findViewById<ImageView>(R.id.category_button_imv)
        val categoryDrawerLayout = view.findViewById<DrawerLayout>(R.id.category_drawerlayout)
        val month_viewpager = view.findViewById<ViewPager2>(R.id.month_viewpager)
        val item_month_btn = view.findViewById<Button>(R.id.item_month_btn)

        val calendar = Calendar.getInstance()

        calendar.time = Date()

        calendarTodayTv.text = calendar.time.date.toString()

        calendarTodayTv.setOnClickListener{
            month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, true)
            item_month_btn.text = "${Date().year+1900}년 ${Date().month+1}월"
        }

        categoryButtonImv.setOnClickListener{
            if(!categoryDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                categoryDrawerLayout.openDrawer(Gravity.RIGHT)
            } else {
                categoryDrawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }

        month_viewpager.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

        adapterMonth = AdapterMonth(viewLifecycleOwner, view)
        month_viewpager.adapter = adapterMonth

        month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)

        val callback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pos: Int) {
                super.onPageSelected(pos)

                adapterMonth.setPosition(pos - Int.MAX_VALUE / 2)

                calendar.time = Date()
                calendar.add(Calendar.MONTH, pos - Int.MAX_VALUE / 2)

                item_month_btn.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
            }
        }

        month_viewpager.registerOnPageChangeCallback(callback)
    }
}
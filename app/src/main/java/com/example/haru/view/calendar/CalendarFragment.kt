package com.example.haru.view.calendar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.Schedule
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.adapter.CategoryAdapter
import com.example.haru.view.checklist.ChecklistInputFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapterMonth: AdapterMonth
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var checkListViewModel: CheckListViewModel

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val status = result.data?.getSerializableExtra("status") as String

        if (result.resultCode == Activity.RESULT_OK) {
            if(status == "update") {
                val index = result.data?.getSerializableExtra("index") as Int
                val data = result.data?.getSerializableExtra("category2") as Category

                categoryAdapter.dataChanged(index, data)
            } else if(status == "delete"){
                val index = result.data?.getSerializableExtra("index") as Int

                categoryAdapter.dataDelete(index)
            } else if(status == "post"){
                val data = result.data?.getSerializableExtra("category2") as Category
                categoryAdapter.dataAdd(data)
            }
        }
    }

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")

        checkListViewModel = CheckListViewModel()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarTodayTv = view.findViewById<TextView>(R.id.calendar_today_tv)

        val categoryAddImage = view.findViewById<ImageView>(R.id.category_add_image)
        val categoryButtonImv = view.findViewById<ImageView>(R.id.category_button_imv)

        val categoryDrawerLayout = view.findViewById<DrawerLayout>(R.id.category_drawerlayout)

        val month_viewpager = view.findViewById<ViewPager2>(R.id.month_viewpager)

        val item_month_btn = view.findViewById<Button>(R.id.item_month_btn)

        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.category_recyclerView)

        val btnAddTodoInCalendar = view.findViewById<FloatingActionButton>(R.id.btn_add_todo_incalendar)

        val todoApplyLayout = view.findViewById<LinearLayout>(R.id.todo_apply_layout)
        val todoApplyImv = view.findViewById<ImageView>(R.id.todo_apply_imv)
        val todoApplyTv = view.findViewById<TextView>(R.id.todo_apply_tv)

        val scheduleApplyLayout = view.findViewById<LinearLayout>(R.id.schedule_apply_layout)
        val scheduleApplyImv = view.findViewById<ImageView>(R.id.schedule_apply_imv)
        val scheduleApplyTv = view.findViewById<TextView>(R.id.schedule_apply_tv)

        val categoryOkTv = view.findViewById<TextView>(R.id.category_ok_tv)
        val allBlindTv = view.findViewById<TextView>(R.id.all_blind_tv)

        val calendar = Calendar.getInstance()

        val calendarViewModel = CalendarViewModel()

        calendar.time = Date()

        item_month_btn.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"

        if(calendarMainData.todoApply) {
            todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image)

            todoApplyTv.setTextColor(
                Color.parseColor("#1DAFFF")
            )
        } else {
            todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image_false)

            todoApplyTv.setTextColor(
                Color.parseColor("#BABABA")
            )
        }

        todoApplyLayout.setOnClickListener{
            if(calendarMainData.todoApply) {
                calendarMainData.todoApply = false

                todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image_false)

                todoApplyTv.setTextColor(
                    Color.parseColor("#BABABA")
                )
            } else {
                calendarMainData.todoApply = true

                todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image)

                todoApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )
            }

            categoryAdapter.dataAllChanged()
        }

        if(calendarMainData.scheduleApply) {
            scheduleApplyImv.setBackgroundResource(R.drawable.calendar_schedule_image)

            scheduleApplyTv.setTextColor(
                Color.parseColor("#1DAFFF")
            )
        } else {
            scheduleApplyImv.setBackgroundResource(R.drawable.calendar_schedule_image_false)

            scheduleApplyTv.setTextColor(
                Color.parseColor("#BABABA")
            )
        }

        scheduleApplyLayout.setOnClickListener{
            if(calendarMainData.scheduleApply) {
                calendarMainData.scheduleApply = false

                scheduleApplyImv.setBackgroundResource(R.drawable.calendar_schedule_image_false)

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#BABABA")
                )
            } else {
                calendarMainData.scheduleApply = true

                scheduleApplyImv.setBackgroundResource(R.drawable.calendar_schedule_image)

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )
            }

            categoryAdapter.dataAllChanged()
        }

        categoryOkTv.setOnClickListener {
            adapterMonth.notifyDataSetChanged()
            categoryDrawerLayout.closeDrawer(Gravity.RIGHT)
        }

        allBlindTv.setOnClickListener {
            var changeStatus = false

            if(calendarMainData.todoApply) {
                todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image_false)

                todoApplyTv.setTextColor(
                    Color.parseColor("#BABABA")
                )

                changeStatus = true
            }

            if(calendarMainData.scheduleApply) {
                scheduleApplyImv.setBackgroundResource(R.drawable.calendar_schedule_image_false)

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#BABABA")
                )

                changeStatus = true
            }

            calendarMainData.todoApply = false
            calendarMainData.scheduleApply = false

            Log.d("changeStatus", changeStatus.toString())

            if(changeStatus) {
                categoryAdapter.dataAllChanged()
            }
        }

        item_month_btn.setOnClickListener {
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)

            val dlg = DatePickerDialog(view.context, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    calendar.time = Date()

                    val diffCalendar = (year-calendar.get(Calendar.YEAR)) * 12 +
                            (month-calendar.get(Calendar.MONTH))

                    month_viewpager.setCurrentItem(Int.MAX_VALUE / 2 + diffCalendar, false)
                }
            }, year, month, date)
            dlg.show()
        }

        calendarTodayTv.text = calendar.time.date.toString()

        calendarTodayTv.setOnClickListener{
            month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
            item_month_btn.text = "${Date().year+1900}년 ${Date().month+1}월"
        }

        categoryButtonImv.setOnClickListener{
            if(!categoryDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                categoryDrawerLayout.openDrawer(Gravity.RIGHT)
            } else {
                categoryDrawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }

        categoryAddImage.setOnClickListener{
            val intent = Intent(view.context, CategoryAddActivity::class.java)
            resultLauncher.launch(intent)
        }

        btnAddTodoInCalendar.setOnClickListener {
            val todoInput = ChecklistInputFragment(checkListViewModel)
            todoInput.show(parentFragmentManager, todoInput.tag)
        }

        month_viewpager.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

        adapterMonth = AdapterMonth(viewLifecycleOwner, view)
        month_viewpager.adapter = adapterMonth

        val callback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pos: Int) {
                super.onPageSelected(pos)

                calendar.time = Date()
                calendar.add(Calendar.MONTH, pos - Int.MAX_VALUE / 2)

                item_month_btn.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
            }
        }

        month_viewpager.registerOnPageChangeCallback(callback)

        month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
        month_viewpager.offscreenPageLimit = 2

        calendarViewModel.getCategories()
        calendarViewModel.liveCategoryList.observe(viewLifecycleOwner){
            categoryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)

            val toArrayListIt = it as ArrayList<Category>
            categoryAdapter = CategoryAdapter(toArrayListIt){ category,index ->
                val intent = Intent(view.context, CategoryCorrectionActivity::class.java)
                intent.putExtra("category", category)
                intent.putExtra("index", index)
                resultLauncher.launch(intent)
            }

            categoryRecyclerView.adapter = categoryAdapter
        }
    }
}
package com.example.haru.view.calendar

import BaseActivity
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.Schedule
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.utils.Alarm
import com.example.haru.utils.FormatDate
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.adapter.CategoryAdapter
import com.example.haru.view.checklist.CalendarAddFragment
import com.example.haru.view.checklist.ChecklistInputFragment
import com.example.haru.view.customDialog.CustomMonthDialog
import com.example.haru.view.etc.AlarmWorker
import com.example.haru.view.sns.SearchFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment() : Fragment(), DrawerLayout.DrawerListener {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapterMonth: AdapterMonth
    private var lastIndex = -1
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var categoryDrawerLayout: DrawerLayout

    private lateinit var checkListViewModel: CheckListViewModel

    private var parentView: RelativeLayout? = null

    private var fabMain_status = false

    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        resultLauncher = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarFragment - onCreate() called")

        checkListViewModel = CheckListViewModel()

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    fun closeAddBtn(ev: MotionEvent?){
        if(ev != null && parentView != null) {
            val x = ev.x
            val y = ev.y

            val touchedView = findTouchedView(x, y)

            if (touchedView != null &&
                touchedView.id != R.id.btn_add_main_incalendar &&
                touchedView.id != R.id.btn_add_todo_incalendar &&
                fabMain_status
            ) {
                Log.d("touchedView","정상 진입")
                binding.btnAddMainIncalendar.setImageResource(R.drawable.big_add_fab)
                binding.btnAddTodoIncalendar.visibility = View.INVISIBLE
                binding.btnAddTodoIncalendar.setClickable(false);

                fabMain_status = false
            }
        }
    }

    private fun findTouchedView(x: Float, y: Float): View? {
        // 자식 뷰들을 순회하며 터치된 뷰를 찾는 로직을 구현합니다.
        for (i in 0 until parentView!!.childCount) {
            val child: View = parentView!!.getChildAt(parentView!!.childCount-i-1)
            val rect = Rect()
            child.getHitRect(rect)
            if (rect.contains(x.toInt(), y.toInt())) {
                return child
            }
        }
        return null
    }

    override fun onResume() {
        super.onResume()

        (activity as BaseActivity).adjustTopMargin(binding.calendarFragmentParentLayout.id)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).adjustTopMargin(binding.calendarFragmentParentLayout.id)

        parentView = view.findViewById<RelativeLayout>(R.id.calendar_fragment_parent_layout)

        val calendarTodayTv = view.findViewById<TextView>(R.id.calendar_today_tv)

        val categoryAddImage = view.findViewById<LinearLayout>(R.id.category_add_image)
        val categoryButtonImv = view.findViewById<ImageView>(R.id.category_button_imv)

        categoryDrawerLayout = view.findViewById<DrawerLayout>(R.id.category_drawerlayout)

        val month_viewpager = view.findViewById<ViewPager2>(R.id.month_viewpager)

        val calendarMonthChooseLayout = view.findViewById<LinearLayout>(R.id.calendar_month_choose_layout)
        val itemYearBtn = view.findViewById<TextView>(R.id.item_year_btn)
        val itemMonthBtn = view.findViewById<TextView>(R.id.item_month_btn)

        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.category_recyclerView)

        val btnAddTodoInCalendar = view.findViewById<FloatingActionButton>(R.id.btn_add_todo_incalendar)
        val btnAddMainInCalendar = view.findViewById<FloatingActionButton>(R.id.btn_add_main_incalendar)

        val todoApplyLayout = view.findViewById<LinearLayout>(R.id.todo_apply_layout)
        val todoApplyImv = view.findViewById<ImageView>(R.id.todo_apply_imv)
        val todoApplyTv = view.findViewById<TextView>(R.id.todo_apply_tv)

        val unclassifiedShowIv = view.findViewById<ImageView>(R.id.unclassified_show_iv)
        val todoIncompleteImv = view.findViewById<ImageView>(R.id.todo_incomplete_imv)
        val todoIncompleteTv = view.findViewById<TextView>(R.id.todo_incomplete_tv)

        val classifiedShowIv = view.findViewById<ImageView>(R.id.classified_show_iv)
        val todoCompleteImv = view.findViewById<ImageView>(R.id.todo_complete_imv)
        val todoCompleteTv = view.findViewById<TextView>(R.id.todo_complete_tv)

        val scheduleApplyLayout = view.findViewById<LinearLayout>(R.id.schedule_apply_layout)
        val scheduleApplyImv = view.findViewById<ImageView>(R.id.schedule_apply_imv)
        val scheduleApplyTv = view.findViewById<TextView>(R.id.schedule_apply_tv)

        val allBlindTv = view.findViewById<TextView>(R.id.all_blind_tv)

        val calendar = Calendar.getInstance()

        val calendarViewModel = CalendarViewModel()

        calendar.time = Date()

        Alarm.initAlarm(viewLifecycleOwner, requireContext())

        itemYearBtn.text = "${calendar.get(Calendar.YEAR)}년"
        itemMonthBtn.text = "${calendar.get(Calendar.MONTH) + 1}월"

        if(calendarMainData.todoApply) {
            todoApplyImv.backgroundTintList = null

            if(!calendarMainData.todoComplete){
                val drawable = todoCompleteImv.background as VectorDrawable

                val matrix = ColorMatrix()

                matrix.setSaturation(0f)

                val filter = ColorMatrixColorFilter(matrix)
                drawable.setColorFilter(filter)

                todoCompleteTv.setTextColor(Color.parseColor("#BABABA"))
            }

            if(!calendarMainData.todoInComplete){
                val drawable = todoIncompleteImv.background as VectorDrawable

                val matrix = ColorMatrix()

                matrix.setSaturation(0f)

                val filter = ColorMatrixColorFilter(matrix)
                drawable.setColorFilter(filter)

                todoIncompleteTv.setTextColor(Color.parseColor("#BABABA"))
            }

            todoApplyTv.setTextColor(
                Color.parseColor("#1DAFFF")
            )
        } else {
            todoApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

            todoApplyTv.setTextColor(
                Color.parseColor("#BABABA")
            )

            val drawable = todoCompleteImv.background as VectorDrawable
            val drawable2 = todoIncompleteImv.background as VectorDrawable

            val matrix = ColorMatrix()

            matrix.setSaturation(0f)

            val filter = ColorMatrixColorFilter(matrix)

            drawable.setColorFilter(filter)
            drawable2.setColorFilter(filter)

            todoCompleteTv.setTextColor(Color.parseColor("#BABABA"))
            todoIncompleteTv.setTextColor(Color.parseColor("#BABABA"))
        }

        classifiedShowIv.setOnClickListener {
            if(calendarMainData.todoApply) {
                val drawable = todoCompleteImv.background as VectorDrawable
                if (calendarMainData.todoComplete) {
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)

                    val filter = ColorMatrixColorFilter(matrix)
                    drawable.setColorFilter(filter)

                    todoCompleteTv.setTextColor(Color.parseColor("#BABABA"))
                    classifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                } else {
                    drawable.setColorFilter(null)

                    todoCompleteTv.setTextColor(Color.parseColor("#191919"))
                    classifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }

                calendarMainData.todoComplete = !calendarMainData.todoComplete
            }
        }

        unclassifiedShowIv.setOnClickListener {
            if(calendarMainData.todoApply) {
                val drawable = todoIncompleteImv.background as VectorDrawable
                if (calendarMainData.todoInComplete) {
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)

                    val filter = ColorMatrixColorFilter(matrix)
                    drawable.setColorFilter(filter)

                    todoIncompleteTv.setTextColor(Color.parseColor("#BABABA"))
                    unclassifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                } else {
                    drawable.setColorFilter(null)

                    todoIncompleteTv.setTextColor(Color.parseColor("#191919"))
                    unclassifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }

                calendarMainData.todoInComplete = !calendarMainData.todoInComplete
            }
        }

        todoApplyLayout.setOnClickListener{
            if(calendarMainData.todoApply) {
                calendarMainData.todoApply = false

                todoApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

                todoApplyTv.setTextColor(
                    Color.parseColor("#ACACAC")
                )

                val drawable = todoCompleteImv.background as VectorDrawable
                val drawable2 = todoIncompleteImv.background as VectorDrawable
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)

                val filter = ColorMatrixColorFilter(matrix)
                drawable.setColorFilter(filter)
                drawable2.setColorFilter(filter)

                todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                unclassifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                classifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)

                if(!calendarMainData.scheduleApply){
                    allBlindTv.text = "모두 표시"
                    allBlindTv.setTextColor(Color.parseColor("#1DAFFF"))
                }
            } else {
                calendarMainData.todoApply = true

                todoApplyImv.backgroundTintList = null

                todoApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )

                if(calendarMainData.todoComplete){
                    val drawable = todoCompleteImv.background as VectorDrawable
                    todoCompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)
                    classifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }

                if(calendarMainData.todoInComplete){
                    val drawable = todoIncompleteImv.background as VectorDrawable
                    todoIncompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)
                    unclassifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }
            }

            categoryAdapter.dataAllChanged()
        }

        if(calendarMainData.scheduleApply) {
            scheduleApplyImv.backgroundTintList = null

            scheduleApplyTv.setTextColor(
                Color.parseColor("#1DAFFF")
            )
        } else {
            scheduleApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

            scheduleApplyTv.setTextColor(
                Color.parseColor("#BABABA")
            )
        }

        scheduleApplyLayout.setOnClickListener{
            if (calendarMainData.scheduleApply) {
                calendarMainData.scheduleApply = false

                scheduleApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#BABABA")
                )

                if(!calendarMainData.todoApply){
                    allBlindTv.text = "모두 표시"
                    allBlindTv.setTextColor(Color.parseColor("#1DAFFF"))
                }
            } else {
                calendarMainData.scheduleApply = true

                scheduleApplyImv.backgroundTintList = null

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )
            }

            categoryAdapter.dataAllChanged()
        }

        categoryDrawerLayout.addDrawerListener(this)

        if(!calendarMainData.todoApply && !calendarMainData.scheduleApply){
            allBlindTv.text = "모두 표시"
            allBlindTv.setTextColor(Color.parseColor("#1DAFFF"))
        }

        //모두 잠그기
        allBlindTv.setOnClickListener {
            if(allBlindTv.text.toString() == "모두 가리기") {
                var changeStatus = false

                if (calendarMainData.todoApply) {
                    todoApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

                    todoApplyTv.setTextColor(
                        Color.parseColor("#ACACAC")
                    )

                    if (calendarMainData.todoInComplete) {
                        val drawable = todoIncompleteImv.background as VectorDrawable
                        val matrix = ColorMatrix()

                        matrix.setSaturation(0f)

                        val filter = ColorMatrixColorFilter(matrix)
                        drawable.setColorFilter(filter)

                        todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                        unclassifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                    }

                    if (calendarMainData.todoComplete) {
                        val drawable = todoCompleteImv.background as VectorDrawable
                        val matrix = ColorMatrix()
                        matrix.setSaturation(0f)

                        val filter = ColorMatrixColorFilter(matrix)
                        drawable.setColorFilter(filter)

                        todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                        classifiedShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                    }

                    changeStatus = true
                }

                if (calendarMainData.scheduleApply) {
                    scheduleApplyImv.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ACACAC"))

                    scheduleApplyTv.setTextColor(
                        Color.parseColor("#ACACAC")
                    )

                    changeStatus = true
                }

                calendarMainData.holidayCategory = false
                calendarMainData.unclassifiedCategory = false
                calendarMainData.todoApply = false
                calendarMainData.scheduleApply = false

                if (changeStatus) {
                    categoryAdapter.dataAllBlind()
                }

                allBlindTv.text = "모두 표시"
                allBlindTv.setTextColor(Color.parseColor("#1DAFFF"))
            } else{
                calendarMainData.holidayCategory = true
                calendarMainData.unclassifiedCategory = true
                calendarMainData.scheduleApply = true
                calendarMainData.todoApply = true

                scheduleApplyImv.backgroundTintList = null

                scheduleApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )

                categoryAdapter.dataAllVisible()

                todoApplyImv.backgroundTintList = null

                todoApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )

                if(calendarMainData.todoComplete){
                    val drawable = todoCompleteImv.background as VectorDrawable
                    todoCompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)

                    classifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }

                if(calendarMainData.todoInComplete){
                    val drawable = todoIncompleteImv.background as VectorDrawable
                    todoIncompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)

                    unclassifiedShowIv.setBackgroundResource(R.drawable.category_show_image)
                }

                allBlindTv.text = "모두 가리기"
                allBlindTv.setTextColor(Color.parseColor("#646464"))
            }
        }

        //달 선택 버튼
        calendarMonthChooseLayout.setOnClickListener {
            val arrowImv = calendarMonthChooseLayout.getChildAt(2) as ImageView
            val today = Calendar.getInstance()
            val timePicker = CustomMonthDialog(calendar.time)

            timePicker.monthPickerClick = object:CustomMonthDialog.MonthPickerClickListener{
                override fun onClick(
                    yearNumberPicker: NumberPicker,
                    monthNumberPicker: NumberPicker
                ) {
                    arrowImv.rotation = 0f
                    val diffCalendar = (yearNumberPicker.value+1923-today.get(Calendar.YEAR)) * 12 +
                            (monthNumberPicker.value-today.get(Calendar.MONTH))

                    month_viewpager.setCurrentItem(Int.MAX_VALUE / 2 + diffCalendar, false)
                }

            }

            timePicker.show(parentFragmentManager, null)
            arrowImv.rotation = 90f
        }
        
        //오늘 날짜 버튼
        calendarTodayTv.text = calendar.time.date.toString()
        
        calendarTodayTv.setOnClickListener{
            month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
            itemYearBtn.text = "${Date().year+1900}년"
            itemMonthBtn.text = "${Date().month+1}월"
        }

        //카테고리 메뉴 버튼
        categoryButtonImv.setOnClickListener{
            if(!categoryDrawerLayout.isDrawerOpen(Gravity.RIGHT)){
                categoryDrawerLayout.openDrawer(Gravity.RIGHT)
            } else {
                categoryDrawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }

        //카테고리 추가 버튼
        categoryAddImage.setOnClickListener{
            val intent = Intent(view.context, CategoryAddActivity::class.java)
            resultLauncher?.launch(intent)
        }
        
        //추가 버튼 2개
        btnAddMainInCalendar.setOnClickListener {
            if (fabMain_status) {
                val scheduleInput = CalendarAddFragment(
                    null,
                    null,
                    requireContext(),
                    viewLifecycleOwner
                ){
                    adapterMonth.notifyDataSetChanged()
                }

                scheduleInput.show(parentFragmentManager, scheduleInput.tag)

                binding.btnAddMainIncalendar.setImageResource(R.drawable.big_add_fab)
                binding.btnAddTodoIncalendar.visibility = View.INVISIBLE
                binding.btnAddTodoIncalendar.setClickable(false);
            } else {
                btnAddMainInCalendar.setImageResource(R.drawable.schedule_add_fab)
                btnAddTodoInCalendar.visibility = View.VISIBLE
                btnAddTodoInCalendar.setClickable(true)
            }

            fabMain_status = !fabMain_status
        }
        
        btnAddTodoInCalendar.setOnClickListener{
            val todoInput = ChecklistInputFragment(checkListViewModel, viewLifecycleOwner, adapterMonth)

            todoInput.onSubmitListener = object : ChecklistInputFragment.OnSubmitListener{
                override fun onSubmit() {
                    adapterMonth.notifyDataSetChanged()
                }
            }

            todoInput.show(parentFragmentManager, todoInput.tag)

            binding.btnAddMainIncalendar.setImageResource(R.drawable.big_add_fab)
            binding.btnAddTodoIncalendar.visibility = View.INVISIBLE
            binding.btnAddTodoIncalendar.setClickable(false);
            fabMain_status = !fabMain_status
        }
        
        month_viewpager.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

        adapterMonth = AdapterMonth(
            requireActivity(),
            viewLifecycleOwner,
            month_viewpager,
            parentFragmentManager,
            this
        )

        month_viewpager.adapter = adapterMonth

        val callback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pos: Int) {
                super.onPageSelected(pos)

                lastIndex = pos
                calendar.time = Date()
                calendar.add(Calendar.MONTH, pos - Int.MAX_VALUE / 2)

                itemYearBtn.text = "${calendar.get(Calendar.YEAR)}년"
                itemMonthBtn.text = "${calendar.get(Calendar.MONTH) + 1}월"
            }
        }

        month_viewpager.registerOnPageChangeCallback(callback)

        if(lastIndex != -1) {
            month_viewpager.setCurrentItem(lastIndex, false)
        } else {
            month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
        }
        month_viewpager.offscreenPageLimit = 1

        categoryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)

        categoryAdapter = CategoryAdapter{ category,index ->
            val intent = Intent(view.context, CategoryCorrectionActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("index", index)
            resultLauncher?.launch(intent)
        }

        categoryRecyclerView.adapter = categoryAdapter

        binding.calendarSearchButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragments_frame, SearchFragment(checkListViewModel))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

    }

    override fun onDrawerOpened(drawerView: View) {

    }

    override fun onDrawerClosed(drawerView: View) {
        adapterMonth.notifyDataSetChanged()
        categoryDrawerLayout.closeDrawer(Gravity.RIGHT)
    }

    override fun onDrawerStateChanged(newState: Int) {

    }
}
package com.example.haru.view.calendar

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.databinding.FragmentCalendarBinding
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.adapter.CategoryAdapter
import com.example.haru.view.checklist.CalendarAddFragment
import com.example.haru.view.checklist.ChecklistInputFragment
import com.example.haru.viewmodel.CalendarViewModel
import com.example.haru.viewmodel.CheckListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class CalendarFragment(private val activity: Activity) : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapterMonth: AdapterMonth
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var checkListViewModel: CheckListViewModel

    private var parentView: RelativeLayout? = null

    private var fabMain_status = false

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

        fun newInstance(activity: Activity) : CalendarFragment {
            return CalendarFragment(activity)
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
                binding.btnAddMainIncalendar.setImageResource(R.drawable.fab)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentView = view.findViewById<RelativeLayout>(R.id.calendar_fragment_parent_layout)

        val calendarTodayTv = view.findViewById<TextView>(R.id.calendar_today_tv)

        val categoryAddImage = view.findViewById<ImageView>(R.id.category_add_image)
        val categoryButtonImv = view.findViewById<ImageView>(R.id.category_button_imv)

        val categoryDrawerLayout = view.findViewById<DrawerLayout>(R.id.category_drawerlayout)

        val month_viewpager = view.findViewById<ViewPager2>(R.id.month_viewpager)

        val item_month_btn = view.findViewById<Button>(R.id.item_month_btn)

        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.category_recyclerView)

        val btnAddTodoInCalendar = view.findViewById<FloatingActionButton>(R.id.btn_add_todo_incalendar)
        val btnAddMainInCalendar = view.findViewById<FloatingActionButton>(R.id.btn_add_main_incalendar)

        val todoApplyLayout = view.findViewById<LinearLayout>(R.id.todo_apply_layout)
        val todoApplyImv = view.findViewById<ImageView>(R.id.todo_apply_imv)
        val todoApplyTv = view.findViewById<TextView>(R.id.todo_apply_tv)

        val todoIncompleteLayout = view.findViewById<LinearLayout>(R.id.todo_incomplete_layout)
        val todoIncompleteImv = view.findViewById<ImageView>(R.id.todo_incomplete_imv)
        val todoIncompleteTv = view.findViewById<TextView>(R.id.todo_incomplete_tv)

        val todoCompleteLayout = view.findViewById<LinearLayout>(R.id.todo_complete_layout)
        val todoCompleteImv = view.findViewById<ImageView>(R.id.todo_complete_imv)
        val todoCompleteTv = view.findViewById<TextView>(R.id.todo_complete_tv)

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

            if(!calendarMainData.todoComplete){
                val drawable = todoCompleteImv.background as VectorDrawable

                val matrix = ColorMatrix()

                matrix.setSaturation(0f)

                val filter = ColorMatrixColorFilter(matrix)
                drawable.setColorFilter(filter)

                todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
            }

            if(!calendarMainData.todoInComplete){
                val drawable = todoIncompleteImv.background as VectorDrawable

                val matrix = ColorMatrix()

                matrix.setSaturation(0f)

                val filter = ColorMatrixColorFilter(matrix)
                drawable.setColorFilter(filter)

                todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
            }

            todoApplyTv.setTextColor(
                Color.parseColor("#1DAFFF")
            )
        } else {
            todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image_false)

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

            todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
            todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
        }

        todoCompleteLayout.setOnClickListener {
            if(calendarMainData.todoApply) {
                val drawable = todoCompleteImv.background as VectorDrawable
                if (calendarMainData.todoComplete) {
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)

                    val filter = ColorMatrixColorFilter(matrix)
                    drawable.setColorFilter(filter)

                    todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                } else {
                    drawable.setColorFilter(null)

                    todoCompleteTv.setTextColor(Color.parseColor("#191919"))
                }

                calendarMainData.todoComplete = !calendarMainData.todoComplete
            }
        }

        todoIncompleteLayout.setOnClickListener {
            if(calendarMainData.todoApply) {
                val drawable = todoIncompleteImv.background as VectorDrawable
                if (calendarMainData.todoInComplete) {
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)

                    val filter = ColorMatrixColorFilter(matrix)
                    drawable.setColorFilter(filter)

                    todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                } else {
                    drawable.setColorFilter(null)

                    todoIncompleteTv.setTextColor(Color.parseColor("#191919"))
                }

                calendarMainData.todoInComplete = !calendarMainData.todoInComplete
            }
        }

        todoApplyLayout.setOnClickListener{
            if(calendarMainData.todoApply) {
                calendarMainData.todoApply = false

                todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image_false)

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

                todoCompleteTv.setTextColor(Color.parseColor("#ACACAC"))
                todoIncompleteTv.setTextColor(Color.parseColor("#ACACAC"))
            } else {
                calendarMainData.todoApply = true

                todoApplyImv.setBackgroundResource(R.drawable.calendar_todo_image)

                todoApplyTv.setTextColor(
                    Color.parseColor("#1DAFFF")
                )

                if(calendarMainData.todoComplete){
                    val drawable = todoCompleteImv.background as VectorDrawable
                    todoCompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)
                }

                if(calendarMainData.todoInComplete){
                    val drawable = todoIncompleteImv.background as VectorDrawable
                    todoIncompleteTv.setTextColor(Color.parseColor("#191919"))
                    drawable.setColorFilter(null)
                }
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

        //모두 잠그기
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

        //달 선택 버튼
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
        
        //오늘 날짜 버튼
        calendarTodayTv.text = calendar.time.date.toString()
        
        calendarTodayTv.setOnClickListener{
            month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
            item_month_btn.text = "${Date().year+1900}년 ${Date().month+1}월"
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
            resultLauncher.launch(intent)
        }
        
        //추가 버튼 2개
        btnAddMainInCalendar.setOnClickListener {
            if (fabMain_status) {
                val scheduleInput = CalendarAddFragment(activity, categoryAdapter.categoryList, adapterMonth)
                scheduleInput.show(parentFragmentManager, scheduleInput.tag)

                binding.btnAddMainIncalendar.setImageResource(R.drawable.fab)
                binding.btnAddTodoIncalendar.visibility = View.INVISIBLE
                binding.btnAddTodoIncalendar.setClickable(false);
            } else {
                btnAddMainInCalendar.setImageResource(R.drawable.calendar_schedule_add_btn)
                btnAddTodoInCalendar.visibility = View.VISIBLE
                btnAddTodoInCalendar.setClickable(true)
            }

            fabMain_status = !fabMain_status
        }
        
        btnAddTodoInCalendar.setOnClickListener{
            val todoInput = ChecklistInputFragment(checkListViewModel, adapterMonth)
            todoInput.show(parentFragmentManager, todoInput.tag)

            binding.btnAddMainIncalendar.setImageResource(R.drawable.fab)
            binding.btnAddTodoIncalendar.visibility = View.INVISIBLE
            binding.btnAddTodoIncalendar.setClickable(false);
            fabMain_status = !fabMain_status
        }
        
        month_viewpager.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

        adapterMonth = AdapterMonth(
            activity,
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

                calendar.time = Date()
                calendar.add(Calendar.MONTH, pos - Int.MAX_VALUE / 2)

                item_month_btn.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
            }
        }

        month_viewpager.registerOnPageChangeCallback(callback)

        month_viewpager.setCurrentItem(Int.MAX_VALUE / 2, false)
        month_viewpager.offscreenPageLimit = 1

        calendarViewModel.getCategories()
        calendarViewModel.liveCategoryList.observe(viewLifecycleOwner){
            categoryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)

            var categoryArrayList = ArrayList<Category?>()
            categoryArrayList.add(null)
            categoryArrayList.addAll(it)

            categoryAdapter = CategoryAdapter(categoryArrayList){ category,index ->
                val intent = Intent(view.context, CategoryCorrectionActivity::class.java)
                intent.putExtra("category", category)
                intent.putExtra("index", index)
                resultLauncher.launch(intent)
            }

            categoryRecyclerView.adapter = categoryAdapter

            adapterMonth.setCategories(categoryAdapter.categoryList)
        }
    }
}
package com.example.haru.view.checklist


import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.haru.R
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.example.haru.utils.FormatDate
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.Format
import java.util.*

class ChecklistInputFragment(checkListViewModel: CheckListViewModel) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistInputBinding
    private var todoAddViewModel: TodoAddViewModel

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    init {
        todoAddViewModel = TodoAddViewModel(checkListViewModel)
    }

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListViewModel: CheckListViewModel): ChecklistInputFragment {
            return ChecklistInputFragment(checkListViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistInputFragment - onCreate() called")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChecklistInputBinding.inflate(inflater)
        binding.viewModel = todoAddViewModel

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER
            textView.setOnClickListener {
                todoAddViewModel.setRepeatVal(i - 1)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.gridMonth.addView(textView, params)
        }

        for (i in 1..12) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.YearMonth, i)
            textView.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.light_gray)))
            textView.gravity = Gravity.CENTER
            textView.setOnClickListener {
                todoAddViewModel.setRepeatVal(i - 1)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.gridYear.addView(textView, params)
        }

        binding.endDateSetLayout.layoutTransition.apply {
            setAnimateParentHierarchy(false)
        }

        // 마감일 레이아웃 크기 계산
        binding.endDateTimeLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setEndTimeHeight(binding.endDateTimeLayout.height)
                binding.endDateTimeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Log.d("20191627", todoAddViewModel.endTimeLayoutHeight.toString())
                binding.endDateTimeLayout.visibility = View.GONE
            }
        })

        // repeatOption 선택 레이아웃 높이 계산
        binding.repeatOptionLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setRepeatOptionH(binding.repeatOptionLayout.height)
                binding.repeatOptionLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Log.d(
                    "20191627",
                    "repeatOptionLayout : " + todoAddViewModel.repeatOptionHeight.toString()
                )
                binding.repeatOptionLayout.visibility = View.GONE
            }
        })

        // repeatWeek 높이 계산
        binding.everyWeekSelectLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setWeekHeight(binding.everyWeekSelectLayout.height)
                Log.d("20191627", "WeekSelect Height : ${todoAddViewModel.repeatWeekHeight}")
                binding.everyWeekSelectLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.everyWeekSelectLayout.visibility = View.GONE
            }
        })

        // repeatMonth 높이 계산
        binding.gridMonth.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setMonthHeight(binding.gridMonth.height)
                binding.gridMonth.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Log.d("20191627", "grid Height : " + todoAddViewModel.gridMonthHeight.toString())
                binding.gridMonth.visibility = View.GONE
            }
        })

        binding.gridYear.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setYearHeight(binding.gridYear.height)
                binding.gridYear.viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.gridYear.visibility = View.GONE
            }
        })

        // 반복 마감 날짜 높이 계산
        binding.repeatEndDateLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                todoAddViewModel.setRepeatEndDateH(binding.repeatEndDateLayout.height)
                binding.gridMonth.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Log.d(
                    "20191627",
                    "repeatEndDateLayout : " + todoAddViewModel.repeatEndDateHeight.toString()
                )
                binding.repeatEndDateLayout.visibility = View.GONE
            }
        })

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 85 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAddViewModel.flagTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.checkFlagTodo.isChecked = it
        })


        todoAddViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    binding.todaySwitch.isChecked = true
                    binding.tvTodayTodo.setTextColor(resources.getColor(R.color.highlight))
                    binding.ivTodayIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.highlight))
                }
                else -> {
                    binding.todaySwitch.isChecked = false
                    binding.tvTodayTodo.setTextColor(resources.getColor(R.color.light_gray))
                    binding.ivTodayIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.light_gray))
                }
            }
        })

        todoAddViewModel.endDateSwitch.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                when (it) {
                    true -> {
                        val date = Date()
                        binding.endDateSwitch.isChecked = it
                        binding.btnEndDatePick.visibility = View.VISIBLE

                        val valueAnimator = ValueAnimator.ofInt(
                            binding.endDateSetLayout.height,
                            binding.endDateSetLayout.height + todoAddViewModel.endTimeLayoutHeight
                        )
                        valueAnimator.addUpdateListener { animator ->
                            val layoutParams = binding.endDateSetLayout.layoutParams
                            layoutParams.height = animator.animatedValue as Int
                            binding.endDateSetLayout.layoutParams = layoutParams
                        }
                        valueAnimator.duration = 400
                        valueAnimator.start()

                        binding.endDateTimeLayout.visibility = View.VISIBLE
                        binding.ivCalendarIcon.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.todo_description))
                        binding.tvEndDateSet.setTextColor(resources.getColor(R.color.todo_description))
                    }
                    else -> {
                        binding.endDateSwitch.isChecked = it
                        binding.btnEndDatePick.visibility = View.INVISIBLE
                        val valueAnimator = ValueAnimator.ofInt(
                            binding.endDateSetLayout.height,
                            binding.endDateSetLayout.height - todoAddViewModel.endTimeLayoutHeight
                        )
                        valueAnimator.addUpdateListener { animator ->
                            val layoutParams = binding.endDateSetLayout.layoutParams
                            layoutParams.height = animator.animatedValue as Int
                            binding.endDateSetLayout.layoutParams = layoutParams
                        }
                        valueAnimator.duration = 400
                        valueAnimator.start()
                        binding.endDateTimeLayout.visibility = View.INVISIBLE
                        binding.ivCalendarIcon.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.light_gray))
                        binding.tvEndDateSet.setTextColor(resources.getColor(R.color.light_gray))
                    }
                }
            })

        todoAddViewModel.isSelectedEndDateTime.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                when (it) {
                    true -> {
                        binding.endDateTimeSwitch.isChecked = it
                        binding.btnEndTimePick.visibility = View.VISIBLE
                        binding.tvEndTimeSet.setTextColor(resources.getColor(R.color.todo_description))
                    }
                    else -> {
                        binding.endDateTimeSwitch.isChecked = it
                        binding.btnEndTimePick.visibility = View.INVISIBLE
                        binding.tvEndTimeSet.setTextColor(resources.getColor(R.color.light_gray))
                    }
                }
            })

        todoAddViewModel.alarmSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    val date = Date()
                    binding.alarmSwitch.isChecked = it
                    binding.btnAlarmDatePick.text = FormatDate.simpleDateToStr(date)
                    binding.btnAlarmTimePick.text = FormatDate.simpleTimeToStr(date)
                    todoAddViewModel.setDate(1, date)
                    todoAddViewModel.setTime(1, date)


                    binding.btnAlarmDatePick.visibility = View.VISIBLE
                    binding.btnAlarmTimePick.visibility = View.VISIBLE
                    binding.ivAlarmIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.todo_description))
                    binding.tvAlarmSet.setTextColor(resources.getColor(R.color.todo_description))
                }
                else -> {
                    binding.alarmSwitch.isChecked = it
                    binding.btnAlarmDatePick.visibility = View.INVISIBLE
                    binding.btnAlarmTimePick.visibility = View.INVISIBLE
                    binding.ivAlarmIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.light_gray))
                    binding.tvAlarmSet.setTextColor(resources.getColor(R.color.light_gray))
                }
            }
        })

        todoAddViewModel.repeatSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    if (todoAddViewModel.endDateSwitch.value == false)
                        todoAddViewModel.setEndDateSwitch()

                    binding.repeatSwitch.isChecked = it
                    binding.ivRepeatIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.todo_description))
                    binding.tvRepeatSet.setTextColor(resources.getColor(R.color.todo_description))
                    todoAddViewModel.setRepeatSetLayoutH(binding.repeatSetLayout.height)

                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        binding.repeatSetLayout.height + todoAddViewModel.repeatOptionHeight + todoAddViewModel.repeatEndDateHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.repeatOptionLayout.visibility = View.VISIBLE
                    binding.repeatEndDateLayout.visibility = View.VISIBLE
                }
                false -> {
                    binding.repeatSwitch.isChecked = it
                    binding.ivRepeatIcon.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.light_gray))
                    binding.tvRepeatSet.setTextColor(resources.getColor(R.color.light_gray))

                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.repeatOptionLayout.visibility = View.INVISIBLE
                    binding.repeatEndDateLayout.visibility = View.INVISIBLE
                }
                else -> {}
            }
        })

        todoAddViewModel.repeatEndDateSwitch.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                when (it) {
                    true -> {
                        val date = Date()
                        binding.tvRepeatEnd.setTextColor(resources.getColor(R.color.todo_description))
                        binding.btnRepeatEndDate.visibility = View.VISIBLE
                        binding.btnRepeatEndDate.text = FormatDate.simpleDateToStr(date)
                        todoAddViewModel.setDate(2, date)
                    }
                    else -> {
                        binding.tvRepeatEnd.setTextColor(resources.getColor(R.color.light_gray))
                        binding.btnRepeatEndDate.visibility = View.INVISIBLE
                    }
                }
            })

        todoAddViewModel.repeatOption.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            for (i in 0 until binding.repeatOptionSelect.childCount) {
                if (i == it)
                    binding.repeatOptionSelect.getChildAt(i).backgroundTintList = null
                else
                    binding.repeatOptionSelect.getChildAt(i).backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.transparent
                            )
                        )
            }
            when (it) {
                0 -> {
                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight + todoAddViewModel.repeatEndDateHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.everyWeekSelectLayout.visibility = View.GONE
                    binding.gridMonth.visibility = View.GONE
                    binding.gridYear.visibility = View.GONE
                }
                1, 2 -> {
                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.repeatWeekHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.everyWeekSelectLayout.visibility = View.VISIBLE
                    binding.gridMonth.visibility = View.GONE
                    binding.gridYear.visibility = View.GONE
                }

                3 -> {
                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.gridMonthHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.gridMonth.visibility = View.VISIBLE
                    binding.everyWeekSelectLayout.visibility = View.GONE
                    binding.gridYear.visibility = View.GONE
                }

                4 -> {
                    val valueAnimator = ValueAnimator.ofInt(
                        binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.gridYearHeight
                    )
                    valueAnimator.addUpdateListener { animator ->
                        val layoutParams = binding.repeatSetLayout.layoutParams
                        layoutParams.height = animator.animatedValue as Int
                        binding.repeatSetLayout.layoutParams = layoutParams
                    }
                    valueAnimator.duration = 400
                    valueAnimator.start()

                    binding.gridMonth.visibility = View.GONE
                    binding.everyWeekSelectLayout.visibility = View.GONE
                    binding.gridYear.visibility = View.VISIBLE
                }
            }
        })

        todoAddViewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnEndDatePick.text = FormatDate.simpleDateToStr(it)
        })
//
        todoAddViewModel.alarmTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnAlarmTimePick.text = FormatDate.simpleTimeToStr(it)
        })
//
        todoAddViewModel.endTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnEndTimePick.text = FormatDate.simpleTimeToStr(it)
        })
//
        todoAddViewModel.alarmDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnAlarmDatePick.text = FormatDate.simpleDateToStr(it)
        })
//
        todoAddViewModel.repeatEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnRepeatEndDate.text = FormatDate.simpleDateToStr(it)
        })

        todoAddViewModel.repeatValue.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val layout = when (it?.length) {
                7 -> binding.everyWeekSelectLayout
                31 -> binding.gridMonth
                12 -> binding.gridYear
                else -> null
            }

            if (layout != null) {
                for (i in 0 until it!!.length) {
                    if (it[i] == '1')
                        (layout.getChildAt(i) as TextView).setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.highlight
                            )
                        )
                    else (layout.getChildAt(i) as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_gray
                        )
                    )
                }
            }

//            if (todoAddViewModel.repeatOption.value != null){
//                var date = Date()
//                FormatDate.cal.time = date
//                when(todoAddViewModel.repeatValue.value?.length){
//                    1 -> FormatDate.cal.add(Calendar.DATE, 1)
//
//                    7 -> {
//
//                    }
//                    31 -> {}
//                    12 -> {}
//                }
//                date = FormatDate.cal.time
//                todoAddViewModel.setDate(0, date)
//            }
        })

        todoAddViewModel.subTodoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (todoAddViewModel.subTodoCnt == binding.subTodoLayout.childCount) {
                val layoutInflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val addView = layoutInflater.inflate(R.layout.subtodo_input_layout, null)

                addView.findViewById<ImageView>(R.id.iv_subTodo_cancel).setOnClickListener {
                    todoAddViewModel.setSubTodoPosition(binding.subTodoLayout.indexOfChild(addView))
                    todoAddViewModel.deleteSubTodo()
                }
                addView.findViewById<EditText>(R.id.et_subTodo)
                    .addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(e: Editable?) {
                            todoAddViewModel.subTodos[binding.subTodoLayout.indexOfChild(addView)] =
                                e.toString()
                        }
                    })

                binding.subTodoLayout.addView(addView, binding.subTodoLayout.childCount - 1)
            } else binding.subTodoLayout.removeViewAt(todoAddViewModel.subTodoClickPosition)

        })

        binding.etMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.ivMemoIcon.backgroundTintList =
                    if (s.toString() == "") ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.icon_gray
                        )
                    ) else ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.todo_description
                        )
                    )
            }

        })

        binding.checkFlagTodo.setOnClickListener(btnListener())
        binding.todaySwitch.setOnClickListener(btnListener())

        binding.endDateSwitch.setOnClickListener(btnListener())
        binding.endDateTimeSwitch.setOnClickListener(btnListener())
        binding.btnEndDatePick.setOnClickListener(btnListener())
        binding.btnEndTimePick.setOnClickListener(btnListener())

        binding.alarmSwitch.setOnClickListener(btnListener())
        binding.btnAlarmDatePick.setOnClickListener(btnListener())
        binding.btnAlarmTimePick.setOnClickListener(btnListener())
//
        binding.repeatSwitch.setOnClickListener(btnListener())
        binding.repeatEndDateSwitch.setOnClickListener(btnListener())
        binding.btnEveryDay.setOnClickListener(btnListener())
        binding.btnEveryWeek.setOnClickListener(btnListener())
        binding.btnEvery2Week.setOnClickListener(btnListener())
        binding.btnEveryMonth.setOnClickListener(btnListener())
        binding.btnEveryYear.setOnClickListener(btnListener())
        binding.btnRepeatEndDate.setOnClickListener(btnListener())

        binding.tvMonday.setOnClickListener(btnListener())
        binding.tvTuesday.setOnClickListener(btnListener())
        binding.tvWednesday.setOnClickListener(btnListener())
        binding.tvThursday.setOnClickListener(btnListener())
        binding.tvFriday.setOnClickListener(btnListener())
        binding.tvSaturday.setOnClickListener(btnListener())
        binding.tvSunday.setOnClickListener(btnListener())

        binding.btnSubmitTodo.setOnClickListener(btnListener())
//
        binding.btnClose.setOnClickListener(btnListener())

        binding.subTodoAddLayout.setOnClickListener(btnListener())
//        binding.ivSubTodoCancel.setOnClickListener(btnListener())

    }

    override fun dismiss() {
        onDismissListener?.invoke()
        super.dismiss()
    }

    inner class btnListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.check_flag_todo -> todoAddViewModel.setFlagTodo()
                R.id.today_switch -> todoAddViewModel.setTodayTodo()

                binding.subTodoAddLayout.id -> todoAddViewModel.plusSubTodo()

                R.id.endDate_switch -> todoAddViewModel.setEndDateSwitch()
                R.id.endDateTime_switch -> todoAddViewModel.setIsSelectedEndDateTime()

                R.id.alarm_switch -> todoAddViewModel.setAlarmSwitch()

                R.id.repeat_switch -> todoAddViewModel.setRepeatSwitch()
                R.id.repeatEndDate_switch -> todoAddViewModel.setRepeatEndSwitch()

                R.id.btn_everyDay -> todoAddViewModel.setRepeatOpt(0)
                R.id.btn_everyWeek -> todoAddViewModel.setRepeatOpt(1)
                R.id.btn_every2Week -> todoAddViewModel.setRepeatOpt(2)
                R.id.btn_everyMonth -> todoAddViewModel.setRepeatOpt(3)
                R.id.btn_everyYear -> todoAddViewModel.setRepeatOpt(4)

                R.id.tv_monday -> todoAddViewModel.setRepeatVal(0)
                R.id.tv_tuesday -> todoAddViewModel.setRepeatVal(1)
                R.id.tv_wednesday -> todoAddViewModel.setRepeatVal(2)
                R.id.tv_thursday -> todoAddViewModel.setRepeatVal(3)
                R.id.tv_friday -> todoAddViewModel.setRepeatVal(4)
                R.id.tv_saturday -> todoAddViewModel.setRepeatVal(5)
                R.id.tv_sunday -> todoAddViewModel.setRepeatVal(6)


                R.id.btn_alarmTime_pick, R.id.btn_endTime_pick -> {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        requireContext(),
                        MyTimeSetListener { view, hourOfDay, minute ->
                            val time = calendar.apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }.time
                            Log.d("20191627", time.toString())
                            if (v.id == R.id.btn_endTime_pick)
                                todoAddViewModel.setTime(0, time)
                            else todoAddViewModel.setTime(1, time)

                            // timepicker 리스너 만들기 버튼을 없애고 시간을 선택하면 바로 적용되게끔
                        },
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()

                }

                R.id.btn_alarmDate_pick, R.id.btn_endDate_pick, R.id.btn_repeat_end_date -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        R.style.MyDatePickerStyle,
                        { _, year, monthOfYear, dayOfMonth ->
                            val calendar = Calendar.getInstance()

                            calendar.set(year, monthOfYear, dayOfMonth)
                            val date = calendar.time

                            when (v.id) {
                                R.id.btn_endDate_pick -> todoAddViewModel.setDate(0, date)
                                R.id.btn_alarmDate_pick -> todoAddViewModel.setDate(1, date)
                                R.id.btn_repeat_end_date -> todoAddViewModel.setDate(2, date)
                            }
                        },
                        year,
                        month,
                        day
                    )
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
                    datePickerDialog.show()
                }

                R.id.btn_submit_todo -> {
                    if (todoAddViewModel.content == "" || todoAddViewModel.content.replace(
                            " ",
                            ""
                        ) == ""
                    )
                        Toast.makeText(context, "할 일이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    else {
                        todoAddViewModel.readyToSubmit()
                        todoAddViewModel.addTodo {
                            Log.d("20191627", "dismiss")
                            dismiss()
                        }
                    }
                }
                R.id.btn_close -> {
                    dismiss()
                }

            }

        }
    }
}




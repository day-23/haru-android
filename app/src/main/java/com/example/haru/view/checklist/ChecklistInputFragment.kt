package com.example.haru.view.checklist


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.haru.R
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.customDialog.CustomTimeDialog
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class ChecklistInputFragment(
    checkListViewModel: CheckListViewModel,
    val adapter: AdapterMonth? = null,
    val today: Boolean? = null,
    val isTimeTable: Boolean? = null,
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistInputBinding
    private var todoAddViewModel: TodoAddViewModel


    private var lastClickTime = SystemClock.elapsedRealtime()

    interface OnDismissListener {
        fun onDismiss()
    }

    interface OnSubmitListener {
        fun onSubmit()
    }

    var onDismissListener: OnDismissListener? = null
    var onSubmitListener: OnSubmitListener? = null

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("20191627", "onCreateView")
        binding = FragmentChecklistInputBinding.inflate(inflater)
        binding.viewModel = todoAddViewModel

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_gray
                    )
                )
            )
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
            textView.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_gray
                    )
                )
            )
            textView.gravity = Gravity.CENTER
            textView.setOnClickListener {
                if ((it as TextView).textColors != ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.delete_red
                        )
                    )
                )
                    todoAddViewModel.setRepeatVal(i - 1)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.gridYear.addView(textView, params)
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
                binding.repeatEndDateLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Log.d(
                    "20191627",
                    "repeatEndDateLayout : " + todoAddViewModel.repeatEndDateHeight.toString()
                )
                binding.repeatEndDateLayout.visibility = View.GONE

                binding.repeatEndDateLayout.post {
                    if (isTimeTable == true) {
                        todoAddViewModel.setEndDateSwitch()
                    }
                }
            }
        })

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = object : BottomSheetDialog(requireContext()) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                val imm: InputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                if (currentFocus is EditText) {
                    currentFocus!!.clearFocus()
                    return false
                }
                return super.dispatchTouchEvent(ev)
            }
        }

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
        Log.e("20191627", "onViewCreated")

        if (today == true)
            todoAddViewModel.setTodayTodo()

        todoAddViewModel.flagTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.checkFlagTodo.isChecked = it
        })


        todoAddViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.highlight else R.color.light_gray
            binding.todaySwitch.isChecked = it
            binding.tvTodayTodo.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.ivTodayIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        })

        todoAddViewModel.endDateSwitch.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                val color = if (it) R.color.todo_description else R.color.light_gray
                binding.endDateSwitch.isChecked = it
                binding.ivCalendarIcon.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
                binding.tvEndDateSet.setTextColor(ContextCompat.getColor(requireContext(), color))
                binding.endDateTimeLayout.visibility = if (it) View.VISIBLE else View.INVISIBLE
                binding.btnEndDatePick.visibility = if (it) View.VISIBLE else View.INVISIBLE

                Log.e("20191627", "endDateSetLayout : ${binding.endDateSetLayout.height}")
                when (it) {
                    true -> {
                        binding.endDateSetLayout.animateViewHeight(
                            400,
                            binding.endDateSetLayout.height,
                            binding.endDateSetLayout.height + todoAddViewModel.endTimeLayoutHeight
                        )
                    }
                    else -> {
                        if (todoAddViewModel.repeatSwitch.value == true)
                            todoAddViewModel.setRepeatSwitch()

                        binding.endDateSetLayout.animateViewHeight(
                            400, binding.endDateSetLayout.height,
                            binding.endDateSetLayout.height - todoAddViewModel.endTimeLayoutHeight
                        )
                    }
                }
            })

        todoAddViewModel.isSelectedEndDateTime.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                binding.endDateTimeSwitch.isChecked = it
                val color = if (it) R.color.todo_description else R.color.light_gray
                binding.tvEndTimeSet.setTextColor(ContextCompat.getColor(requireContext(), color))
                binding.btnEndTimePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            })

        todoAddViewModel.alarmSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.alarmSwitch.isChecked = it
            binding.btnAlarmDatePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.btnAlarmTimePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.ivAlarmIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvAlarmSet.setTextColor(ContextCompat.getColor(requireContext(), color))
        })

        todoAddViewModel.repeatSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it && todoAddViewModel.endDateSwitch.value != true) {
                todoAddViewModel.setEndDateSwitch()
            }

            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.repeatSwitch.isChecked = it
            binding.ivRepeatIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvRepeatSet.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.repeatOptionLayout.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.repeatEndDateLayout.visibility = if (it) View.VISIBLE else View.INVISIBLE
            when (it) {
                true -> {
                    todoAddViewModel.setRepeatSetLayoutH(binding.repeatSetLayout.height)
                    Log.e("20191627", "repeatSetLayout : ${todoAddViewModel.repeatSetLayoutHeight}")

                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        binding.repeatSetLayout.height +
                                todoAddViewModel.repeatOptionHeight +
                                todoAddViewModel.repeatEndDateHeight
                    )
                }
                false -> {
                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight
                    )
                }
                else -> {}
            }
        })

        todoAddViewModel.repeatEndDateSwitch.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                val color = if (it) R.color.todo_description else R.color.light_gray
                binding.repeatEndDateSwitch.isChecked = it
                binding.tvRepeatEnd.setTextColor(ContextCompat.getColor(requireContext(), color))
                binding.btnRepeatEndDate.visibility = if (it) View.VISIBLE else View.INVISIBLE
            })

        todoAddViewModel.repeatOption.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            for (i in 0 until binding.repeatOptionSelect.childCount)
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

            when (it) {
                0 -> {
                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight +
                                todoAddViewModel.repeatOptionHeight +
                                todoAddViewModel.repeatEndDateHeight
                    )
                }
                1, 2 -> {
                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.repeatWeekHeight
                    )
                }

                3 -> {
                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.gridMonthHeight
                    )
                }

                4 -> {
                    binding.repeatSetLayout.animateViewHeight(
                        400, binding.repeatSetLayout.height,
                        todoAddViewModel.repeatSetLayoutHeight + todoAddViewModel.repeatOptionHeight
                                + todoAddViewModel.repeatEndDateHeight + todoAddViewModel.gridYearHeight
                    )
                }
            }

            binding.everyWeekSelectLayout.visibility =
                if (it == 1 || it == 2) View.VISIBLE else View.GONE
            binding.gridMonth.visibility = if (it == 3) View.VISIBLE else View.GONE
            binding.gridYear.visibility = if (it == 4) View.VISIBLE else View.GONE
        })

        todoAddViewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnEndDatePick.text = FormatDate.simpleDateToStr(it)
            Log.d("20191627", it.toString())

            if (todoAddViewModel.repeatOption.value == 4) {  // 매년
                FormatDate.cal.time = it
                val days = FormatDate.cal.get(Calendar.DAY_OF_MONTH)
                todoAddViewModel.day = days
                Log.d("20191627", days.toString())

                when (days) {
                    31  // 2,4,6,9,11 달들 선택 불가
                    -> for (i in listOf(2, 4, 6, 9, 11))
                        if (todoAddViewModel.repeatValue.value!![i - 1] != '2')
                            todoAddViewModel.setRepeatVal(i - 1, '2')
                    30 // 2월달만
                    -> for (i in listOf(2, 4, 6, 9, 11))
                        if (i == 2 && todoAddViewModel.repeatValue.value!![i - 1] != '2')
                            todoAddViewModel.setRepeatVal(i - 1, '2')
                        else if (i != 2 && todoAddViewModel.repeatValue.value!![i - 1] == '2')
                            todoAddViewModel.setRepeatVal(i - 1, '0')

                    else -> {
                        for (i in listOf(2, 4, 6, 9, 11))
                            if (todoAddViewModel.repeatValue.value!![i - 1] == '2')
                                todoAddViewModel.setRepeatVal(i - 1, '0')
                    }
                }
            }
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
                    else if (it[i] == '0') (layout.getChildAt(i) as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_gray
                        )
                    ) else
                        (layout.getChildAt(i) as TextView).setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.delete_red
                            )
                        )
                }
            }

            if (todoAddViewModel.repeatOption.value != null) {
                val flagOne = todoAddViewModel.repeatValue.value!!.contains('1')
                val flagTwo = todoAddViewModel.repeatValue.value!!.contains('2')

                if (todoAddViewModel.selectDateFlag)
                    return@Observer

                if (!flagOne && !flagTwo) {  // 1도 없고, 2도 없는 0으로만 이루어진 상황
                    todoAddViewModel.setDate(0, Date())
                    return@Observer
                } else if (flagTwo && !flagOne) { // endDate를 직접 설정하면 무조건 그 날짜를 표시, 그 후에 반복 옵션을 건드리면 날짜 계산 방식으로 변경해야한다.
                    if (todoAddViewModel.selectedDate.value == null) {
                        FormatDate.cal.time = Date()
                        FormatDate.cal.set(Calendar.DAY_OF_MONTH, todoAddViewModel.day!!)
                    } else {
                        FormatDate.cal.time = todoAddViewModel.selectedDate.value!!
                    }
                    todoAddViewModel.setDate(0, FormatDate.cal.time)
                    return@Observer
                } else {
                    Log.d("20191627", "최적 날짜 찾기")
                    val date = when (todoAddViewModel.repeatValue.value?.length) {
                        7 -> FormatDate.nextEndDateEveryWeek(
                            todoAddViewModel.repeatValue.value,
                            todoAddViewModel.repeatOption.value,
                            null,
                            null
                        )
                        31 -> FormatDate.nextEndDateEveryMonth(
                            todoAddViewModel.repeatValue.value!!,
                            null,
                            null
                        )
                        12 -> {
                            FormatDate.cal.time = todoAddViewModel.endDate.value!!

                            val day = FormatDate.cal.get(Calendar.DAY_OF_MONTH)

                            FormatDate.nextEndDateEveryYear(
                                todoAddViewModel.repeatValue.value!!,
                                null,
                                null,
                                day
                            )
                        }
                        else -> {
                            FormatDate.cal.time = Date()
                            FormatDate.cal.time
                        }
                    }
                    if (date == null)
                        todoAddViewModel.setDate(0, Date())
                    else
                        todoAddViewModel.setDate(0, date)
                }
            }
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
//------------------------------
        todoAddViewModel.tagLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.ivTagIcon.backgroundTintList = if (it.isEmpty())
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_gray))
            else
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )

            if (it.size < binding.tagContainerLayout.childCount - 2)
                for (i in 1 until binding.tagContainerLayout.childCount - 1) { // chip을 검사해서 리스트에 없으면 삭제
                    val chip = binding.tagContainerLayout.getChildAt(i) as LinearLayout
                    if (!it.contains((chip.getChildAt(0) as AppCompatButton).text)) {
                        binding.tagContainerLayout.removeViewAt(i)
                        break
                    }
                }
            else if (it.size > binding.tagContainerLayout.childCount - 2) {
                val layoutInflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val childCount = binding.tagContainerLayout.childCount
                for (i in childCount - 2 until it.size) {
                    val chip = layoutInflater.inflate(R.layout.custom_chip, null)
                    chip.findViewById<AppCompatButton>(R.id.tag_chip).apply {
                        text = it[i]
                        setOnClickListener {
                            todoAddViewModel.subTagList(this.text.toString())
                        }
                    }

                    binding.tagContainerLayout.addView(
                        chip,
                        binding.tagContainerLayout.childCount - 1
                    )
                }
            }
        })

        binding.tagEt.addTextChangedListener(object : TextWatcher { // 소프트웨어 키보드의 스페이스바 감지
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return

                val str = s.toString()
                if (str == "")
                    return

                if (str[str.length - 1] == ' ') {
                    todoAddViewModel.addTagList()
                    binding.tagEt.setText("")
                }
            }

        })
// -------------------------------------------
        binding.todoEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                val color = if (str == "")
                    ContextCompat.getColor(requireContext(), R.color.light_gray)
                else ContextCompat.getColor(requireContext(), R.color.todo_description)
                val typeFace = if (str == "") Typeface.NORMAL else Typeface.BOLD

                binding.btnSubmitTodo.backgroundTintList = ColorStateList.valueOf(color)
                binding.btnClose.backgroundTintList = ColorStateList.valueOf(color)
                binding.todoEt.setTypeface(null, typeFace)
            }

        })

        binding.etMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.ivMemoIcon.backgroundTintList =
                    if (s.toString() == "") ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_gray
                        )
                    ) else ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.todo_description
                        )
                    )
            }
        })

        binding.tagEt.setOnKeyListener { view, keyCode, keyEvent -> // 엔터키와 하드웨어 키보드의 스페이스바 입력 감지
            if (keyCode == KeyEvent.KEYCODE_SPACE && keyEvent.action == KeyEvent.ACTION_UP) {
                todoAddViewModel.addTagList()
                (view as EditText).setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

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
    }

    override fun onStart() {
        super.onStart()
        Log.e("20191627", "onStart")
    }

    fun View.animateViewHeight(duration: Long, startHeight: Int, endHeight: Int) {
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val newHeight = (startHeight + (endHeight - startHeight) * interpolatedTime).toInt()
                val params = this@animateViewHeight.layoutParams as ViewGroup.LayoutParams
                params.height = newHeight
                this@animateViewHeight.layoutParams = params
            }
        }
        animation.duration = duration
        this@animateViewHeight.startAnimation(animation)
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

                R.id.tv_sunday -> todoAddViewModel.setRepeatVal(0)
                R.id.tv_monday -> todoAddViewModel.setRepeatVal(1)
                R.id.tv_tuesday -> todoAddViewModel.setRepeatVal(2)
                R.id.tv_wednesday -> todoAddViewModel.setRepeatVal(3)
                R.id.tv_thursday -> todoAddViewModel.setRepeatVal(4)
                R.id.tv_friday -> todoAddViewModel.setRepeatVal(5)
                R.id.tv_saturday -> todoAddViewModel.setRepeatVal(6)


                R.id.btn_alarmTime_pick, R.id.btn_endTime_pick -> {
                    val timePicker = when (v.id) {
                        binding.btnEndTimePick.id -> CustomTimeDialog(todoAddViewModel.endTime.value)
                        binding.btnAlarmTimePick.id -> CustomTimeDialog(todoAddViewModel.alarmTime.value)
                        else -> CustomTimeDialog()
                    }
                    timePicker.timePickerClick =
                        object : CustomTimeDialog.TimePickerClickListener {
                            override fun onClick(
                                timeDivider: NumberPicker,
                                hourNumberPicker: NumberPicker,
                                minuteNumberPicker: NumberPicker
                            ) {
                                val timeDivision = timeDivider.value
                                var hour = hourNumberPicker.value
                                val minute = minuteNumberPicker.value
                                if (timeDivision == 0) {
                                    if (hour == 11)
                                        hour = 0
                                    else hour++
                                } else {
                                    if (hour == 11)
                                        hour++
                                    else hour += 13
                                }

                                FormatDate.cal.apply {
                                    set(Calendar.HOUR_OF_DAY, hour)
                                    set(Calendar.MINUTE, minute * 5)
                                }
                                val time = FormatDate.cal.time
                                when (v.id) {
                                    binding.btnEndTimePick.id ->
                                        todoAddViewModel.setTime(0, time)
                                    binding.btnAlarmTimePick.id ->
                                        todoAddViewModel.setTime(1, time)
                                }
                            }
                        }
                    timePicker.show(parentFragmentManager, null)
                }

                R.id.btn_alarmDate_pick, R.id.btn_endDate_pick, R.id.btn_repeat_end_date -> {
                    val datePicker = when (v.id) {
                        binding.btnEndDatePick.id -> CustomCalendarDialog(todoAddViewModel.endDate.value)
                        binding.btnRepeatEndDate.id -> CustomCalendarDialog(
                            todoAddViewModel.repeatEndDate.value,
                            todoAddViewModel.endDate.value
                        )
                        binding.btnAlarmDatePick.id -> CustomCalendarDialog(todoAddViewModel.alarmDate.value)
                        else -> CustomCalendarDialog()
                    }
                    datePicker.calendarClick =
                        object : CustomCalendarDialog.CalendarClickListener {
                            override fun onClick(view: View, year: Int, month: Int, day: Int) {
                                FormatDate.cal.set(year, month, day)
                                val date = FormatDate.cal.time
                                when (v.id) {
                                    binding.btnAlarmDatePick.id -> todoAddViewModel.setDate(1, date)
                                    binding.btnEndDatePick.id -> {
                                        todoAddViewModel.setSelectDate(date)
                                        todoAddViewModel.setDate(0, date)
                                    }
                                    binding.btnRepeatEndDate.id -> todoAddViewModel.setDate(2, date)
                                }
                            }
                        }
                    datePicker.show(parentFragmentManager, null)
                }

                R.id.btn_submit_todo -> {
                    if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
                        Log.d("20191627", "새로운 투두 추가 1초간 터치 금지")
                        return
                    }
                    lastClickTime = SystemClock.elapsedRealtime()

                    if (todoAddViewModel.content == "" ||
                        todoAddViewModel.content.replace(" ", "") == ""
                    )
                        Toast.makeText(context, "할 일이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    else {
                        todoAddViewModel.readyToSubmit()

                        if (adapter == null) {
                            todoAddViewModel.addTodo {
                                onSubmitListener?.onSubmit()
                                Log.d("20191627", "dismiss")
                                dismiss()
                            }
                        } else if (isTimeTable == true) {
                            todoAddViewModel.addTodo(true) {
                                adapter.notifyDataSetChanged()
                                onSubmitListener?.onSubmit()
                                Log.d("20191627", "dismiss")
                                dismiss()
                            }
                        } else {
                            todoAddViewModel.addTodo(true) {
                                adapter.notifyDataSetChanged()
                                onSubmitListener?.onSubmit()
                                Log.d("20191627", "dismiss")
                                dismiss()
                            }
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




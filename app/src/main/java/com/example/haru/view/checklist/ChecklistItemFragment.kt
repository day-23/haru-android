package com.example.haru.view.checklist

import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.data.model.Alarm
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentChecklistItemInfoBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.customDialog.CustomCalendarDialog
import com.example.haru.view.customDialog.CustomTimeDialog
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import java.util.*

class ChecklistItemFragment(checkListViewModel: CheckListViewModel, id: String, val todo: Todo?=null) : Fragment() {
    private lateinit var binding: FragmentChecklistItemInfoBinding
    private var todoAddViewModel: TodoAddViewModel
    private var id: String

    init {
        this.todoAddViewModel = TodoAddViewModel(checkListViewModel)
        this.id = id
    }

    companion object {
        const val TAG: String = "로그"

        fun newInstance(
            checkListViewModel: CheckListViewModel,
            id: String,
            todo: Todo
        ): ChecklistItemFragment {
            return ChecklistItemFragment(checkListViewModel, id, todo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistFragment - onCreate() called")

        MainActivity.hideNavi(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "ChecklistItemFragment - onCreateView() called")

        binding = FragmentChecklistItemInfoBinding.inflate(inflater)

        for (i in 1..31) {
            val textView = TextView(requireContext())
            textView.text = getString(R.string.MonthDay, i)
            textView.setTextColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.light_gray)
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
            binding.infoGridMonth.addView(textView, params)
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
                todoAddViewModel.setRepeatVal(i - 1)
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
            }
            binding.infoGridYear.addView(textView, params)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAddViewModel.setClickTodo(id, todo)
        binding.vm = todoAddViewModel

        if (todo != null)
            Log.d("20191627", todo.endDate?: "null")

        if (todoAddViewModel.clickedTodo!!.completed){
            binding.infoSubTodoAddLayout.visibility = View.GONE
        }

        Log.d("20191627", todoAddViewModel.clickedTodo.toString())

        // flag 관련 UI Update
        todoAddViewModel.flagTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.cbInfoFlag.isChecked = it
        })

        // complete 관련 UI Update  --> 수정창에서 완료하는 기능을 뺐으므로 수정 예정
        todoAddViewModel.completedTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.light_gray else R.color.todo_description
            binding.btnInfoSave.visibility = if (it) View.GONE else View.VISIBLE
//            binding.cbInfoCompleted.isChecked = it
            binding.etInfoContent.paintFlags =
                if (it)
                    Paint.STRIKE_THRU_TEXT_FLAG
                else binding.etInfoContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            binding.etInfoContent.setTextColor(ContextCompat.getColor(requireContext(), color))
        })

        // subTodo 관련 UI Update
        todoAddViewModel.subTodoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (todoAddViewModel.subTodoCnt > binding.infoSubTodoLayout.childCount - 1) {
                for (i in binding.infoSubTodoLayout.childCount - 1 until todoAddViewModel.subTodoCnt) {
                    val layoutInflater =
                        context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val addView = layoutInflater.inflate(R.layout.subtodo_input_layout, null)

                    addView.findViewById<ImageView>(R.id.iv_subTodo_cancel).setOnClickListener {
                        todoAddViewModel.setSubTodoPosition(
                            binding.infoSubTodoLayout.indexOfChild(
                                addView
                            )
                        )
                        todoAddViewModel.deleteSubTodo()
                    }

                    if (todoAddViewModel.subTodoCompleted.isNotEmpty() && i < todoAddViewModel.subTodoCompleted.size)
                        addView.findViewById<EditText>(R.id.et_subTodo).apply {
                            setText(todoAddViewModel.subTodos[i])
                            paintFlags =
                                if (todoAddViewModel.subTodoCompleted[i])
                                    Paint.STRIKE_THRU_TEXT_FLAG
                                else binding.etInfoContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                            if (todoAddViewModel.subTodoCompleted[i]) setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.light_gray
                                )
                            )

                        }
                    addView.findViewById<EditText>(R.id.et_subTodo).addTextChangedListener(object :
                        TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(e: Editable?) {
                            todoAddViewModel.subTodos[binding.infoSubTodoLayout.indexOfChild(addView)] =
                                e.toString()
                        }
                    })

                    binding.infoSubTodoLayout.addView(
                        addView,
                        binding.infoSubTodoLayout.childCount - 1
                    )
                    todoAddViewModel.subTodoClickPosition++
                }
            } else binding.infoSubTodoLayout.removeViewAt(todoAddViewModel.subTodoClickPosition)
        })

        // todayTodo 관련 UI Update
        todoAddViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.highlight else R.color.light_gray
            binding.infoTodaySwitch.isChecked = it
            binding.ivInfoTodayIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvInfoTodayTodo.setTextColor(ContextCompat.getColor(requireContext(), color))
        })

        // endDateLayout 관련 UI Update
        todoAddViewModel.endDateSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.infoEndDateSwitch.isChecked = it
            binding.ivInfoCalendarIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvInfoEndDateSet.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.btnInfoEndDatePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.infoEndDateTimeLayout.visibility = if (it) View.VISIBLE else View.GONE
        })

        // endDate Button UI Update
        todoAddViewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d("20191627", "endDAte $it")
            binding.btnInfoEndDatePick.text = FormatDate.simpleDateToStr(it)
        })

        // endDateTime 관련 UI Update
        todoAddViewModel.isSelectedEndDateTime.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                binding.infoEndDateTimeSwitch.isChecked = it
                val color = if (it) R.color.todo_description else R.color.light_gray
                binding.tvInfoEndTimeSet.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
                binding.btnInfoEndTimePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            })

        // endDateTime Button UI Update
        todoAddViewModel.endTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnInfoEndTimePick.text = FormatDate.simpleTimeToStr(it)
        })

        // alarm 관련 UI Update
        todoAddViewModel.alarmSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.infoAlarmSwitch.isChecked = it
            binding.ivInfoAlarmIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvInfoAlarmSet.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.btnInfoAlarmDatePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
            binding.btnInfoAlarmTimePick.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        // alarm Date Update
        todoAddViewModel.alarmDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnInfoAlarmDatePick.text = FormatDate.simpleDateToStr(it)
        })

        // alarm Time Update
        todoAddViewModel.alarmTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnInfoAlarmTimePick.text = FormatDate.simpleTimeToStr(it)
        })

        // repeat Switch 관련 UI Update
        // 반복설정, 태그 연동, 하위 항목, 메모 만들어서 마무리하기
        todoAddViewModel.repeatSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it && todoAddViewModel.endDateSwitch.value != true) {
                todoAddViewModel.setEndDateSwitch()
            }

            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.infoRepeatSwitch.isChecked = it
            binding.ivInfoRepeatIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
            binding.tvInfoRepeatSet.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.infoRepeatOptionLayout.visibility = if (it) View.VISIBLE else View.GONE
            binding.infoRepeatEndDateLayout.visibility = if (it) View.VISIBLE else View.GONE

            binding.infoEveryWeekSelectLayout.visibility =
                if (it && todoAddViewModel.repeatOption.value in listOf(
                        1,
                        2
                    )
                ) View.VISIBLE else View.GONE
            binding.infoGridMonth.visibility =
                if (it && todoAddViewModel.repeatOption.value == 3) View.VISIBLE else View.GONE
            binding.infoGridYear.visibility =
                if (it && todoAddViewModel.repeatOption.value == 4) View.VISIBLE else View.GONE

        })

        // repeat Option 관련 UI Update
        todoAddViewModel.repeatOption.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            for (i in 0 until binding.infoRepeatOptionSelect.childCount)
                if (i == it)
                    binding.infoRepeatOptionSelect.getChildAt(i).backgroundTintList = null
                else
                    binding.infoRepeatOptionSelect.getChildAt(i).backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                android.R.color.transparent
                            )
                        )

            binding.infoEveryWeekSelectLayout.visibility =
                if (it == 1 || it == 2) View.VISIBLE else View.GONE
            binding.infoGridMonth.visibility = if (it == 3) View.VISIBLE else View.GONE
            binding.infoGridYear.visibility = if (it == 4) View.VISIBLE else View.GONE
        })

        todoAddViewModel.repeatValue.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d("20191627", it.toString())
            val layout = when (it?.length) {
                7 -> binding.infoEveryWeekSelectLayout
                31 -> binding.infoGridMonth
                12 -> binding.infoGridYear
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

            if (!todoAddViewModel.calculateDateFlag) {
                todoAddViewModel.calculateDateFlag = true
            } else if (todoAddViewModel.repeatOption.value != null) {
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

        // repeat EndDate Switch UI Update
        todoAddViewModel.repeatEndDateSwitch.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                val color = if (it) R.color.todo_description else R.color.light_gray
                binding.infoRepeatEndDateSwitch.isChecked = it
                binding.tvInfoRepeatEnd.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
                binding.btnInfoRepeatEndDate.visibility = if (it) View.VISIBLE else View.INVISIBLE
            })

        // repeat EndDate 관련 UI Update
        todoAddViewModel.repeatEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.btnInfoRepeatEndDate.text = FormatDate.simpleDateToStr(it)
        })

        todoAddViewModel.tagLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.ivInfoTagIcon.backgroundTintList = if (it.isEmpty())
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icon_gray))
            else
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )

            if (it.size < binding.infoTagContainerLayout.childCount - 2)
                for (i in 1 until binding.infoTagContainerLayout.childCount - 1) { // chip을 검사해서 리스트에 없으면 삭제
                    val chip = binding.infoTagContainerLayout.getChildAt(i) as LinearLayout
                    if (!it.contains((chip.getChildAt(0) as AppCompatButton).text)) {
                        binding.infoTagContainerLayout.removeViewAt(i)
                        break
                    }
                }
            else if (it.size > binding.infoTagContainerLayout.childCount - 2) {
                val layoutInflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val childCount = binding.infoTagContainerLayout.childCount
                for (i in childCount - 2 until it.size) {
                    val chip = layoutInflater.inflate(R.layout.custom_chip, null)
                    chip.findViewById<AppCompatButton>(R.id.tag_chip).apply {
                        text = it[i]
                        setOnClickListener {
                            todoAddViewModel.subTagList(this.text.toString())
                        }
                    }
                    binding.infoTagContainerLayout.addView(
                        chip,
                        binding.infoTagContainerLayout.childCount - 1
                    )
                }
            }
        })

        binding.infoTagEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return

                val str = s.toString()
                if (str == "")
                    return

                if (str[str.length - 1] == ' '){
                    todoAddViewModel.addTagList()
                    binding.infoTagEt.setText("")
                }
            }

        })

        binding.etInfoMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.ivInfoMemoIcon.backgroundTintList =
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


        // flag click event
        binding.cbInfoFlag.setOnClickListener(BtnClickListener())

        // complete click event
//        binding.cbInfoCompleted.setOnClickListener(BtnClickListener())

        binding.infoSubTodoAddLayout.setOnClickListener(BtnClickListener())

        // today Todo switch click event
        binding.infoTodaySwitch.setOnClickListener(BtnClickListener())
        // end Date Switch 토글 버튼
        binding.infoEndDateSwitch.setOnClickListener(BtnClickListener())
        // end Date Time 토글 버튼
        binding.infoEndDateTimeSwitch.setOnClickListener(BtnClickListener())
        // alarm 토글 버튼
        binding.infoAlarmSwitch.setOnClickListener(BtnClickListener())
        //  endDate 설정 버튼
        binding.btnInfoEndDatePick.setOnClickListener(BtnClickListener())
        // alarm Date 설정 버튼
        binding.btnInfoAlarmDatePick.setOnClickListener(BtnClickListener())
        // endDate Time 설정 버튼
        binding.btnInfoEndTimePick.setOnClickListener(BtnClickListener())
        // alarm Time 설정 버튼
        binding.btnInfoAlarmTimePick.setOnClickListener(BtnClickListener())

        // repeat Switch 토글 버튼
        binding.infoRepeatSwitch.setOnClickListener(BtnClickListener())

        binding.btnInfoRepeatEndDate.setOnClickListener(BtnClickListener())
        binding.infoRepeatEndDateSwitch.setOnClickListener(BtnClickListener())

        // repeat Option 선택 버튼
        binding.btnInfoEveryDay.setOnClickListener(BtnClickListener())
        binding.btnInfoEveryWeek.setOnClickListener(BtnClickListener())
        binding.btnInfoEvery2Week.setOnClickListener(BtnClickListener())
        binding.btnInfoEveryMonth.setOnClickListener(BtnClickListener())
        binding.btnInfoEveryYear.setOnClickListener(BtnClickListener())

        binding.tvInfoMonday.setOnClickListener(BtnClickListener())
        binding.tvInfoTuesday.setOnClickListener(BtnClickListener())
        binding.tvInfoWednesday.setOnClickListener(BtnClickListener())
        binding.tvInfoThursday.setOnClickListener(BtnClickListener())
        binding.tvInfoFriday.setOnClickListener(BtnClickListener())
        binding.tvInfoSaturday.setOnClickListener(BtnClickListener())
        binding.tvInfoSunday.setOnClickListener(BtnClickListener())

        binding.ivBackIcon.setOnClickListener(BtnClickListener())
        binding.btnInfoSave.setOnClickListener(BtnClickListener())
        binding.btnInfoDelete.setOnClickListener(BtnClickListener())

//        binding.root.setOnClickListener(BtnClickListener())
    }

    inner class BtnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.cbInfoFlag.id -> todoAddViewModel.setFlagTodo()
//                binding.cbInfoCompleted.id -> todoAddViewModel.setCompleteTodo()

                binding.infoSubTodoAddLayout.id -> todoAddViewModel.plusSubTodo()

                binding.infoTodaySwitch.id -> todoAddViewModel.setTodayTodo()

                binding.infoEndDateSwitch.id -> todoAddViewModel.setEndDateSwitch()

                binding.infoEndDateTimeSwitch.id -> todoAddViewModel.setIsSelectedEndDateTime()

                binding.infoAlarmSwitch.id -> todoAddViewModel.setAlarmSwitch()

                binding.btnInfoEndDatePick.id,
                binding.btnInfoAlarmDatePick.id,
                binding.btnInfoRepeatEndDate.id -> {
                    val datePicker = when (v.id) {
                        binding.btnInfoEndDatePick.id -> CustomCalendarDialog(todoAddViewModel.endDate.value)
                        binding.btnInfoRepeatEndDate.id -> CustomCalendarDialog(todoAddViewModel.repeatEndDate.value, todoAddViewModel.endDate.value)
                        binding.btnInfoAlarmDatePick.id -> CustomCalendarDialog(todoAddViewModel.alarmDate.value)
                        else -> CustomCalendarDialog()
                    }
                    datePicker.calendarClick =
                        object : CustomCalendarDialog.CalendarClickListener {
                            override fun onClick(view: View, year: Int, month: Int, day: Int) {
                                FormatDate.cal.set(year, month, day)
                                val date = FormatDate.cal.time
                                when (v.id) {
                                    binding.btnInfoAlarmDatePick.id -> todoAddViewModel.setDate(
                                        1,
                                        date
                                    )
                                    binding.btnInfoEndDatePick.id -> {
                                        todoAddViewModel.setSelectDate(date)
                                        todoAddViewModel.setDate(0, date)
                                    }
                                    binding.btnInfoRepeatEndDate.id -> todoAddViewModel.setDate(
                                        2,
                                        date
                                    )
                                }
                            }
                        }
                    datePicker.show(parentFragmentManager, null)
                }

                binding.btnInfoEndTimePick.id,
                binding.btnInfoAlarmTimePick.id -> {
                    val timePicker = when(v.id){
                        binding.btnInfoEndTimePick.id -> {CustomTimeDialog(todoAddViewModel.endTime.value)}
                        binding.btnInfoAlarmTimePick.id -> {CustomTimeDialog(todoAddViewModel.alarmTime.value)}
                        else -> CustomTimeDialog()
                    }
                    timePicker.timePickerClick = object : CustomTimeDialog.TimePickerClickListener{
                        override fun onClick(
                            timeDivider : NumberPicker,
                            hourNumberPicker: NumberPicker,
                            minuteNumberPicker: NumberPicker
                        ) {
                            val timeDivision = timeDivider.value
                            var hour = hourNumberPicker.value
                            val minute = minuteNumberPicker.value
                            if (timeDivision == 0){
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
                            when(v.id){
                                binding.btnInfoEndTimePick.id ->
                                    todoAddViewModel.setTime(0, time)
                                binding.btnInfoAlarmTimePick.id ->
                                    todoAddViewModel.setTime(1, time)
                            }
                        }
                    }
                    timePicker.show(parentFragmentManager, null)
                }

                binding.infoRepeatSwitch.id -> todoAddViewModel.setRepeatSwitch()

                binding.btnInfoEveryDay.id -> todoAddViewModel.setRepeatOpt(0)
                binding.btnInfoEveryWeek.id -> todoAddViewModel.setRepeatOpt(1)
                binding.btnInfoEvery2Week.id -> todoAddViewModel.setRepeatOpt(2)
                binding.btnInfoEveryMonth.id -> todoAddViewModel.setRepeatOpt(3)
                binding.btnInfoEveryYear.id -> todoAddViewModel.setRepeatOpt(4)

                binding.tvInfoSunday.id -> todoAddViewModel.setRepeatVal(0)
                binding.tvInfoMonday.id -> todoAddViewModel.setRepeatVal(1)
                binding.tvInfoTuesday.id -> todoAddViewModel.setRepeatVal(2)
                binding.tvInfoWednesday.id -> todoAddViewModel.setRepeatVal(3)
                binding.tvInfoThursday.id -> todoAddViewModel.setRepeatVal(4)
                binding.tvInfoFriday.id -> todoAddViewModel.setRepeatVal(5)
                binding.tvInfoSaturday.id -> todoAddViewModel.setRepeatVal(6)

                binding.infoRepeatEndDateSwitch.id -> todoAddViewModel.setRepeatEndSwitch()

                binding.btnInfoDelete.id -> {
                    if (todoAddViewModel.clickedTodo!!.repeatOption != null) {
                        val option = DeleteOptionDialogFragment(todoAddViewModel)
                        option.show(parentFragmentManager, option.tag)
                    } else todoAddViewModel.deleteTodo {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnInfoSave.id -> {
                    todoAddViewModel.readyToSubmit()
                    if (todoAddViewModel.clickedTodo!!.repeatOption != null) {
                        // front, middle, back 구분할 수 있는 데이터로 분기 설정 front 면 아래 코드
                        val checkEndDate = todoAddViewModel.checkChangeEndDate()
                        val checkRepeatData = todoAddViewModel.checkChangeRepeat()
//                        val type : Int
                        // if (~~ front == true)
//                        val type = if (checkEndDate && checkRepeatData) 0
//                        else if (checkEndDate) 1 else if (checkRepeatData) 2 else 3

                        when(todoAddViewModel.clickedTodo?.location){
                            0 -> { // front
                                if (checkEndDate && checkRepeatData) {
                                    // 전체 이벤트 수정
                                } else if (checkEndDate){
                                    // 이 이벤트만 수정
                                } else if (checkRepeatData) {
                                    // 전체 이벤트 수정
                                } else {
                                    // 전체 이벤트 수정, 이 이벤트만 수정
                                }
                            }
                            1, 2 -> { // middle, back
                                if (checkEndDate && checkRepeatData){
                                    // 전체 이벤트 수정, 이 이벤트부터 수정
                                } else if(checkEndDate){
                                    // 이 이벤트만 수정
                                } else if (checkRepeatData) {
                                    // 전체 이벤트 수정, 이 이벤트부터 수정
                                } else {
                                    // 전체 이벤트 수정, 이 이벤트부터 수정, 이 이벤트만 수정
                                }
                            }

                        }
//                        Front
//                        * 디폴트 → “전체 이벤트 수정”, “이 이벤트만 수정”
//                        * 반복 일정 수정 → “전체 이벤트 수정”
//                        * 마감일 수정 → “이 이벤트만 수정”
//                        * 둘다 수정시 → “전체 이벤트 수정”

//                        Middle, Back
//                        * 디폴트 → “전체 이벤트 수정”, “이 이벤트부터 수정”, “이 이벤트만 수정”
//                        * 반복 일정 수정 → “전체 이벤트 수정”, “이 이벤트부터 수정”
//                        * 마감일 수정 → “이 이벤트만 수정”
//                        * 둘다 수정시 → “전체 이벤트 수정”, “이 이벤트부터 수정”

//                        val option = UpdateOptionDialogFragment(todoAddViewModel, type)
//                        option.show(parentFragmentManager, option.tag)

                    } else {
                        todoAddViewModel.updateTodo {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
                binding.ivBackIcon.id -> requireActivity().supportFragmentManager.popBackStack()

            }
        }
    }


}
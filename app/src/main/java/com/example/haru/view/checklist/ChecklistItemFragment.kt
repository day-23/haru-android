package com.example.haru.view.checklist

import android.app.DatePickerDialog
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.data.model.Alarm
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentChecklistItemInfoBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.view.customCalendar.CustomCalendarDialog
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import java.util.*

class ChecklistItemFragment(checkListViewModel: CheckListViewModel, id: String) : Fragment() {
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
            id: String
        ): ChecklistItemFragment {
            return ChecklistItemFragment(checkListViewModel, id)
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

        todoAddViewModel.setClickTodo(id)
        Log.d("20191627", todoAddViewModel.clickedTodo.toString())
        binding.vm = todoAddViewModel

        // flag 관련 UI Update
        todoAddViewModel.flagTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.cbInfoFlag.isChecked = it
        })

        // complete 관련 UI Update
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
                    if (it[i] == '1') {
                        Log.d("20191627", it[i].toString())
                        (layout.getChildAt(i) as TextView).setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.highlight
                            )
                        )
                    } else (layout.getChildAt(i) as TextView).setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_gray
                        )
                    )
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

        binding.infoTagEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.ivInfoTagIcon.backgroundTintList = if (s.toString() == "")
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.icon_gray
                        )
                    )
                else
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.todo_description
                        )
                    )
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
                        binding.btnInfoRepeatEndDate.id -> CustomCalendarDialog(todoAddViewModel.repeatEndDate.value)
                        binding.btnInfoAlarmDatePick.id -> CustomCalendarDialog(todoAddViewModel.alarmDate.value)
                        else -> CustomCalendarDialog()
                    }
                    datePicker.calendarClick =
                        object : CustomCalendarDialog.CalendarClickListener {
                            override fun onClick(view: View, year: Int, month: Int, day: Int) {
                                Log.d("20191627", "클릭")
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
                            when (v.id) {
                                binding.btnInfoEndTimePick.id ->
                                    todoAddViewModel.setTime(0, time)
                                binding.btnInfoAlarmTimePick.id ->
                                    todoAddViewModel.setTime(1, time)
                            }
                            // timepicker 리스너 만들기 버튼을 없애고 시간을 선택하면 바로 적용되게끔
                        },
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()
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
                        val option = OptionDialogFragment(todoAddViewModel)
                        option.show(parentFragmentManager, option.tag)
                    } else todoAddViewModel.deleteTodo {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnInfoSave.id -> {
                    todoAddViewModel.readyToSubmit()
                    todoAddViewModel.updateTodo {
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.ivBackIcon.id -> requireActivity().supportFragmentManager.popBackStack()

//                binding.root.id -> {
//                    if (activity != null && requireActivity().currentFocus != null){
//                        val imm: InputMethodManager =   // 자동으로 키보드 내리기
//                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//                    }
//                }
            }
        }
    }


}
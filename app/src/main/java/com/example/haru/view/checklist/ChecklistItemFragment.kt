package com.example.haru.view.checklist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.data.model.Alarm
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentChecklistItemInfoBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.MainActivity
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import java.util.*

class ChecklistItemFragment(checkListViewModel: CheckListViewModel, position: Int) : Fragment() {
    private lateinit var binding: FragmentChecklistItemInfoBinding
    private var todoAddViewModel: TodoAddViewModel
    private var position: Int

    init {
        this.todoAddViewModel = TodoAddViewModel(checkListViewModel)
        this.position = position
    }

    companion object {
        const val TAG: String = "로그"

        fun newInstance(
            checkListViewModel: CheckListViewModel,
            position: Int
        ): ChecklistItemFragment {
            return ChecklistItemFragment(checkListViewModel, position)
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initView()
        todoAddViewModel.setClickTodo(this.position)
        binding.todoItem = todoAddViewModel.clickedTodo
        // flag 관련 UI Update
        todoAddViewModel.flagTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.cbInfoFlag.isChecked = it
        })

        // complete 관련 UI Update
        todoAddViewModel.completedTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.cbInfoCompleted.isChecked = it
            binding.tvInfoContent.paintFlags =
                if (it)
                    Paint.STRIKE_THRU_TEXT_FLAG
                else binding.tvInfoContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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
        todoAddViewModel.isSelectedEndDateTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.infoEndDateTimeSwitch.isChecked = it
            val color = if (it) R.color.todo_description else R.color.light_gray
            binding.tvInfoEndTimeSet.setTextColor(ContextCompat.getColor(requireContext(), color))
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

        // 반복설정, 태그 연동, 하위 항목, 메모 만들어서 마무리하기

        binding.cbInfoFlag.setOnClickListener(BtnClickListener())
        binding.cbInfoCompleted.setOnClickListener(BtnClickListener())
        binding.infoTodaySwitch.setOnClickListener(BtnClickListener())
        binding.infoEndDateSwitch.setOnClickListener(BtnClickListener())
        binding.infoEndDateTimeSwitch.setOnClickListener(BtnClickListener())
        binding.infoAlarmSwitch.setOnClickListener(BtnClickListener())
        binding.btnInfoEndDatePick.setOnClickListener(BtnClickListener())
        binding.btnInfoAlarmDatePick.setOnClickListener(BtnClickListener())
        binding.btnInfoEndTimePick.setOnClickListener(BtnClickListener())
        binding.btnInfoAlarmTimePick.setOnClickListener(BtnClickListener())

    }

    inner class BtnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.cbInfoFlag.id -> todoAddViewModel.setFlagTodo()
                binding.cbInfoCompleted.id -> todoAddViewModel.setCompleteTodo()

                binding.infoTodaySwitch.id -> todoAddViewModel.setTodayTodo()

                binding.infoEndDateSwitch.id -> todoAddViewModel.setEndDateSwitch()

                binding.infoEndDateTimeSwitch.id -> todoAddViewModel.setIsSelectedEndDateTime()

                binding.infoAlarmSwitch.id -> todoAddViewModel.setAlarmSwitch()

                binding.btnInfoEndDatePick.id,
                binding.btnInfoAlarmDatePick.id -> {
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
                                binding.btnInfoEndDatePick.id ->
                                    todoAddViewModel.setDate(0, date)
                                binding.btnInfoAlarmDatePick.id ->
                                    todoAddViewModel.setDate(1, date)
//                                R.id.btn_endDate_pick -> todoAddViewModel.setDate(0, date)
//                                R.id.btn_alarmDate_pick -> todoAddViewModel.setDate(1, date)
//                                R.id.btn_repeat_end_date -> todoAddViewModel.setDate(2, date)
                            }
                        },
                        year,
                        month,
                        day
                    )
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
                    datePickerDialog.show()
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
                            when(v.id){
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
            }
        }
    }

//    private fun initView() {
//        binding.cbInfoCompleted.isChecked = binding.todoItem!!.completed
//        if (binding.cbInfoCompleted.isChecked)
//            binding.tvInfoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
//        binding.tvInfoContent.text = binding.todoItem!!.content
//        binding.cbInfoFlag.isChecked = binding.todoItem!!.flag
//
//        if (binding.todoItem!!.tags != emptyList<Tag>()) {
//            binding.infoTagEt.apply {
//                var text = ""
//                for (i in 0 until binding.todoItem!!.tags.size)
//                    text += "${binding.todoItem!!.tags[i].content} "
//                setText(text)
//            }
//        }
//
//        if (binding.todoItem!!.todayTodo) {
//            binding.ivInfoTodayIcon.backgroundTintList =
//                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.highlight))
//            binding.tvInfoTodayTodo.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.highlight
//                )
//            )
//            binding.infoTodaySwitch.isChecked = binding.todoItem!!.todayTodo
//        }
//
//        if (binding.todoItem!!.endDate != null) {
//            binding.ivInfoCalendarIcon.backgroundTintList =
//                ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.todo_description
//                    )
//                )
//            binding.tvInfoEndDateSet.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.todo_description
//                )
//            )
//            binding.btnInfoEndDatePick.apply {
//                visibility = View.VISIBLE
//                text = FormatDate.todoDateToStr(binding.todoItem!!.endDate!!)
//            }
//            binding.infoEndDateSwitch.isChecked = true
//            binding.infoEndDateTimeLayout.visibility = View.VISIBLE
//        } else binding.btnInfoEndDatePick.text = FormatDate.simpleDateToStr(Date())
//
//        if (binding.todoItem!!.isSelectedEndDateTime) {
//            binding.infoEndDateTimeLayout.visibility = View.VISIBLE
//            binding.tvInfoEndTimeSet.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.todo_description
//                )
//            )
//            binding.btnInfoEndTimePick.apply {
//                visibility = View.VISIBLE
//                text = FormatDate.todoTimeToStr(binding.todoItem!!.endDate!!)
//            }
//            binding.infoEndDateTimeSwitch.isChecked = binding.todoItem!!.isSelectedEndDateTime
//        } else binding.btnInfoEndTimePick.text = FormatDate.simpleTimeToStr(Date())
//
//        if (binding.todoItem!!.alarms != emptyList<Alarm>()) {
//            binding.ivInfoAlarmIcon.backgroundTintList =
//                ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.todo_description
//                    )
//                )
//            binding.tvInfoAlarmSet.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.todo_description
//                )
//            )
//            binding.btnInfoAlarmDatePick.apply {
//                visibility = View.VISIBLE
//                text = FormatDate.todoDateToStr(binding.todoItem!!.alarms[0].time)
//            }
//            binding.btnInfoAlarmTimePick.apply {
//                visibility = View.VISIBLE
//                text = FormatDate.todoTimeToStr(binding.todoItem!!.alarms[0].time)
//            }
//            binding.infoAlarmSwitch.isChecked = true
//        } else {
//            binding.btnInfoAlarmDatePick.text = FormatDate.simpleDateToStr(Date())
//            binding.btnInfoAlarmTimePick.text = FormatDate.simpleTimeToStr(Date())
//        }
//
//        if (binding.todoItem!!.repeatOption != null) {
//            binding.infoRepeatEndDateLayout.visibility = View.VISIBLE
//            binding.ivInfoRepeatIcon.backgroundTintList =
//                ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.todo_description
//                    )
//                )
//            binding.tvInfoRepeatSet.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.todo_description
//                )
//            )
//            binding.infoRepeatSwitch.isChecked = true
//            binding.infoRepeatOptionLayout.visibility = View.VISIBLE
//            when (binding.todoItem!!.repeatOption) {
//                "매일" -> {
//                    binding.btnInfoEveryDay.backgroundTintList = null
//                }
//                "2주마다", "매주" -> {
//                    if (binding.todoItem!!.repeatOption == "매주")
//                        binding.btnInfoEveryWeek.backgroundTintList = null
//                    else binding.btnInfoEvery2Week.backgroundTintList = null
//                    binding.infoEveryWeekSelectLayout.visibility = View.VISIBLE
//                    for (i in 0 until binding.todoItem!!.repeatValue!!.length) {
//                        if (binding.todoItem!!.repeatValue!![i] == '1')
//                            (binding.infoEveryWeekSelectLayout.getChildAt(i) as TextView).setTextColor(
//                                ContextCompat.getColor(requireContext(), R.color.highlight)
//                            )
//                    }
//                }
//                "매달" -> {
//                    binding.btnInfoEveryMonth.backgroundTintList = null
//                    binding.infoGridMonth.visibility = View.VISIBLE
//                    for (i in 0 until 31) {
//                        val textView = TextView(requireContext())
//                        textView.text = getString(R.string.MonthDay, i + 1)
//                        if (binding.todoItem!!.repeatValue!![i] == '1')
//                            textView.setTextColor(
//                                ColorStateList.valueOf(
//                                    ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.highlight
//                                    )
//                                )
//                            )
//                        else textView.setTextColor(
//                            ColorStateList.valueOf(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.light_gray
//                                )
//                            )
//                        )
//                        textView.gravity = Gravity.CENTER
//
//                        val params = GridLayout.LayoutParams().apply {
//                            width = 0
//                            height = GridLayout.LayoutParams.WRAP_CONTENT
//                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
//                        }
//                        binding.infoGridMonth.addView(textView, params)
//                    }
//                }
//                "매년" -> {
//                    binding.btnInfoEveryYear.backgroundTintList = null
//                    binding.infoGridYear.visibility = View.VISIBLE
//                    for (i in 0 until 12) {
//                        val textView = TextView(requireContext())
//                        textView.text = getString(R.string.YearMonth, i + 1)
//                        if (binding.todoItem!!.repeatValue!![i] == '1')
//                            textView.setTextColor(
//                                ColorStateList.valueOf(
//                                    ContextCompat.getColor(
//                                        requireContext(),
//                                        R.color.highlight
//                                    )
//                                )
//                            )
//                        else textView.setTextColor(
//                            ColorStateList.valueOf(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.light_gray
//                                )
//                            )
//                        )
//                        textView.gravity = Gravity.CENTER
//
//                        val params = GridLayout.LayoutParams().apply {
//                            width = 0
//                            height = GridLayout.LayoutParams.WRAP_CONTENT
//                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
//                        }
//                        binding.infoGridYear.addView(textView, params)
//                    }
//                }
//            }
//        }
//
//        if (binding.todoItem!!.repeatEnd != null) {
//            binding.infoRepeatEndDateSwitch.isChecked = true
//            binding.tvInfoRepeatEnd.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    R.color.todo_description
//                )
//            )
//            binding.btnInfoRepeatEndDate.apply {
//                visibility = View.VISIBLE
//                text = FormatDate.todoDateToStr(binding.todoItem!!.repeatEnd!!)
//            }
//        } else binding.btnInfoRepeatEndDate.text = FormatDate.simpleDateToStr(Date())
//
//        if (binding.todoItem!!.memo != "") {
//            binding.etInfoMemo.setText(binding.todoItem!!.memo)
//            binding.ivInfoMemoIcon.backgroundTintList =
//                ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.todo_description
//                    )
//                )
//        }
//    }

}
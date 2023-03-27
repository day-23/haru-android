package com.example.haru.view.checklist

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

class ChecklistItemFragment(checkListViewModel: CheckListViewModel, position: Int) : Fragment() {
    private lateinit var binding: FragmentChecklistItemInfoBinding
    private var checkListViewModel: CheckListViewModel
    private var position: Int

    init {
        this.checkListViewModel = checkListViewModel
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
        binding.todoItem = checkListViewModel.todoDataList.value!![position]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        binding.todoItem
    }

    private fun initView(){
        binding.cbInfoCompleted.isChecked = binding.todoItem!!.completed
        if (binding.cbInfoCompleted.isChecked)
            binding.tvInfoContent.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvInfoContent.text = binding.todoItem!!.content
        binding.cbInfoFlag.isChecked = binding.todoItem!!.flag

        if (binding.todoItem!!.tags != emptyList<Tag>()){
            binding.infoTagEt.apply {
                var text = ""
                for(i in 0 until binding.todoItem!!.tags.size)
                    text += "${binding.todoItem!!.tags[i].content} "
                setText(text)
            }
        }

        if (binding.todoItem!!.todayTodo) {
            binding.ivInfoTodayIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.highlight))
            binding.tvInfoTodayTodo.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.highlight
                )
            )
            binding.infoTodaySwitch.isChecked = binding.todoItem!!.todayTodo
        }

        if (binding.todoItem!!.endDate != null) {
            binding.ivInfoCalendarIcon.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )
            binding.tvInfoEndDateSet.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.todo_description
                )
            )
            binding.btnInfoEndDatePick.apply {
                visibility = View.VISIBLE
                text = FormatDate.todoDateToStr(binding.todoItem!!.endDate!!)
            }
            binding.infoEndDateSwitch.isChecked = true
        }

        if (binding.todoItem!!.isSelectedEndDateTime) {
            binding.infoEndDateTimeLayout.visibility = View.VISIBLE
            binding.tvInfoEndTimeSet.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.todo_description
                )
            )
            binding.btnInfoEndTimePick.apply {
                visibility = View.VISIBLE
                text = FormatDate.todoTimeToStr(binding.todoItem!!.endDate!!)
            }
            binding.infoEndDateTimeSwitch.isChecked = binding.todoItem!!.isSelectedEndDateTime
        }

        if (binding.todoItem!!.alarms != emptyList<Alarm>()) {
            binding.ivInfoAlarmIcon.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )
            binding.tvInfoAlarmSet.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.todo_description
                )
            )
            binding.btnInfoAlarmDatePick.apply {
                visibility = View.VISIBLE
                text = FormatDate.todoDateToStr(binding.todoItem!!.alarms[0].time)
            }
            binding.btnInfoAlarmTimePick.apply {
                visibility = View.VISIBLE
                text = FormatDate.todoTimeToStr(binding.todoItem!!.alarms[0].time)
            }
            binding.infoAlarmSwitch.isChecked = true
        }

        if (binding.todoItem!!.repeatOption != null) {
            binding.infoRepeatEndDateSwitch.visibility = View.VISIBLE
            binding.ivInfoRepeatIcon.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )
            binding.tvInfoRepeatSet.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.todo_description
                )
            )
            binding.infoRepeatSwitch.isChecked = true
            binding.infoRepeatOptionLayout.visibility = View.VISIBLE
            when (binding.todoItem!!.repeatOption) {
                "매일" -> {
                    binding.btnInfoEveryDay.backgroundTintList = null
                }
                "2주마다", "매주" -> {
                    if (binding.todoItem!!.repeatOption == "매주")
                        binding.btnInfoEveryWeek.backgroundTintList = null
                    else binding.btnInfoEvery2Week.backgroundTintList = null
                    binding.infoEveryWeekSelectLayout.visibility = View.VISIBLE
                    for (i in 0 until binding.todoItem!!.repeatValue!!.length) {
                        if (binding.todoItem!!.repeatValue!![i] == '1')
                            (binding.infoEveryWeekSelectLayout.getChildAt(i) as TextView).setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.highlight)
                            )
                    }
                }
                "매달" -> {
                    binding.btnInfoEveryMonth.backgroundTintList = null
                    binding.infoGridMonth.visibility = View.VISIBLE
                    for (i in 0 until 31) {
                        val textView = TextView(requireContext())
                        textView.text = getString(R.string.MonthDay, i+1)
                        if (binding.todoItem!!.repeatValue!![i] == '1')
                            textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.highlight)))
                        else textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_gray)))
                        textView.gravity = Gravity.CENTER

                        val params = GridLayout.LayoutParams().apply {
                            width = 0
                            height = GridLayout.LayoutParams.WRAP_CONTENT
                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                        }
                        binding.infoGridMonth.addView(textView, params)
                    }
                }
                "매년" -> {
                    binding.btnInfoEveryYear.backgroundTintList = null
                    binding.infoGridYear.visibility = View.VISIBLE
                    for (i in 0 until 12) {
                        val textView = TextView(requireContext())
                        textView.text = getString(R.string.YearMonth, i + 1)
                        if (binding.todoItem!!.repeatValue!![i] == '1')
                            textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.highlight)))
                        else textView.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.light_gray)))
                        textView.gravity = Gravity.CENTER

                        val params = GridLayout.LayoutParams().apply {
                            width = 0
                            height = GridLayout.LayoutParams.WRAP_CONTENT
                            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f)
                        }
                        binding.infoGridYear.addView(textView, params)
                    }
                }
            }
        }

        if (binding.todoItem!!.repeatEnd != null){
            binding.infoRepeatEndDateLayout.visibility = View.VISIBLE
            binding.infoRepeatEndDateSwitch.isChecked = true
            binding.tvInfoRepeatEnd.setTextColor(ContextCompat.getColor(requireContext(), R.color.todo_description))
            binding.btnInfoRepeatEndDate.apply {
                visibility = View.VISIBLE
                text = FormatDate.todoDateToStr(binding.todoItem!!.repeatEnd!!)
            }
        }

        if (binding.todoItem!!.memo != ""){
            binding.etInfoMemo.setText(binding.todoItem!!.memo)
            binding.ivInfoMemoIcon.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.todo_description))
        }
    }

}
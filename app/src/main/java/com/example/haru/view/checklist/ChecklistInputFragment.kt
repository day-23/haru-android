package com.example.haru.view.checklist

import android.app.Application
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.R
import com.example.haru.data.model.Repeat
import com.example.haru.data.model.TodoRequest
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class ChecklistInputFragment :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistInputBinding
    private lateinit var todoAddViewModel: TodoAddViewModel

    private var repeatData: Repeat = Repeat("", "")
    private var content: String? = ""
    private var memo: String? = ""
    private var todayTodo: Boolean = false
    private var flag: Boolean = false
    private var endDate: String? = null
    private var endDateTime: String? = null
    private var repeatEnd: String? = null
    private var tags: List<String>? = mutableListOf()
    private var subTodos: List<String>? = mutableListOf()
    private var alarms: List<String>? = mutableListOf()


    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistInputFragment {
            return ChecklistInputFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistInputFragment - onCreate() called")

        todoAddViewModel = TodoAddViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChecklistInputBinding.inflate(inflater)
        binding.viewModel = todoAddViewModel
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
            binding.checkTodayTodo.isChecked = it
        })

        todoAddViewModel.repeatOptionStr.observe(viewLifecycleOwner,androidx.lifecycle.Observer {
            if (it != null)
                binding.layoutRepeatEndDate.visibility = View.VISIBLE
            else binding.layoutRepeatEndDate.visibility = View.GONE

            binding.btnRepeatOption.text = todoAddViewModel.repeatOptionStr.value?: "선택"
        })

        todoAddViewModel.endDateTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            binding.btnTimePick.text = timeFormat.format(it)
        })

        todoAddViewModel.alarmDateTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
            binding.btnAlarmTime.text = timeFormat.format(it)
        })

        binding.checkFlagTodo.setOnClickListener(btnListener())
        binding.checkTodayTodo.setOnClickListener(btnListener())

        binding.btnDatePick.setOnClickListener(btnListener())

        binding.btnAlarmDate.setOnClickListener(btnListener())

        binding.btnTimePick.setOnClickListener(btnListener())

        binding.btnAlarmTime.setOnClickListener(btnListener())

        binding.btnRepeatOption.setOnClickListener(btnListener())

        binding.btnRepeatEndDate.setOnClickListener(btnListener())

        binding.btnSubmitTodo.setOnClickListener(btnListener())

        binding.btnClose.setOnClickListener(btnListener())

    }

    inner class btnListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btn_alarm_time, R.id.btn_time_pick -> {
                    val calendar = Calendar.getInstance()
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        requireContext(),
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                            val time = calendar.apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }.time

                            if (v.id == R.id.btn_time_pick) todoAddViewModel.setTime(0, time) else todoAddViewModel.setTime(1, time)
                        },
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                        .setTextColor(Color.BLACK)
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                        .setTextColor(Color.BLACK)
                }

                R.id.btn_alarm_date, R.id.btn_date_pick, R.id.btn_repeat_end_date -> {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        R.style.MySpinnerDatePickerStyle,
                        { _, year, monthOfYear, dayOfMonth ->
                            val month = monthOfYear + 1

                            val calendar = Calendar.getInstance()

                            calendar.set(year, monthOfYear, dayOfMonth)
                            val date = calendar.time

                            val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                            val dayName: String = dateFormat.format(date)

                            when (v.id) {
                                R.id.btn_alarm_date -> {
                                    val submitDateFormat =
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                                    binding.btnAlarmDate.text =
                                        "$year.$month.$dayOfMonth ($dayName)"

                                }
                                R.id.btn_date_pick -> {
                                    binding.btnDatePick.text =
                                        "$year.$month.$dayOfMonth ($dayName)"
                                    val submitDateFormat =
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                                    endDate = submitDateFormat.format(date)
                                }
                                R.id.btn_repeat_end_date -> {
                                    val submitDateFormat =
                                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                                    binding.btnRepeatEndDate.text =
                                        "$year.$month.$dayOfMonth ($dayName)"
                                    repeatEnd = submitDateFormat.format(date)
                                }
                            }
                        },
                        year,
                        month,
                        day
                    )
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
                    datePickerDialog.show()
                    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                        .setTextColor(resources.getColor(R.color.black))
                    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(R.color.black))
                }

                R.id.check_flag_todo -> todoAddViewModel.setFlagTodo()
                R.id.check_today_todo -> todoAddViewModel.setTodayTodo()

                R.id.btn_repeat_option -> {
                    val repeatOptionInput = ChecklistRepeatFragment(todoAddViewModel)
                    repeatOptionInput.show(parentFragmentManager, repeatOptionInput.tag)
                }

                R.id.btn_close -> dismiss()

                R.id.btn_submit_todo -> {

                    content = binding.todoEt.text.toString()
                    memo = binding.etMemo.text.toString()
                    Log.d("20191627", content.toString())


                    if (content == null || content!!.replace(" ", "").equals(""))
//                        CustomToast.makeText(context, "할 일이 비어있습니다.", Toast.LENGTH_SHORT)
                    else {
                        if (memo == null || memo!!.replace(" ", "").equals(""))
                            memo = ""
                        else
                            memo = binding.etMemo.text.toString()
                        todayTodo = binding.checkTodayTodo.isChecked
                        flag = binding.checkFlagTodo.isChecked
                        tags = binding.tagEt.text.toString().split(" ")
                        var todoRequest = TodoRequest(
                            content!!,
                            memo!!,
                            todayTodo,
                            flag,
                            endDate,
                            endDateTime,
                            repeatData.repeatOption,
                            repeatData.repeat,
                            repeatEnd,
                            tags!!,
                            subTodos!!,
                            alarms!!
                        )
                        Log.d("20191627", todoRequest.toString())


                    }

                }

            }
        }
    }


}
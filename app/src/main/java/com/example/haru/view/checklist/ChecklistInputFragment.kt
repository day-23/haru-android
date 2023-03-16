package com.example.haru.view.checklist


import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.haru.R
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

    private var onDismissListener: (() -> Unit)? = null

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

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

        todoAddViewModel.repeatOptionStr.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null)
                binding.layoutRepeatEndDate.visibility = View.VISIBLE
            else binding.layoutRepeatEndDate.visibility = View.GONE

            binding.btnRepeatOption.text = todoAddViewModel.repeatOptionStr.value ?: "선택"
        })

        todoAddViewModel.endTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnTimePick.text = timeFormat.format(it)
            } else binding.btnTimePick.text = "선택"
        })

        todoAddViewModel.alarmTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnAlarmTime.text = timeFormat.format(it)
            } else binding.btnAlarmTime.text = "선택"
        })

        todoAddViewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnDatePick.text = timeFormat.format(it)
            } else binding.btnDatePick.text = "선택"
        })

        todoAddViewModel.alarmDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnAlarmDate.text = timeFormat.format(it)
            } else binding.btnAlarmDate.text = "선택"
        })

        todoAddViewModel.repeatEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnRepeatEndDate.text = timeFormat.format(it)
            } else binding.btnRepeatEndDate.text = "선택"
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

    override fun dismiss() {
        onDismissListener?.invoke()
        super.dismiss()
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
                        MyTimeSetListener(object : TimePickerDialog.OnTimeSetListener {
                            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                                val time = calendar.apply {
                                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    set(Calendar.MINUTE, minute)
                                }.time

                                if (v.id == R.id.btn_time_pick)
                                    todoAddViewModel.setTime(0, time)
                                else todoAddViewModel.setTime(1, time)

                                // timepicker 리스너 만들기 버튼을 없애고 시간을 선택하면 바로 적용되게끔
                            }
                        }),
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()

                }

                R.id.btn_alarm_date, R.id.btn_date_pick, R.id.btn_repeat_end_date -> {
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
                                R.id.btn_date_pick -> todoAddViewModel.setDate(0, date)
                                R.id.btn_alarm_date -> todoAddViewModel.setDate(1, date)
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

                R.id.check_flag_todo -> todoAddViewModel.setFlagTodo()
                R.id.check_today_todo -> todoAddViewModel.setTodayTodo()

                R.id.btn_repeat_option -> {
                    val repeatOptionInput = ChecklistRepeatFragment(todoAddViewModel)
                    repeatOptionInput.show(parentFragmentManager, repeatOptionInput.tag)
                }

                R.id.btn_close -> {
                    todoAddViewModel.clearSubmitTodo()
                    dismiss()
                }

                R.id.btn_submit_todo -> {
                    if (todoAddViewModel.content == "" || todoAddViewModel.content.replace(
                            " ",
                            ""
                        ) == ""
                    )
//                        CustomToast.makeText(context, "할 일이 비어있습니다.", Toast.LENGTH_SHORT)
                    else {
                        todoAddViewModel.readyToSubmit()
                        todoAddViewModel.submitTodo()
                        todoAddViewModel.clearSubmitTodo()

                        dismiss()
                    }

                }

            }
        }
    }


}
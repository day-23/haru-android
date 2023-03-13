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
import com.example.haru.R
import com.example.haru.data.model.Repeat
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class ChecklistInputFragment : BottomSheetDialogFragment(),
    ChecklistRepeatFragment.RepeatDismissListener {
    private lateinit var binding: FragmentChecklistInputBinding
    private lateinit var repeatData: Repeat

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistInputFragment {
            return ChecklistInputFragment()
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

        binding.btnClose.setOnClickListener(btnListener())

        binding.checkFlagTodo.setOnClickListener(btnListener())

        binding.checkTodayTodo.setOnClickListener(btnListener())

        binding.btnDatePick.setOnClickListener(btnListener())

        binding.btnAlarmDate.setOnClickListener(btnListener())

        binding.btnTimePick.setOnClickListener(btnListener())

        binding.btnAlarmTime.setOnClickListener(btnListener())

        binding.btnRepeatOption.setOnClickListener(btnListener())

        binding.btnRepeatEndDate.setOnClickListener(btnListener())


    }

    override fun repeatDismissListener(repeatData: Repeat, callback : (Repeat) -> Unit) {
        this@ChecklistInputFragment.repeatData = repeatData
        callback(repeatData)
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
                            val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                            val time = calendar.apply {
                                set(Calendar.HOUR_OF_DAY, hourOfDay)
                                set(Calendar.MINUTE, minute)
                            }.time
                            Log.d("TAG", time.toString())
                            when (v.id) {
                                R.id.btn_alarm_time -> binding.btnAlarmTime.text =
                                    timeFormat.format(time)
                                R.id.btn_time_pick -> binding.btnTimePick.text =
                                    timeFormat.format(time)
                            }
                        },
                        hour,
                        minute,
                        false
                    )

                    timePickerDialog.show()
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)
                        .setTextColor(resources.getColor(R.color.black))
                    timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                        .setTextColor(resources.getColor(R.color.black))
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
                                R.id.btn_alarm_date -> binding.btnAlarmDate.text =
                                    "$year.$month.$dayOfMonth ($dayName)"
                                R.id.btn_date_pick -> binding.btnDatePick.text =
                                    "$year.$month.$dayOfMonth ($dayName)"
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

                R.id.check_flag_todo -> binding.checkFlagTodo.toggle()
                R.id.check_today_todo -> binding.checkTodayTodo.toggle()

                R.id.btn_repeat_option -> {
                    val repeatOptionInput =
                        ChecklistRepeatFragment(this@ChecklistInputFragment) { repeatData ->
                            if (this@ChecklistInputFragment.repeatData.repeatOption != "")
                                binding.layoutRepeatEndDate.visibility = View.VISIBLE
                            else binding.layoutRepeatEndDate.visibility = View.GONE
                        }
                    repeatOptionInput.show(parentFragmentManager, repeatOptionInput.tag)
                }
                R.id.btn_close -> dismiss()

            }
        }
    }




}
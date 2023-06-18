package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentAlarmBinding
import com.example.haru.utils.Alarm
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.customDialog.CustomTimeDialog
import com.example.haru.viewmodel.EtcViewModel
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAlarmBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): AlarmFragment {
            return AlarmFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "AlarmFragment - onCreate() called")
        MainActivity.hideNavi(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.alarmHeaderTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireContext().getSharedPreferences("ApplyData", 0)
        val editor = sharedPreference.edit()

        val timeformatter = SimpleDateFormat("a h:mm", Locale.KOREA)

        (activity as BaseActivity).adjustTopMargin(binding.alarmHeaderTitle.id)
        binding.ivBackIconAlarm.setOnClickListener(ClickListener())

        binding.amAlarmSwitch.isChecked = User.amAlarmAprove
        binding.pmAlarmSwitch.isChecked = User.pmAlarmAprove

        binding.amAlarmTime.text = User.amAlarmDate
        binding.pmAlarmTime.text = User.pmAlarmDate

        if(binding.amAlarmSwitch.isChecked){
            binding.amAlarmTime.visibility = View.VISIBLE
        }

        if(binding.pmAlarmSwitch.isChecked){
            binding.pmAlarmTime.visibility = View.VISIBLE
        }

        binding.amAlarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.amAlarmTime.visibility = View.VISIBLE
            } else {
                binding.amAlarmTime.visibility = View.INVISIBLE
            }

            User.amAlarmAprove = isChecked
            editor.putBoolean("amAlarmAprove", User.amAlarmAprove)
        }

        binding.pmAlarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                binding.pmAlarmTime.visibility = View.VISIBLE
            } else {
                binding.pmAlarmTime.visibility = View.INVISIBLE
            }

            User.pmAlarmAprove = isChecked
            editor.putBoolean("pmAlarmAprove", User.pmAlarmAprove)
        }

        binding.amAlarmTime.setOnClickListener {
            val timePicker = CustomTimeDialog(timeformatter.parse(binding.amAlarmTime.text.toString()))
            timePicker.timePickerClick = object : CustomTimeDialog.TimePickerClickListener {
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

                    val date = Date()
                    date.hours = hour
                    date.minutes = minute*5
                    date.seconds = 0

                    binding.amAlarmTime.text = timeformatter.format(date)
                    User.amAlarmDate = timeformatter.format(date)
                    editor.putString("amAlarmDate", User.amAlarmDate)
                    editor.apply()

                    Alarm.amPmAlarmEdit(requireContext())
                }
            }
            timePicker.show(parentFragmentManager, null)
        }

        binding.pmAlarmTime.setOnClickListener {
            val timeformatter = SimpleDateFormat("a h:mm", Locale.KOREA)

            val timePicker = CustomTimeDialog(timeformatter.parse(binding.pmAlarmTime.text.toString()))
            timePicker.timePickerClick = object : CustomTimeDialog.TimePickerClickListener {
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

                    val date = Date()
                    date.hours = hour
                    date.minutes = minute*5
                    date.seconds = 0

                    binding.pmAlarmTime.text = timeformatter.format(date)
                    User.pmAlarmDate = timeformatter.format(date)
                    editor.putString("pmAlarmDate", User.pmAlarmDate)
                    editor.apply()

                    Alarm.amPmAlarmEdit(requireContext())
                }
            }
            timePicker.show(parentFragmentManager, null)
        }
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAlarm.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}
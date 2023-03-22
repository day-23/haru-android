package com.example.haru.view.checklist


import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.BackgroundColorSpan
import android.text.style.ImageSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.TranslateAnimation
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.view.get
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.haru.R
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.example.haru.viewmodel.CheckListViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipDrawable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ChecklistInputFragment(checkListViewModel: CheckListViewModel) :
    BottomSheetDialogFragment(){
    private lateinit var binding: FragmentChecklistInputBinding
    private lateinit var todoAddViewModel: TodoAddViewModel

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

        binding.endDateSetLayout.layoutTransition.apply {
            setAnimateParentHierarchy(false)
        }

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

        todoAddViewModel.isSelectedEndDateTime.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                when (it) {
                    true -> {
                        binding.endDateSwitch.isChecked = it
                        binding.btnEndDatePick.visibility = View.VISIBLE
                        Log.d("20191627", LocalDateTime.now().toString())
//                        binding.btnEndDatePick.text = dateFormat(LocalDateTime.now())

                        binding.endDateTimeLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                            override fun onGlobalLayout() {
                                binding.endDateTimeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                                val valueAnimator = ValueAnimator.ofInt(binding.endDateSetLayout.height, binding.endDateSetLayout.height + binding.endDateTimeLayout.measuredHeight)
                                valueAnimator.addUpdateListener { animator ->
                                    val layoutParams = binding.endDateSetLayout.layoutParams
                                    layoutParams.height = animator.animatedValue as Int
                                    binding.endDateSetLayout.layoutParams = layoutParams
                                }
                                valueAnimator.duration = 400
                                valueAnimator.start()
                            }
                        })

                        binding.endDateTimeLayout.visibility = View.VISIBLE

                    }
                    else -> {
                        binding.endDateSwitch.isChecked = it
                        binding.btnEndDatePick.visibility = View.INVISIBLE
                        binding.endDateTimeLayout.visibility = View.GONE
                    }
                }
            })

        todoAddViewModel.endTimeSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    binding.endDateTimeSwitch.isChecked = it
                    binding.btnEndTimePick.visibility = View.VISIBLE
//                    binding.btnEndTimePick.text = timeFormat(LocalDateTime.now())
                }
                else -> {
                    binding.endDateTimeSwitch.isChecked = it
                    binding.btnEndTimePick.visibility = View.INVISIBLE
                }
            }
        })

        todoAddViewModel.alarmSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    binding.alarmSwitch.isChecked = it
                    binding.btnAlarmDatePick.text = dateFormat(LocalDateTime.now())
                    binding.btnAlarmTimePick.text = timeFormat(LocalDateTime.now())
                    binding.btnAlarmDatePick.visibility = View.VISIBLE
                    binding.btnAlarmTimePick.visibility = View.VISIBLE
                }
                else -> {
                    binding.alarmSwitch.isChecked = it
                    binding.btnAlarmDatePick.visibility = View.INVISIBLE
                    binding.btnAlarmTimePick.visibility = View.INVISIBLE
                }
            }
        })

        todoAddViewModel.repeatSwitch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                true -> {
                    binding.repeatSwitch.isChecked = it
                }
                else -> {
                    binding.repeatSwitch.isChecked = it
                }
            }
        })

//        todoAddViewModel.repeatOptionStr.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            if (it != null)
//                binding.layoutRepeatEndDate.visibility = View.VISIBLE
//            else binding.layoutRepeatEndDate.visibility = View.GONE
//
//            binding.btnRepeatOption.text = todoAddViewModel.repeatOptionStr.value ?: "선택"
//        })
//
        todoAddViewModel.endTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnEndTimePick.text = timeFormat.format(it)
            } else binding.btnEndTimePick.text = "시간 선택"
        })
//
        todoAddViewModel.alarmTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
                binding.btnAlarmTimePick.text = timeFormat.format(it)
            } else binding.btnAlarmTimePick.text = "시간 선택"
        })
//
        todoAddViewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.US)
                binding.btnEndDatePick.text = timeFormat.format(it)
            } else binding.btnEndDatePick.text = "날짜 선택"
        })
//
        todoAddViewModel.alarmDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)
                binding.btnAlarmDatePick.text = timeFormat.format(it)
            } else binding.btnAlarmDatePick.text = "날짜 선택"
        })
//
        todoAddViewModel.repeatEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val timeFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.US)
                binding.btnRepeatEndDate.text = timeFormat.format(it)
            } else binding.btnRepeatEndDate.text = "날짜 선택"
        })
//
////        todoAddViewModel.tag.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
////            Log.d("20191627", it)
////            if (it.isNotEmpty() && it.replace(" ", "") != "" && it.last() == ' '){
////                todoAddViewModel.addTagList()
//////                binding.tagEt.text
////            }
////        })
//
        binding.checkFlagTodo.setOnClickListener(btnListener())
        binding.todaySwitch.setOnClickListener(btnListener())

        binding.endDateSwitch.setOnClickListener(btnListener())
        binding.endDateTimeSwitch.setOnClickListener(btnListener())
        binding.alarmSwitch.setOnClickListener(btnListener())
        binding.repeatSwitch.setOnClickListener(btnListener())
//
        binding.btnEndDatePick.setOnClickListener(btnListener())

        binding.btnAlarmDatePick.setOnClickListener(btnListener())

        binding.btnEndTimePick.setOnClickListener(btnListener())

        binding.btnAlarmTimePick.setOnClickListener(btnListener())

//        binding.btnRepeatOption.setOnClickListener(btnListener())
//
        binding.btnRepeatEndDate.setOnClickListener(btnListener())
//
//        binding.btnSubmitTodo.setOnClickListener(btnListener())
//
//        binding.btnClose.setOnClickListener(btnListener())


//        binding.tagEt.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                Log.d(
//                    "20191627",
//                    "before : " + s.toString() + "start - ${start}, count - ${count}, after - ${after}"
//                )
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (start == 0 && s?.last() == ' ')
//                    binding.tagEt.text?.clear()
//                Log.d(
//                    "20191627",
//                    "change : " + s.toString() + "start - ${start}, before - ${before}, count - ${count}"
//                )
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                Log.d("20191627", "after : " + s.toString())
//            }
//        })
//
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
                R.id.endDate_switch -> todoAddViewModel.setIsSelectedEndDateTime()
                R.id.endDateTime_switch -> todoAddViewModel.setEndTimeSwitch()
                R.id.alarm_switch -> todoAddViewModel.setAlarmSwitch()
                R.id.repeat_switch -> todoAddViewModel.setRepeatSwitch()

                R.id.btn_alarmTime_pick, R.id.btn_endTime_pick -> {
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

                                if (v.id == R.id.btn_endTime_pick)
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

//                R.id.btn_repeat_option -> {
//                    val repeatOptionInput = ChecklistRepeatFragment(todoAddViewModel)
//                    repeatOptionInput.show(parentFragmentManager, repeatOptionInput.tag)
//                }
//
//                R.id.btn_close -> {
//                    todoAddViewModel.clearSubmitTodo()
//                    dismiss()
//                }

//                R.id.btn_submit_todo -> {
//                    if (todoAddViewModel.content == "" || todoAddViewModel.content.replace(
//                            " ",
//                            ""
//                        ) == ""
//                    )
//                        Toast.makeText(context, "할 일이 비어있습니다.", Toast.LENGTH_SHORT).show()
//                    else {
//                        todoAddViewModel.readyToSubmit()
//                        todoAddViewModel.addTodo {
//                            Log.d("20191627", "dismiss")
//                            dismiss()
//                        }
//
//                        todoAddViewModel.clearSubmitTodo()
//                    }
//
//                }

            }
        }
    }

    fun timeFormat(date: LocalDateTime): String {
        val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)
        return timeFormat.format(date)
    }

    fun dateFormat(date: LocalDateTime): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)
        return dateFormat.format(date)
    }


}
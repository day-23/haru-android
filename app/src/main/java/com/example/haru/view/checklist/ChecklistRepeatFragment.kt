package com.example.haru.view.checklist


import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.haru.R
import com.example.haru.data.model.Repeat
import com.example.haru.databinding.FragmentChecklistRepeatBinding
import com.example.harudemo.utils.CustomToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChecklistRepeatFragment(var listener : RepeatDismissListener, private val callback :(Repeat) -> Unit) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistRepeatBinding
    private var repeatOption: Int = 0
    var repeatData: Repeat = Repeat("","")

    companion object {
        const val TAG: String = "로그"

        fun newInstance(listener: RepeatDismissListener): ChecklistRepeatFragment {
            return ChecklistRepeatFragment(listener, callback = {})

        }
    }

    interface RepeatDismissListener{
        fun repeatDismissListener(repeatData : Repeat, callback: (Repeat) -> Unit)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistRepeatFragment - onCreate() called")

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChecklistRepeatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMonday.setOnClickListener(ClickListener())
        binding.tvTuesday.setOnClickListener(ClickListener())
        binding.tvWednesday.setOnClickListener(ClickListener())
        binding.tvThursday.setOnClickListener(ClickListener())
        binding.tvFriday.setOnClickListener(ClickListener())
        binding.tvSaturday.setOnClickListener(ClickListener())
        binding.tvSunday.setOnClickListener(ClickListener())

        binding.layoutEveryDay.setOnClickListener(ClickListener())
        binding.layoutEveryWeek.setOnClickListener(ClickListener())
        binding.layoutEvery2week.setOnClickListener(ClickListener())
        binding.layoutEveryMonth.setOnClickListener(ClickListener())
        binding.layoutEveryYear.setOnClickListener(ClickListener())
//        binding.layoutRepeatCustom.setOnClickListener(ClickListener())

        binding.btnCloseRepeat.setOnClickListener(ClickListener())
        binding.btnAddRepeat.setOnClickListener(ClickListener())

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.repeatDismissListener(repeatData, callback)
    }



    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.tv_monday,
                R.id.tv_tuesday,
                R.id.tv_wednesday,
                R.id.tv_thursday,
                R.id.tv_friday,
                R.id.tv_saturday,
                R.id.tv_sunday -> {
                    if (repeatOption != R.id.layout_every_week) {
                        return
                    }
                    if ((v as TextView).currentTextColor == resources.getColor(R.color.black))
                        v.setTextColor(Color.BLUE)
                    else v.setTextColor(Color.BLACK)
                }

                R.id.layout_every_day,
                R.id.layout_every_week,
                R.id.layout_every_2week,
                R.id.layout_every_month,
                R.id.layout_every_year -> {
                    if (repeatOption == 0) {
                        repeatOption = v.id
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.VISIBLE
                    } else {
                        for (i in 0 until binding.layoutRepeatSelectDay.childCount) {
                            (binding.layoutRepeatSelectDay.getChildAt(i) as TextView).setTextColor(
                                Color.BLACK
                            )
                        }
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.INVISIBLE
                        repeatOption = v.id
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.VISIBLE
                    }
                }

                R.id.btn_add_repeat -> {
                    Log.d("20191627", "clicked btn_add_repeat")
                    if (repeatOption == 0)
                        CustomToast.makeText(
                            requireContext(),
                            "반복 옵션을 선택하지 않았습니다.",
                            Toast.LENGTH_SHORT
                        )
                    else if (repeatOption == binding.layoutEveryWeek.id) {
                        repeatData.repeatOption = binding.tvEveryWeek.text.toString()
                        for (i in 0 until binding.layoutRepeatSelectDay.childCount) {
                            if ((binding.layoutRepeatSelectDay.getChildAt(i) as TextView).currentTextColor == Color.BLUE)
                                repeatData.repeat += "1"
                            else repeatData.repeat += "0"
                        }
                        dismiss()
                    } else {
                        repeatData.repeatOption = (binding.root.findViewById<LinearLayout>(repeatOption).getChildAt(0) as TextView).text.toString()
                        dismiss()
                    }
                }
                R.id.btn_close_repeat -> {
                    dismiss()
                }
            }
        }
    }
}
package com.example.haru.view.checklist

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.haru.R
import com.example.haru.databinding.FragmentChecklistRepeatBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChecklistRepeatFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistRepeatBinding
    private var repeatOption: Int = 0

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistRepeatFragment {
            return ChecklistRepeatFragment()

        }
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

        binding.tvMonday.setOnClickListener(clickListener())
        binding.tvTuesday.setOnClickListener(clickListener())
        binding.tvWednesday.setOnClickListener(clickListener())
        binding.tvThursday.setOnClickListener(clickListener())
        binding.tvFriday.setOnClickListener(clickListener())
        binding.tvSaturday.setOnClickListener(clickListener())
        binding.tvSunday.setOnClickListener(clickListener())

        binding.layoutEveryDay.setOnClickListener(clickListener())
        binding.layoutEveryWeek.setOnClickListener(clickListener())
        binding.layoutEvery2week.setOnClickListener(clickListener())
        binding.layoutEveryMonth.setOnClickListener(clickListener())
        binding.layoutEveryYear.setOnClickListener(clickListener())
        binding.layoutRepeatCustom.setOnClickListener(clickListener())

        binding.btnCloseRepeat.setOnClickListener(clickListener())

    }

    inner class clickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.tv_monday,
                R.id.tv_tuesday,
                R.id.tv_wednesday,
                R.id.tv_thursday,
                R.id.tv_friday,
                R.id.tv_saturday,
                R.id.tv_sunday -> {
                    if (repeatOption != R.id.layout_repeat_custom){
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
                R.id.layout_every_year,
                R.id.layout_repeat_custom-> {
                    if (repeatOption == 0) {
                        repeatOption = v.id
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.VISIBLE
                    } else {
                        for(i in 0 until binding.layoutRepeatSelectDay.childCount){
                            (binding.layoutRepeatSelectDay.getChildAt(i) as TextView).setTextColor(Color.BLACK)
                        }
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.INVISIBLE
                        repeatOption = v.id
                        binding.root.findViewById<LinearLayout>(repeatOption)
                            .getChildAt(1).visibility = View.VISIBLE
                    }
                }

                R.id.btn_close_repeat -> dismiss()
            }
        }
    }
}
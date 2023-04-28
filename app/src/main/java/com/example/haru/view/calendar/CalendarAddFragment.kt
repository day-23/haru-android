package com.example.haru.view.checklist

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.haru.R
import com.example.haru.databinding.FragmentCalendarInputBinding
import com.example.haru.view.calendar.CalendarFragment.Companion.TAG
import com.example.haru.viewmodel.CheckListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CalendarAddFragment :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCalendarInputBinding

    private var repeatSetLayoutHeight = 0
    private var repeatOptionHeight = 0
    private var repeatEndDateHeight = 0

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): CalendarAddFragment {
            return CalendarAddFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CalendarAddFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarInputBinding.inflate(inflater)

        binding.btnCloseSchedule.setOnClickListener { dismiss() }

        binding.alldaySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.alldayIv.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))
                binding.alldayTv.setTextColor(Color.parseColor("#191919"))

                binding.repeatStartTimeBtn.visibility = View.GONE
                binding.repeatEndTimeBtn.visibility = View.GONE
            } else {
                binding.alldayIv.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)
                binding.alldayTv.setTextColor(Color.LTGRAY)

                binding.repeatStartTimeBtn.visibility = View.VISIBLE
                binding.repeatEndTimeBtn.visibility = View.VISIBLE
            }
        }

        binding.repeatSwitchSchedule.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.repeatTvSchedule.setTextColor(Color.parseColor("#191919"))
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#191919"))

                binding.repeatOptionSelectSchedule.visibility = View.VISIBLE
                binding.repeatEndLayout.visibility = View.VISIBLE
            } else {
                binding.repeatTvSchedule.setTextColor(Color.LTGRAY)
                binding.repeatIvSchedule.backgroundTintList =
                    ColorStateList.valueOf(Color.LTGRAY)

                binding.repeatOptionSelectSchedule.visibility = View.GONE
                binding.repeatEndLayout.visibility = View.GONE
            }
        }

        binding.btnEveryDaySchedule.setOnClickListener{onOptionClick(binding, 0)}
        binding.btnEveryWeekSchedule.setOnClickListener{onOptionClick(binding, 1)}
        binding.btnEvery2WeekSchedule.setOnClickListener{onOptionClick(binding, 2)}
        binding.btnEveryMonthSchedule.setOnClickListener{onOptionClick(binding, 3)}
        binding.btnEveryYearSchedule.setOnClickListener{onOptionClick(binding, 4)}

        return binding.root
    }

    fun onOptionClick(binding: FragmentCalendarInputBinding, opt:Int){
        for (i in 0 until binding.repeatOptionSelectSchedule.childCount) {
            if (i == opt)
                binding.repeatOptionSelectSchedule.getChildAt(i).backgroundTintList = null
            else
                binding.repeatOptionSelectSchedule.getChildAt(i).backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.transparent
                        )
                    )
        }

        if(opt == 1){
            binding.everyWeekLayout.visibility = View.VISIBLE
        } else {
            binding.everyWeekLayout.visibility = View.GONE
        }
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
}




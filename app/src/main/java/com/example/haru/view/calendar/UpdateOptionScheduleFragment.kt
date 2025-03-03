package com.example.haru.view.calendar

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.example.haru.R
import com.example.haru.databinding.FragmentOptionUpdateBinding
import com.example.haru.databinding.FragmentOptionUpdateScheduleBinding
import com.example.haru.view.checklist.ChecklistItemFragment.UpdateType
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOptionScheduleFragment(val sizeOption: Int, val callback: (Int) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionUpdateScheduleBinding
    private var ratio: Int = 43


    init {
        ratio = when (sizeOption) {
            // 취소 버튼 제외하고 선택지가 1개인 경우
            0,1,2,3,6 -> {
                27
            }

            // 취소 버튼 제외하고 선택지가 2개인 경우
            else -> {
                35
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOptionUpdateScheduleBinding.inflate(inflater)

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
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * ratio / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params: LinearLayout.LayoutParams =
            binding.layoutParentBtnUpdate.layoutParams as LinearLayout.LayoutParams

        params.weight = when (sizeOption) {
            0 -> { // 단일 일정
                binding.textViewUpdateInfo.text =
                    getString(R.string.updateDescription).substring(0, 12)
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAllUpdate)
                    removeView(binding.btnOptionAfterUpdate)
                }
                2f
            }
            1 -> { // 모든 일정 수정 (반복옵션 수정) front
                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionAllUpdate)
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAfterUpdate)
                    removeView(binding.btnOptionSave)
                }
                2f
            }
            2,3,6 -> { // 이 이벤트부터 수정(반복옵션 수정) middle, back
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionAllUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionSave)
                }
                2f
            }
//            3 -> { // 전체 할일 수정 (마감일, 반복 옵션 둘다 수정)
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionOneUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                2f
//            }
            4 -> { // 디폴트(반복옵션 수정x) front
                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionAfterUpdate)
                }
                3f
            }
            else -> { // 디폴트(반복옵션 수정x) middle
                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionAfterUpdate)
                    removeView(binding.btnOptionAllUpdate)
                    removeView(binding.btnOptionSave)
                }
                3f
            }
//            else -> { // 모든 옵션을 보여주는 상황
//                binding.layoutParentBtnUpdate.removeView(binding.btnOptionSave)
//                4f
//            }
        }

        binding.layoutParentBtnUpdate.apply {
            layoutParams = params
            (getChildAt(childCount - 1) as AppCompatButton).setBackgroundResource(R.drawable.option_last_view_bg)
        }

        binding.btnOptionOneUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAfterUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionSave.setOnClickListener(ButtonClickListener())
        binding.btnOptionUpdateCancel.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnOptionOneUpdate.id -> {
                    binding.btnOptionOneUpdate.isClickable = false
                    callback(1)
                    dismiss()
                }
                binding.btnOptionAllUpdate.id -> {
                    binding.btnOptionAllUpdate.isClickable = false
                    callback(2)
                    dismiss()
                }
                binding.btnOptionAfterUpdate.id -> {
                    binding.btnOptionAfterUpdate.isClickable = false
                    callback(3)
                    dismiss()
                }

                binding.btnOptionSave.id -> {
                    binding.btnOptionSave.isClickable = false
                    callback(0)
                    dismiss()
                }

                binding.btnOptionUpdateCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
package com.example.haru.view.calendar

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.example.haru.R
import com.example.haru.databinding.FragmentOptionDeleteBinding
import com.example.haru.databinding.FragmentOptionDeleteScheduleBinding
import com.example.haru.view.checklist.ChecklistItemFragment.*
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteOptionScheduleFragment(val callback: (Int) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionDeleteScheduleBinding
    private var ratio: Int = 35

    init {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOptionDeleteScheduleBinding.inflate(inflater)

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
            binding.layoutParentBtnDelete.layoutParams as LinearLayout.LayoutParams

        params.weight = 3f
//        params.weight = when (type) {
//            DeleteType.REPEAT_FRONT, DeleteType.REPEAT_MIDDLE, DeleteType.REPEAT_BACK -> {
//                binding.layoutParentBtnDelete.removeView(binding.btnOptionDelete)
//                3f
//            }
//            DeleteType.NOT_REPEAT -> {
//                binding.layoutParentBtnDelete.removeView(binding.btnOptionAllDelete)
//                binding.layoutParentBtnDelete.removeView(binding.btnOptionOneDelete)
//                binding.textViewDeleteInfo.text = "이 일정을 삭제할까요?"
//                2f
//            }
//        }
        binding.layoutParentBtnDelete.apply {
            layoutParams = params
            (getChildAt(childCount - 1) as AppCompatButton).setBackgroundResource(R.drawable.option_last_view_bg)
        }

        binding.btnOptionOneDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionDeleteCancel.setOnClickListener(ButtonClickListener())
        binding.btnOptionDelete.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnOptionOneDelete.id -> {
                    binding.btnOptionOneDelete.isClickable = false
                    callback(1)
                    dismiss()
                    // 반복 할 일의 front 삭제
//                    when (type) {
//                        DeleteType.REPEAT_FRONT -> { // 반복 할 일의 front 삭제
//                            todoAddViewModel.deleteRepeatFrontTodo {
//                                binding.btnOptionOneDelete.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        DeleteType.REPEAT_MIDDLE -> { // 반복 할 일의 middle 삭제
//                            todoAddViewModel.deleteRepeatMiddleTodo {
//                                binding.btnOptionOneDelete.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        DeleteType.REPEAT_BACK -> { // 반복 할 일의 back 삭제
//                            todoAddViewModel.deleteRepeatBackTodo {
//                                binding.btnOptionOneDelete.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        else -> {
//                            binding.btnOptionOneDelete.isClickable = true
//                        }
//                    }
                }

                binding.btnOptionAllDelete.id -> {
                    binding.btnOptionAllDelete.isClickable = false
                    callback(2)
                    dismiss()
//                    todoAddViewModel.deleteTodo {
//                        binding.btnOptionAllDelete.isClickable = true
//                        dismiss()
//                        requireActivity().supportFragmentManager.popBackStack()
//                    }
                }
                binding.btnOptionDelete.id -> {
                    binding.btnOptionDelete.isClickable = false
                    callback(0)
                    dismiss()
//                    todoAddViewModel.deleteTodo {
//                        binding.btnOptionDelete.isClickable = true
//                        dismiss()
//                        requireActivity().supportFragmentManager.popBackStack()
//                    }
                }

                binding.btnOptionDeleteCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
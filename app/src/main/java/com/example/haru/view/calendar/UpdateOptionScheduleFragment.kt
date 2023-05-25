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

class UpdateOptionScheduleFragment(val callback: (Int) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionUpdateScheduleBinding
    private var ratio: Int = 43


    init {
//        ratio = when (type) {
//            // 취소 버튼 제외하고 선택지가 1개인 경우
//            UpdateType.FRONT_ONE, UpdateType.FRONT_TWO,
//            UpdateType.BACK_TWO, UpdateType.NOT_REPEAT, UpdateType.MIDDLE_TWO -> {
//                27
//            }
//
//            // 취소 버튼 제외하고 선택지가 2개인 경우
//            UpdateType.FRONT_THREE, UpdateType.MIDDLE_ONE, UpdateType.BACK_ONE -> {
//                35
//            }
//
//            // 취소 버튼 제외하고 선택지가 3개인 경우
//            UpdateType.MIDDLE_THREE, UpdateType.BACK_THREE -> {
//                43
//            }
//        }
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

        params.weight = 4f

//        params.weight = when (type) {
//            UpdateType.FRONT_ONE -> { // 전체 할일 수정 (마감일, 반복 옵션 둘다 수정)
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionOneUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                2f
//            }
//            UpdateType.FRONT_TWO -> { // 이 할일만 수정 (마감일 수정, 반복 옵션 수정X)
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionAllUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                2f
//            }
//            UpdateType.FRONT_THREE -> { // 전체 할일 수정, 이 할일만 수정
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionAfterUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                3f
//            }
//            UpdateType.MIDDLE_ONE, UpdateType.BACK_ONE -> { // 전체 할일 수정, 이 할일부터 수정
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionOneUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                3f
//            }
//            UpdateType.MIDDLE_TWO, UpdateType.BACK_TWO -> { // 이 할일만 수정
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionAllUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
//                    removeView(binding.btnOptionSave)
//                }
//                2f
//            }
//            UpdateType.MIDDLE_THREE, UpdateType.BACK_THREE -> { // 모든 옵션을 보여주는 상황
//                binding.layoutParentBtnUpdate.removeView(binding.btnOptionSave)
//                4f
//            }
//            UpdateType.NOT_REPEAT -> {
//                binding.textViewUpdateInfo.text =
//                    getString(R.string.updateDescription).substring(0, 12)
//                binding.layoutParentBtnUpdate.apply {
//                    removeView(binding.btnOptionOneUpdate)
//                    removeView(binding.btnOptionAllUpdate)
//                    removeView(binding.btnOptionAfterUpdate)
//                }
//                2f
//            }
//        }

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
//                    when (type) {
//                        UpdateType.FRONT_TWO, UpdateType.FRONT_THREE -> { // front
//                            todoAddViewModel.updateRepeatFrontTodo {
//                                binding.btnOptionOneUpdate.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        UpdateType.MIDDLE_TWO, UpdateType.MIDDLE_THREE -> { // middle
//                            todoAddViewModel.updateRepeatMiddleTodo {
//                                binding.btnOptionOneUpdate.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        UpdateType.BACK_TWO, UpdateType.BACK_THREE -> { // back
//                            todoAddViewModel.updateRepeatBackTodo {
//                                binding.btnOptionOneUpdate.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        else -> {
//                            Log.d("20191627", "UpdateOptionDialog -> OneUpdate 잘못된 Type")
//                        }
//                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    binding.btnOptionAllUpdate.isClickable = false
                    callback(2)
                    dismiss()
//                    todoAddViewModel.updateTodo {
//                        binding.btnOptionAllUpdate.isClickable = true
//                        dismiss()
//                        requireActivity().supportFragmentManager.popBackStack()
//                    }
                }
                binding.btnOptionAfterUpdate.id -> {
                    binding.btnOptionAfterUpdate.isClickable = false
                    callback(3)
                    dismiss()
                    // middle, back의 이후부터 수정
//                    when (type) {
//                        UpdateType.MIDDLE_ONE, UpdateType.MIDDLE_THREE,
//                        UpdateType.BACK_ONE, UpdateType.BACK_THREE -> {
//                            todoAddViewModel.updateRepeatBackTodo {
//                                binding.btnOptionAfterUpdate.isClickable = true
//                                dismiss()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                        }
//                        else -> {}
//                    }

                }

                binding.btnOptionSave.id -> {
                    binding.btnOptionSave.isClickable = false
                    callback(0)
                    dismiss()
//                    Log.d("20191627", "update")
//                    todoAddViewModel.updateTodo {
//                        binding.btnOptionSave.isClickable = true
//                        dismiss()
//                        requireActivity().supportFragmentManager.popBackStack()
//                    }
                }

                binding.btnOptionUpdateCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
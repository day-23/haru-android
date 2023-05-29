package com.example.haru.view.checklist

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
import com.example.haru.view.checklist.ChecklistItemFragment.UpdateType
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel, type: UpdateType) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio: Int = 30
    private var type: UpdateType
    private var todoAddViewModel: TodoAddViewModel


    init {
        this.type = type

        ratio = when (type) {
            // 취소 버튼 제외하고 선택지가 1개인 경우
            UpdateType.FRONT_UPDATE_REPEAT, UpdateType.MIDDLE_UPDATE_REPEAT,
            UpdateType.BACK, UpdateType.NOT_REPEAT ->
                27
//            UpdateType.FRONT_ONE, UpdateType.FRONT_TWO,
//            UpdateType.BACK_TWO, UpdateType.NOT_REPEAT, UpdateType.MIDDLE_TWO -> {
//                27
//            }

            // 취소 버튼 제외하고 선택지가 2개인 경우
            UpdateType.FRONT_NOT_UPDATE_REPEAT, UpdateType.MIDDLE_NOT_UPDATE_REPEAT ->
                35
//            UpdateType.FRONT_THREE, UpdateType.MIDDLE_ONE, UpdateType.BACK_ONE -> {
//                35
//            }

            // 취소 버튼 제외하고 선택지가 3개인 경우
//            UpdateType.MIDDLE_THREE, UpdateType.BACK_THREE -> {
//                43
//            }
        }

        this.todoAddViewModel = todoAddViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOptionUpdateBinding.inflate(inflater)

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

        params.weight = when (type) {
            UpdateType.FRONT_UPDATE_REPEAT -> {
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAfterUpdate)
                }
                2f
            }
            UpdateType.FRONT_NOT_UPDATE_REPEAT -> {
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionAfterUpdate)
                }
                3f
            }

            UpdateType.MIDDLE_UPDATE_REPEAT -> {
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAllUpdate)
                }
                2f
            }

            UpdateType.MIDDLE_NOT_UPDATE_REPEAT -> {
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionAllUpdate)
                }
                3f
            }

            UpdateType.BACK -> {
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionSave)
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAllUpdate)
                }
                2f
            }
            UpdateType.NOT_REPEAT -> {
                binding.textViewUpdateInfo.text =
                    getString(R.string.updateDescription).substring(0, 12)
                binding.layoutParentBtnUpdate.apply {
                    removeView(binding.btnOptionOneUpdate)
                    removeView(binding.btnOptionAllUpdate)
                    removeView(binding.btnOptionAfterUpdate)
                }
                2f
            }
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
                    when (type) {
                        UpdateType.FRONT_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateRepeatFrontTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        UpdateType.MIDDLE_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateRepeatMiddleTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
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
                        else -> {
                            binding.btnOptionOneUpdate.isClickable = true
                            Log.d("20191627", "UpdateOptionDialog -> OneUpdate 잘못된 Type")
                        }
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    binding.btnOptionAllUpdate.isClickable = false
                    when (type) {
                        UpdateType.FRONT_UPDATE_REPEAT, UpdateType.FRONT_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateTodo {
                                binding.btnOptionAllUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAllUpdate.isClickable = true
                            Log.d("20191627", "UpdateOptionDialog -> AllUpdate 잘못된 Type")
                        }
                    }
                }
                binding.btnOptionAfterUpdate.id -> {
                    binding.btnOptionAfterUpdate.isClickable = false

                    when (type) {
                        UpdateType.MIDDLE_UPDATE_REPEAT, UpdateType.MIDDLE_NOT_UPDATE_REPEAT,
                        UpdateType.BACK -> {
                            todoAddViewModel.updateRepeatBackTodo {
                                binding.btnOptionAfterUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAfterUpdate.isClickable = true
                            Log.d("20191627", "UpdateOptionDialog -> AfterUpdate 잘못된 Type")
                        }
                    }

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
                    when (type) {
                        UpdateType.NOT_REPEAT -> {
                            todoAddViewModel.updateTodo {
                                binding.btnOptionSave.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionSave.isClickable = true
                            Log.d("20191627", "UpdateOptionDialog -> Save 잘못된 Type")
                        }
                    }
                }

                binding.btnOptionUpdateCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
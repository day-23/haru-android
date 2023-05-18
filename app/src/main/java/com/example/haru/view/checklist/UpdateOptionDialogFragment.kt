package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            UpdateType.FRONT_ONE, UpdateType.FRONT_TWO,
            UpdateType.BACK_TWO, UpdateType.NOT_REPEAT, UpdateType.MIDDLE_TWO -> 23

            // 취소 버튼 제외하고 선택지가 2개인 경우
            UpdateType.FRONT_THREE, UpdateType.MIDDLE_ONE, UpdateType.BACK_ONE -> 30

            // 취소 버튼 제외하고 선택지가 3개인 경우
            UpdateType.MIDDLE_THREE, UpdateType.BACK_THREE -> 38
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

        when (type) {
            UpdateType.FRONT_ONE -> { // 전체 할일 수정 (마감일, 반복 옵션 둘다 수정)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
                binding.btnOptionSave.visibility = View.GONE
            }
            UpdateType.FRONT_TWO -> { // 이 할일만 수정 (마감일 수정, 반복 옵션 수정X)
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
                binding.btnOptionSave.visibility = View.GONE
            }
            UpdateType.FRONT_THREE -> { // 전체 할일 수정, 이 할일만 수정
                binding.btnOptionAfterUpdate.visibility = View.GONE
                binding.btnOptionSave.visibility = View.GONE
            }
            UpdateType.MIDDLE_ONE, UpdateType.BACK_ONE -> { // 전체 할일 수정, 이 할일부터 수정
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionSave.visibility = View.GONE
            }
            UpdateType.MIDDLE_TWO, UpdateType.BACK_TWO -> { // 이 할일만 수정
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
                binding.btnOptionSave.visibility = View.GONE
            }
            UpdateType.MIDDLE_THREE, UpdateType.BACK_THREE -> {} // 모든 옵션을 보여주는 상황
            UpdateType.NOT_REPEAT -> {
                binding.textViewUpdateInfo.text =
                    getString(R.string.updateDescription).substring(0, 12)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
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
                        UpdateType.FRONT_TWO, UpdateType.FRONT_THREE -> { // front
                            todoAddViewModel.updateRepeatFrontTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        UpdateType.MIDDLE_TWO, UpdateType.MIDDLE_THREE -> { // middle
                            todoAddViewModel.updateRepeatMiddleTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        UpdateType.BACK_TWO, UpdateType.BACK_THREE -> { // back
                            todoAddViewModel.updateRepeatBackTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            Log.d("20191627", "UpdateOptionDialog -> OneUpdate 잘못된 Type")
                        }
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    binding.btnOptionAllUpdate.isClickable = false
                    todoAddViewModel.updateTodo {
                        binding.btnOptionAllUpdate.isClickable = true
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionAfterUpdate.id -> {
                    binding.btnOptionAfterUpdate.isClickable = false
                    // middle, back의 이후부터 수정
                    when (type) {
                        UpdateType.MIDDLE_ONE, UpdateType.MIDDLE_THREE,
                        UpdateType.BACK_ONE, UpdateType.BACK_THREE -> {
                            todoAddViewModel.updateRepeatBackTodo {
                                binding.btnOptionAfterUpdate.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {}
                    }

                }

                binding.btnOptionSave.id -> {
                    Log.d("20191627", "update")
                    binding.btnOptionSave.isClickable = false
                    todoAddViewModel.updateTodo {
                        binding.btnOptionSave.isClickable = true
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }

                binding.btnOptionUpdateCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
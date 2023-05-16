package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.R
import com.example.haru.databinding.FragmentOptionUpdateBinding
import com.example.haru.view.checklist.ChecklistItemFragment.Type
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel, type: Type) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio: Int = 30
    private var type: Type
    private var todoAddViewModel: TodoAddViewModel


    init {
        this.type = type
        ratio = when (type) {
            // 취소 버튼 제외하고 선택지가 1개인 경우
            Type.FRONT_ONE, Type.FRONT_TWO, Type.MID_BACK_TWO, Type.NOT_REPEAT -> 23

            // 취소 버튼 제외하고 선택지가 2개인 경우
            Type.FRONT_THREE, Type.MID_BACK_ONE -> 30

            // 취소 버튼 제외하고 선택지가 3개인 경우
            Type.MID_BACK_THREE -> 38
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
            Type.FRONT_ONE -> { // 전체 할일 수정 (마감일, 반복 옵션 둘다 수정)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            Type.FRONT_TWO -> { // 이 할일만 수정 (마감일 수정, 반복 옵션 수정X)
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            Type.FRONT_THREE -> { // 전체 할일 수정, 이 할일만 수정
                binding.btnOptionOneUpdate.visibility = View.GONE
            }
            Type.MID_BACK_ONE -> { // 전체 할일 수정, 이 할일부터 수정
                binding.btnOptionOneUpdate.visibility = View.GONE
            }
            Type.MID_BACK_TWO -> { // 이 할일만 수정
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            Type.MID_BACK_THREE -> {} // 모든 옵션을 보여주는 상황
            Type.NOT_REPEAT -> {
                binding.textViewInfo.text = getString(R.string.updateDescription).substring(0, 12)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
        }

        binding.btnOptionOneUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAfterUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionUpdateCancel.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnOptionOneUpdate.id -> {
                    if (type == Type.FRONT_TWO)  // front에서의 하나만 업데이트
                        todoAddViewModel.updateRepeatFrontTodo {
                            dismiss()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    else {
                        // middle, back에서의 하나만 업데이트
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    todoAddViewModel.updateTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionAfterUpdate.id -> {
                    // middle, back의 이후부터 수정

                }
                binding.btnOptionUpdateCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
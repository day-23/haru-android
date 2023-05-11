package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.databinding.FragmentOptionUpdateBinding
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel, type : Int = 3) : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio : Int = 30
    private var type : Int = 3
    private var todoAddViewModel: TodoAddViewModel


    init{
        this.type = type
        ratio = when(type){
            0, 1, 2 -> 23  // 취소 버튼 제외하고 선택지가 1개인 경우
            3 -> 30 // 취소 버튼 제외하고 선택지가 2개인 경우
            3 -> 38 // 취소 버튼 제외하고 선택지가 3개인 경우
            else -> 30
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
        val dialog : Dialog = super.onCreateDialog(savedInstanceState)

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

        when(type){
            0 -> { // 전체 할일 수정 (마감일, 반복 옵션 둘다 수정)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            1 -> { // 이 할일만 수정 (마감일 수정, 반복 옵션 수정X)
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            2 -> { // 전체 할일 수정 (마감일 수정X, 반복 옵션 수정)
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            3 -> { // default, 전체 할일 수정, 이 할일만 수정 (마감일 수정X, 반복 옵션 수정X)
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
        }

        binding.btnOptionOneUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionAfterUpdate.setOnClickListener(ButtonClickListener())
        binding.btnOptionCancel.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                binding.btnOptionOneUpdate.id -> {
                    // front에서의 하나만 업데이트
                    todoAddViewModel.updateRepeatFrontTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    // middle, back에서의 하나만 업데이트

                }
                binding.btnOptionAllUpdate.id -> {
                    // front에서의 전체 업데이트
                    todoAddViewModel.updateTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                    // middle, back에서의 전체 업데이트
                }
//                binding.btnOptionAfterUpdate.id -> {
//                    middle, back에서의 이 할일부터 업데이트
//
//                }
                binding.btnOptionCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
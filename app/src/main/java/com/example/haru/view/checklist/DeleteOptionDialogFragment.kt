package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.R
import com.example.haru.databinding.FragmentOptionDeleteBinding
import com.example.haru.view.checklist.ChecklistItemFragment.*
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteOptionDialogFragment(todoAddViewModel: TodoAddViewModel, type : DeleteType) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionDeleteBinding
    private var ratio : Int = 30
    private var type : DeleteType
    private var todoAddViewModel: TodoAddViewModel

    init {
        this.type = type
        ratio = when (type) {
            DeleteType.REPEAT_FRONT, DeleteType.REPEAT_MIDDLE, DeleteType.REPEAT_BACK -> 30
            DeleteType.NOT_REPEAT -> 23
        }
        this.todoAddViewModel = todoAddViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOptionDeleteBinding.inflate(inflater)

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

        when(type){
            DeleteType.REPEAT_FRONT, DeleteType.REPEAT_MIDDLE, DeleteType.REPEAT_BACK -> {
                binding.btnOptionDelete.visibility = View.GONE
            }
            DeleteType.NOT_REPEAT -> {
                binding.btnOptionAllDelete.visibility = View.GONE
                binding.btnOptionOneDelete.visibility = View.GONE
                binding.textViewDeleteInfo.text = getString(R.string.deleteDescription).substring(0, 12)
            }
        }

        binding.btnOptionOneDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionDeleteCancel.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnOptionOneDelete.id -> {
                    // 반복 할 일의 front 삭제
                    binding.btnOptionOneDelete.isClickable = false
                    when(type){
                        DeleteType.REPEAT_FRONT -> { // 반복 할 일의 front 삭제
                            todoAddViewModel.deleteRepeatFrontTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        DeleteType.REPEAT_MIDDLE -> { // 반복 할 일의 middle 삭제
                            todoAddViewModel.deleteRepeatMiddleTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        DeleteType.REPEAT_BACK -> { // 반복 할 일의 back 삭제
                            todoAddViewModel.deleteRepeatBackTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionOneDelete.isClickable = true
                        }
                    }
                }

                binding.btnOptionAllDelete.id -> {
                    binding.btnOptionAllDelete.isClickable = false
                    todoAddViewModel.deleteTodo {
                        binding.btnOptionAllDelete.isClickable = true
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionDelete.id -> {
                    binding.btnOptionDelete.isClickable = false
                    todoAddViewModel.deleteTodo {
                        binding.btnOptionDelete.isClickable = true
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }

                binding.btnOptionDeleteCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
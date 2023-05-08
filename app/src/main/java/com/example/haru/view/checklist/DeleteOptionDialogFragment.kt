package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.haru.databinding.FragmentOptionDeleteBinding
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteOptionDialogFragment(todoAddViewModel : TodoAddViewModel) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionDeleteBinding
    private var todoAddViewModel: TodoAddViewModel

    init{
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
        return getWindowHeight() * 30 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOptionOneDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionCancel.setOnClickListener(ButtonClickListener())
    }

    inner class ButtonClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                binding.btnOptionOneDelete.id -> {
                    todoAddViewModel.deleteRepeatTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionAllDelete.id -> {
                    todoAddViewModel.deleteTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                }
                binding.btnOptionCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
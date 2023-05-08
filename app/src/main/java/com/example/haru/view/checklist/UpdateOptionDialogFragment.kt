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

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel, count : Int = 2) : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio : Int = 30
    private var todoAddViewModel: TodoAddViewModel


    init{
        ratio = when(count){
            1 -> 23
            2 -> 30
            3 -> 38
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

        when(ratio){
            23 -> {
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            30 -> {}
            38 -> {}
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
                    todoAddViewModel.deleteRepeatTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    todoAddViewModel.deleteTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }

                }
                binding.btnOptionAfterUpdate.id -> {

                }

                binding.btnOptionCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
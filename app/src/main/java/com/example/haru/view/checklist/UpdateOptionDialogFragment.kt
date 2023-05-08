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

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel, type : Int = 2) : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio : Int = 30
    private var type : Int = 2
    private var todoAddViewModel: TodoAddViewModel


    init{
        this.type = type
        ratio = when(type){
            0 -> 23  // 이 후 이벤트 수정 만 표시  -> 반복 정보, 마감일 둘 다 수정 시
            1 -> 23 // 이 이벤트 수정 만 표시   -> 반복 정보는 유지하고, 마감일만 수정시
            2 -> 30  // 이후 이벤트 수정과 전체 이벤트 수정 -> 마감일은 유지하고, 반복 정보만 수정 시
            3 -> 38 // 이 후 이벤트 수정과 전체 이벤트 수정, 이 이벤트 수정 표시 -> 마감일과 반복 정보 유지하고, 다른 정보 수정 시
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
            0 -> {
                binding.btnOptionOneUpdate.visibility = View.GONE
                binding.btnOptionAllUpdate.visibility = View.GONE
            }
            1 -> {
                binding.btnOptionAllUpdate.visibility = View.GONE
                binding.btnOptionAfterUpdate.visibility = View.GONE
            }
            2 -> {
                binding.btnOptionOneUpdate.visibility = View.GONE
            }
            3 -> {}
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
                    todoAddViewModel.updateRepeatTodo {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    todoAddViewModel.updateTodo {
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
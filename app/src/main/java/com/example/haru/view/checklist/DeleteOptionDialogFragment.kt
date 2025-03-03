package com.example.haru.view.checklist

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import com.example.haru.R
import com.example.haru.databinding.FragmentOptionDeleteBinding
import com.example.haru.utils.Alarm
import com.example.haru.view.checklist.ChecklistItemFragment.*
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteOptionDialogFragment(todoAddViewModel: TodoAddViewModel,
                                 type: DeleteType,
                                 val lifecycle: LifecycleOwner) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionDeleteBinding
    private var ratio: Int = 30
    private var type: DeleteType
    private var todoAddViewModel: TodoAddViewModel
    var dismissEvent : DismissEvent? = null

    interface DismissEvent {
        fun onDismiss()
    }
    init {
        this.type = type
        ratio = when (type) {
            DeleteType.REPEAT_FRONT, DeleteType.REPEAT_BACK -> 35
            DeleteType.REPEAT_MIDDLE -> 43
            DeleteType.NOT_REPEAT -> 27
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

        val params: LinearLayout.LayoutParams =
            binding.layoutParentBtnDelete.layoutParams as LinearLayout.LayoutParams

        params.weight = when (type) {
            DeleteType.REPEAT_FRONT, DeleteType.REPEAT_BACK -> {
                binding.layoutParentBtnDelete.apply {
                    removeView(binding.btnOptionDelete)
                    removeView(binding.btnOptionAfterDelete)
                }
                3f
            }

            DeleteType.REPEAT_MIDDLE -> {
                binding.layoutParentBtnDelete.removeView(binding.btnOptionDelete)
                4f
            }

            DeleteType.NOT_REPEAT -> {
                binding.layoutParentBtnDelete.apply {
                    removeView(binding.btnOptionAllDelete)
                    removeView(binding.btnOptionOneDelete)
                    removeView(binding.btnOptionAfterDelete)
                }
                binding.textViewDeleteInfo.text =
                    getString(R.string.deleteDescription).substring(0, 12)
                2f
            }
        }
        binding.layoutParentBtnDelete.apply {
            layoutParams = params
            (getChildAt(childCount - 1) as AppCompatButton).setBackgroundResource(R.drawable.option_last_view_bg)
        }

        binding.btnOptionOneDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionAllDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionAfterDelete.setOnClickListener(ButtonClickListener())
        binding.btnOptionDeleteCancel.setOnClickListener(ButtonClickListener())
        binding.btnOptionDelete.setOnClickListener(ButtonClickListener())
    }

    override fun dismiss() {
        super.dismiss()
        dismissEvent?.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dismissEvent?.onDismiss()
    }
    inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.btnOptionOneDelete.id -> {
                    // 반복 할 일의 front 삭제
                    binding.btnOptionOneDelete.isClickable = false
                    when (type) {
                        DeleteType.REPEAT_FRONT -> { // 반복 할 일의 front 삭제
                            todoAddViewModel.deleteRepeatFrontTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        DeleteType.REPEAT_MIDDLE -> { // 반복 할 일의 middle 삭제
                            todoAddViewModel.deleteRepeatMiddleTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        DeleteType.REPEAT_BACK -> { // 반복 할 일의 back 삭제
                            todoAddViewModel.deleteRepeatBackTodo {
                                binding.btnOptionOneDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionOneDelete.isClickable = true
                            Log.e("20191627", "DeleteOption -> OneDelete 잘못된 Type")
                        }
                    }
                }

                binding.btnOptionAllDelete.id -> {
                    binding.btnOptionAllDelete.isClickable = false
                    when (type) {
                        DeleteType.REPEAT_FRONT, DeleteType.REPEAT_MIDDLE,
                        DeleteType.REPEAT_BACK -> {
                            todoAddViewModel.deleteTodo {
                                binding.btnOptionAllDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAllDelete.isClickable = true
                            Log.e("20191627", "DeleteOption -> AllDelete 잘못된 Type")
                        }
                    }
//                    todoAddViewModel.deleteTodo {
//                        binding.btnOptionAllDelete.isClickable = true
//                        dismiss()
//                        requireActivity().supportFragmentManager.popBackStack()
//                    }
                }

                binding.btnOptionAfterDelete.id -> {
                    binding.btnOptionAfterDelete.isClickable = false
                    when (type) {
                        DeleteType.REPEAT_MIDDLE -> {
                            todoAddViewModel.deleteRepeatBackTodo {
                                binding.btnOptionAfterDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAfterDelete.isClickable = true
                            Log.e("20191627", "DeleteOption -> AfterDelete 잘못된 Type")
                        }
                    }
                }

                binding.btnOptionDelete.id -> {
                    binding.btnOptionDelete.isClickable = false
                    when (type) {
                        DeleteType.NOT_REPEAT -> {
                            todoAddViewModel.deleteTodo {
                                binding.btnOptionDelete.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionDelete.isClickable = true
                            Log.e("20191627", "DeleteOption -> Delete 잘못된 Type")
                        }
                    }
                }

                binding.btnOptionDeleteCancel.id -> {
                    dismiss()
                }
            }
        }
    }
}
package com.example.haru.view.checklist

import android.app.Dialog
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
import com.example.haru.databinding.FragmentOptionUpdateBinding
import com.example.haru.utils.Alarm
import com.example.haru.utils.Tags
import com.example.haru.view.checklist.ChecklistItemFragment.UpdateType
import com.example.haru.viewmodel.TodoAddViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateOptionDialogFragment(todoAddViewModel: TodoAddViewModel,
                                 type: UpdateType,
                                 val lifecycle: LifecycleOwner) :
    BottomSheetDialogFragment() {
    private lateinit var binding: FragmentOptionUpdateBinding
    private var ratio: Int = 30
    private var type: UpdateType
    private var todoAddViewModel: TodoAddViewModel
    var dismissEvent : DismissEvent? = null
    interface DismissEvent {
        fun onDismiss()
    }


    init {
        this.type = type

        ratio = when (type) {
            // 취소 버튼 제외하고 선택지가 1개인 경우
            UpdateType.FRONT_UPDATE_REPEAT, UpdateType.MIDDLE_UPDATE_REPEAT,
            UpdateType.BACK, UpdateType.NOT_REPEAT ->
                27
            // 취소 버튼 제외하고 선택지가 2개인 경우
            UpdateType.FRONT_NOT_UPDATE_REPEAT, UpdateType.MIDDLE_NOT_UPDATE_REPEAT ->
                35
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
                binding.btnOptionOneUpdate.id -> {
                    binding.btnOptionOneUpdate.isClickable = false
                    when (type) {
                        UpdateType.FRONT_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateRepeatFrontTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        UpdateType.MIDDLE_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateRepeatMiddleTodo {
                                binding.btnOptionOneUpdate.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionOneUpdate.isClickable = true
                            Log.d(Tags.log, "UpdateOptionDialog -> OneUpdate 잘못된 Type")
                        }
                    }
                }
                binding.btnOptionAllUpdate.id -> {
                    binding.btnOptionAllUpdate.isClickable = false
                    when (type) {
                        UpdateType.FRONT_UPDATE_REPEAT, UpdateType.FRONT_NOT_UPDATE_REPEAT -> {
                            todoAddViewModel.updateTodo {
                                binding.btnOptionAllUpdate.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAllUpdate.isClickable = true
                            Log.d(Tags.log, "UpdateOptionDialog -> AllUpdate 잘못된 Type")
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
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionAfterUpdate.isClickable = true
                            Log.d(Tags.log, "UpdateOptionDialog -> AfterUpdate 잘못된 Type")
                        }
                    }
                }

                binding.btnOptionSave.id -> {
                    binding.btnOptionSave.isClickable = false
                    when (type) {
                        UpdateType.NOT_REPEAT -> {
                            todoAddViewModel.updateTodo {
                                binding.btnOptionSave.isClickable = true
                                Alarm.initAlarm(lifecycle, requireContext())
                                dismiss()
                                requireActivity().supportFragmentManager.popBackStack()
                            }
                        }
                        else -> {
                            binding.btnOptionSave.isClickable = true
                            Log.d(Tags.log, "UpdateOptionDialog -> Save 잘못된 Type")
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
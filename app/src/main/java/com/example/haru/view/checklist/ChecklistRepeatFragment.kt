package com.example.haru.view.checklist


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Repeat
import com.example.haru.databinding.FragmentChecklistRepeatBinding
import com.example.haru.viewmodel.RecyclerViewModel
import com.example.haru.viewmodel.TodoAddViewModel
import com.example.harudemo.utils.CustomToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChecklistRepeatFragment(val todoAddViewModel: TodoAddViewModel) : BottomSheetDialogFragment(){
    private lateinit var binding: FragmentChecklistRepeatBinding


    companion object {
        const val TAG: String = "로그"
        fun newInstance(todoAddViewModel: TodoAddViewModel): ChecklistRepeatFragment {
            return ChecklistRepeatFragment(todoAddViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistRepeatFragment - onCreate() called")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChecklistRepeatBinding.inflate(inflater)
        binding.viewModel = todoAddViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAddViewModel.repeatOption.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.ivDayCheck.visibility = if (it == 0) View.VISIBLE else View.INVISIBLE
            binding.ivWeekCheck.visibility= if (it == 1) View.VISIBLE else View.INVISIBLE
            binding.iv2weekCheck.visibility = if (it == 2) View.VISIBLE else View.INVISIBLE
            binding.ivMonthCheck.visibility= if (it == 3) View.VISIBLE else View.INVISIBLE
            binding.ivYearCheck.visibility = if (it == 4) View.VISIBLE else View.INVISIBLE
        })

        todoAddViewModel.repeatDay.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            for(i in 0 until binding.layoutRepeatSelectDay.childCount)
                if (it.get(i))
                    (binding.layoutRepeatSelectDay.getChildAt(i) as TextView).setTextColor(Color.BLUE)
                else (binding.layoutRepeatSelectDay.getChildAt(i) as TextView).setTextColor(Color.BLACK)
        })

        binding.btnCloseRepeat.setOnClickListener(ClickListener())
        binding.btnAddRepeat.setOnClickListener(ClickListener())

    }


    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btn_add_repeat -> {
                    if (todoAddViewModel.repeatOption.value == null)
                        CustomToast.makeText(
                            context,
                            "반복 옵션을 선택하지 않았습니다.",
                            Toast.LENGTH_SHORT
                        )
                    else if (todoAddViewModel.repeatOption.value == 1) {
                        todoAddViewModel.setRepeatOptionWithDay()
                        dismiss()
                    } else {
                        todoAddViewModel.setRepeatOptionWithoutDay()
                        dismiss()
                    }
                }
                R.id.btn_close_repeat -> {
                    todoAddViewModel.setRepeatClear()
                    dismiss()
                }
            }
        }
    }
}
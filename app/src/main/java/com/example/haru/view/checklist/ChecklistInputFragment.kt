package com.example.haru.view.checklist

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.haru.R
import com.example.haru.databinding.FragmentChecklistInputBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChecklistInputFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChecklistInputBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistInputFragment {
            return ChecklistInputFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistInputFragment - onCreate() called")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

//        return inflater.inflate(R.layout.fragment_checklist_input,container,false)
        binding = FragmentChecklistInputBinding.inflate(inflater)
        return binding.root
    }

//    override fun setupDialog(dialog: Dialog, style: Int) {
//        super.setupDialog(dialog, style)
//        val
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val btnclose = view.findViewById<Button>(R.id.btn_close)
//
//        btnclose.setOnClickListener{
//            dismiss()
//        }
        binding.btnClose.setOnClickListener{
            dismiss()
        }
    }

}
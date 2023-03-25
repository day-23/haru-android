package com.example.haru.view.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentChecklistItemInfoBinding
import com.example.haru.view.MainActivity
import com.example.haru.viewmodel.CheckListViewModel
import kotlin.properties.Delegates

class ChecklistItemFragment(checkListViewModel: CheckListViewModel, position : Int) : Fragment() {
    private lateinit var binding: FragmentChecklistItemInfoBinding
    private var checkListViewModel : CheckListViewModel
    private var position: Int

    init {
        this.checkListViewModel = checkListViewModel
        this.position = position
    }
    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListViewModel: CheckListViewModel, position: Int): ChecklistItemFragment {
            return ChecklistItemFragment(checkListViewModel, position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistFragment - onCreate() called")

        MainActivity.hideNavi(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.hideNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "ChecklistItemFragment - onCreateView() called")

        binding = FragmentChecklistItemInfoBinding.inflate(inflater)

        binding.todoItem = checkListViewModel.todoDataList.value!![position]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}
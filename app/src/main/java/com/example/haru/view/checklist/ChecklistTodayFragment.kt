package com.example.haru.view.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Completed
import com.example.haru.data.model.Flag
import com.example.haru.data.model.Folded
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistTodayBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.CheckListViewModel
import java.util.*

class ChecklistTodayFragment(checkListVewModel: CheckListViewModel) : Fragment() {
    private lateinit var binding : FragmentChecklistTodayBinding
    private var checkListViewModel : CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListVewModel: CheckListViewModel): ChecklistTodayFragment {
            return ChecklistTodayFragment(checkListVewModel)
        }
    }

    init{
        this.checkListViewModel = checkListVewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChecklistTodayBinding.inflate(inflater)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        checkListViewModel.clearToday()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToday()

        binding.tvTodayDate.text = FormatDate.simpleTodayToStr(Date())

        binding.ivTodayBackIcon.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initToday(){
        val todayRecyclerView : RecyclerView = binding.todayRecyclerView
        val todoAdapter = TodoAdapter(requireContext())

        todoAdapter.todoClick = object : TodoAdapter.TodoClick {
            override fun onClick(view: View, id: String) {
                if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.type == 2){
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, ChecklistItemFragment(checkListViewModel, id))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        todoAdapter.flagClick = object : TodoAdapter.FlagClick {
            override fun onClick(view: View, id: String) {
                val flag = if (checkListViewModel.todayTodo.value!!.find { it.id == id }!!.flag) Flag(false)
                else Flag(true)
                checkListViewModel.updateFlag(
                    flag,
                    id
                )
            }
        }

        todayRecyclerView.adapter = todoAdapter
        todayRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        checkListViewModel.todayTodo.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val todayTodo = it.filterIsInstance<Todo>()
            todoAdapter.setDataList(todayTodo)
        })
    }
}
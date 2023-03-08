package com.example.haru.view.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistBinding
import com.example.haru.view.adapter.TagAdapter
import com.example.haru.viewmodel.RecyclerViewModel
import java.util.*

class ChecklistFragment : Fragment(), LifecycleObserver {
    private lateinit var binding: FragmentChecklistBinding
    private lateinit var recyclerViewModel: RecyclerViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ChecklistFragment {
            return ChecklistFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ChecklistFragment - onCreate() called")

        recyclerViewModel = RecyclerViewModel(RecyclerViewModel.requestTagList)

        binding.btnAddTodo.setOnClickListener{

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ChecklistFragment - onCreateView() called")

        binding = FragmentChecklistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTagList()
//        tagAdapter = TagAdapter(datas)
//        binding.recyclerTags.layoutManager =
//            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//        binding.recyclerTags.adapter = tagAdapter
    }

    private fun initTagList() {
        val tagListView: RecyclerView = binding.recyclerTags
        val tagAdapter = TagAdapter(requireContext())

        tagListView.adapter = tagAdapter
        tagListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        recyclerViewModel.dataList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val dataList = it.filterIsInstance<Tag>()
            tagAdapter.setDataList(dataList)
        })
    }

}
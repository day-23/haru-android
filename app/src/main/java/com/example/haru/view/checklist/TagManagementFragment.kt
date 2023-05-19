package com.example.haru.view.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentTagManagementBinding
import com.example.haru.viewmodel.CheckListViewModel

class TagManagementFragment(checkListViewModel: CheckListViewModel) : Fragment() {
    private lateinit var binding : FragmentTagManagementBinding
    private val checkListViewModel : CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListViewModel: CheckListViewModel): TagManagementFragment {
            return TagManagementFragment(checkListViewModel)
        }
    }

    init {
        this.checkListViewModel = checkListViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTagManagementBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.etTagContent.setText()

        binding.ivBackIconTag.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ivSubmitIconTag.setOnClickListener {

        }


    }
}
package com.example.haru.view.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentTagManagementBinding
import com.example.haru.viewmodel.CheckListViewModel

class TagManagementFragment(checkListViewModel: CheckListViewModel, val tag: Tag, val cnt : Int?) : Fragment() {
    private lateinit var binding: FragmentTagManagementBinding
    private val checkListViewModel: CheckListViewModel

    companion object {
        const val TAG: String = "로그"

        fun newInstance(checkListViewModel: CheckListViewModel, tag: Tag, cnt: Int?): TagManagementFragment {
            return TagManagementFragment(checkListViewModel, tag, cnt)
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

        binding.etTagContent.setText(tag.content)

        binding.tvRelatedTodoCount.text = if (cnt == null) "0개" else "${cnt}개"

        binding.switchTagVisibility.isChecked = tag.isSelected

        binding.ivBackIconTag.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.ivSubmitIconTag.setOnClickListener {
            val content = checkListViewModel.readyCreateTag(binding.etTagContent.text.toString())
            val flag = binding.switchTagVisibility.isChecked
            if (content == null)
                Toast.makeText(requireContext(), "태그에 공백이 포함될 수 없습니다.", Toast.LENGTH_SHORT).show()
            else{
                checkListViewModel.updateTag(
                    tag.id,
                    TagUpdate(content = content, isSelected = flag)
                ) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

        binding.layoutTagDelete.setOnClickListener {
            checkListViewModel.deleteTagList(TagIdList(listOf(tag.id))) {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


    }
}
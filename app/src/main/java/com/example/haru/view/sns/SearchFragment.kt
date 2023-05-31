package com.example.haru.view.sns

import BaseActivity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.databinding.FragmentSearchBinding
import com.example.haru.viewmodel.CheckListViewModel

class SearchFragment(val viewModel : Any): Fragment() {
    lateinit var binding : FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SearchFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SearchFragment - onCreateView() called")

        binding = FragmentSearchBinding.inflate(inflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).adjustTopMargin(binding.searchHeader.id)

        // checklist와 캘린더에서 접근한 검색 화면일 경우
        if (viewModel is CheckListViewModel){
            val searchView : RecyclerView = binding.searchRecycler
//            val searchAdapter =
        } else{
            TODO()
        }


        // 검색어 입력시에만 bold처리
        binding.etSearchContent.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                if (str == "") {
                    binding.etSearchContent.setTypeface(null, Typeface.NORMAL)
                    return
                }

                binding.etSearchContent.setTypeface(null, Typeface.BOLD)
            }

        })

        binding.ivSearchCancel.setOnClickListener(ClickListener())


    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.searchHeader.id)
    }

    inner class ClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                binding.ivSearchCancel.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

            }
        }
    }

}
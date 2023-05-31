package com.example.haru.view.sns

import BaseActivity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haru.R
import com.example.haru.databinding.FragmentWriteAddTagBinding
import com.example.haru.view.adapter.TemplateAdapter
import com.example.haru.viewmodel.MyPageViewModel

class WriteHaruTagFragment : Fragment() {
    lateinit var binding : FragmentWriteAddTagBinding
    lateinit var templateViewModel : MyPageViewModel
    lateinit var templateAdapter: TemplateAdapter

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.writeTagHeaderTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.writeTagHeaderTitle.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        templateViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteAddTagBinding.inflate(inflater, container, false)
        val addPostRecycler = binding.writeHaryTemplates
        templateAdapter = TemplateAdapter(requireContext(), arrayListOf())
        addPostRecycler.adapter = templateAdapter
        addPostRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        templateViewModel.getTemplates()

        templateViewModel.Templates.observe(viewLifecycleOwner){ templates ->
            Log.d("20191668", "$templates")
            templateAdapter.addTemplate(templates)
       }

        binding.addtagCancel.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        templateViewModel.PostTagLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.postIvTagIcon.backgroundTintList = if (it.isEmpty())
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.icon_gray))
            else
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.todo_description
                    )
                )

            if (it.size < binding.postTagContainerLayout.childCount - 2)
                for (i in 1 until binding.postTagContainerLayout.childCount - 1) { // chip을 검사해서 리스트에 없으면 삭제
                    val chip = binding.postTagContainerLayout.getChildAt(i) as LinearLayout
                    if (!it.contains((chip.getChildAt(0) as AppCompatButton).text)) {
                        binding.postTagContainerLayout.removeViewAt(i)
                        break
                    }
                }
            else if (it.size > binding.postTagContainerLayout.childCount - 2) {
                val layoutInflater =
                    context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val childCount = binding.postTagContainerLayout.childCount
                for (i in childCount - 2 until it.size) {
                    val chip = layoutInflater.inflate(R.layout.custom_chip, null)
                    chip.findViewById<AppCompatButton>(R.id.tag_chip).apply {
                        text = it[i]
                        setOnClickListener {
                            templateViewModel.subTagList(this.text.toString())
                        }
                    }

                    binding.postTagContainerLayout.addView(
                        chip,
                        binding.postTagContainerLayout.childCount - 1
                    )
                }
            }
        })

        binding.postTagEt.addTextChangedListener(object : TextWatcher { // 소프트웨어 키보드의 스페이스바 감지
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null)
                    return

                val str = s.toString()
                if (str == "")
                    return

                if (str[str.length - 1] == ' ') {
                    templateViewModel.addTagList(str)
                    binding.postTagEt.setText("")
                }
            }

        })

        return binding.root
    }

}
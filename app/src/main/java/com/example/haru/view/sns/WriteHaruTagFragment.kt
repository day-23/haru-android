package com.example.haru.view.sns

import BaseActivity
import android.animation.ValueAnimator
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
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.databinding.FragmentWriteAddTagBinding
import com.example.haru.view.adapter.TemplateAdapter
import com.example.haru.viewmodel.MyPageViewModel

interface onTemplateListener{

    fun onTemplateClicked(url : String)
}

class WriteHaruTagFragment(val content:String) : Fragment(), onTemplateListener{
    lateinit var binding : FragmentWriteAddTagBinding
    lateinit var templateViewModel : MyPageViewModel
    lateinit var templateAdapter: TemplateAdapter
    var toggle = true

    override fun onTemplateClicked(url: String) {
        Glide.with(requireContext())
            .load(url)
            .into(binding.writeHaruImages)
    }
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
        val addPostRecycler = binding.writeHaruTemplates
        templateAdapter = TemplateAdapter(requireContext(), arrayListOf(),this)
        addPostRecycler.adapter = templateAdapter
        addPostRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        templateViewModel.getTemplates()
        binding.writeTagContent.text = content
        templateViewModel.Templates.observe(viewLifecycleOwner){ templates ->
            Log.d("20191668", "$templates")
            templateAdapter.addTemplate(templates)
       }

        binding.writeHaruCancel.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.popBackStack()
        }

        binding.templateToggle.setOnClickListener {
            toggleClicked()
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

    fun toggleClicked(){
        val layoutparam = binding.popupTemplate.layoutParams
        val startHeight = layoutparam.height
        //dp into px
        val density = resources.displayMetrics.density
        val pixels = (279 * density).toInt()

        if (!toggle) {
            layoutparam.height = pixels
            toggle = true
            binding.templateToggle.rotation = 270f
            binding.writeHaruTemplates.visibility = View.VISIBLE
            binding.setTextBlack.visibility = View.VISIBLE
            binding.setTextWhite.visibility = View.VISIBLE
        } else {
            layoutparam.height = 0
            toggle = false
            binding.templateToggle.rotation = 90f
            binding.writeHaruTemplates.visibility = View.GONE
            binding.setTextBlack.visibility = View.GONE
            binding.setTextWhite.visibility = View.GONE
        }
        val targetHeight = layoutparam.height

        val animator = ValueAnimator.ofInt(startHeight, targetHeight)
        val duration = 200

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            layoutparam.height = animatedValue
            binding.popupTemplate.layoutParams = layoutparam
        }

        animator.duration = duration.toLong()
        animator.start()
    }

}
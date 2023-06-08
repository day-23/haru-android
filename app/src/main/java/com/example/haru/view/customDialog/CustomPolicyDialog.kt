package com.example.haru.view.customDialog

import BaseActivity
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.CustomPolicyDialogBinding

class CustomPolicyDialog(val type: Boolean) : Fragment() {
    private lateinit var binding: CustomPolicyDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CustomPolicyDialogBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
        //type가 true면 이용약관, false면 개인정보 정책
        val text =
            if (type) requireContext().getText(R.string.termsOfService) else requireContext().getText(
                R.string.privacy_policy
            )
        val span = SpannableStringBuilder(text)
        var start = text.indexOf("##")
        var end: Int

        while (start != -1) {
            end = span.indexOf("\n", startIndex = start)
            span.apply {
                setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.todo_description
                        )
                    ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(RelativeSizeSpan(2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                replace(start, start + 2, "")
            }
            start = span.indexOf("##", startIndex = end)

        }
        binding.tvContent.text = span

        binding.tvTitle.text = if(type) "이용 약관" else "개인정보 정책"

        binding.ivBackIconProtect.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

}
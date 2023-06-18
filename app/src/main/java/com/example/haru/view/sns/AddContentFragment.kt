package com.example.haru.view.sns

import BaseActivity
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.FragmentAddContentBinding
import com.example.haru.view.MainActivity
import com.example.haru.viewmodel.MyPageViewModel
import okhttp3.MultipartBody

class AddContentFragment(
    val images: MutableList<MultipartBody.Part>,
    val select: ArrayList<ExternalImages>,
    val myPageViewModel: MyPageViewModel
) : Fragment() {
    lateinit var binding: FragmentAddContentBinding

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.addContentHeader.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.addContentHeader.id)
        MainActivity.hideNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddContentBinding.inflate(inflater, container, false)

        binding.addContentCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.addContentApply.setOnClickListener {
            val content = binding.addContentText.text.toString()
            val newFrag = AddTagFragment(images, content, select, myPageViewModel)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.addContentText.addTextChangedListener(object :
            TextWatcher { // 소프트웨어 키보드의 스페이스바 감지
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    binding.addContentCancel.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.cancel_icon)
                    binding.addContentCancel.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.date_text
                        )
                    )
                    return
                }

                val str = s.toString()
                if (str == "") {
                    binding.addContentCancel.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.cancel_icon)
                    binding.addContentCancel.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.date_text
                        )
                    )
                    return
                }

                binding.addContentCancel.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.back_arrow)
                binding.addContentCancel.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.highlight
                    )
                )
            }

        })

        return binding.root
    }
}
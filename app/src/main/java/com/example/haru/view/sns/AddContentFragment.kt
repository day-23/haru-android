package com.example.haru.view.sns

import BaseActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.FragmentAddContentBinding
import com.example.haru.databinding.FragmentAddpostAddtagBinding
import okhttp3.MultipartBody

class AddContentFragment(
    val images: MutableList<MultipartBody.Part>,
    val select: ArrayList<ExternalImages>
) : Fragment() {
    lateinit var binding: FragmentAddContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.addContentHeader.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.addContentHeader.id)
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
            val newFrag = AddTagFragment(images, content, select)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return binding.root
    }
}
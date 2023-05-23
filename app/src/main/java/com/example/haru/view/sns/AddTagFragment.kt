package com.example.haru.view.sns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.FragmentAddpostAddtagBinding
import com.example.haru.view.adapter.AddTagPagerAdapter
import com.example.haru.viewmodel.MyPageViewModel
import okhttp3.MultipartBody

class AddTagFragment(images: MutableList<MultipartBody.Part>, content: String, select: ArrayList<ExternalImages>) : Fragment(){
    lateinit var binding : FragmentAddpostAddtagBinding
    val images = images
    val content = content
    val Uris = select
    lateinit var galleryViewmodel : MyPageViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            galleryViewmodel = ViewModelProvider(this).get(MyPageViewModel::class.java)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val fragmentManager = parentFragmentManager
            binding = FragmentAddpostAddtagBinding.inflate(inflater, container, false)
            val adapter = AddTagPagerAdapter(requireContext(), Uris)
            binding.addtagImages.adapter = adapter
            binding.addTagContent.text = content
            val hashtag = arrayListOf("해시스완") //TODO:하드코딩 동적으로 바꾸어야함

            binding.addtagCancel.setOnClickListener {
                fragmentManager.popBackStack()
            }

            binding.addpostApply.setOnClickListener {
                Toast.makeText(requireContext(), "게시글 작성중...", Toast.LENGTH_SHORT).show()
                galleryViewmodel.postRequest(images, content, hashtag)

                galleryViewmodel.PostDone.observe(viewLifecycleOwner) { done ->
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val fragment = SnsFragment()
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragments_frame, fragment)
                    transaction.commit()
                }
            }

            return binding.root
        }
}

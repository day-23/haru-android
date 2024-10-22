package com.example.haru.view.sns

import BaseActivity
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.FragmentAddpostAddtagBinding
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.AddTagPagerAdapter
import com.example.haru.view.adapter.TodoAdapter
import com.example.haru.viewmodel.MyPageViewModel
import kakao.k.p
import okhttp3.MultipartBody

class AddTagFragment(
    images: MutableList<MultipartBody.Part>,
    content: String,
    select: ArrayList<ExternalImages>,
    val galleryViewmodel: MyPageViewModel
) : Fragment() {
    lateinit var binding: FragmentAddpostAddtagBinding
    val images = images
    val content = content
    val Uris = select


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.addTagHeaderTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.addTagHeaderTitle.id)

        MainActivity.hideNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentManager = parentFragmentManager
        binding = FragmentAddpostAddtagBinding.inflate(inflater, container, false)
        val adapter = AddTagPagerAdapter(requireContext(), Uris, galleryViewmodel)
        binding.addtagImages.adapter = adapter
        binding.addTagContent.text = content
        var hashtag: List<String>

        binding.addtagCancel.setOnClickListener {
            fragmentManager.popBackStack()
        }
        val loading = LoadingAnimation()

        binding.addpostApply.setOnClickListener {
            binding.addpostApply.isClickable = false
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.fragments_frame, loading)
                .addToBackStack(null)
                .commit()
            hashtag = galleryViewmodel.getTagList()
            galleryViewmodel.postRequest(images, content, hashtag)
            galleryViewmodel.resetValue()
        }

        galleryViewmodel.PostDone.observe(viewLifecycleOwner) { done ->
            Log.d("SNS", "response code $done")
            if(done == 201) {
                loading.dismiss{
                    galleryViewmodel.resetValue()
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 0) {
                        fragmentManager.popBackStack("snsmain", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }
            }else if(done == 429){
                Log.d("SNS", "$done, 글 도배")
                galleryViewmodel.resetCode()
                loading.dismiss {
                    Toast.makeText(requireContext(),"글을 너무 자주 작성할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    binding.addpostApply.visibility = View.GONE
                }
            }else if(done == 403){
                galleryViewmodel.resetCode()
                Log.d("SNS", "$done, 욕설")
                loading.dismiss {
                    Toast.makeText(requireContext(), "부적절한 단어는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    binding.addpostApply.visibility = View.GONE
                }
            }else if (done != 0){
                galleryViewmodel.resetCode()
                Log.d("SNS", "Error Here?")
                loading.dismiss {
                    Toast.makeText(requireContext(), "전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.addTagPictureIndex.text = "1/${Uris.size}"
        binding.addtagImages.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                binding.addTagPictureIndex.text = "${position + 1}/${Uris.size}"
            }
        })

        galleryViewmodel.PostTagLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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
                            galleryViewmodel.subTagList(this.text.toString())
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
                    galleryViewmodel.addTagList(str)
                    binding.postTagEt.setText("")
                }
            }

        })

        return binding.root
    }
}


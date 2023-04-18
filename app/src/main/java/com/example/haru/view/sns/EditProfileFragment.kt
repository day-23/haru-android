package com.example.haru.view.sns

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentEditProfileBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.haru.viewmodel.MyPageViewModel

class EditProfileFragment: Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var imageUri: Uri
    private lateinit var profileViewModel: MyPageViewModel

    // 이미지 선택 결과 MultipartBody.part로 바꾸기
    private var getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                imageUri = it
                val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
                val file = File(requireContext().cacheDir, "image.jpg")
                inputStream?.let { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                val requestFile: RequestBody = RequestBody.create(MediaType.parse(mimeType), file)
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)
                // body 객체를 사용하여 서버에 업로드
                profileViewModel.updateProfile(body)
            }
        }
    }
    companion object{
        const val TAG : String = "로그"

        fun newInstance() : EditProfileFragment {
            return EditProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SnsMypageFragment - onCreate() called")
        profileViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SnsFragment - onCreateView() called")

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            val newFrag = MyPageFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        // 이미지 선택 버튼에 클릭 이벤트 리스너 등록
        binding.editProfileImage.setOnClickListener {
            // 내부 저장소에서 이미지 선택하는 창 열기
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            getContent.launch(intent)
        }

        //프로필 사진 반영
        profileViewModel.Profile.observe(viewLifecycleOwner){profile ->
            Log.d("TAG", "profile update ${profile.url}")
            if(profile.url != "") {
                Log.d("TAG", "${profile.url}")
                Glide.with(this)
                    .load(profile.url)
                    .into(binding.editProfileImage)
            }
        }

        return binding.root
    }
}

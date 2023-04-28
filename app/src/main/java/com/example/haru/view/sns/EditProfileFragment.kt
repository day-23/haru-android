package com.example.haru.view.sns

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.Profile
import com.example.haru.databinding.CustomGalleryBinding
import com.example.haru.databinding.FragmentEditProfileBinding
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.view.adapter.GalleryAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.jar.Manifest

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
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/*"
//            getContent.launch(intent)

            // TODO: 버전별로 나누어 처리해야함
            // TODO: 권한을 허락받자마자 코드가 계속 실행되면 좋을거 같음
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없는 경우 권한 요청
                Log.d("Image", "denied")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                    101
                )
            } else {
                // 권한이 있는 경우
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.RELATIVE_PATH)

                val path = Environment.getExternalStorageDirectory().absolutePath
                val mimeType = "image/*"
                MediaScannerConnection.scanFile(context, arrayOf(path), arrayOf(mimeType), null)


                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
                val gallery = ArrayList<ExternalImages>()
                val cursor = requireContext().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder)
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                        val absuri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        Log.d("Image","${id}, ${name}, ${path} ${absuri}")
                        gallery.add(ExternalImages(id,name,path,absuri))
                    } while (cursor.moveToNext())
                }
                cursor?.close()
                Log.d("Image", "${gallery}")
                profileViewModel.loadGallery(gallery)
                binding.popupGallery.visibility = View.VISIBLE
                val fragment = GalleryFragment()
                val fragmentManager = childFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.add(R.id.popup_gallery, fragment)
                transaction.commit()
            }
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

class GalleryFragment : Fragment() {
    private lateinit var profileViewModel: MyPageViewModel
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var binding: CustomGalleryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileViewModel = ViewModelProvider(requireParentFragment()).get(MyPageViewModel::class.java)
        binding = CustomGalleryBinding.inflate(inflater, container, false)
        var galleryImages = profileViewModel.getGallery()
        val recycler = binding.customGalleryImage
        profileViewModel.StoredImages.observe(viewLifecycleOwner){image ->
            galleryImages = image
            galleryAdapter = GalleryAdapter(requireContext(), galleryImages)
            recycler.adapter = galleryAdapter
            val gridLayoutManager = GridLayoutManager(requireContext(), 3)
            recycler.layoutManager = gridLayoutManager
        }
        return binding.root
    }

    companion object {
        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }
}

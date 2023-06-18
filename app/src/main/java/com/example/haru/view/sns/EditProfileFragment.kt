package com.example.haru.view.sns

import BaseActivity
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Rect
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.CustomGalleryBinding
import com.example.haru.databinding.FragmentEditProfileBinding
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.GalleryAdapter
import com.example.haru.viewmodel.MyPageViewModel
import okhttp3.internal.wait

class EditProfileFragment(userId: String) : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var profileViewModel: MyPageViewModel
    var userId = userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "SnsMypageFragment - onCreate() called")
        profileViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "SnsFragment - onCreateView() called")

        binding = FragmentEditProfileBinding.inflate(inflater)

        return binding.root
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.editProfileRootview.id)

        MainActivity.hideNavi(true)

        binding.backButton.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                // 이전 프래그먼트를 제거하고 맨 위에 있는 프래그먼트로 전환
                fragmentManager.popBackStack()
            }
        }

        userId = User.id
        profileViewModel.getUserInfo(userId)

        profileViewModel.UserInfo.observe(viewLifecycleOwner){profile ->
            if(profile.profileImage != null) {

                Glide.with(this)
                    .load(profile.profileImage)
                    .into(binding.editProfileImage)
            }else{
                binding.editProfileImage.setImageResource(R.drawable.default_profile)
            }
            binding.editName.setText(profile.name)
            binding.editIntroduction.setText(profile.introduction)
        }

        profileViewModel.EditUri.observe(viewLifecycleOwner) {
            Log.e("20191627", it.toString())

            Glide.with(requireContext())
                .load(it)
                .into(binding.editProfileImage)
        }

        binding.editProfileApply.setOnClickListener {
            binding.editProfileApply.isClickable = false
            val multipart = profileViewModel.EditImage.value
            val name = binding.editName.text.toString()
            val introduction = binding.editIntroduction.text.toString()
            if (multipart != null) {
                profileViewModel.editProfile(multipart, name, introduction) {
                    val fragmentManager = parentFragmentManager
                    if (fragmentManager.backStackEntryCount > 0) {
                        // 이전 프래그먼트를 제거하고 맨 위에 있는 프래그먼트로 전환
                        fragmentManager.popBackStack()
                    }
                }
            } else {
                profileViewModel.editProfileName(name, introduction) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.backStackEntryCount > 0) {
                        // 이전 프래그먼트를 제거하고 맨 위에 있는 프래그먼트로 전환
                        fragmentManager.popBackStack()
                    }
                }
            }
        }

        // 이미지 선택 버튼에 클릭 이벤트 리스너 등록
        binding.editProfileImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 권한 요청
                Log.d("Image", "denied")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        101
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        101
                    )
                }

            } else {
                Log.d("Image", "accepted")
                val projection: Array<String>
                // 권한이 있는 경우
                if (Build.VERSION.SDK_INT >= 33) {
                    projection = arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.RELATIVE_PATH
                    )
                } else {
                    projection = arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_MODIFIED
                    )
                }

                val path = Environment.getExternalStorageDirectory().absolutePath
                val mimeType = "image/*"
                MediaScannerConnection.scanFile(context, arrayOf(path), arrayOf(mimeType), null)


                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
                val gallery = ArrayList<ExternalImages>()
                val cursor = requireContext().contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
                )
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val path =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                        val absuri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        gallery.add(ExternalImages(id, name, path, absuri))
                    } while (cursor.moveToNext())
                }
                cursor?.close()
                profileViewModel.loadGallery(gallery)
                binding.popupGallery.visibility = View.VISIBLE
                val fragment = GalleryFragment()
                val fragmentManager = childFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.add(R.id.popup_gallery, fragment)
                transaction.commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.editProfileRootview.id)
    }

}

class GalleryFragment : Fragment() {
    private lateinit var profileViewModel: MyPageViewModel
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var binding: CustomGalleryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(requireParentFragment()).get(MyPageViewModel::class.java)
        binding = CustomGalleryBinding.inflate(inflater, container, false)
        var galleryImages = profileViewModel.getGallery()
        val recycler = binding.customGalleryImage

        profileViewModel.StoredImages.observe(viewLifecycleOwner) { image ->
            galleryImages = image
            galleryAdapter = GalleryAdapter(requireContext(), galleryImages, profileViewModel)
            recycler.adapter = galleryAdapter
            val gridLayoutManager = GridLayoutManager(requireContext(), 3)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 1 // 각 아이템의 너비를 1로 설정
                }
            }
            recycler.layoutManager = gridLayoutManager

            recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view) // item position
                    val column = position % 3 // item column
                    if (column == 3) outRect.set(0, 0, 0, 3)
                    else outRect.set(0, 0, 3, 3)
                }
            })
        }

        return binding.root
    }

    companion object {
        fun newInstance(): GalleryFragment {
            return GalleryFragment()
        }
    }
}

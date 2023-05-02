package com.example.haru.view.sns

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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.view.adapter.AddPostAdapter
import com.example.haru.view.adapter.GalleryAdapter
import com.example.haru.view.adapter.MyFeedAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel

class AddPostFragment : Fragment() {
    lateinit var binding : FragmentAddPostBinding
    lateinit var galleryRecyclerView: RecyclerView
    lateinit var galleryViewmodel: MyPageViewModel

        companion object{
            const val TAG : String = "로그"

            fun newInstance() : AddPostFragment {
                return AddPostFragment()
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.d(TAG, "SnsMypageFragment - onCreate() called")
            galleryViewmodel = ViewModelProvider(this).get(MyPageViewModel::class.java)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            Log.d(TAG, "SnsFragment - onCreateView() called")

            binding = FragmentAddPostBinding.inflate(inflater, container, false)
            galleryRecyclerView = binding.addpostGalleryImage


            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {getpermission()}
            getimage()

            galleryViewmodel.StoredImages.observe(viewLifecycleOwner){images ->
                val galleryAdapter = GalleryAdapter(requireContext(), images, galleryViewmodel)
                galleryRecyclerView.adapter = AddPostAdapter(requireContext(), images, galleryViewmodel)
                val gridLayoutManager = GridLayoutManager(requireContext(), 3)
                gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return 1 // 각 아이템의 너비를 1로 설정
                    }
                }
                galleryRecyclerView.layoutManager = gridLayoutManager
                galleryRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view) // item position
                        val column = position % 3 // item column
                        if(column + 1 == 0) outRect.set(0, 0, 0, 3)
                        else outRect.set(0,0,3,3)
                    }
                })
            }

            galleryViewmodel.SelectedPosition.observe(viewLifecycleOwner){selected ->
                for(i in selected){
                    val layoutManager = galleryRecyclerView.layoutManager
                    if(layoutManager != null){
                        val targetView = layoutManager.findViewByPosition(i)
                        val textView = targetView?.findViewById<TextView>(R.id.select_index)
                        textView?.text = "${selected.indexOf(i) + 1}"
                    }
                }
            }

            binding.addpostApply.setOnClickListener{
                val converedImage =  galleryViewmodel.convertMultiPart(requireContext())
                val content = binding.addpostContent.text.toString()
                val hashtag = arrayListOf("해시스완")

                galleryViewmodel.postRequest(converedImage, content, hashtag)
                galleryViewmodel.resetValue()

                val newFrag = SnsFragment.newInstance()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragments_frame, newFrag)
                transaction.commit()
            }

            binding.addpostCancel.setOnClickListener {
                galleryViewmodel.resetValue()
                val newFrag = SnsFragment.newInstance()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragments_frame, newFrag)
                transaction.commit()
            }

            return binding.root
        }

        fun getpermission(){
            if (Build.VERSION.SDK_INT >= 33) {
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
        }

        fun getimage(){
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
                galleryViewmodel.loadGallery(gallery)
        }
}

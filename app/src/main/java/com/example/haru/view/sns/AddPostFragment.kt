package com.example.haru.view.sns

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.*
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.CommentBody
import com.example.haru.data.model.Comments
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.User
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.databinding.PopupSnsCommentCancelBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.view.adapter.*
import com.example.haru.viewmodel.MyPageViewModel

interface PostInterface{
    fun Postpopup(position: Int)
}

class AddPostFragment : Fragment(), PostInterface{
    lateinit var binding : FragmentAddPostBinding
    lateinit var galleryRecyclerView: RecyclerView
    lateinit var galleryViewmodel: MyPageViewModel
    lateinit var galleryAdapter: AddPostAdapter
    var toggle = false

    override fun Postpopup(position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.add_post_anchor)

        if(fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()

            if(position == 0){
                val backManager = parentFragmentManager
                backManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragment = SnsFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, fragment)
                    .commit()
            }
        }

    }

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

        @SuppressLint("NotifyDataSetChanged")
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

            galleryViewmodel.SelectedImage.observe(viewLifecycleOwner){ index->
                val lastindex = galleryViewmodel.getLastImage()
                val layoutManager = galleryRecyclerView.layoutManager
                if(layoutManager != null && index != -1){
                    Log.d("20191668", "current :$index last :$lastindex")
                    val targetView = layoutManager.findViewByPosition(index)
                    val imageView = targetView!!.findViewById<ImageView>(R.id.image)
                    imageView!!.setColorFilter(Color.argb(127, 0, 0, 0), PorterDuff.Mode.SRC_OVER)

                    if(lastindex != -1){
                        val LastTargetView = layoutManager.findViewByPosition(lastindex)
                        val LastImageView = LastTargetView!!.findViewById<ImageView>(R.id.image)
                        LastImageView!!.setColorFilter(null)
                        if(lastindex == index){
                            galleryViewmodel.resetSelection()
                        }
                    }
                }
            }

            galleryViewmodel.StoredImages.observe(viewLifecycleOwner){images ->
                galleryAdapter = AddPostAdapter(requireContext(), images, galleryViewmodel)
                galleryRecyclerView.adapter = galleryAdapter
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
                if(selected != null) {
                    for (i in selected) {
                        Log.d("20191668", "i : $i")
                        val layoutManager = galleryRecyclerView.layoutManager
                        if (layoutManager != null) {
                            val targetView = layoutManager.findViewByPosition(i)
                            val textView = targetView?.findViewById<TextView>(R.id.select_index)
                            textView?.text = "${selected.indexOf(i) + 1}"
                        }
                    }
                }
            }

            binding.imageMultiSelect.setOnClickListener {
                galleryViewmodel.resetSelection()
                val result = galleryAdapter.setMultiSelect()
                if (result){
                    binding.imageMultiSelect.setImageResource(R.drawable.multi_select_on)
                } else{
                    binding.imageMultiSelect.setImageResource(R.drawable.multi_select_picture)
                }
                galleryAdapter.notifyDataSetChanged()
            }

            binding.addpostApply.setOnClickListener{
                if(galleryViewmodel.SelectedImage.value != -1 || galleryViewmodel.SelectedPosition.value!!.size > 0) {
                    val converedImage = galleryViewmodel.convertMultiPart(requireContext())
                    val content = binding.addpostContent.text.toString()
                    val hashtag = arrayListOf("해시스완")
                    Toast.makeText(requireContext(), "게시글 작성중...", Toast.LENGTH_SHORT).show()

                    galleryViewmodel.postRequest(converedImage, content, hashtag)
                    galleryViewmodel.resetValue()


                    galleryViewmodel.PostDone.observe(viewLifecycleOwner) { done ->
                        val fragmentManager = parentFragmentManager
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        val fragment = SnsFragment()
                        val transaction = parentFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragments_frame, fragment)
                        transaction.commit()
                    }
                }else{
                    Toast.makeText(requireContext(), "사진을 선택해 주세요" ,Toast.LENGTH_SHORT).show()
                }
            }

            binding.addpostCancel.setOnClickListener {
                galleryViewmodel.resetValue()

                val fragment = PopupPost(this)
                val fragmentManager = childFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.add(R.id.add_post_anchor, fragment)
                transaction.commit()


            }

            binding.galleyToggle.setOnClickListener {
                val layoutparam = binding.popupGallery.layoutParams
                val startHeight = layoutparam.height
                if (!toggle) {
                    layoutparam.height = (binding.addpostRootView.measuredHeight - binding.addpostTitle.measuredHeight).toInt()
                    toggle = true
                    binding.galleyToggle.rotation = 270f
                    binding.imageMultiSelect.isClickable = true
                } else {
                    layoutparam.height = 0
                    toggle = false
                    binding.galleyToggle.rotation = 90f
                    binding.imageMultiSelect.isClickable = false
                }
                val targetHeight = layoutparam.height

                val animator = ValueAnimator.ofInt(startHeight, targetHeight)
                val duration = 200

                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    layoutparam.height = animatedValue
                    binding.popupGallery.layoutParams = layoutparam
                }

                animator.duration = duration.toLong()
                animator.start()
                galleryAdapter.notifyDataSetChanged()
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

class PopupPost(listener: PostInterface) : Fragment() {

    lateinit var popupbinding : PopupSnsPostCancelBinding
    val listener = listener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        popupbinding = PopupSnsPostCancelBinding.inflate(inflater, container, false)

        popupbinding.snsAddPostUnsave.setOnClickListener {
            listener.Postpopup(0)
        }

        popupbinding.snsAddPostCancel.setOnClickListener {
            listener.Postpopup(1)
        }

        return popupbinding.root
    }

}

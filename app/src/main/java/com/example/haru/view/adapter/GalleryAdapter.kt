package com.example.haru.view.adapter

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.SnsPost
import com.example.haru.viewmodel.MyPageViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GalleryAdapter (val context: Context,
                      private var itemList: ArrayList<ExternalImages>,
                      private val myPageViewModel: MyPageViewModel): RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.GalleryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.GalleryViewHolder, position: Int) {
        Glide.with(context).load(itemList[position].absuri).into(holder.image)
        holder.image.setOnClickListener {
            val data = itemList[position]
            val cursor = context.contentResolver.query(data.absuri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath = it.getString(columnIndex)
                val fileName = data.name.substringAfterLast('.')
                val fileExtension = "image/" + fileName
                Log.d("Image", "1 ${fileExtension}")
                Log.d("Image", "2 ${imagePath}")
                Log.d("Image", "3 ${data.name}")

                val file = File(imagePath)
                Log.d("Image", "4 $file")
                val requestFile = RequestBody.create(MediaType.parse(fileExtension), file)
                val part = MultipartBody.Part.createFormData("image", data.name, requestFile)
                myPageViewModel.updateProfile(part)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.image)
    }
}
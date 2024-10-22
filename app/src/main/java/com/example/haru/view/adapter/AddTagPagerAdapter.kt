package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.viewmodel.MyPageViewModel

class AddTagPagerAdapter(private val context: Context,
                         private var imageList: ArrayList<ExternalImages> = arrayListOf(),
                         private val myPageViewModel: MyPageViewModel
) : RecyclerView.Adapter<AddTagPagerAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.pager_image_set, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(myPageViewModel.imageList.size != 0) {
            Log.d("CropImages", "image: ${imageList[position]}")

            holder.bind(
                imageList[position].absuri.toString(),
                myPageViewModel.imageList[position],
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateImage(images : ArrayList<ExternalImages>){
        if(images.size > 0)
            imageList = images
        else
            imageList.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.post_image)

        fun bind(image: String, externalimage: ExternalImages, position: Int) {
            imageView.setOnClickListener {
                if(myPageViewModel.selectedPostionList[position] != -1) {
                    myPageViewModel.getCrop(externalimage)
                    myPageViewModel.lastTouchPosition = position
                }
            }

            Glide.with(context)
                .load(image)
                .into(imageView)
        }
    }
}
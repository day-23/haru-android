package com.example.haru.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Profile

interface ImageClickListener{
    fun OnImageClick(position: Int)
}

class AddCommentPagerAdapter(private val context: Context, private val imageList: ArrayList<Profile>, listener: ImageClickListener) :
    RecyclerView.Adapter<AddCommentPagerAdapter.ViewHolder>(){
    val listener = listener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.pager_image_set, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position].url)
        holder.imageView.setOnClickListener {
            listener.OnImageClick(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.post_image)
        fun bind(image: String) {
            Glide.with(context)
                .load(image)
                .into(imageView)
        }
    }
}
package com.example.haru.view.adapter

import com.example.haru.data.model.Post
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Media
import com.example.haru.data.model.SnsPost
import com.example.haru.view.sns.LookAroundClick
import kakao.k.p

class LookAroundAdapter (val context: Context,
                         private var itemList: ArrayList<Post>,
                         val lookAroundClick: LookAroundClick): RecyclerView.Adapter<LookAroundAdapter.LookAroundViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookAroundAdapter.LookAroundViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return LookAroundViewHolder(view)
    }

    override fun onBindViewHolder(holder: LookAroundViewHolder, position: Int) {
        Glide.with(context).load(itemList[position].images[0].url).into(holder.picture)
        holder.picture.setOnClickListener {
            lookAroundClick.pictureClicked(itemList[position])
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun newPage(post: ArrayList<Post>){
        itemList.addAll(post)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun firstPage(post: ArrayList<Post>){
        itemList = post
        notifyDataSetChanged()
    }

    inner class LookAroundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var picture = itemView.findViewById<ImageView>(R.id.image)
    }
}
package com.example.haru.view.adapter

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
import com.example.haru.view.sns.MediaClick

class MediaAdapter (val context: Context,
                   private var itemList: ArrayList<Media>,
                   val mediaClick: MediaClick): RecyclerView.Adapter<MediaAdapter.MyFeedViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaAdapter.MyFeedViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return MyFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaAdapter.MyFeedViewHolder, position: Int) {
        if(itemList[position].images.size > 0){
            Glide.with(context).load(itemList[position].images[0].url).into(holder.picture)
            holder.picture.setOnClickListener {
                mediaClick.onMediaClick(itemList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun newPage(media: ArrayList<Media>){
        itemList.addAll(media)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun firstPage(media: ArrayList<Media>){
        itemList = media
        notifyDataSetChanged()
    }

    inner class MyFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var picture = itemView.findViewById<ImageView>(R.id.image)
    }
}
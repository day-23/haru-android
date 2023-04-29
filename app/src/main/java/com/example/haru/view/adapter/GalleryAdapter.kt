package com.example.haru.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.SnsPost

class GalleryAdapter (val context: Context,
                      private var itemList: ArrayList<ExternalImages>): RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.GalleryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryAdapter.GalleryViewHolder, position: Int) {
        Glide.with(context).load(itemList[position].absuri).into(holder.image)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    inner class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.image)
    }
}
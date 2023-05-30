package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Media
import com.example.haru.data.model.Tag
import com.example.haru.view.sns.OnMediaTagClicked

class MediaTagAdapter (val context: Context,
                       private var itemList: ArrayList<Tag> = arrayListOf(),
                       val tagClicked : OnMediaTagClicked
): RecyclerView.Adapter<MediaTagAdapter.MediaTagViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaTagAdapter.MediaTagViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.custom_chip, parent, false)
        return MediaTagViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaTagViewHolder, position: Int) {
        holder.tag.text = itemList[position].content

        holder.tag.setOnClickListener {
            tagClicked.onTagClicked(itemList[position], holder)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun newPage(tags: ArrayList<Tag>){
        itemList.addAll(tags)
        notifyDataSetChanged()
    }


    inner class MediaTagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tag = itemView.findViewById<Button>(R.id.tag_chip)
    }
}
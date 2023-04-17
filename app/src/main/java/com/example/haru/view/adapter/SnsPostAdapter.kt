package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.SnsPost
import com.example.haru.data.model.timetable_data

class SnsPostAdapter(val context: Context,
                     private var itemList: ArrayList<SnsPost>): RecyclerView.Adapter<SnsPostAdapter.SnsPostViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_sns_post, parent, false)
        return SnsPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsPostViewHolder, position: Int) {
        holder.userid.text = "momo"
        holder.picture.setBackgroundResource(R.drawable.momo)
        holder.content.text = "Draw Of MOMO"
        var likeclick = 0
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class SnsPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userid = itemView.findViewById<TextView>(R.id.user_id)
        var picture = itemView.findViewById<ImageView>(R.id.post_picture)
        var content = itemView.findViewById<TextView>(R.id.post_contents)
        var likeBtn = itemView.findViewById<ImageView>(R.id.button_like)
    }
}
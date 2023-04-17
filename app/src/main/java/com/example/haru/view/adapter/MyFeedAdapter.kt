package com.example.haru.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.SnsPost

class MyFeedAdapter (val context: Context,
                   private var itemList: ArrayList<SnsPost>): RecyclerView.Adapter<MyFeedAdapter.MyFeedViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFeedAdapter.MyFeedViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_sns_post, parent, false)
        return MyFeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFeedAdapter.MyFeedViewHolder, position: Int) {
        holder.userid.text = "momo"
        holder.picture.setBackgroundResource(R.drawable.momo)
        holder.content.text = "Draw Of MOMO"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userid = itemView.findViewById<TextView>(R.id.user_id)
        var picture = itemView.findViewById<ImageView>(R.id.post_picture)
        var content = itemView.findViewById<TextView>(R.id.post_contents)
    }
}
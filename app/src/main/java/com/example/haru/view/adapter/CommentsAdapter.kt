package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Comments


class CommentsAdapter(val context: Context,
                      private var itemList: ArrayList<Comments>): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_comment_list, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentsViewHolder, position: Int) {
        holder.userid.text = itemList[position].user.name
        holder.content.text = itemList[position].content
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile = itemView.findViewById<ImageView>(R.id.comment_profile)
        var userid = itemView.findViewById<TextView>(R.id.comment_id)
        var content = itemView.findViewById<TextView>(R.id.comment_content)
        var disclosure = itemView.findViewById<ImageView>(R.id.comment_disclosure)
    }
}
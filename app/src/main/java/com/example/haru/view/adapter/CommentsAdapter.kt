package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Im
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
import com.example.haru.data.model.Comments
import com.example.haru.data.model.PatchCommentBody
import com.example.haru.view.sns.onCommentClick
import com.example.haru.viewmodel.SnsViewModel

class CommentsAdapter(val context: Context,
                      private var itemList: ArrayList<Comments>,
                      val listener: onCommentClick): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>(){

    var disclosure = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_comment_list, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentsViewHolder, position: Int) {

        if(itemList[position].user.profileImage != "https://harus3.s3.ap-northeast-2.amazonaws.com/null") {
            Glide.with(holder.itemView.context)
                .load(itemList[position].user.profileImage)
                .into(holder.profile)
        }
        holder.userid.text = itemList[position].user.name
        holder.content.text = itemList[position].content

        holder.setup.setOnClickListener {
            listener.onDeleteClick(itemList[position].user.id, itemList[position].id, position)
        }

        if(!itemList[position].isPublic){
            holder.disclosure.setImageResource(R.drawable.comment_not_public)
            disclosure = false
        }else{
            holder.disclosure.setImageResource(R.drawable.on_write_comment)
        }

        holder.disclosure.setOnClickListener {
            if(disclosure){
                holder.disclosure.setImageResource(R.drawable.comment_not_public)
                disclosure = false
            }else{
                holder.disclosure.setImageResource(R.drawable.on_write_comment)
                disclosure = true
            }
            val body = PatchCommentBody(itemList[position].x, itemList[position].y, disclosure)
            listener.onPublicClick(itemList[position].user.id, itemList[position].id, body)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int){
        itemList.removeAt(position)
        notifyDataSetChanged()
    }

    inner class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile = itemView.findViewById<ImageView>(R.id.comment_profile)
        var userid = itemView.findViewById<TextView>(R.id.comment_id)
        var content = itemView.findViewById<TextView>(R.id.comment_content)
        var disclosure = itemView.findViewById<ImageView>(R.id.comment_disclosure)
        var setup = itemView.findViewById<ImageView>(R.id.comment_setup)
    }
}
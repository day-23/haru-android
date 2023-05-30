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
import org.w3c.dom.Comment

class CommentsAdapter(val context: Context,
                      private var itemList: ArrayList<Comments> = arrayListOf(),
                      val listener: onCommentClick): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_comment_list, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentsViewHolder, position: Int) {
        val item = itemList[position]
        var disclosure = item.isPublic!!

        Log.d("20191668", "${item.content} : ${item.isPublic}")
        if(itemList[position].user!!.profileImage != "https://harus3.s3.ap-northeast-2.amazonaws.com/null") {
            Glide.with(holder.itemView.context)
                .load(itemList[position].user!!.profileImage)
                .into(holder.profile)
        }
        holder.userid.text = itemList[position].user!!.name
        holder.content.text = itemList[position].content

        holder.setup.setOnClickListener {
            listener.onDeleteClick(itemList[position].user!!.id, itemList[position].id, item)
        }

        if(disclosure){
            holder.disclosure.setImageResource(R.drawable.on_write_comment)
        }else{
            holder.disclosure.setImageResource(R.drawable.comment_not_public)
        }

        holder.disclosure.setOnClickListener {
            if(disclosure){
                holder.disclosure.setImageResource(R.drawable.comment_not_public)
                disclosure = false
            }else{
                holder.disclosure.setImageResource(R.drawable.on_write_comment)
                disclosure = true
            }
            val body = PatchCommentBody(item.x!!, item.y!!, disclosure!!)
            listener.onPublicClick(item.user!!.id, item.id, body)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun getLastComment(): String {
        return itemList[itemCount - 1].createdAt!!
    }

    @SuppressLint("NotifyDataSetChanged")
    fun newComment(items: ArrayList<Comments>){
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun firstComment(items: ArrayList<Comments>){
        itemList = items
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(item: Comments){
        itemList.remove(item)
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
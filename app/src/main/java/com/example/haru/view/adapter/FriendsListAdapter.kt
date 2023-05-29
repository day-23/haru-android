package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.FriendInfo
import com.example.haru.view.sns.OnFriendClicked

class FriendsListAdapter(val context: Context,
                         private var itemList: ArrayList<FriendInfo> = arrayListOf(),
                         val onFriendClicked: OnFriendClicked
                        ) : RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListAdapter.FriendsListViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_searched_user, parent, false)
        return FriendsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {
        hideButtons(holder)
        showButtons(holder, itemList[position].friendStatus!!)

        Glide.with(holder.itemView.context)
            .load(itemList[position].profileImageUrl)
            .into(holder.profile)

        holder.name.text = itemList[position].name

        holder.delete.setOnClickListener {
            onFriendClicked.onDeleteClick(itemList[position])
        }

        holder.accept.setOnClickListener {
            onFriendClicked.onAcceptClick(itemList[position])
        }

        holder.reject.setOnClickListener {
            onFriendClicked.onRejectClick(itemList[position])
        }

        holder.profile.setOnClickListener {
            onFriendClicked.onProfileClick(itemList[position])
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun showButtons(holder: FriendsListViewHolder, status: Int){
        when(status){
            1 -> {
                holder.accept.visibility = View.VISIBLE
                holder.reject.visibility = View.VISIBLE
                }
            2 -> {
                holder.delete.visibility = View.VISIBLE
                holder.setup.visibility = View.VISIBLE
            }
        }
    }

    fun hideButtons(holder: FriendsListViewHolder){
        holder.delete.visibility = View.GONE
        holder.accept.visibility = View.GONE
        holder.reject.visibility = View.GONE
        holder.setup.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items : ArrayList<FriendInfo>){
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFirstList(items: ArrayList<FriendInfo>){
        if(items.size > 0)
            itemList = items
        else{
            itemList.clear()
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFriend(targetUser : FriendInfo){
        itemList.remove(targetUser)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun patchInfo(item: FriendInfo){
        val index = itemList.indexOf(item)
        itemList[index] = item
        notifyDataSetChanged()
    }

    inner class FriendsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile = itemView.findViewById<ImageView>(R.id.search_profile)
        var name = itemView.findViewById<TextView>(R.id.search_id)
        var delete = itemView.findViewById<TextView>(R.id.friend_delete)
        var accept = itemView.findViewById<TextView>(R.id.friend_accept)
        var reject = itemView.findViewById<TextView>(R.id.friend_reject)
        var setup = itemView.findViewById<ImageView>(R.id.search_setup)
    }
}
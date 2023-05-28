package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.FriendInfo
import com.example.haru.data.model.SnsPost
import com.kakao.sdk.talk.model.Friends

class FriendsListAdapter(val context: Context,
                         private var itemList: ArrayList<FriendInfo> = arrayListOf()
                        ) : RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListAdapter.FriendsListViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_searched_user, parent, false)
        return FriendsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items : ArrayList<FriendInfo>){
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    fun addFirstList(items: ArrayList<FriendInfo>){
        itemList = items
        notifyDataSetChanged()
    }

    inner class FriendsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}
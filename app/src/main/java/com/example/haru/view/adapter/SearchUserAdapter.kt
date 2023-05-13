package com.example.haru.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Post
import kotlin.math.sign

class SearchUserAdapter(val context: Context,
                        private var itemList: ArrayList<Post> = ArrayList()): RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserAdapter.SearchUserViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_searched_user, parent, false)
        return SearchUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchUserAdapter.SearchUserViewHolder, position: Int) {

    }

    inner class SearchUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.search_profile)
        val name = itemView.findViewById<TextView>(R.id.search_id)
        val setup = itemView.findViewById<ImageView>(R.id.search_setup)
    }
}
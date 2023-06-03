package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Profile
import com.example.haru.view.sns.onTemplateListener
import kotlinx.coroutines.NonDisposableHandle.parent

class TemplateAdapter(val context: Context,
                      private var itemList : ArrayList<com.example.haru.data.model.Profile> = arrayListOf(),
                      val listener : onTemplateListener) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateAdapter.TemplateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_template_image, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateAdapter.TemplateViewHolder, position: Int) {
        Glide.with(context)
            .load(itemList[position].url)
            .into(holder.template)

        holder.template.setOnClickListener {
            listener.onTemplateClicked(itemList[position].url, itemList[position].id, holder)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addTemplate(templates : ArrayList<Profile>){
        itemList = templates
        notifyDataSetChanged()
    }

    inner class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       var template = itemView.findViewById<ImageView>(R.id.template_image)
    }

}
package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistTagBinding

//private var data: MutableList<Tag>,
class TagAdapter(val context: Context) :

    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    private var data = emptyList<Tag>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding =
            FragmentChecklistTagBinding.inflate(LayoutInflater.from(context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class TagViewHolder(val binding: FragmentChecklistTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tag) {
            binding.tag = item
            Log.d("TAG", binding.tag.toString())
        }
    }

    fun setDataList(dataList: List<Tag>) {
        this.data = dataList
        notifyDataSetChanged()
    }


}
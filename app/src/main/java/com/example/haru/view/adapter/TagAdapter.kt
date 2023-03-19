package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistTagBinding
import com.example.haru.databinding.FragmentChecklistTagHeaderBinding

class TagAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val HEADER = 0
    val ITEM = 1
    val EMPTY = 2

    interface TagClick {
        fun onClick(view: View, position: Int)
    }

    var tagClick: TagClick? = null

    private var data = emptyList<Tag>()

    override fun getItemViewType(position: Int): Int {
        return if (data.isNotEmpty()) {
            if (position == 0) HEADER else ITEM
        } else {
            if (position == 0) HEADER else EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            HEADER -> HeaderViewHolder(
                FragmentChecklistTagHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
            )

            ITEM -> TagItemViewHolder(FragmentChecklistTagBinding.inflate(LayoutInflater.from(context), parent, false))

            else -> {
                throw ClassCastException("Unknown viewType $viewType")
            }
        }
//        val binding =
//            FragmentChecklistTagBinding.inflate(LayoutInflater.from(context), parent, false)
//
//        return TagItemViewHolder(binding)
    }

    override fun getItemCount(): Int = data.count() + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is HeaderViewHolder -> {
                if (tagClick != null) {
                    holder.binding.tagHeader.setOnClickListener {
                        tagClick?.onClick(it, position)
                    }
                }
            }
            is TagItemViewHolder -> {
                holder.bind(data[position - 1])
                if (tagClick != null) {
                    holder.binding.tagBtn.setOnClickListener {
                        tagClick?.onClick(it, position)
                    }
                }
            }
        }
//        if (position == 0) {
//            //Header
//        } else holder.bind(data[position])

    }

    inner class TagItemViewHolder(val binding: FragmentChecklistTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tag) {
            binding.tag = item
        }

        init {
            // 클릭 리스너
//            binding.tagBtn.setOnClickListener {
//                val pos = adapterPosition
//                Log.d("20191627", pos.toString() + ": setonClicke")
//            }

            // 롱클릭 리스너
//            binding.tagBtn.setOnLongClickListener {
//                return@setOnLongClickListener true
//            }
        }

    }

    inner class HeaderViewHolder(val binding: FragmentChecklistTagHeaderBinding) :
        RecyclerView.ViewHolder(binding.root){

            init {
//                binding.tagHeader.setOnClickListener {
//                    val pos = adapterPosition
//                    Log.d("20191627", pos.toString() + ": 눌렸다")
//                }
            }
        }

    fun setDataList(dataList: List<Tag>) {
        this.data = dataList
        notifyDataSetChanged()
    }


}
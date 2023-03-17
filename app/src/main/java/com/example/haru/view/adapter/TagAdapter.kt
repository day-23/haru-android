package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistTagBinding

//private var data: MutableList<Tag>,
class TagAdapter(val context: Context) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
    interface TagClick{
        fun onClick(view: View, position: Int)
    }

    var tagClick: TagClick? = null

    private var data = emptyList<Tag>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding =
            FragmentChecklistTagBinding.inflate(LayoutInflater.from(context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(data[position])

        if (tagClick != null) {
            holder.binding.tagBtn.setOnClickListener{
                tagClick?.onClick(it, position)
            }
        }
    }

    inner class TagViewHolder(val binding: FragmentChecklistTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tag) {
            binding.tag = item
        }

        init {
            // 클릭 리스너
            binding.tagBtn.setOnClickListener{
                val pos = adapterPosition
                Log.d("20191627", pos.toString() + ": 눌렸다")


            }

            // 롱클릭 리스너
//            binding.tagBtn.setOnLongClickListener {
//                return@setOnLongClickListener true
//            }
        }

    }

    fun setDataList(dataList: List<Tag>) {
        this.data = dataList
        notifyDataSetChanged()
    }


}
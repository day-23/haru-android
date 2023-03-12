package com.example.haru.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistItemBinding


class TodoAdapter(val context: Context) :

    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private var data = emptyList<Todo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val binding =
            FragmentChecklistItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class TodoViewHolder(val binding: FragmentChecklistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        fun bind(item: Todo) {
            binding.tvTitle.text = item.content
            binding.tvDescription.text = item.memo
            binding.checkFlag.isChecked = item.flag
        }
    }

    fun setDataList(dataList: List<Todo>) {
        this.data = dataList
        notifyDataSetChanged()
    }
}
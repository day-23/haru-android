package com.example.haru.view.adapter

import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.ChecklistEmptyBinding
import com.example.haru.databinding.ChecklistHeaderType1Binding
import com.example.haru.databinding.ChecklistHeaderType2Binding
import com.example.haru.databinding.FragmentChecklistDividerBinding
import com.example.haru.databinding.FragmentChecklistItemBinding


class TodoAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val HeaderType1 = 0
    val HeaderType2 = 1
    val Item = 2
    val Divider = 3
    val Empty = 4

    var tags = mutableListOf<Tag>(Tag("", "분류"), Tag("", "미분류"), Tag("", "완료"), Tag("", ""))

    private var data = mutableListOf<Todo>()

    private var todoByTag = false
    private var flagCount = 0
    private var tagCount = 0
    private var untagCount = 0
    private var completeCount = 0

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            HeaderType1 -> HeaderTypeOneViewHolder(
                ChecklistHeaderType1Binding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )

            HeaderType2 -> HeaderTypeTwoViewHolder(
                ChecklistHeaderType2Binding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )

            Item -> TodoViewHolder(
                FragmentChecklistItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            Divider -> DividerViewHolder(
                FragmentChecklistDividerBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )

            Empty -> EmptyViewHolder(
                ChecklistEmptyBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            else -> {
                throw ClassCastException("Unknown viewType $viewType")
            }
        }

    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderTypeOneViewHolder -> {}
            is HeaderTypeTwoViewHolder ->  holder.bind(data[position].content)
            is DividerViewHolder -> {}
            is TodoViewHolder -> holder.bind(data[position])
        }
    }

    inner class HeaderTypeOneViewHolder(val binding: ChecklistHeaderType1Binding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class HeaderTypeTwoViewHolder(val binding: ChecklistHeaderType2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.str = item
        }
    }

    inner class DividerViewHolder(val binding: FragmentChecklistDividerBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class TodoViewHolder(val binding: FragmentChecklistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Todo) {
            binding.todo = item
        }
    }

    inner class EmptyViewHolder(val binding: ChecklistEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    fun setDataList(dataList: List<Todo>) {
        this.data = dataList as MutableList<Todo>
        if (todoByTag){
            this.data.add(0, Todo(type = 1, content = tags[3].content))
        } else {
            this.data.apply {
                // flag
                add(0, Todo(type = 0))
                add(flagCount + 1, Todo(type = 3))

                // tag
                add(flagCount + 2, Todo(type = 1, content = tags[0].content))
                add(flagCount + tagCount + 3, Todo(type = 3))

                //untag
                add(flagCount + tagCount + 4, Todo(type = 1, content = tags[1].content))
                add(flagCount + tagCount + untagCount + 5, Todo(type = 3))

                //complete
                add(flagCount + tagCount + untagCount + 6, Todo(type = 1, content = tags[2].content))
            }
        }
        notifyDataSetChanged()
    }

    fun setFlagCount(count: Int?) {
        flagCount = count ?: 0
    }

    fun setTagCount(count: Int?) {
        tagCount = count ?: 0
    }

    fun setUnTagCount(count: Int?) {
        untagCount = count ?: 0
    }

    fun setCompleteCount(count: Int?) {
        completeCount = count ?: 0
    }

    fun setTodoByTag(content: String?) {
        if (content != null) {
            todoByTag = true
            tags[3].content = content
        } else todoByTag = false
    }
}
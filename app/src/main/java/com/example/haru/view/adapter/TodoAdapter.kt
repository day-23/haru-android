package com.example.haru.view.adapter

import android.content.Context
import android.graphics.Paint
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.ChecklistEmptyBinding
import com.example.haru.databinding.ChecklistHeaderType1Binding
import com.example.haru.databinding.ChecklistHeaderType2Binding
import com.example.haru.databinding.ChecklistHeaderType3Binding
import com.example.haru.databinding.FragmentChecklistDividerBinding
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.utils.FormatDate


class TodoAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface TodoClick {
        fun onClick(view: View, position: Int)
    }

    interface CompleteClick {
        fun onClick(view: View, position: Int)
    }

    interface FlagClick {
        fun onClick(view: View, position: Int)
    }

    var todoClick: TodoClick? = null
    var completeClick: CompleteClick? = null
    var flagClick: FlagClick? = null

    val HeaderType1 = 0
    val HeaderType2 = 1
    val Item = 2
    val Divider = 3
    val HeaderType3 = 4
    val Empty = 5

    var tags = mutableListOf<Tag>(Tag("", "분류"), Tag("", "미분류"), Tag("", "완료"), Tag("", ""))

    var data = mutableListOf<Todo>()

    private var todoByTag = false
    private var todoByFlag = false
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

            HeaderType3 -> HeaderTypeThreeViewHolder(
                ChecklistHeaderType3Binding.inflate(
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
            is HeaderTypeTwoViewHolder -> holder.bind(data[position].content)
            is HeaderTypeThreeViewHolder -> holder.bind(data[position].content)
            is DividerViewHolder -> {}
            is TodoViewHolder -> {
                holder.bind(data[position])
                if (todoClick != null) {
                    holder.binding.ClickLayout.setOnClickListener {
                        todoClick?.onClick(it, position)
                    }
                }

                if (flagClick != null) {
                    holder.binding.checkFlag.setOnClickListener {
                        flagClick?.onClick(it, position)
                    }
                }

                if (completeClick != null) {
                    holder.binding.checkDone.setOnClickListener {
                        completeClick?.onClick(it, position)
                    }
                }
            }
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

    inner class HeaderTypeThreeViewHolder(val binding: ChecklistHeaderType3Binding) :
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

            if (item.endDate == null && item.tags.isEmpty() && !item.todayTodo && item.alarms.isEmpty() && item.memo == "" && item.repeatOption == null){
                binding.blankView.visibility = View.VISIBLE
                binding.tvTagDescription.text = ""
                binding.tvEndDateDescription.text = ""
            }
            else {
                binding.blankView.visibility = View.GONE
                var tag = ""
                for (i in 0 until item.tags.size) {
                    tag += "${item.tags[i].content} "
                }
                if (tag != "") {
                    binding.tvTagDescription.text = tag.dropLast(1)
                    binding.tvTagDescription.visibility = View.VISIBLE
                } else {
                    binding.tvTagDescription.visibility = View.GONE
                    binding.tvTagDescription.text =  tag
                }

                if (item.endDate != null) {
                    binding.tvEndDateDescription.text =
                        FormatDate.todoDateToStr(item.endDate!!).substring(5, 10) + "까지"
                    binding.tvEndDateDescription.visibility = View.VISIBLE
                } else {
                    binding.tvEndDateDescription.text = ""
                    binding.tvEndDateDescription.visibility = View.GONE
                }
            }

            if (item.completed) binding.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            else binding.tvTitle.paintFlags = 0

            if (item.subTodos.isEmpty()) return

            for(i in 0 until item.subTodos.size){
                val layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val addView = layoutInflater.inflate(R.layout.subtodo_layout, null)
                // subtodo completed 클릭 리스너
                // TODO

                addView.findViewById<CheckBox>(R.id.cb_subTodo_complete).isChecked = item.subTodos[i].completed
                addView.findViewById<TextView>(R.id.tv_subTodo).text = item.subTodos[i].content

                binding.subTodoItemLayout.addView(addView)
            }
        }
    }

    inner class EmptyViewHolder(val binding: ChecklistEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    fun setDataList(dataList: List<Todo>) {
        this.data = dataList as MutableList<Todo>
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
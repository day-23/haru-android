package com.example.haru.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.utils.FormatDate

class SearchTodoAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var todoData = emptyList<Todo>()


    private val header = 0
    private val item = 1
    private val empty = 2


    override fun getItemViewType(position: Int): Int {
        return if (todoData.isEmpty())
            empty
        else {
            if (position == 0)
                header
            else item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            header -> {
//
//            }

            item -> TodoViewHolder(
                FragmentChecklistItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
            else ->
                TodoViewHolder(
                    FragmentChecklistItemBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            

        }
    }


    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(holder){
//            is
//        }
    }

    inner class TodoViewHolder(val binding: FragmentChecklistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var complete = false

        fun bind(item: Todo) {
            binding.checklistItem.layoutParams = if (item.visibility) LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) else LinearLayout.LayoutParams(0, 0)
            binding.checklistItem.visibility = if (item.visibility) View.VISIBLE else View.GONE
            if (!item.visibility)
                return

            binding.todo = item
            binding.tvTitle.text = item.content
//            if (todoClick != null) {
//                binding.ClickLayout.setOnClickListener {
//                    todoClick?.onClick(it, item.id)
//                }
//            }
//
//            if (flagClick != null) {
//                binding.checkFlag.setOnClickListener {
//                    flagClick?.onClick(it, item.id){ flag, successData ->
//                        if (successData == null)
//                            binding.checkFlag.isChecked = !flag.flag
//                        else if (!successData.success)
//                            binding.checkFlag.isChecked = !flag.flag
//                    }
//                }
//            }
//
//            if (completeClick != null) {
//                binding.checkDone.setOnClickListener {
//                    completeClick?.onClick(it, item.id){ completed, successData ->
//                        if (successData == null)
//                            binding.checkDone.isChecked = !completed.completed
//                        else if (!successData.success)
//                            binding.checkDone.isChecked = !completed.completed
//                    }
//                }
//            }
//
//            if (subTodoCompleteClick != null) {
//                binding.subTodoItemLayout.setOnClickListener {
//                    subTodoClickId = item.id
//                }
//            }
//
//            if (toggleClick != null) {
//                binding.subTodoToggle.setOnClickListener {
//                    binding.subTodoToggle.isSelected = !it.isSelected
//                    binding.subTodoItemLayout.visibility =
//                        if (it.isSelected) View.GONE else View.VISIBLE
//                    toggleClick?.onClick(it, item.id)
//                }
//            }

            if (item.endDate == null && item.tags.isEmpty() && !item.todayTodo && item.alarms.isEmpty() && item.memo == "" && item.repeatOption == null) {
                binding.blankView.visibility = View.VISIBLE
                binding.iconLayout.visibility = View.GONE
            } else {
                binding.blankView.visibility = View.GONE
                binding.iconLayout.visibility = View.VISIBLE
                var tag = ""
                var totalLen = 8
                var count = item.tags.size

                for (i in 0 until item.tags.size) {
                    val len = item.tags[i].content.length
                    if (totalLen - len >= 3) { // 3은 " +${count}"자리이다.
                        tag += "${item.tags[i].content} "
                        count--
                        totalLen -= (len + 1)
                    } else if (totalLen - len >= 0) {
                        tag += item.tags[i].content
                        count--
                        break
                    } else break
                }
                if (count != 0)
                    tag += "+$count"

                if (tag != "") {
                    binding.tvTagDescription.text = tag
                    binding.tvTagDescription.visibility = View.VISIBLE
                } else {
                    binding.tvTagDescription.visibility = View.GONE
                    binding.tvTagDescription.text = tag
                }

                val endDate = FormatDate.strToDate(item.endDate)
                val checkToday = FormatDate.checkToday(endDate)

                if (endDate != null && item.isAllDay) {
                    binding.tvEndDateDescription.text = if (checkToday == true)
                        FormatDate.todoTimeToStr(item.endDate!!)
                    else FormatDate.todoDateToStr(item.endDate!!)
                    binding.tvEndDateDescription.visibility = View.VISIBLE
                } else if (endDate != null) {
                    binding.tvEndDateDescription.text = if (checkToday == true)
                        context.resources.getString(R.string.endDateToday)
                    else FormatDate.todoDateToStr(item.endDate!!)
                    binding.tvEndDateDescription.visibility = View.VISIBLE
                } else {
                    binding.tvEndDateDescription.text = ""
                    binding.tvEndDateDescription.visibility = View.GONE
                }
            }


            complete = item.completed

            binding.tvTitle.typeface = context.resources.getFont(R.font.pretendard_bold)
            binding.tvTitle.text = item.content


            binding.subTodoItemLayout.removeAllViews()
            if (item.subTodos.isEmpty()) {
                binding.subTodoToggle.visibility = View.INVISIBLE
                return
            }

            binding.subTodoToggle.visibility = View.VISIBLE
            for (i in 0 until item.subTodos.size) {
                val layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val addView = layoutInflater.inflate(R.layout.subtodo_layout, null)

                // subtodo completed 클릭 리스너
                addView.findViewById<CheckBox>(R.id.cb_subTodo_complete).apply {
                    isChecked = item.subTodos[i].completed
//                    if (subTodoCompleteClick != null) {
//                        this.setOnClickListener {
//                            binding.subTodoItemLayout.performClick()
//                            subTodoCompleteClick?.onClick(
//                                it,
//                                binding.subTodoItemLayout.indexOfChild(addView)
//                            )
//                        }
//                    }
                }

                addView.findViewById<TextView>(R.id.tv_subTodo).apply {
                    text = item.subTodos[i].content
//                    paintFlags =
//                        if (item.subTodos[i].completed) Paint.ANTI_ALIAS_FLAG or Paint.STRIKE_THRU_TEXT_FLAG else Paint.ANTI_ALIAS_FLAG
                    if (item.subTodos[i].completed)
                        setTextColor(ContextCompat.getColor(context, R.color.light_gray))
                }

                binding.subTodoItemLayout.addView(addView)
            }
            if (item.folded) {
                binding.subTodoToggle.isSelected = true
                binding.subTodoItemLayout.visibility = View.GONE
            } else {
                binding.subTodoToggle.isSelected = false
                binding.subTodoItemLayout.visibility = View.VISIBLE
            }

        }
    }

}
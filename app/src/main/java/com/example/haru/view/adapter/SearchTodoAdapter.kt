package com.example.haru.view.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Completed
import com.example.haru.data.model.Flag
import com.example.haru.data.model.SuccessFail
import com.example.haru.data.model.Todo
import com.example.haru.databinding.FragmentChecklistDividerBinding
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentSearchTodoHeaderBinding
import com.example.haru.utils.FormatDate

class SearchTodoAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface TodoClick {
        fun onClick(view: View, id: String)
    }

    interface FlagClick {
        fun onClick(
            view: View,
            id: String,
            callback: (flag: Flag, successData: SuccessFail?) -> Unit
        )
    }

    interface CompleteClick {
        fun onClick(
            view: View,
            id: String,
            callback: (completed: Completed, successData: SuccessFail?) -> Unit
        )
    }

    interface SubTodoCompleteClick {
        fun onClick(view: View, subTodoPosition: Int)
    }

    interface ToggleClick {
        fun onClick(view: View, id: String)
    }

    var todoClick: TodoClick? = null
    var flagClick: FlagClick? = null
    var completeClick: CompleteClick? = null
    var subTodoCompleteClick: SubTodoCompleteClick? = null
    var toggleClick: ToggleClick? = null

    var subTodoClickId: String? = null

    private val divider = 0
    private val header = 1
    private val item = 2

    var content: String? = null

    private val diffCallback = object : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffCallback)


    override fun getItemViewType(position: Int): Int = diffUtil.currentList[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            header -> HeaderViewHolder(
                FragmentSearchTodoHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            item -> TodoViewHolder(
                FragmentChecklistItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            divider -> DividerViewHolder(
                FragmentChecklistDividerBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    override fun getItemCount(): Int = diffUtil.currentList.count()


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = diffUtil.currentList[position]
        when (holder) {
            is HeaderViewHolder -> {}
            is DividerViewHolder -> {}
            is TodoViewHolder -> holder.bind(todo)
        }
    }

    inner class TodoViewHolder(val binding: FragmentChecklistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        var complete = false

        fun bind(item: Todo) {
            binding.checklistItem.layoutParams = if (item.visibility) LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) else LinearLayout.LayoutParams(0, 0)
            binding.checklistItem.visibility = if (item.visibility) View.VISIBLE else View.GONE
            if (!item.visibility)
                return

            binding.todo = item

            if (content != null) {
                val start = item.content.indexOf(content!!, ignoreCase = true)
                val end = start + content!!.length
                val span = SpannableString(item.content)
                span.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.highlight
                        )
                    ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvTitle.text = span
            } else binding.tvTitle.text = item.content

            if (todoClick != null) {
                binding.ClickLayout.setOnClickListener {
                    todoClick?.onClick(it, item.id)
                }
            }

            if (flagClick != null) {
                binding.checkFlag.setOnClickListener {
                    flagClick?.onClick(it, item.id){ flag, successData ->
                        if (successData == null)
                            binding.checkFlag.isChecked = !flag.flag
                        else if (!successData.success)
                            binding.checkFlag.isChecked = !flag.flag
                    }
                }
            }
//
            if (completeClick != null) {
                binding.checkDone.setOnClickListener {
                    completeClick?.onClick(it, item.id){ completed, successData ->
                        if (successData == null)
                            binding.checkDone.isChecked = !completed.completed
                        else if (!successData.success)
                            binding.checkDone.isChecked = !completed.completed
                    }
                }
            }
//
            if (subTodoCompleteClick != null) {
                binding.subTodoItemLayout.setOnClickListener {
                    subTodoClickId = item.id
                }
            }
//
            if (toggleClick != null) {
                binding.subTodoToggle.setOnClickListener {
                    binding.subTodoToggle.isSelected = !it.isSelected
                    binding.subTodoItemLayout.visibility =
                        if (it.isSelected) View.GONE else View.VISIBLE
                    toggleClick?.onClick(it, item.id)
                }
            }

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
                    if (subTodoCompleteClick != null) {
                        this.setOnClickListener {
                            binding.subTodoItemLayout.performClick()
                            subTodoCompleteClick?.onClick(
                                it,
                                binding.subTodoItemLayout.indexOfChild(addView)
                            )
                        }
                    }
                }

                addView.findViewById<TextView>(R.id.tv_subTodo).apply {
                    text = item.subTodos[i].content
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

    inner class DividerViewHolder(val binding: FragmentChecklistDividerBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class HeaderViewHolder(val binding: FragmentSearchTodoHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {}


    fun setDataList(dataList: List<Todo>?) {
        if (dataList == null)
            return
        diffUtil.submitList(dataList as MutableList<Todo>)
    }

}
package com.example.haru.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Typeface
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Tag
import com.example.haru.data.model.Todo
import com.example.haru.databinding.ChecklistBlankBinding
import com.example.haru.databinding.ChecklistEmptyBinding
import com.example.haru.databinding.ChecklistHeaderType1Binding
import com.example.haru.databinding.ChecklistHeaderType2Binding
import com.example.haru.databinding.ChecklistHeaderType3Binding
import com.example.haru.databinding.FragmentChecklistDividerBinding
import com.example.haru.databinding.FragmentChecklistItemBinding
import com.example.haru.databinding.FragmentTestBinding
import com.example.haru.utils.FormatDate
import com.example.haru.view.checklist.ChecklistItemTouchHelperCallback
import com.example.haru.view.checklist.ItemTouchHelperListener
import java.util.*


class TodoAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperListener {

    interface SectionToggleClick {
        fun onClick(view: View, str: String)
    }

    interface TodoClick {
        fun onClick(view: View, id: String)
    }

    interface CompleteClick {
        fun onClick(view: View, id: String)
    }

    interface FlagClick {
        fun onClick(view: View, id: String)
    }

    interface SubTodoCompleteClick {
        fun onClick(view: View, subTodoPosition: Int)
    }

    interface ToggleClick {
        fun onClick(view: View, id: String)
    }

    var sectionToggleClick: SectionToggleClick? = null
    var todoClick: TodoClick? = null
    var completeClick: CompleteClick? = null
    var flagClick: FlagClick? = null
    var subTodoCompleteClick: SubTodoCompleteClick? = null
    var toggleClick: ToggleClick? = null

    //    val HeaderType1 = 0   -> 디자인 시안 변경으로 인해 사용 X
//    val HeaderType2 = 1
    val Item = 2
    val Divider = 3
    val HeaderType3 = 4
    val Empty = 5
    val Blank = 6

    var tags = mutableListOf<Tag>(Tag("", "분류"), Tag("", "미분류"), Tag("", "완료"), Tag("", ""))

    var data = mutableListOf<Todo>()

    var subTodoClickId: String? = null

    private var todoByTag = false
    private var todoByFlag = false
    private var flagCount = 0
    private var tagCount = 0
    private var untagCount = 0
    private var completeCount = 0

    private var dragLimitTop: Int? = null
    private var dragLimitBottom: Int? = null

    private val diffCallback = object : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    val diffUtil = AsyncListDiffer(this, diffCallback)

    override fun getItemViewType(position: Int): Int = diffUtil.currentList[position].type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            HeaderType1 -> HeaderTypeOneViewHolder(    // 디자인 시안 변경으로 인해 사용 X
//                ChecklistHeaderType1Binding.inflate(
//                    LayoutInflater.from(context), parent, false
//                )
//            )
//
//            HeaderType2 -> HeaderTypeTwoViewHolder(
//                ChecklistHeaderType2Binding.inflate(
//                    LayoutInflater.from(context), parent, false
//                )
//            )

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

            Blank -> BlankViewHolder(
                ChecklistBlankBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )

            else -> {
                throw ClassCastException("Unknown viewType $viewType")
            }
        }

    }

    override fun getItemCount(): Int = diffUtil.currentList.count()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val todo = diffUtil.currentList[position]
        when (holder) {
//            is HeaderTypeOneViewHolder -> {}
//            is HeaderTypeTwoViewHolder -> holder.bind(todo.content)
            is HeaderTypeThreeViewHolder -> holder.bind(todo.content)
            is DividerViewHolder -> {}
            is TodoViewHolder -> {
                holder.bind(todo)
            }
            is EmptyViewHolder -> holder.bind(todo)
            is BlankViewHolder -> {}
        }
    }

//    inner class HeaderTypeOneViewHolder(val binding: ChecklistHeaderType1Binding) :
//        RecyclerView.ViewHolder(binding.root) {}
//
//    inner class HeaderTypeTwoViewHolder(val binding: ChecklistHeaderType2Binding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: String) {
//            binding.str = item
//        }
//    }

    inner class HeaderTypeThreeViewHolder(val binding: ChecklistHeaderType3Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.str = item
            if (sectionToggleClick != null)
                binding.ivSectionArrow.setOnClickListener {
                    binding.ivSectionArrow.isSelected = !it.isSelected
                    sectionToggleClick?.onClick(it, item)
                }
        }
    }

    inner class DividerViewHolder(val binding: FragmentChecklistDividerBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class BlankViewHolder(val binding: ChecklistBlankBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    inner class TodoViewHolder(val binding: FragmentChecklistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Todo) {
            Log.d("20191627", "bind 호출")
            binding.checklistItem.layoutParams = if (item.visibility) LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) else LinearLayout.LayoutParams(0, 0)
            binding.checklistItem.visibility = if (item.visibility) View.VISIBLE else View.GONE
            if (!item.visibility)
                return

            binding.todo = item
            binding.tvTitle.text = item.content
            if (todoClick != null) {
                binding.ClickLayout.setOnClickListener {
                    todoClick?.onClick(it, item.id)
                }
            }

            if (flagClick != null) {
                binding.checkFlag.setOnClickListener {
                    flagClick?.onClick(it, item.id)
                }
            }

            if (completeClick != null) {
                binding.checkDone.setOnClickListener {
                    completeClick?.onClick(it, item.id)
                }
            }

            if (subTodoCompleteClick != null) {
                binding.subTodoItemLayout.setOnClickListener {
                    subTodoClickId = item.id
                }
            }

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
                    } else if (count == 1 && totalLen - len == 0) {
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

            if (item.completed) binding.tvTitle.paintFlags =
                Paint.ANTI_ALIAS_FLAG or Paint.STRIKE_THRU_TEXT_FLAG
            else binding.tvTitle.paintFlags = Paint.ANTI_ALIAS_FLAG

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
                    paintFlags =
                        if (item.subTodos[i].completed) Paint.ANTI_ALIAS_FLAG or Paint.STRIKE_THRU_TEXT_FLAG else Paint.ANTI_ALIAS_FLAG
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

    inner class EmptyViewHolder(val binding: ChecklistEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Todo) {
            binding.tvTodoEmpty.text = item.content
            binding.tvTodoEmpty.visibility = if (item.visibility) View.VISIBLE else View.GONE
            binding.tvTodoEmpty.layoutParams = if (item.visibility) LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) else LinearLayout.LayoutParams(0, 0)
            if (!item.visibility)
                return
        }
    }

    fun setDataList(dataList: List<Todo>) {
        data = dataList as MutableList<Todo>
        diffUtil.submitList(dataList)
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

    override fun onItemMove(formPosition: Int, toPosition: Int): Boolean {
        for (i in formPosition downTo 0)
            if (diffUtil.currentList[i].type in listOf(0, 1, 4)) {
                dragLimitTop = i
                break
            }

        for (i in formPosition until diffUtil.currentList.size)
            if (diffUtil.currentList[i].type == 3) {
                dragLimitBottom = i
                break
            }

        if ((toPosition > dragLimitTop!!) && (toPosition < dragLimitBottom!!)) {
            val list = mutableListOf<Todo>()
            list.addAll(diffUtil.currentList)
            Collections.swap(list, formPosition, toPosition)
            setDataList(list)
            return true
        }
        return false

    }

}
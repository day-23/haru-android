package com.example.haru.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Tag
import com.example.haru.databinding.FragmentChecklistTagBinding
import com.example.haru.databinding.FragmentChecklistTagHeaderBinding

class TagAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var clickedPosition : Int? = null

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

        return when (viewType) {
            HEADER -> HeaderViewHolder(
                FragmentChecklistTagHeaderBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            ITEM -> TagItemViewHolder(
                FragmentChecklistTagBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
            )

            else -> {
                throw ClassCastException("Unknown viewType $viewType")
            }
        }
    }

    override fun getItemCount(): Int = data.count() + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                if (tagClick != null) {
                    holder.binding.tagHeader.setOnClickListener {
                        tagClick?.onClick(it, position)

                        if (clickedPosition != null){
                            data[clickedPosition!! - 1].clicked = false
                            notifyItemChanged(clickedPosition!!)
                        }
                    }
                }
            }
            is TagItemViewHolder -> {
                holder.bind(data[position - 1])
                if (tagClick != null) {
                    holder.binding.tagBtn.setOnClickListener {
                        tagClick?.onClick(it, position)

                        if (clickedPosition != null){
                            data[clickedPosition!! - 1].clicked = false
                            notifyItemChanged(clickedPosition!!)
                        }

                        setTagPosition(position)
                        data[position - 1].clicked = true
                        holder.binding.tagBtn.background =
                            ContextCompat.getDrawable(context, R.drawable.tag_btn_clicked)
                        holder.binding.tagBtn.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }
            }
        }

    }

    inner class TagItemViewHolder(val binding: FragmentChecklistTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Tag) {
            binding.tag = item

            binding.tagLayout.layoutParams = if (item.isSelected) LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ) else LinearLayout.LayoutParams(0, 0)

            binding.tagLayout.visibility = if (!item.isSelected) View.GONE else View.VISIBLE

            if (!item.isSelected)
                return


            val drawable: Drawable?
            val color: Int

            if (item.clicked) {
                drawable = ContextCompat.getDrawable(context, R.drawable.tag_btn_clicked)
                color = ContextCompat.getColor(context, R.color.white)
            } else {
                drawable = ContextCompat.getDrawable(context, R.drawable.tag_btn_custom)
                color = ContextCompat.getColor(context, R.color.todo_description)
            }
            binding.tagBtn.background = drawable
            binding.tagBtn.setTextColor(color)
        }
    }

    inner class HeaderViewHolder(val binding: FragmentChecklistTagHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    fun setTagPosition(position : Int?){
        clickedPosition = position
    }
    fun setDataList(dataList: List<Tag>) {
        this.data = dataList
        notifyDataSetChanged()
    }
}
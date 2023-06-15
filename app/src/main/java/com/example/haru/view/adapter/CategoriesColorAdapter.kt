package com.example.haru.view.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.view.calendar.CategoryAddActivity
import com.example.haru.view.calendar.CategoryCorrectionActivity

class CategoriesColorAdapter(initIndex: Int = -1, val callback : (String) -> Unit): RecyclerView.Adapter<CategoriesColorAdapter.ColorsView>(){

    var lastChooseIndex = initIndex
    val colorsList = listOf(
        "#2E2E2E", "#656565", "#818181", "#9D9D9D", "#B9B9B9", "#D5D5D5",
        "#FF0959", "#FF509C", "#FF5AB6", "#FE7DCD", "#FFAAE5", "#FFBDFB",
        "#B237BB", "#C93DEB", "#B34CED", "#9D5BE3", "#BB93F8", "#C6B2FF",
        "#4C45FF", "#2E57FF", "#4D8DFF", "#45BDFF", "#6DDDFF", "#65F4FF",
        "#FE7E7E", "#FF572E", "#C22E2E", "#A07553", "#E3942E", "#E8A753",
        "#FF892E", "#FFAB4C", "#FFD166", "#FFDE2E", "#CFE855", "#B9D32E",
        "#105C08", "#39972E", "#3EDB67", "#55E1B6", "#69FFD0", "#05C5C0",
    )

    inner class ColorsView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesColorAdapter.ColorsView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_colors,
            parent,
            false
        )

        return ColorsView(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CategoriesColorAdapter.ColorsView, position: Int) {
        val categoryImage = holder.itemView.findViewById<FrameLayout>(R.id.color_item_layout)
        val categoryOutsideLayout = holder.itemView.findViewById<FrameLayout>(R.id.color_item_outside_layout)

        val drawable = categoryImage.background as VectorDrawable
        drawable.setColorFilter(Color.parseColor(colorsList[position]), PorterDuff.Mode.SRC_ATOP)

        if(lastChooseIndex == position){
            categoryOutsideLayout.setBackgroundResource(R.drawable.category_choose_image)
        } else {
            categoryOutsideLayout.setBackgroundResource(com.kakao.sdk.friend.R.color.transparent)
        }

        categoryImage.setOnClickListener {
            callback(colorsList[position])

            if(lastChooseIndex != -1){
                notifyItemChanged(lastChooseIndex)
                notifyItemChanged(position)
                lastChooseIndex = position
            } else {
                notifyItemChanged(position)
                lastChooseIndex = position
            }
        }
    }

    override fun getItemCount(): Int {
        return 42
    }
}
package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category

class CategoriesAdapterInPost(val categories: List<Category?>, val listener: (Category) -> Unit) : RecyclerView.Adapter<CategoriesAdapterInPost.CategoryView>() {

    private var plusIndex = 0
    private var chooseIndex = -1

    inner class CategoryView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesAdapterInPost.CategoryView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_categories_choose,
            parent,
            false
        )

        return CategoryView(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CategoriesAdapterInPost.CategoryView, @SuppressLint("RecyclerView") position: Int) {
        while(categories[position+plusIndex] == null){
            plusIndex++
        }

        if(categories[position+plusIndex] != null) {
            val newposition = position + plusIndex

            val categoryColorPost =
                holder.itemView.findViewById<ImageView>(R.id.category_Color_post)
            val categoryColorOutsidePost =
                holder.itemView.findViewById<ImageView>(R.id.category_Color_outside_post)
            val categoryNamePost = holder.itemView.findViewById<TextView>(R.id.category_name_post)

            categoryColorPost.setColorFilter(
                Color.parseColor(categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            val drawable = categoryColorPost.background as VectorDrawable

            if (chooseIndex == position) {
                drawable.setColorFilter(
                    Color.parseColor(categories[newposition]!!.color),
                    PorterDuff.Mode.SRC_ATOP
                )

                categoryNamePost.setTextColor(Color.parseColor("#191919"))
            } else {
                drawable.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_ATOP
                )

                categoryNamePost.setTextColor(Color.parseColor("#ACACAC"))
            }

            categoryColorPost.setOnClickListener {
                val lastIndex = chooseIndex

                chooseIndex = position

                if (lastIndex != -1) {
                    notifyItemChanged(lastIndex)
                }

                notifyItemChanged(chooseIndex)

                listener(categories[newposition]!!)
            }

            categoryColorOutsidePost.setColorFilter(
                Color.parseColor(categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            val drawable2 = categoryColorOutsidePost.background as VectorDrawable
            drawable2.setColorFilter(
                Color.parseColor(categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            categoryNamePost.text = categories[newposition]!!.content
        }
    }

    override fun getItemCount(): Int {
        var size = categories.size
        if(categories.contains(null)){
            size -= 1
        }
        return size
    }
}
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
import com.example.haru.utils.User

class CategoriesAdapterInPost(val listener: (Category) -> Unit) : RecyclerView.Adapter<CategoriesAdapterInPost.CategoryView>() {

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
        Log.d("categories", User.categories.toString())
        while(User.categories[position+plusIndex] == null){
            plusIndex++
        }

        if(User.categories[position+plusIndex] != null) {
            val newposition = position + plusIndex

            val categoryColorPost =
                holder.itemView.findViewById<ImageView>(R.id.category_Color_post)
            val categoryColorOutsidePost =
                holder.itemView.findViewById<ImageView>(R.id.category_Color_outside_post)
            val categoryNamePost = holder.itemView.findViewById<TextView>(R.id.category_name_post)

            categoryColorPost.setColorFilter(
                Color.parseColor(User.categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            val drawable = categoryColorPost.background as VectorDrawable

            if (chooseIndex == position) {
                drawable.setColorFilter(
                    Color.parseColor(User.categories[newposition]!!.color),
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

                listener(User.categories[newposition]!!)
            }

            categoryColorOutsidePost.setColorFilter(
                Color.parseColor(User.categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            val drawable2 = categoryColorOutsidePost.background as VectorDrawable
            drawable2.setColorFilter(
                Color.parseColor(User.categories[newposition]!!.color),
                PorterDuff.Mode.SRC_ATOP
            )

            categoryNamePost.text = User.categories[newposition]!!.content
        }
    }

    override fun getItemCount(): Int {
        var size = User.categories.size - 2
        return size
    }
}
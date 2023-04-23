package com.example.haru.view.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.view.calendar.CategoryCorrectionActivity

class CategoryAdapter(categoryList: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryView>(){
    var content = categoryList

    inner class CategoryView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.CategoryView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_categories,
            parent,
            false
        )

        return CategoryView(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CategoryAdapter.CategoryView, position: Int) {
        val categoryColor = holder.itemView.findViewById<ImageView>(R.id.category_Color)
        val cateogryName = holder.itemView.findViewById<TextView>(R.id.category_name)
        val cateogryCorrection = holder.itemView.findViewById<ImageView>(R.id.category_correction)

        val drawable = categoryColor.background as VectorDrawable
        drawable.setColorFilter(Color.parseColor(content[position].color),PorterDuff.Mode.SRC_ATOP)
//        drawable.setColor(Color.parseColor(content[position].color))

        cateogryName.text = content[position].content

        cateogryCorrection.setOnClickListener {
            val intent = Intent(holder.itemView.context, CategoryCorrectionActivity::class.java)
            val bundle = Bundle()
            intent.putExtra("category", content[position])
            intent.putExtras(bundle)
            startActivity(holder.itemView.context,intent,bundle)
        }
    }

    override fun getItemCount(): Int {
        return content.size
    }
}
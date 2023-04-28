package com.example.haru.view.adapter

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.model.Schedule
import com.example.haru.view.calendar.CategoryCorrectionActivity
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.viewmodel.CalendarViewModel
import java.util.Vector

class CategoryAdapter(val categoryList: ArrayList<Category>, private val onItemClicked: (Category, Int) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryView>() {

    inner class CategoryView(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun dataChanged(index: Int, category: Category){
        categoryList[index] = category
        notifyItemChanged(index)
    }

    fun dataDelete(index: Int){
        categoryList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeRemoved(index, 1)
    }

    fun dataAdd(category: Category){
        categoryList.add(category)
        notifyItemInserted(categoryList.size-1)
    }

    fun dataAllChanged(){
        notifyDataSetChanged()
    }

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
        val categoryColorOutside = holder.itemView.findViewById<ImageView>(R.id.category_Color_outside)
        val cateogryName = holder.itemView.findViewById<TextView>(R.id.category_name)
        val cateogryCorrection = holder.itemView.findViewById<ImageView>(R.id.category_correction)

        val drawable = categoryColor.background as VectorDrawable

        if(categoryList[position].isSelected) {
            if(calendarMainData.scheduleApply) {
                drawable.setColorFilter(
                    Color.parseColor(categoryList[position].color),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else {
                drawable.setColorFilter(
                    Color.parseColor("#BABABA"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        } else {
            drawable.setColorFilter(
                Color.parseColor("#FFFFFF"),
                PorterDuff.Mode.SRC_ATOP
            )
        }

        categoryColor.setOnClickListener {
            if(calendarMainData.scheduleApply) {
                val calendarViewModel = CalendarViewModel()

                categoryList[position].isSelected = !categoryList[position].isSelected
                calendarViewModel.updateCategory(categoryList[position])

                notifyItemChanged(position)
            }
        }

        val drawable2 = categoryColorOutside.background as VectorDrawable

        if(!calendarMainData.scheduleApply){
            drawable2.setColorFilter(
                Color.parseColor("#BABABA"),
                PorterDuff.Mode.SRC_ATOP
            )

            cateogryName.setTextColor(Color.parseColor("#BABABA"))
        } else {
            drawable2.setColorFilter(
                Color.parseColor(categoryList[position].color),
                PorterDuff.Mode.SRC_ATOP
            )

            cateogryName.setTextColor(Color.parseColor("#191919"))
        }

        cateogryName.text = categoryList[position].content

        cateogryCorrection.setOnClickListener {
            onItemClicked(categoryList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
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
import android.media.Image
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
import com.example.haru.data.model.CategoriesUpdate
import com.example.haru.data.model.Category
import com.example.haru.data.model.Schedule
import com.example.haru.view.calendar.CategoryCorrectionActivity
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.viewmodel.CalendarViewModel
import java.util.Vector

class CategoryAdapter(val categoryList: ArrayList<Category?>, private val onItemClicked: (Category, Int) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryView>() {

    inner class CategoryView(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun dataAllBlind(){
        val calendarViewModel = CalendarViewModel()
        val ids = ArrayList<String>()
        val selected = ArrayList<Boolean>()

        for(i in 2 until categoryList.size){
            if(categoryList[i]!!.isSelected){
                categoryList[i]!!.isSelected = false
                ids.add(categoryList[i]!!.id)
                selected.add(false)
            }
        }

        calendarViewModel.selectedCategories(
            CategoriesUpdate(
                ids,selected
            )
        )

        notifyDataSetChanged()
    }

    fun dataAllVisible(){
        val calendarViewModel = CalendarViewModel()
        val ids = ArrayList<String>()
        val selected = ArrayList<Boolean>()

        for(i in 2 until categoryList.size){
            if(!categoryList[i]!!.isSelected){
                categoryList[i]!!.isSelected = true
                ids.add(categoryList[i]!!.id)
                selected.add(true)
            }
        }

        calendarViewModel.selectedCategories(
            CategoriesUpdate(
                ids,selected
            )
        )

        notifyDataSetChanged()
    }

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
        val cateogryName = holder.itemView.findViewById<TextView>(R.id.category_name)
        val cateogryCorrection = holder.itemView.findViewById<ImageView>(R.id.category_correction)
        val categoryShowIv = holder.itemView.findViewById<ImageView>(R.id.category_show_iv)

        val drawable = categoryColor.background as VectorDrawable
        if (categoryList[position] != null) {
            if (categoryList[position]!!.isSelected) {
                if (calendarMainData.scheduleApply) {
                    drawable.setColorFilter(
                        Color.parseColor(categoryList[position]!!.color),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    cateogryName.setTextColor(Color.parseColor("#191919"))

                    categoryShowIv.setBackgroundResource(R.drawable.category_show_image)
                } else {
                    drawable.setColorFilter(
                        Color.parseColor("#ACACAC"),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                    categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                }
            } else {
                drawable.setColorFilter(
                    Color.parseColor("#ACACAC"),
                    PorterDuff.Mode.SRC_ATOP
                )

                cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
            }

            categoryShowIv.setOnClickListener {
                if (calendarMainData.scheduleApply) {
                    val calendarViewModel = CalendarViewModel()

                    categoryList[position]!!.isSelected = !categoryList[position]!!.isSelected
                    calendarViewModel.updateCategory(categoryList[position]!!)

                    notifyItemChanged(position)
                }
            }

            cateogryName.text = categoryList[position]!!.content

            cateogryCorrection.setOnClickListener {
                onItemClicked(categoryList[position]!!, position)
            }
        } else {
            cateogryCorrection.visibility = View.INVISIBLE

            if (position == 0) {
                if (calendarMainData.unclassifiedCategory) {
                    if (calendarMainData.scheduleApply) {
                        drawable.setColorFilter(
                            Color.parseColor("#AAD7FF"),
                            PorterDuff.Mode.SRC_ATOP
                        )

                        cateogryName.setTextColor(Color.parseColor("#191919"))

                        categoryShowIv.setBackgroundResource(R.drawable.category_show_image)
                    } else {
                        drawable.setColorFilter(
                            Color.parseColor("#ACACAC"),
                            PorterDuff.Mode.SRC_ATOP
                        )

                        cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                        categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                    }
                } else {
                    drawable.setColorFilter(
                        Color.parseColor("#ACACAC"),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                    categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                }

                categoryShowIv.setOnClickListener {
                    if (calendarMainData.scheduleApply) {
                        calendarMainData.unclassifiedCategory =
                            !calendarMainData.unclassifiedCategory

                        notifyItemChanged(position)
                    }
                }

                cateogryName.text = "미분류"
            } else {
                if (calendarMainData.holidayCategory) {
                    if (calendarMainData.scheduleApply) {
                        drawable.setColorFilter(
                            Color.parseColor("#F71E58"),
                            PorterDuff.Mode.SRC_ATOP
                        )

                        cateogryName.setTextColor(Color.parseColor("#191919"))

                        categoryShowIv.setBackgroundResource(R.drawable.category_show_image)
                    } else {
                        drawable.setColorFilter(
                            Color.parseColor("#ACACAC"),
                            PorterDuff.Mode.SRC_ATOP
                        )

                        cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                        categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                    }
                } else {
                    drawable.setColorFilter(
                        Color.parseColor("#ACACAC"),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    cateogryName.setTextColor(Color.parseColor("#ACACAC"))

                    categoryShowIv.setBackgroundResource(R.drawable.category_unshow_image)
                }

                categoryShowIv.setOnClickListener {
                    if (calendarMainData.scheduleApply) {
                        calendarMainData.holidayCategory =
                            !calendarMainData.holidayCategory

                        notifyItemChanged(position)
                    }
                }

                cateogryName.text = "공휴일"
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}
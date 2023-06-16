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
import com.example.haru.utils.User
import com.example.haru.view.calendar.CategoryCorrectionActivity
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.viewmodel.CalendarViewModel
import java.util.Vector

class CategoryAdapter(private val onItemClicked: (Category, Int) -> Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryView>() {

    inner class CategoryView(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun dataAllBlind(){
        val calendarViewModel = CalendarViewModel()
        val ids = ArrayList<String>()
        val selected = ArrayList<Boolean>()

        for(i in 2 until User.categories.size){
            if(User.categories[i]!!.isSelected){
                User.categories[i]!!.isSelected = false
                ids.add(User.categories[i]!!.id)
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

        for(i in 2 until User.categories.size){
            if(!User.categories[i]!!.isSelected){
                User.categories[i]!!.isSelected = true
                ids.add(User.categories[i]!!.id)
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
        User.categories[index] = category
        notifyItemChanged(index)
    }

    fun dataDelete(index: Int){
        User.categories.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeRemoved(index, 1)
    }

    fun dataAdd(category: Category){
        User.categories.add(category)
        notifyItemInserted(User.categories.size-1)
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
        if (User.categories[position] != null) {
            if (User.categories[position]!!.isSelected) {
                if (calendarMainData.scheduleApply) {
                    drawable.setColorFilter(
                        Color.parseColor(User.categories[position]!!.color),
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

                    User.categories[position]!!.isSelected = !User.categories[position]!!.isSelected
                    calendarViewModel.updateCategory(User.categories[position]!!)

                    notifyItemChanged(position)
                }
            }

            cateogryName.text = User.categories[position]!!.content

            cateogryCorrection.setOnClickListener {
                onItemClicked(User.categories[position]!!, position)
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
        return User.categories.size
    }
}
package com.example.haru.view.calendar

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category

class CategoryCorrectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_correction)

        val category = intent.getSerializableExtra("category") as Category
        val choiseCategoryColor = findViewById<ImageView>(R.id.choise_category_color)
        val correctionScheduleName = findViewById<TextView>(R.id.correction_schedule_name)
        val correctionSwitch = findViewById<Switch>(R.id.correction_switch)
        val categoriesRecyclerview = findViewById<RecyclerView>(R.id.categories_recyclerview)
        val correctionBackImageview = findViewById<ImageView>(R.id.correction_back_imageView)
        val correctionCheckImageview = findViewById<ImageView>(R.id.correction_check_imageView)

        correctionScheduleName.text = category.content
        correctionSwitch.isChecked = category.isSelected

        val drawable = choiseCategoryColor.background as VectorDrawable

        drawable.setColorFilter(Color.parseColor(category.color), PorterDuff.Mode.SRC_ATOP)

        categoriesRecyclerview.layoutManager = GridLayoutManager(this,6)

        correctionBackImageview.setOnClickListener {
            finish()
        }

        correctionCheckImageview.setOnClickListener {
            finish()
        }
    }
}
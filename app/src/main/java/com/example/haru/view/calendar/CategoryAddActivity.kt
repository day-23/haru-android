package com.example.haru.view.calendar

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.view.adapter.CategoriesColorAdapter
import com.example.haru.viewmodel.CalendarViewModel

class CategoryAddActivity : AppCompatActivity() {
    private var content: String = ""
    private var color: String = ""

    fun changeColor(newColor:String){
        val choiseCategoryColor = findViewById<ImageView>(R.id.add_choise_category_color)
        val drawable = choiseCategoryColor.background as VectorDrawable

        color = newColor

        drawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_add)

        val addChoiseCategoryColor = findViewById<ImageView>(R.id.add_choise_category_color)
        val addScheduleName = findViewById<EditText>(R.id.add_schedule_name)
        val addCategoriesRecyclerview = findViewById<RecyclerView>(R.id.add_categories_recyclerview)
        val addBackImageview = findViewById<ImageView>(R.id.add_back_imageView)
        val addCheckImageview = findViewById<ImageView>(R.id.add_check_imageView)

        val drawable = addChoiseCategoryColor.background as VectorDrawable

        drawable.setColorFilter(Color.parseColor("#BBE7FF"), PorterDuff.Mode.SRC_ATOP)

        addCategoriesRecyclerview.layoutManager = GridLayoutManager(this,6)
        addCategoriesRecyclerview.addItemDecoration(MyItemDecoration())
        addCategoriesRecyclerview.adapter = CategoriesColorAdapter(null, this)

        addBackImageview.setOnClickListener {
            finish()
        }

        addCheckImageview.setOnClickListener {
            content = addScheduleName.text.toString()

            val calendarviewmodel = CalendarViewModel()
            calendarviewmodel.postCategory(content, color){
                val intent = this.intent
                intent.putExtra("status", "post")
                intent.putExtra("category2", it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
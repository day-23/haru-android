package com.example.haru.view.calendar

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.data.repository.CategoryRepository
import com.example.haru.view.adapter.CategoriesColorAdapter
import com.example.haru.viewmodel.CalendarViewModel

class CategoryCorrectionActivity : AppCompatActivity() {
    private var content: String = ""
    private var color: String? = ""
    private var isSelected: Boolean = false

    fun changeColor(newColor:String){
        val choiseCategoryColor = findViewById<ImageView>(R.id.choise_category_color)
        val drawable = choiseCategoryColor.background as VectorDrawable

        color = newColor

        drawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_correction)

        var category = intent.getSerializableExtra("category") as Category
        val index = intent.getSerializableExtra("index") as Int

        val choiseCategoryColor = findViewById<ImageView>(R.id.choise_category_color)
        val correctionScheduleName = findViewById<EditText>(R.id.correction_schedule_name)
        val correctionSwitch = findViewById<SwitchCompat>(R.id.correction_event_alarm_switch)
        val categoriesRecyclerview = findViewById<RecyclerView>(R.id.categories_recyclerview)
        val correctionBackImageview = findViewById<ImageView>(R.id.correction_back_imageView)
        val correctionCheckImageview = findViewById<ImageView>(R.id.correction_check_imageView)
        val categoryDeleteTv = findViewById<TextView>(R.id.category_delete_tv)

        content = category.content
        correctionScheduleName.setText(content)

        isSelected = category.isSelected
        correctionSwitch.isChecked = isSelected

        val drawable = choiseCategoryColor.background as VectorDrawable

        color = category.color

        if(color != null) {
            drawable.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP)
        } else {
            drawable.setColorFilter(Color.parseColor("#BBE7FF"), PorterDuff.Mode.SRC_ATOP)
        }

        categoriesRecyclerview.layoutManager = GridLayoutManager(this,6)
        categoriesRecyclerview.addItemDecoration(MyItemDecoration())
        categoriesRecyclerview.adapter = CategoriesColorAdapter(this)

        correctionBackImageview.setOnClickListener {
            intent.putExtra("status", "back")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        correctionCheckImageview.setOnClickListener {
            content = correctionScheduleName.text.toString()
            isSelected = correctionSwitch.isSelected

            category.color = color
            category.content = content
            category.isSelected = isSelected

            val calendarviewmodel = CalendarViewModel()

            calendarviewmodel.updateCategory(category)

            val intent = this.intent
            intent.putExtra("status", "update")
            intent.putExtra("index", index)
            intent.putExtra("category2", category)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        categoryDeleteTv.setOnClickListener{
            val calendarviewmodel = CalendarViewModel()
            calendarviewmodel.deleteCategory(category.id)

            val intent = this.intent
            intent.putExtra("status", "delete")
            intent.putExtra("index", index)
            intent.putExtra("category2", category)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
package com.example.haru.view.calendar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContentProviderCompat.requireContext
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

    val colorsList = listOf(
        "#2E2E2E", "#656565", "#818181", "#9D9D9D", "#B9B9B9", "#D5D5D5",
        "#FF0959", "#FF509C", "#FF5AB6", "#FE7DCD", "#FFAAE5", "#FFBDFB",
        "#B237BB", "#C93DEB", "#B34CED", "#9D5BE3", "#BB93F8", "#C6B2FF",
        "#4C45FF", "#2E57FF", "#4D8DFF", "#45BDFF", "#6DDDFF", "#65F4FF",
        "#FE7E7E", "#FF572E", "#C22E2E", "#A07553", "#E3942E", "#E8A753",
        "#FF892E", "#FFAB4C", "#FFD166", "#FFDE2E", "#CFE855", "#B9D32E",
        "#105C08", "#39972E", "#3EDB67", "#55E1B6", "#69FFD0", "#05C5C0",
    )

    fun changeColor(newColor:String){
        color = newColor
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText){
            currentFocus!!.clearFocus()
            return false
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        intent.putExtra("status", "back")
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_correction)

        var category = intent.getSerializableExtra("category") as Category
        val index = intent.getSerializableExtra("index") as Int

        val correctionScheduleName = findViewById<EditText>(R.id.correction_schedule_name)
        val categoriesRecyclerview = findViewById<RecyclerView>(R.id.categories_recyclerview)
        val correctionBackImageview = findViewById<ImageView>(R.id.correction_back_imageView)
        val correctionCheckImageview = findViewById<ImageView>(R.id.correction_check_imageView)
        val categoryDeleteTv = findViewById<TextView>(R.id.category_delete_tv)

        content = category.content
        correctionScheduleName.setText(content)

        categoriesRecyclerview.layoutManager = GridLayoutManager(this,6)
        categoriesRecyclerview.addItemDecoration(MyItemDecoration())
        categoriesRecyclerview.adapter = CategoriesColorAdapter(this, null, colorsList.indexOf(category.color))

        correctionBackImageview.setOnClickListener {
            intent.putExtra("status", "back")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        correctionCheckImageview.setOnClickListener {
            if(correctionScheduleName.text.toString().replace(" ", "") == ""){
                Toast.makeText(this, "카테고리명을 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            content = correctionScheduleName.text.toString()

            category.color = color
            category.content = content

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
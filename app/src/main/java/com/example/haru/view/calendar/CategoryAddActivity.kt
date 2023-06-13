package com.example.haru.view.calendar

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        intent.putExtra("status", "back")
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_add)

        val addScheduleName = findViewById<EditText>(R.id.add_schedule_name)
        val addCategoriesRecyclerview = findViewById<RecyclerView>(R.id.add_categories_recyclerview)
        val addBackImageview = findViewById<ImageView>(R.id.add_back_imageView)
        val addCheckImageview = findViewById<ImageView>(R.id.add_check_imageView)

        addCategoriesRecyclerview.layoutManager = GridLayoutManager(this,6)
        addCategoriesRecyclerview.adapter = CategoriesColorAdapter(null, this)

        addBackImageview.setOnClickListener {
            intent.putExtra("status", "back")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        addScheduleName.addTextChangedListener {
            if (it.toString().length > 8) {
                addScheduleName.setText(it.toString().substring(0, 8))
                addScheduleName.setSelection(8)
            }
        }

        addCheckImageview.setOnClickListener {
            if(addScheduleName.text.toString().replace(" ", "") == ""){
                Toast.makeText(this, "카테고리명을 입력해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if(color == ""){
                Toast.makeText(this, "색상을 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
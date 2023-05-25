package com.example.haru.view.calendar

import android.app.Dialog
import android.util.Log
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haru.data.model.Category
import com.example.haru.databinding.CategoryChooseDialogBinding
import com.example.haru.view.adapter.CategoriesAdapterInPost
import com.example.haru.view.checklist.CalendarAddFragment

class CategoryChooseDialog (private val context : CalendarAddFragment?, private val context2 : CalendarItemFragment?=null){

    fun show(categories: List<Category?>, listener: (Category) -> Unit) {
        if(context != null) {
            val dlg = Dialog(context.requireContext())
            val binding = CategoryChooseDialogBinding.inflate(context.layoutInflater)

            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dlg.setContentView(binding.root)

            binding.categoriesChooseRecyclerview.layoutManager = LinearLayoutManager(context.requireContext())
            binding.categoriesChooseRecyclerview.adapter = CategoriesAdapterInPost(categories){
                listener(it)
            }

            dlg.setCancelable(true)

            dlg.show()
        } else if(context2 != null){
            val dlg = Dialog(context2.requireContext())
            val binding = CategoryChooseDialogBinding.inflate(context2.layoutInflater)

            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dlg.setContentView(binding.root)

            binding.categoriesChooseRecyclerview.layoutManager = LinearLayoutManager(context2.requireContext())
            Log.d("20191630",categories.toString())

            binding.categoriesChooseRecyclerview.adapter = CategoriesAdapterInPost(categories){
                listener(it)
            }

            dlg.setCancelable(true)

            dlg.show()
        }
    }
}
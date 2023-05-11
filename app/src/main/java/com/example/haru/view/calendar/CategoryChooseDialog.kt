package com.example.haru.view.calendar

import android.app.Dialog
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haru.data.model.Category
import com.example.haru.databinding.CategoryChooseDialogBinding
import com.example.haru.view.adapter.CategoriesAdapterInPost
import com.example.haru.view.checklist.CalendarAddFragment

class CategoryChooseDialog (private val context : CalendarAddFragment){
    private lateinit var binding : CategoryChooseDialogBinding
    private val dlg = Dialog(context.requireContext())   //부모 액티비티의 context 가 들어감

    fun show(categories: List<Category?>, listener: (Category) -> Unit) {
        binding = CategoryChooseDialogBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)

        binding.categoriesChooseRecyclerview.layoutManager = LinearLayoutManager(context.requireContext())
        binding.categoriesChooseRecyclerview.adapter = CategoriesAdapterInPost(categories){
            listener(it)
        }

        //ok 버튼 동작
//        binding.ok.setOnClickListener {
//
//            //TODO: 부모 액티비티로 내용을 돌려주기 위해 작성할 코드
//
//            dlg.dismiss()
//        }
//
//        //cancel 버튼 동작
//        binding.cancel.setOnClickListener {
//            dlg.dismiss()
//        }

        dlg.setCancelable(true)

        dlg.show()
    }
}
package com.example.haru.view.calendar

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.haru.data.model.Category
import com.example.haru.databinding.CategoryChooseDialogBinding
import com.example.haru.databinding.CustomTimePickerBinding
import com.example.haru.view.adapter.CategoriesAdapterInPost
import com.example.haru.view.checklist.CalendarAddFragment

class CategoryChooseDialog (private val context : CalendarAddFragment?,
                            private val context2 : CalendarItemFragment?=null,
                            private val categories: List<Category?>,
                            private val listener: (Category) -> Unit
): DialogFragment(){
    private lateinit var binding: CategoryChooseDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CategoryChooseDialogBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width = 0.7f
        val height = 0.65f
        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(context != null) {
//            val dlg = Dialog(context.requireContext())
//            val binding = CategoryChooseDialogBinding.inflate(context.layoutInflater)
//
//            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dlg.setContentView(binding.root)

            binding.categoriesChooseRecyclerview.layoutManager = LinearLayoutManager(context.requireContext())
            binding.categoriesChooseRecyclerview.adapter = CategoriesAdapterInPost(categories){
                listener(it)
            }
//
//            dlg.setCancelable(true)
//
//            dlg.show()
        } else if(context2 != null){
//            val dlg = Dialog(context2.requireContext())
//            val binding = CategoryChooseDialogBinding.inflate(context2.layoutInflater)

//            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dlg.setContentView(binding.root)

            binding.categoriesChooseRecyclerview.layoutManager = LinearLayoutManager(context2.requireContext())

            binding.categoriesChooseRecyclerview.adapter = CategoriesAdapterInPost(categories){
                listener(it)
            }

//            dlg.setCancelable(true)
//
//            dlg.show()
        }
    }
}
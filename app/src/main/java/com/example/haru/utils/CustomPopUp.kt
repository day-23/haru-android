package com.example.haru.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.haru.R
import com.example.haru.databinding.CustomPopupWindowBinding

class CustomPopUp(
    val context: Context,
    val popupList: MutableList<String>,
    val selected: String? = null,
    private val menuItemListener: (View?, String, Int) -> Unit,
) : PopupWindow(context) {

    init {
        protectPopup()

    }

    private fun protectPopup() {
        val inflater = LayoutInflater.from(context)

        val binding = CustomPopupWindowBinding.inflate(inflater)

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in 0 until popupList.size) {
            val addView =
                layoutInflater.inflate(R.layout.custom_popup_item, null)

            val tvMenuTitle = addView.findViewById(R.id.tv_item_title) as TextView
            val divider = addView.findViewById(R.id.popup_divider) as View

            tvMenuTitle.text = popupList[i]
            if (selected == popupList[i])
                tvMenuTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.highlight
                    )
                )

            divider.visibility = if (i < popupList.size - 1) View.VISIBLE else View.INVISIBLE

            addView.setOnClickListener {
                menuItemListener.invoke(it, popupList[i], i)
                dismiss()
            }

            binding.popupParentLayout.addView(addView)
        }
        contentView = binding.root
        elevation = 10f
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.shape_rect_radius_10))

//        val layout = contentView
//        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        height = layout.measuredHeight

    }
}
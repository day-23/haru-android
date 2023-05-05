package com.example.haru.view.calendar

import android.graphics.Rect
import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class TouchEventDecoration(val index: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val totalHeight = parent.measuredHeight
        val itemHeight = totalHeight / index

        view.updateLayoutParams {
            height = itemHeight
        }
    }
}
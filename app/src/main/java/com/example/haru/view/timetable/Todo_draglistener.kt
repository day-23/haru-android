package com.example.haru.view.timetable

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Todo


class Todo_draglistener () : View.OnDragListener {
    override fun onDrag(view: View, event: DragEvent): Boolean {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)

        val viewSource = event.localState as View
        var targetRecyclerView: RecyclerView
        val sourceRecyclerView: RecyclerView = viewSource.parent.parent as RecyclerView

        if (event.action == DragEvent.ACTION_DROP) {
            val viewId = view.id

            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            sourceRecyclerView.setBackgroundColor(Color.TRANSPARENT)
            if (targetRecyclerView.id != sourceRecyclerView.id) {
                val sourceAdapter = sourceRecyclerView.adapter as TodotableAdapter?
                val sourcePosition = sourceRecyclerView.getChildAdapterPosition(viewSource.parent as View)
                val targetAdapter = targetRecyclerView.adapter as TodotableAdapter?

                var sourceList: ArrayList<Todo> = ArrayList()
                sourceAdapter?.getItems()?.let { sourceList = it }
                val item = sourceList[sourcePosition]
                sourceAdapter?.removeItem(sourcePosition)

                var targetList: ArrayList<Todo> = ArrayList()
                targetAdapter?.setItem(item)
                targetRecyclerView.setBackgroundColor(Color.TRANSPARENT)

            }
            return true
        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED){
            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            targetRecyclerView.setBackgroundColor(Color.parseColor("#4D7f7f7f"))
            return true
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED){
            try {
                targetRecyclerView = view.parent.parent as RecyclerView
            }
            catch (e: java.lang.ClassCastException){
                targetRecyclerView = view as RecyclerView
            }
            targetRecyclerView.setBackgroundColor(Color.TRANSPARENT)
        }
        return true
    }
}
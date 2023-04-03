package com.example.haru.view.timetable

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Todo


class ScheduleDraglistener () : View.OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {

        val viewSource = event.localState as View
        var targetFramelayout: FrameLayout
        //val sourceFrameLayout: FrameLayout = viewSource.parent as FrameLayout

        if (event.action == DragEvent.ACTION_DROP) {

        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED) {
            try {
                targetFramelayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFramelayout = view.parent.parent as FrameLayout
            }

            // Get the drop point
            val x = event.x
            val y = event.y

            // Get the dragged view
            val draggedView = event.localState as View

            // Calculate the drop position
            val dropX = x - draggedView.width / 2
            val dropY = y - draggedView.height / 2

            // Set the position of the dragged view
            draggedView.x = dropX
            draggedView.y = dropY

            // Add the dragged view to the FrameLayout
            targetFramelayout.addView(draggedView)

            true
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {


        }
        return true
    }
}

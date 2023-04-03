package com.example.haru.view.timetable

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Todo
import com.example.haru.viewmodel.TimetableViewModel


class ScheduleDraglistener (private val timetableViewModel: TimetableViewModel, layoutIndex: ArrayList<Int>) : View.OnDragListener{
    val layoutIndex = layoutIndex
    override fun onDrag(view: View, event: DragEvent): Boolean {
        val viewSource = event.localState as View
        var targetFramelayout: FrameLayout

        if (event.action == DragEvent.ACTION_DROP) {
            val draggedView = event.localState as View
            try {
                targetFramelayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFramelayout = view.parent.parent as FrameLayout
            }
            val displayMetrics = targetFramelayout.resources.displayMetrics
            val x = event.x
            val y = ((event.y / displayMetrics.density).toInt() / 10) * 10

            val dropY = Math.round( y * displayMetrics.density)
            draggedView.y = dropY.toFloat()
            val SourceLayout = draggedView.parent as LinearLayout
            Log.d("DRAGGED", "${targetFramelayout.id} , $y")

            var index = -1
            for(i : Int in 0..6){
                if(targetFramelayout.id == layoutIndex[i]){
                    index = i
                    break
                }
            }

            timetableViewModel.scheduleMoved(y, draggedView, index)
//            SourceLayout.removeView(draggedView)
//            targetFramelayout.addView(draggedView)
        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED) {
            try {
                targetFramelayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFramelayout = view.parent.parent as FrameLayout
            }

            true
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {

        }
        return true
    }
}

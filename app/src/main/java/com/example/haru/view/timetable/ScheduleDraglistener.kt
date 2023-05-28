package com.example.haru.view.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.viewmodel.TimetableViewModel


class ScheduleDraglistener (private val timetableViewModel: TimetableViewModel,
                            layoutIndex: ArrayList<Int>,
                            val context : Context) : View.OnDragListener{
    val layoutIndex = layoutIndex
    var lastY = 0
    val shadowView = Button(context)
    override fun onDrag(view: View, event: DragEvent): Boolean {
        var targetFrameLayout: FrameLayout

        if (event.action == DragEvent.ACTION_DROP) {
            val draggedView = event.localState as View
            try {
                targetFrameLayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFrameLayout = view.parent.parent as FrameLayout
            }
            val displayMetrics = targetFrameLayout.resources.displayMetrics
            val x = event.x
            val y = ((event.y / displayMetrics.density).toInt() / 10) * 6

            val dropY = Math.round( y * displayMetrics.density)
            draggedView.y = dropY.toFloat()
            val sourceLayout = draggedView.parent as LinearLayout
            Log.d("DRAGGED", "${targetFrameLayout.id} , $y")

            var index = -1
            for(i : Int in 0..6){
                if(targetFrameLayout.id == layoutIndex[i]){
                    index = i
                    break
                }
            }
            sourceLayout.removeView(draggedView)
            timetableViewModel.scheduleMoved(y, draggedView, index)
        }

        if (event.action == DragEvent.ACTION_DRAG_LOCATION) {
            val draggedView = event.localState as TextView
            try {
                targetFrameLayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFrameLayout = view.parent.parent as FrameLayout
            }
            val shadowParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, draggedView.height)
            shadowView.alpha = 1f
            shadowView.layoutParams = shadowParams
            val displayMetrics = targetFrameLayout.resources.displayMetrics
            val y = ((event.y / displayMetrics.density).toInt() / 10) * 6
            val dropY = Math.round( y * displayMetrics.density)
            shadowView.setBackgroundResource(R.drawable.timetable_schedule)
            shadowView.alpha = 0.5f

            if(dropY != lastY) {
                try {
                    val shadowParent = shadowView.parent as FrameLayout
                    shadowParent.removeView(shadowView)
                } catch (e: java.lang.NullPointerException){}
                shadowView.y = dropY.toFloat()
                shadowView.text = draggedView.text
                targetFrameLayout.addView(shadowView)
                true
            }

        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {
            try {
                targetFrameLayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFrameLayout = view.parent.parent as FrameLayout
            }
            targetFrameLayout.removeView(shadowView)
        }
        return true
    }
}

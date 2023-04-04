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
            SourceLayout.removeView(draggedView)
            timetableViewModel.scheduleMoved(y, draggedView, index)
        }

        if (event.action == DragEvent.ACTION_DRAG_LOCATION) {
            val draggedView = event.localState as TextView
            try {
                targetFramelayout = view as FrameLayout
            } catch (e: java.lang.ClassCastException) {
                targetFramelayout = view.parent.parent as FrameLayout
            }
            val shadowparams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, draggedView.height)
//            shadowView.alpha = 1f
            shadowView.layoutParams = shadowparams
            val displayMetrics = targetFramelayout.resources.displayMetrics
            val y = ((event.y / displayMetrics.density).toInt() / 10) * 10
            val dropY = Math.round( y * displayMetrics.density)
            shadowView.setBackgroundResource(R.drawable.timetable_schedule)

            if(dropY != lastY) {
                try {
                    val shadowParent = shadowView.parent as FrameLayout
                    shadowParent.removeView(shadowView)
                } catch (e: java.lang.NullPointerException){}
                shadowView.y = dropY.toFloat()
                shadowView.text = draggedView.text
                targetFramelayout.addView(shadowView)
                true
            }

        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {

        }
        return true
    }
}

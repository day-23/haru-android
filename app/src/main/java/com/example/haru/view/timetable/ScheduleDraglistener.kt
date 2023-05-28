package com.example.haru.view.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.haru.viewmodel.TimetableViewModel


class ScheduleDraglistener(private val timetableViewModel: TimetableViewModel,
                            layoutIndex: ArrayList<Int>,
                            val context : Context) : View.OnDragListener{
    val layoutIndex = layoutIndex
    val shadowView = Button(context)

    override fun onDrag(view: View, event: DragEvent): Boolean {
        val targetFrameLayout = getTargetFrameLayout(view)
        val draggedView = event.localState as TextView

        when (event.action) {
            DragEvent.ACTION_DROP -> handleDropAction(targetFrameLayout, draggedView, event)
            DragEvent.ACTION_DRAG_LOCATION -> handleDragLocationAction(targetFrameLayout, draggedView, event)
            DragEvent.ACTION_DRAG_EXITED -> handleDragExitedAction(targetFrameLayout)
        }
        return true
    }

    private fun handleDropAction(targetFrameLayout: FrameLayout, draggedView: View, event: DragEvent) {
        val displayMetrics = targetFrameLayout.resources.displayMetrics

        var y = ((event.y - draggedView.height / 2) / displayMetrics.density).toInt()
        val dropY = Math.round(y * displayMetrics.density)

        val parentBounds = getParentBounds(targetFrameLayout)
        val newBottom = dropY + draggedView.height

        // Check if the new location is within the parent bounds
        if (parentBounds.contains(parentBounds.left, dropY, parentBounds.right, newBottom)) {
            // If it is within bounds, update the location of the view
            shadowView.y = dropY.toFloat()
            Log.d("patchMoved", "contain")
        } else if(dropY <= parentBounds.top){
            shadowView.y = parentBounds.top.toFloat()
            Log.d("patchMoved", "dropY <= parentBounds.top")
        } else if(newBottom > parentBounds.bottom){
            shadowView.y = (parentBounds.bottom - draggedView.height).toFloat()
            y = ((parentBounds.bottom - draggedView.height) / displayMetrics.density).toInt()

            Log.d("patchMoved", "newBottom > parentBounds.bottom")
        }

        Log.d("patchMoved", "patchMoved: ${newBottom}, ${parentBounds.bottom}, ${draggedView.height}, ${shadowView.y}, ${shadowView.y + draggedView.height}")
        val sourceLayout = draggedView.parent as LinearLayout

        var index = -1
        for(i : Int in 0..6){
            if(targetFrameLayout.id == layoutIndex[i]){
                index = i
                break
            }
        }
        sourceLayout.removeView(draggedView)

        if(y <= 0) y = 0
        timetableViewModel.scheduleMoved(y, draggedView, index)
    }

    private fun handleDragLocationAction(targetFrameLayout: FrameLayout, draggedView: TextView, event: DragEvent) {
        shadowView.layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, draggedView.height)
        shadowView.background = makeShapeDrawable()
        shadowView.alpha = 0.5f

        try {
            val shadowParent = shadowView.parent as FrameLayout
            shadowParent.removeView(shadowView)
        } catch (_: java.lang.NullPointerException) { }

        val displayMetrics = targetFrameLayout.resources.displayMetrics
        val newLocation = calculateNewLocation(event.y, draggedView.height, displayMetrics)
        val parentBounds = getParentBounds(targetFrameLayout)

        Log.d("handleDragLocationAction", "handleDragLocationAction: $newLocation, ${parentBounds.top} , ${parentBounds.bottom}")

        updateViewLocation(draggedView, newLocation, parentBounds, targetFrameLayout)
    }

    private fun handleDragExitedAction(targetFrameLayout: FrameLayout) {
        targetFrameLayout.removeView(shadowView)
    }


    private fun getTargetFrameLayout(view: View): FrameLayout {
        return try {
            view as FrameLayout
        } catch (e: java.lang.ClassCastException) {
            view.parent.parent as FrameLayout
        }
    }

    private fun calculateNewLocation(y: Float, height: Int, displayMetrics: DisplayMetrics): Int {
        return Math.round(((y - height / 2) / displayMetrics.density) * displayMetrics.density)
    }

    private fun getParentBounds(targetFrameLayout: FrameLayout): Rect {
        return Rect(targetFrameLayout.left, targetFrameLayout.top,
            targetFrameLayout.right, targetFrameLayout.bottom)
    }

    private fun updateViewLocation(draggedView: TextView, newLocation: Int, parentBounds: Rect, targetFrameLayout: FrameLayout) {
        val newTop = newLocation
        val newBottom = newTop + draggedView.height

        // Check if the new location is within the parent bounds
        if (parentBounds.contains(parentBounds.left, newTop, parentBounds.right, newBottom)) {
            // If it is within bounds, update the location of the view
            shadowView.y = newLocation.toFloat()
        } else if(newTop < parentBounds.top){
            shadowView.y = parentBounds.top.toFloat()
        } else if(newBottom > parentBounds.bottom){
            shadowView.y = (parentBounds.bottom - draggedView.height).toFloat()
        }
        shadowView.text = draggedView.text
        targetFrameLayout.addView(shadowView)
    }


    private fun makeShapeDrawable(): LayerDrawable {
        // Create a GradientDrawable with rounded corners
        val gd = GradientDrawable()
        gd.setColor(Color.parseColor("#FFAAE5")) // Initial color
        gd.cornerRadius = 30f

        // Create another GradientDrawable as background
        val bg = GradientDrawable()
        bg.setColor(Color.WHITE) // Set the color to white
        bg.cornerRadius = 30f

        // Use a LayerDrawable to put two drawables together
        val ld = LayerDrawable(arrayOf(bg, gd))
        ld.setLayerInset(1, 1, 1, 1, 1) // This is equivalent to padding 1dp to gd

        return ld
    }
}

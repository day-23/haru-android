package com.example.haru.view.timetable

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.widget.NestedScrollView
import com.example.haru.viewmodel.TimetableViewModel


class TimeTableScheduleDragListener(private val timetableViewModel: TimetableViewModel,
                           layoutIndex: ArrayList<Int>,
                           val context : Context,
                           private val nestedScrollView: NestedScrollView
) : View.OnDragListener{
    val layoutIndex = layoutIndex
    val shadowView = Button(context)
    private val moveOffset = 20
    private val moveThreshold = 50
    private var isRunning = false

    /* 드래그 이벤트 중 스크롤뷰의 스크롤을 위한 코드 */
    private var lastScrollY = -1
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            settingNestedScrollViewByShadowView()
            // Schedule the next update
            if (isRunning) {
                handler.postDelayed(this, 100)
            }
        }
    }


    override fun onDrag(view: View, event: DragEvent): Boolean {
        val targetFrameLayout = getTargetFrameLayout(view)
        val draggedView = event.localState as TextView

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                isRunning = true
                handler.post(runnable)  // Start the recurring task
            }
            DragEvent.ACTION_DROP -> {
                handleDropAction(targetFrameLayout, draggedView, event)
                isRunning = false
                handler.removeCallbacks(runnable)  // Stop the recurring task
            }
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
        } else if(dropY <= parentBounds.top){
            shadowView.y = parentBounds.top.toFloat()
        } else if(newBottom > parentBounds.bottom){
            shadowView.y = (parentBounds.bottom - draggedView.height).toFloat()
            y = ((parentBounds.bottom - draggedView.height) / displayMetrics.density).toInt()
        }
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
        shadowView.apply {
            layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, draggedView.height)
            background = draggedView.background
            alpha = 0.5f
        }

        try {
            val shadowParent = shadowView.parent as FrameLayout
            shadowParent.removeView(shadowView)
        } catch (_: java.lang.NullPointerException) { }

        val displayMetrics = targetFrameLayout.resources.displayMetrics
        val newLocation = calculateNewLocation(event.y, draggedView.height, displayMetrics)
        val parentBounds = getParentBounds(targetFrameLayout)

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


        shadowView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Ensure this listener is only called once
                shadowView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                settingNestedScrollViewByShadowView()
            }
        })






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




    private fun settingNestedScrollViewByShadowView(){
        val shadowViewLocation = IntArray(2)
        shadowView.getLocationOnScreen(shadowViewLocation)
        val shadowViewTop = shadowViewLocation[1]
        val shadowViewBottom = shadowViewTop + shadowView.height
        val shadowViewMedian = (shadowViewTop + shadowViewBottom) / 2

        // Now shadowViewTop should be correctly calculated


        val nestedScrollViewLocation = IntArray(2)
        nestedScrollView.getLocationOnScreen(nestedScrollViewLocation)
        val nestedScrollViewTop = nestedScrollViewLocation[1]
        val nestedScrollViewBottom = nestedScrollViewTop + nestedScrollView.height

        Log.d("Scroll", "shadowView: ${shadowViewTop} ${shadowViewBottom} nested : ${nestedScrollViewTop} ${nestedScrollViewBottom}")
        Log.d("Scroll", "threshold: ${shadowViewTop - nestedScrollViewTop} ${nestedScrollViewBottom - shadowViewBottom}")

        val displayMetrics = shadowView.resources.displayMetrics
        val calculatedOffset = (((moveOffset * displayMetrics.density) / 5) * 5).toInt()
        Log.d("Scroll", "settingNestedScrollViewByShadowView: ${calculatedOffset}}")

        if (shadowViewTop - nestedScrollViewTop < moveThreshold) {
            nestedScrollView.smoothScrollBy(0, -calculatedOffset)
        } else if (nestedScrollViewBottom - shadowViewBottom < moveThreshold) {
            nestedScrollView.smoothScrollBy(0, calculatedOffset)
        } else {
            // Stop the Runnable if none of the conditions are met
            isRunning = false
        }
    }
}

package com.example.haru.view.timetable

import android.graphics.Color
import android.util.Log
import android.view.DragEvent
import android.view.View

/* 꾹 눌러서 새로 일정 만드는 애 드래그 리스너 */
class TimetableDraglistener () : View.OnDragListener{
    override fun onDrag(view: View, event: DragEvent): Boolean {
        val viewSource = event.localState as View
        var selected  = false
        val selectedView = ArrayList<View>()
        selectedView.add(viewSource)

        if (event.action == DragEvent.ACTION_DROP) {

        }

        if (event.action == DragEvent.ACTION_DRAG_ENTERED) {
            Log.d("DRAG", "${view.id}")
        }

        if (event.action == DragEvent.ACTION_DRAG_EXITED) {

        }
        return true
    }
}
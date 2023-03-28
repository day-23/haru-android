package com.example.haru.view.timetable

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoTable_data

class Todo_draglistener () : View.OnDragListener {

    override fun onDrag(view: View, event: DragEvent): Boolean {
        val viewSource = event.localState as View
        val targetRecyclerView: RecyclerView
        val sourceRecyclerView: RecyclerView = viewSource.parent.parent as RecyclerView

        val sunRecyclerviewId = R.id.sun_todos_recycler
        val monRecyclerviewId = R.id.mon_todos_recycler
        val tueRecyclerviewId = R.id.thu_todos_recycler
        val wedRecyclerviewId = R.id.wed_todos_recycler
        val thuRecyclerviewId = R.id.thu_todos_recycler
        val friRecyclerviewId = R.id.fri_todos_recycler
        val satRecyclerviewId = R.id.sat_todos_recycler

        if (event.action == DragEvent.ACTION_DROP) {
            val viewId = view.id
            var targetPosition = -1

            when (viewId) {
                sunRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(sunRecyclerviewId) as RecyclerView
                }
                monRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(monRecyclerviewId) as RecyclerView
                }
                tueRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(thuRecyclerviewId) as RecyclerView
                }
                wedRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(wedRecyclerviewId) as RecyclerView
                }
                thuRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(thuRecyclerviewId) as RecyclerView
                }
                friRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(friRecyclerviewId) as RecyclerView
                }
                satRecyclerviewId -> {
                    targetRecyclerView =
                        view.rootView.findViewById(satRecyclerviewId) as RecyclerView
                }
                else -> {
                    targetRecyclerView = view.parent.parent as RecyclerView
                }
            }
            Log.d("dragged", "$targetRecyclerView")


            if (targetRecyclerView.id != sourceRecyclerView.id) {
                val sourceAdapter = sourceRecyclerView.adapter as TodotableAdapter?
                val sourcePosition = sourceRecyclerView.getChildAdapterPosition(viewSource.parent as View)
                val targetAdapter = targetRecyclerView.adapter as TodotableAdapter?

                var sourceList: ArrayList<TodoTable_data> = ArrayList()
                sourceAdapter?.getItems()?.let { sourceList = it }
                val item = sourceList[sourcePosition]
                sourceAdapter?.removeItem(sourcePosition)

                var targetList: ArrayList<TodoTable_data> = ArrayList()
                targetAdapter?.setItem(item)
            }
            return true
        }
        return true
    }
}
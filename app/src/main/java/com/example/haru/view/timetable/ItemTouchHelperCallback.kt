package com.example.haru.view.timetable

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(val adapter: TimetableAdapter) : ItemTouchHelper.Callback() {

    //private var isDragging = false
    private var selectedViews = ArrayList<View>()

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.DOWN
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if(!selectedViews.contains(target.itemView) ){
            selectedViews.add(target.itemView)
            target.itemView.setBackgroundResource(android.R.color.darker_gray)
        }
        Log.d("drag", "${selectedViews.size}")

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            //isDragging = true
            viewHolder?.itemView?.alpha = 0.7f
            selectedViews.add(viewHolder?.itemView!!)

            viewHolder.itemView.setBackgroundResource(android.R.color.darker_gray)

            //viewHolder?.itemView?.setBackgroundResource(android.R.color.darker_gray)
            Toast.makeText(viewHolder?.itemView?.context, "드래그중...", Toast.LENGTH_SHORT).show()

        }
    }
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        for (view in selectedViews) {
            view.setBackgroundResource(android.R.color.white)
        }
        //viewHolder?.itemView?.setBackgroundResource(android.R.color.white)
        Toast.makeText(viewHolder?.itemView?.context, "드래그끝...", Toast.LENGTH_SHORT).show()
        //isDragging = false
        viewHolder.itemView.alpha = 1.0f
        selectedViews.clear()
    }
}
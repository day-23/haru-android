package com.example.haru.view.checklist

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.Todo
import com.example.haru.view.adapter.TodoAdapter

class ChecklistItemTouchHelperCallback(val listener: ItemTouchHelperListener) :
    ItemTouchHelper.Callback() {
    private var itemTouchHelperListener: ItemTouchHelperListener = listener

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = if (viewHolder is TodoAdapter.TodoViewHolder && !viewHolder.complete) ItemTouchHelper.UP or ItemTouchHelper.DOWN else 0
//        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemTouchHelperListener.onItemMove(
            viewHolder.adapterPosition,
            target.adapterPosition
        )
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        when(actionState){
            ItemTouchHelper.ACTION_STATE_IDLE -> itemTouchHelperListener.onDropAdapter()
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    override fun isLongPressDragEnabled(): Boolean = true
    override fun isItemViewSwipeEnabled(): Boolean = false
}
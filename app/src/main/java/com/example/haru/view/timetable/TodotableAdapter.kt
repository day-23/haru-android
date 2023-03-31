package com.example.haru.view.timetable

import android.content.ClipData
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.adapters.ViewBindingAdapter.setOnLongClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Timetable_date
import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoTable_data

class TodotableAdapter(val context: Context,
                       private var itemList: ArrayList<TodoTable_data>,
                       private val dragListener: Todo_draglistener) : RecyclerView.Adapter<TodotableAdapter.TodotableViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodotableViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.items_todotable_todo, parent, false)
            return TodotableViewHolder(view)
        }

        override fun onBindViewHolder(holder: TodotableViewHolder, position: Int) {
            if(itemList[position].id != ""){
                holder.todotable_item_content.text = itemList[position].content
            }
            holder.todotable_item_content.setOnLongClickListener { view ->
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view?.startDragAndDrop(data, shadowBuilder, view, 0)
                false
            }
            holder.todotable_item_content.setOnDragListener(dragListener)
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }

        fun getItems(): ArrayList<TodoTable_data>{
            return itemList
        }

        fun setItem(item : TodoTable_data){
            Log.d("item", "${item.content}")
            itemList.add(item)
            val sortedList = ArrayList(itemList.sortedBy { it.endDate })
            itemList = sortedList
            notifyDataSetChanged()
        }

        fun removeItem(position : Int) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }

        inner class TodotableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var todotable_item_content = itemView.findViewById<TextView>(R.id.todo_content)

        }
}
package com.example.haru.view.timetable

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.TodoTable_data

class TodotableAdapter(val context: Context,
                       private var itemList: ArrayList<TodoTable_data>) : RecyclerView.Adapter<TodotableAdapter.TodotableViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodotableViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.items_todotable_todo, parent, false)
            return TodotableViewHolder(view)
        }

        override fun onBindViewHolder(holder: TodotableViewHolder, position: Int) {

            if(itemList[position].id != ""){
                holder.todotable_item_content.text = itemList[position].content
            }
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }

        inner class TodotableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var todotable_item_content = itemView.findViewById<TextView>(R.id.todo_content)
        }
}
package com.example.haru.view.timetable

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.Todotable_date

class TodotableAdapter(val context: Context,
                       private var itemList: ArrayList<Todotable_date>) : RecyclerView.Adapter<TodotableAdapter.TodotableViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodotableViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.items_todotable, parent, false)
            return TodotableViewHolder(view)
        }

        override fun onBindViewHolder(holder: TodotableViewHolder, position: Int) {
            Log.d("TAG", "itemList:${itemList.size}")
            holder.todotable_dayofweek.text = itemList[position].dayofweek
            holder.todotable_dayofmonth.text = itemList[position].dayofmonth
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }

        fun setData(newData : ArrayList<Todotable_date>){
            itemList = newData
            notifyDataSetChanged()
        }

        inner class TodotableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var todotable_dayofweek = itemView.findViewById<TextView>(R.id.day_of_week)
            var todotable_dayofmonth = itemView.findViewById<TextView>(R.id.day_of_month)
        }
}
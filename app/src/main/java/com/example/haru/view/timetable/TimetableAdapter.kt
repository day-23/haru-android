package com.example.haru.view.timetable

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class TimetableAdapter(val context: Context,
                       val itemList: ArrayList<timetable_data>) : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
            val view = LayoutInflater.from(context).inflate(com.example.haru.R.layout.items_timetable, parent, false)
            return TimetableViewHolder(view)
        }

        override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
            Log.d("rv has been called", "did")
            holder.timetable_time.text = itemList[position].time
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }

        class TimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView!!) {
            var timetable_time = itemView.findViewById<TextView>(com.example.haru.R.id.tv_timetable_time)
        }

}
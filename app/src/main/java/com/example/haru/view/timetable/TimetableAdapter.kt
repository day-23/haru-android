package com.example.haru.view.timetable

import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.User
import com.example.haru.data.model.timetable_data
import kotlin.collections.ArrayList


class TimetableAdapter(val context: Context,
                       private var itemList: ArrayList<timetable_data>) : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.items_timetable, parent, false)
        return TimetableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        holder.timetable_time.text = itemList[position].time
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    fun setData(newData : ArrayList<timetable_data>){
        itemList = newData
        notifyDataSetChanged()
    }

    inner class TimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timetable_time = itemView.findViewById<TextView>(R.id.tv_timetable_time)
    }
}
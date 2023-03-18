package com.example.haru.view.timetable

import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.core.view.DragStartHelper.OnDragStartListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.haru.R
import java.util.*
import kotlin.collections.ArrayList


class TimetableAdapter(val context: Context,
                       val itemList: ArrayList<timetable_data>) : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>() {

        val selectedItems = SparseBooleanArray()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
            val view = LayoutInflater.from(context).inflate(com.example.haru.R.layout.items_timetable, parent, false)
            return TimetableViewHolder(view)
        }

        override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
            holder.timetable_time.text = itemList[position].time
            if(selectedItems.get(position, false)) {
                holder.itemView.isSelected = true
            } else {
                holder.itemView.isSelected = false
            }
        }
        override fun getItemCount(): Int {
            return itemList.count()
        }

        inner class TimetableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            var timetable_time = itemView.findViewById<TextView>(com.example.haru.R.id.tv_timetable_time)
            var mon = itemView.findViewById<View>(R.id.tv_timetable_mon)
            var tue = itemView.findViewById<View>(R.id.tv_timetable_tue)
            var wed = itemView.findViewById<View>(R.id.tv_timetable_wed)
            var thu = itemView.findViewById<View>(R.id.tv_timetable_thu)
            var fri = itemView.findViewById<View>(R.id.tv_timetable_fri)
            var sat = itemView.findViewById<View>(R.id.tv_timetable_sat)
            var sun = itemView.findViewById<View>(R.id.tv_timetable_sun)
            init {
                itemView.setOnClickListener(this)
            }
            override fun onClick(v: View?) {
                val position = adapterPosition
                Log.d("pos", "${position}")
                if (selectedItems.get(position, false)) {
                    selectedItems.delete(position)
                    v?.isSelected = false
                } else {
                    selectedItems.put(position, true)
                    v?.isSelected = true
                }
            }
        }
}
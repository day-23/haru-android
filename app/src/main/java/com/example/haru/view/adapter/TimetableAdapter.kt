package com.example.haru.view.adapter

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.timetable_data
import com.example.haru.view.timetable.TimetableDraglistener
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
        val draglistener = TimetableDraglistener()

        holder.timetable_sun.setOnDragListener(draglistener)
        holder.timetable_mon.setOnDragListener(draglistener)
        holder.timetable_tue.setOnDragListener(draglistener)
        holder.timetable_wed.setOnDragListener(draglistener)
        holder.timetable_thu.setOnDragListener(draglistener)
        holder.timetable_fri.setOnDragListener(draglistener)
        holder.timetable_sat.setOnDragListener(draglistener)

        holder.timetable_sun.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_mon.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_tue.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_wed.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_thu.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_fri.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
        holder.timetable_sat.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                v.startDrag(data, shadowBuilder, v, 0)
                return true
            }
        })
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
        var timetable_sun = itemView.findViewById<ImageView>(R.id.tv_timetable_sun)
        var timetable_mon = itemView.findViewById<ImageView>(R.id.tv_timetable_mon)
        var timetable_tue = itemView.findViewById<ImageView>(R.id.tv_timetable_tue)
        var timetable_wed = itemView.findViewById<ImageView>(R.id.tv_timetable_wed)
        var timetable_thu = itemView.findViewById<ImageView>(R.id.tv_timetable_thu)
        var timetable_fri = itemView.findViewById<ImageView>(R.id.tv_timetable_fri)
        var timetable_sat = itemView.findViewById<ImageView>(R.id.tv_timetable_sat)
    }
}
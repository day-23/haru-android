package com.example.haru.view.adapter

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R

class AdapterCalendarTouch(val size: Int, val touchList: ArrayList<Boolean>) : RecyclerView.Adapter<AdapterCalendarTouch.TouchView>(){
    inner class TouchView(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun itemChange(startIndex: Int, index: Int, value: Boolean){
        for (i in 0 until touchList.size){
            touchList[i] = false
        }

        for(i in startIndex..index){
            touchList[i] = value
        }

        notifyDataSetChanged()
    }

    fun itemChange(){
        for (i in 0 until touchList.size){
            touchList[i] = false
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCalendarTouch.TouchView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_touch_event,
            parent,
            false
        )

        return TouchView(view)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: TouchView, position: Int) {
        val background =
            holder.itemView.findViewById<ConstraintLayout>(R.id.touch_background_layout)
        if(touchList[position]) {
            background.visibility = View.VISIBLE
        } else {
            background.visibility = View.INVISIBLE
        }
    }
}
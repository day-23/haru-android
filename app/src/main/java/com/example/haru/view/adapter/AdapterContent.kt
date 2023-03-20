package com.example.haru.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.TextView
import com.example.haru.R
import com.example.haru.data.model.CalendarItem

class AdapterContent (val calendarItem:CalendarItem, val todo_schdule: Boolean, val categories:ArrayList<String>? = null) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_content, null)

        val frame = view.findViewById<FrameLayout>(R.id.backframe_layout)
        val content_text = view.findViewById<TextView>(R.id.content_text)

        if(calendarItem.todos!![position].mark) {
            content_text.text = calendarItem.todos!![position].todo!!.content
            frame.setBackgroundColor(Color.rgb(0x00, 0xbf, 0xff))
        } else {
            frame.setBackgroundColor(Color.rgb(0x00, 0xbf, 0xff))
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        if(todo_schdule && calendarItem.todos != null){
            return calendarItem.todos!!.size
        } else return 0
    }
}
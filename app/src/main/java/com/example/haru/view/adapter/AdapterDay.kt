package com.example.haru.view.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.data.model.CalendarContent
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.ContentMark
import com.example.haru.databinding.ListItemDayBinding

class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
    private var date = emptyList<CalendarDate>()
    private var content = emptyList<ContentMark>()

    inner class DayView(private val binding: ListItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(date: CalendarDate?, content: ContentMark?, dateOrContent:Boolean, nullable: Boolean){
//                if(!dateOrContent){
//                    var layoutparam: LayoutParams = binding.itemDayText.layoutParams
//                    layoutparam
//                    binding.itemDayText.gravity
//                }

                if(!dateOrContent && !nullable){
                    binding.textFrameLayout.setBackgroundColor(Color.LTGRAY)
                }

                binding.calendarDate = date
                binding.calendarContent = content
                binding.dateOrContent = dateOrContent
                binding.nullable = nullable
                binding.nullData = ""
            }
        }

    fun updateData(newdayList:List<CalendarDate>, newcontentList:List<ContentMark>) {
        date = newdayList
        content = newcontentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ListItemDayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DayView(binding)
    }

    var duplist = ArrayList<ContentMark>()

    var dateOrContent = true
    var contentcnt = 0
    var datePosition = 0
    var contentPosition = 0

    override fun onBindViewHolder(holder: DayView, position: Int) {
        if(dateOrContent){
            holder.bind(date[datePosition], null,true,false)

            datePosition++

            if (datePosition % 7 == 0){
                dateOrContent = false
            }
        } else {
            Log.d("contentPosition", contentPosition.toString())

            for(con in content){
                if(!duplist.contains(con) && con.position == contentPosition){
                    Log.d("내용","성공")
                    duplist.add(con)
                    contentPosition += con.cnt
                    holder.bind(null, con,false,false)
                    return
                }
            }

            contentPosition++

            if(contentPosition % 7 == 0){
                contentPosition -= 7
                contentcnt++
            }

            if(contentcnt == 4){
                contentPosition += 7
                contentcnt = 0
                dateOrContent = true
            }

            holder.bind(null, null,false,true)
        }
    }

    override fun getItemCount(): Int {
        var size = date.size * 5

        for(con in content){
            if(con.cnt > 1){
                size -= con.cnt-1
            }
        }

        return size
    }
}
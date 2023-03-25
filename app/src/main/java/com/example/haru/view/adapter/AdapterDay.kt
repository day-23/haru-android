package com.example.haru.view.adapter

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.CalendarDate
import com.example.haru.data.model.ContentMark
import com.example.haru.databinding.ListItemDayBinding

class AdapterDay : RecyclerView.Adapter<AdapterDay.DayView>() {
    private var date = emptyList<CalendarDate>()
    private var content = emptyList<ContentMark>()

    inner class DayView(private val binding: ListItemDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(date: CalendarDate?, content: ContentMark?, dateOrContent:Boolean, nullable: Boolean, background:Boolean){
                val textlayout =
                    if (nullable) "" else if (dateOrContent) Integer.toString(date!!.date.date) else content!!.todo.content

                if(background){
                    val layoutParams = binding.itemDayText.layoutParams as FrameLayout.LayoutParams

                    layoutParams.gravity = Gravity.LEFT

                    binding.textFrameLayout.setBackgroundResource(R.drawable.calendar_border)
                    binding.itemDayText.layoutParams = layoutParams
                }

                binding.calendarDate = date
                binding.calendarContent = textlayout
                binding.dateOrContent = dateOrContent
                binding.black = Color.BLACK
            }
        }

    fun updateData(newdayList:List<CalendarDate>, newcontentList:List<ContentMark>) {
        date = newdayList
        content = newcontentList
        notifyDataSetChanged()
    }

    var duplist = ArrayList<ContentMark>()

    var positionplus = 0

    var saveLineList = ArrayList<Int>()
    var saveCntList = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        val binding = ListItemDayBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DayView(binding)
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        if(position == 0){
            duplist = ArrayList()
            positionplus = 0
            saveLineList = ArrayList()
            saveCntList = ArrayList()
        }

        val newpostion = positionplus + position

        if (newpostion / 7 in arrayOf(0, 5, 10, 15, 20, 25, 30)) {
            val dateposition = arrayOf(0,5,10,15,20,25,30).indexOf(newpostion/7)*7
            holder.bind(date[dateposition + newpostion % 35], null, true, false, false)
        } else {
            val contentPosition = newpostion % 7 + newpostion / (date.size) * 7
            val contentLine = (newpostion / 7) % 5

            if (saveLineList.contains(contentLine)) {
                val index = saveLineList.indexOf(contentLine)

                if (saveCntList[index] > 7) {
                    saveCntList[index] -= 7
                    positionplus += 6
                    holder.bind(null,null,false,true,true)
                    return
                } else {
                    val returncnt = saveCntList[index]

                    saveCntList.removeAt(index)
                    saveLineList.removeAt(index)

                    positionplus += returncnt - 1
                    holder.bind(null,null,false,true,true)
                    return
                }
            }

            for (con in content) {
                if (!duplist.contains(con) && con.position == contentPosition) {
                    duplist.add(con)
                    val lastcontentPosition = contentPosition

//                    if (lastcontentPosition / 7 != contentPosition / 7) {
//                        saveLineList.add(contentLine)
//
//                        val returncnt = contentPosition - lastcontentPosition
//                        saveCntList.add(con.cnt - returncnt)
//
//                        positionplus += returncnt - 1
//                        holder.bind(null,con,false,false,true)
//                        return
//                    }

                    positionplus += con.cnt - 1
                    holder.bind(null,con,false,false,true)
                    return
                }
            }

            holder.bind(null,null,false,true,false)
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
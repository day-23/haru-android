package com.example.haru.view.adapter

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R

class AdapterCalendarDetailDialog : RecyclerView.Adapter<AdapterCalendarDetailDialog.DetailView>(){
    inner class DetailView(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCalendarDetailDialog.DetailView {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_detail_dialog,
            parent,
            false
        )

        return DetailView(view)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: DetailView, position: Int) {
    }
}
package com.example.haru.view.checklist

interface ItemTouchHelperListener {
    fun onItemMove(formPosition: Int, toPosition: Int): Boolean
    fun onDropAdapter()

    fun onLiftItem(position : Int?)
//    fun onItemSwipe(position: Int)
}
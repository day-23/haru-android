package com.example.haru.view.checklist

interface ItemTouchHelperListener {
    fun onItemMove(formPosition: Int, toPosition: Int): Boolean
//    fun onItemSwipe(position: Int)
}
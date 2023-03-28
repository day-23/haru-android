package com.example.haru.view.timetable

import com.example.haru.data.model.Todo
import com.example.haru.data.model.TodoTable_data

interface Todo_modifylistener {
    fun setTopData(list: ArrayList<TodoTable_data>)
    fun setBottomData(list: MutableList<TodoTable_data>)
}
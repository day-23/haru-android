package com.example.haru.view.checklist

import android.app.TimePickerDialog
import android.widget.TimePicker

class MyTimeSetListener(private val listener: TimePickerDialog.OnTimeSetListener) :
    TimePickerDialog.OnTimeSetListener {
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.onTimeSet(view, hourOfDay, minute)
    }
}